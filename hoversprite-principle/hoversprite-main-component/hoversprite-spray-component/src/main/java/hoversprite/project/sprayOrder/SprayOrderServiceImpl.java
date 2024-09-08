package hoversprite.project.sprayOrder;

import hoversprite.project.common.base.AbstractService;
import hoversprite.project.common.domain.*;
import hoversprite.project.common.validator.ValidationUtils;
import hoversprite.project.partner.PersonDTO;
import hoversprite.project.partner.PersonGlobalService;
import hoversprite.project.payment.PaymentGlobalService;
import hoversprite.project.payment.request.PaymentRequest;
import hoversprite.project.request.SprayOrderRequest;
import hoversprite.project.request.SpraySessionRequest;
import hoversprite.project.spraySession.SpraySession2GlobalService;
import hoversprite.project.spraySession.SpraySessionDTO;
import hoversprite.project.sprayerAssignment.SprayerAssignmentGlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
class SprayOrderServiceImpl extends AbstractService<SprayOrderDTO, SprayOrderRequest, Long, PersonRole> implements SprayOrderService {

    private static final int MAX_SPRAYS_SESSIONS_IN_ONE_HOUR = 2;
    private static final int COST_PER_DECARE = 30000;
    private static final String NO_MORE_THAN_2_SESSIONS_ALLOWED_AT_THE_SAME_TIME = "Cannot create more than 2 sessions per time slot on a given day.";

    @Autowired
    private SprayOrderRepository sprayOrderRepository;

    @Autowired
    private PersonGlobalService personGlobalService;

    @Autowired
    private SpraySession2GlobalService spraySession2GlobalService;

    @Autowired
    private SprayerAssignmentGlobalService sprayerAssignmentGlobalService;

    @Autowired
    private SprayOrderActionService sprayOrderActionService;

    @Autowired
    private PaymentGlobalService paymentGlobalService;

//    @Autowired
//    private NotificationService notificationService;


    @Override
    public SprayOrderDTO findById(Long id) {
        return SprayOrderMapper.INSTANCE.toDto(sprayOrderRepository.findById(id).orElse(null));
    }

    @Override
    public List<SprayOrderDTO> findAll() {
        return sprayOrderRepository.findAll().stream()
                .map(SprayOrderMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        sprayOrderRepository.deleteById(id);
    }

    @Override
    protected void validateForSave(ValidationUtils validator, SprayOrderRequest sprayOrder, PersonRole personRole) {
    }

    @Override
    protected SprayOrderDTO executeSave(Long userid, SprayOrderRequest sprayOrderRequest, PersonRole personRole) {
        SpraySessionRequest spraySessionRequest = sprayOrderRequest.getSpraySession();
        SprayOrder sprayOrder = SprayOrderMapper.INSTANCE.toEntitySave(sprayOrderRequest);
        if (personRole.hasRole(PersonRole.RECEPTIONIST)) {
            sprayOrder.setStatus(SprayStatus.CONFIRMED);
            sprayOrder.setReceptionist(Long.valueOf(userid));
        } else if (personRole.hasRole(PersonRole.FARMER)) {
            sprayOrder.setFarmer(Long.valueOf(userid));
        }
        calculateCostPerArea(sprayOrder);


        SprayOrder savedSprayOrder = sprayOrderRepository.save(sprayOrder);
        spraySessionRequest.setSprayOrder(savedSprayOrder.getId());
        SpraySessionDTO savedSpraySession = spraySession2GlobalService.save(userid, spraySessionRequest, personRole);
        savedSprayOrder.setSpraySession(savedSpraySession.getId());

        // Save the order
        SprayOrderDTO savedOrderDTO = SprayOrderMapper.INSTANCE.toDto(sprayOrderRepository.save(savedSprayOrder));

        // Notify the farmer about the new order
//        notificationService.notifyFarmer(userid.longValue(), "Your order has been created successfully!");

        return savedOrderDTO;
    }


    @Override
    protected void validateForUpdate(ValidationUtils validator, SprayOrderRequest sprayOrder, PersonRole personRole) {
        switch (personRole) {
            case RECEPTIONIST:
                if (sprayOrder.getStatus() != null) {
                    validator.isTrue(
                            (sprayOrder.getStatus() == SprayStatus.CANCELLED || sprayOrder.getStatus() == SprayStatus.PENDING ||
                                    sprayOrder.getStatus() == SprayStatus.CONFIRMED), "The receptionist user is only allowed to create, cancel or confirm the order.");
                }
                break;
            case SPRAYER:
                if (sprayOrder.getStatus() != null) {
                    validator.isTrue(
                            (sprayOrder.getStatus() == SprayStatus.IN_PROGRESS || sprayOrder.getStatus() == SprayStatus.SPRAY_COMPLETED), "The selected user is only allowed to set in progress or set completed for the order.");
                }
                break;
        }
    }

    @Override
    protected SprayOrderDTO executeUpdate(Long userId, Long orderId, SprayOrderRequest sprayOrder, PersonRole personRole) {
        SprayOrder existingSprayOrder = sprayOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + sprayOrder.getId()));

        SprayStatus newStatus = sprayOrder.getStatus();
        SprayStatus oldStatus = existingSprayOrder.getStatus();

        // Notification messages
        String farmerNotificationMessage = "";
        String sprayerNotificationMessage = "";

        if ((personRole.hasRole(PersonRole.FARMER) || personRole.hasRole(PersonRole.RECEPTIONIST)) &&
                (oldStatus == SprayStatus.PENDING || oldStatus == null) && (newStatus == SprayStatus.PENDING)) {
            existingSprayOrder.setCropType(sprayOrder.getCropType());
            existingSprayOrder.setArea(sprayOrder.getArea());
            existingSprayOrder.setDateTime(sprayOrder.getDateTime());
            existingSprayOrder.setLocation(sprayOrder.getLocation());

//            for (SprayerAssignment assignment : existingSprayOrder.getSprayerAssignments()) {
//                notificationService.notifySprayer(assignment.getSprayer().getId(), "You have been assigned to a new spray order.");
//            }
            sprayOrder.getSpraySession().setSprayOrder(existingSprayOrder.getId());
            SpraySessionDTO savedSpraySession = spraySession2GlobalService.update(userId, existingSprayOrder.getSpraySession(), sprayOrder.getSpraySession(), personRole);
            existingSprayOrder.setSpraySession(savedSpraySession.getId());
        }

        if (personRole == PersonRole.ADMIN) {
            // ADMIN can perform both RECEPTIONIST and SPRAYER actions
            handleReceptionistActions(userId, oldStatus, newStatus, sprayOrder, existingSprayOrder, personRole);
            handleSprayerActions(oldStatus, newStatus, existingSprayOrder);
        } else {
            switch (personRole) {
                case RECEPTIONIST:
                    handleReceptionistActions(userId, oldStatus, newStatus, sprayOrder, existingSprayOrder, personRole);
                    break;

                case SPRAYER:
                    handleSprayerActions(oldStatus, newStatus, existingSprayOrder);
                    break;

                default:
                    throw new RuntimeException("Unsupported role: " + personRole);
            }
        }

        // Handle payment and order completion
        if (oldStatus == SprayStatus.SPRAY_COMPLETED && personRole.hasRole(PersonRole.SPRAYER)) {
            BigDecimal paymentReceivedAmount = sprayOrder.getPaymentReceivedAmount();
            if (paymentReceivedAmount == null || paymentReceivedAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Invalid payment amount. Payment must be greater than zero.");
            }

            BigDecimal totalCost = BigDecimal.valueOf(existingSprayOrder.getCost());
            if (paymentReceivedAmount.compareTo(totalCost) < 0) {
                throw new RuntimeException("Payment received is less than the total cost. Cannot complete the order.");
            }

            BigDecimal changeAmount = paymentReceivedAmount.subtract(totalCost);
            existingSprayOrder.setPaymentReceivedAmount(paymentReceivedAmount);
            existingSprayOrder.setChangeAmount(changeAmount);

            paymentGlobalService.save(userId,
                    PaymentRequest.builder()
                            .sprayOrder(existingSprayOrder.getId())
                            .farmer(userId)
                            .amount(paymentReceivedAmount)
                            .paymentMethod(sprayOrder.getPayment().getPaymentMethod())
                            .paymentDate(LocalDateTime.now())
                            .status(sprayOrder.getPayment().getStatus())
                            .build(), personRole);

            existingSprayOrder.setStatus(SprayStatus.COMPLETED);
            farmerNotificationMessage = "Your spray order has been fully completed and paid.";
        }

//        // Send notifications
//        if (!farmerNotificationMessage.isEmpty()) {
//            notificationService.notifyFarmer(existingSprayOrder.getFarmer().getId(), farmerNotificationMessage);
//        }
//        if (!sprayerNotificationMessage.isEmpty()) {
//            for (SprayerAssignment assignment : existingSprayOrder.getSprayerAssignments()) {
//                notificationService.notifySprayer(assignment.getSprayer().getId(), sprayerNotificationMessage);
//            }
//        }

        return SprayOrderMapper.INSTANCE.toDto(sprayOrderRepository.save(existingSprayOrder));
    }

    private void handleReceptionistActions(Long userId, SprayStatus oldStatus, SprayStatus newStatus, SprayOrderRequest sprayOrder, SprayOrder existingSprayOrder, PersonRole personRole) {
        if (oldStatus == SprayStatus.PENDING) {
            if (newStatus == SprayStatus.CANCELLED) {
                existingSprayOrder.setStatus(newStatus);
                existingSprayOrder.setSpraySession(null);
//                farmerNotificationMessage = "Your order has been cancelled.";
            }
            if (newStatus == SprayStatus.CONFIRMED) {
                existingSprayOrder.setStatus(newStatus);
//                farmerNotificationMessage = "Your order has been confirmed!";
            }
        }
        if (oldStatus == SprayStatus.CONFIRMED) {
            if (sprayOrder.getSprayerAssignments().isEmpty()) {
                throw new RuntimeException("No SPRAYERs assigned. Cannot set the status to ASSIGNED.");
            }

            List<Long> sprayerIds = sprayOrder.getSprayerAssignments()
                    .stream().map(sprayerAssignmentDTO -> sprayerAssignmentDTO.getSprayer().getId())
                    .collect(Collectors.toList());

            List<PersonDTO> sprayers = personGlobalService.getUserByIds(sprayerIds);
            if (sprayers.isEmpty()) {
                throw new RuntimeException("SPRAYERs not found. Cannot set the status to ASSIGNED.");
            }

            boolean hasApprentice = sprayers.stream()
                    .anyMatch(sprayer -> sprayer.getExpertise() == PersonExpertise.APPRENTICE);

            if (hasApprentice) {
                throw new RuntimeException("Cannot assign an Apprentice SPRAYER. Please select an Adept or Expert SPRAYER.");
            }

            sprayOrder.getSprayerAssignments().stream()
                    .map(assignmentRequest -> {
                        assignmentRequest.setSprayOrder(existingSprayOrder.getId());
                        return sprayerAssignmentGlobalService.save(userId, assignmentRequest, personRole);
                    })
                    .collect(Collectors.toList());

            existingSprayOrder.setStatus(SprayStatus.ASSIGNED);
//            farmerNotificationMessage = "Your order has been assigned!";
//            sprayerNotificationMessage = "You have been assigned to a new spray order.";
        }
    }

    private void handleSprayerActions(SprayStatus oldStatus, SprayStatus newStatus, SprayOrder existingSprayOrder) {
        if (newStatus == SprayStatus.IN_PROGRESS && oldStatus == SprayStatus.ASSIGNED) {
            existingSprayOrder.setStatus(SprayStatus.IN_PROGRESS);
//            farmerNotificationMessage = "Your spray order is now in progress.";
        }
        if (newStatus == SprayStatus.SPRAY_COMPLETED && oldStatus == SprayStatus.IN_PROGRESS) {
            existingSprayOrder.setStatus(SprayStatus.SPRAY_COMPLETED);
//            farmerNotificationMessage = "Your spray order has been completed.";
        }
    }



    private void calculateCostPerArea(SprayOrder sprayOrder) {
        sprayOrder.setCost(sprayOrder.getArea().doubleValue() * COST_PER_DECARE);
    }

    @Override
    public List<SprayOrderDTO> getOrdersByUser(long id) {
        return sprayOrderRepository.getOrdersByUser(id).stream().map(SprayOrderMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public List<SprayOrderDTO> getOrdersBySprayer(Long sprayerId) {
        return sprayOrderRepository.getOrdersBySprayer(sprayerId).stream().map(SprayOrderMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public List<SprayOrderDTO> getUnAssignedSprayOrders() {
        return sprayOrderRepository.getUnAssignedSprayOrders().stream().map(SprayOrderMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public void automateSprayerSelection(SprayOrderDTO sprayOrder) {
        boolean assignmentsMade = sprayOrderActionService.automateSprayerSelected(sprayOrder);
        if (assignmentsMade) {
            lockAndUnlockStatus(sprayOrder, SprayStatus.ASSIGNED);
        }
    }

    @Override
    public SprayOrderDTO lockAndUnlockStatus(SprayOrderDTO sprayOrder, SprayStatus status) {
        SprayOrder existingSprayOrder = sprayOrderRepository.findById(sprayOrder.getId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + sprayOrder.getId()));

        existingSprayOrder.setStatus(status);
        return SprayOrderMapper.INSTANCE.toDto(sprayOrderRepository.save(existingSprayOrder));
    }
}
