package hoversprite.project.project.service.impl;

import hoversprite.project.project.base.AbstractService;
import hoversprite.project.project.mapper.SprayOrderMapper;
import hoversprite.project.project.mapper.SpraySessionMapper;
import hoversprite.project.project.model.dto.PersonExpertise;
import hoversprite.project.project.model.dto.SprayOrderDTO;
import hoversprite.project.project.model.dto.SpraySessionDTO;
import com.example.project.model.entity.*;
import hoversprite.project.project.model.entity.*;
import hoversprite.project.project.repository.SprayOrderRepository;
import hoversprite.project.project.service.PersonService;
import hoversprite.project.project.service.SprayOrderService;
import hoversprite.project.project.service.SpraySession2Service;
import hoversprite.project.project.validator.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SprayOrderServiceImpl extends AbstractService<SprayOrderDTO, Integer, PersonRole> implements SprayOrderService {

    private static final int MAX_SPRAYS_SESSIONS_IN_ONE_HOUR = 2;
    private static final int COST_PER_DECARE = 30000;
    private static final String NO_MORE_THAN_2_SESSIONS_ALLOWED_AT_THE_SAME_TIME = "Cannot create more than 2 sessions per time slot on a given day.";

    @Autowired
    private SprayOrderRepository sprayOrderRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private SpraySession2Service spraySession2Service;

    @Autowired
    private NotificationService notificationService;


    @Override
    public SprayOrderDTO findById(Integer id) {
        return SprayOrderMapper.INSTANCE.toDto(sprayOrderRepository.findById(id).orElse(null));
    }

    @Override
    public List<SprayOrderDTO> findAll() {
        return sprayOrderRepository.findAll().stream()
                .map(SprayOrderMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        sprayOrderRepository.deleteById(id);
    }

    @Override
    protected void validateForSave(ValidationUtils validator, SprayOrderDTO sprayOrderDTO, PersonRole personRole) {
        verifySessionAvailable(validator, sprayOrderDTO);
    }

    private void verifySessionAvailable(ValidationUtils validator, SprayOrderDTO sprayOrderDTO) {
        SpraySessionDTO spraySessionDTO = sprayOrderDTO.getSpraySession();
        if (spraySessionDTO.getId() == null) {
            List<SpraySession_2> existingSessions = spraySession2Service.findSpraySessionByDate(spraySessionDTO.getDate(), spraySessionDTO.getStartTime());
            validator.isTrue(existingSessions.size() < MAX_SPRAYS_SESSIONS_IN_ONE_HOUR, NO_MORE_THAN_2_SESSIONS_ALLOWED_AT_THE_SAME_TIME);
        }
    }

    @Override
    protected SprayOrderDTO executeSave(Integer userid, SprayOrderDTO sprayOrderDTO, PersonRole personRole) {
        SpraySessionDTO spraySessionDTO = sprayOrderDTO.getSpraySession();
        SprayOrder sprayOrder = SprayOrderMapper.INSTANCE.toEntitySave(sprayOrderDTO);
        SpraySession_2 spraySession = SpraySessionMapper.INSTANCE.toEntitySave(spraySessionDTO);

        if (personRole == PersonRole.RECEPTIONIST) {
            sprayOrder.setStatus(SprayStatus.CONFIRMED);
        }

        calculateCostPerArea(sprayOrder);
        spraySession.setSprayOrder(sprayOrder);
        sprayOrder.setSpraySession(spraySession);

        // Save the order
        SprayOrderDTO savedOrderDTO = SprayOrderMapper.INSTANCE.toDto(sprayOrderRepository.save(sprayOrder));

        // Notify the farmer about the new order
        notificationService.notifyFarmer(userid.longValue(), "Your order has been created successfully!");

        return savedOrderDTO;
    }


    @Override
    protected void validateForUpdate(ValidationUtils validator, SprayOrderDTO sprayOrderDTO, PersonRole personRole) {
        verifySessionAvailable(validator, sprayOrderDTO);
        switch (personRole) {
            case RECEPTIONIST:
                if (sprayOrderDTO.getStatus() != null) {
                    validator.isTrue(
                            (sprayOrderDTO.getStatus() == SprayStatus.CANCELLED || sprayOrderDTO.getStatus() == SprayStatus.PENDING ||
                                    sprayOrderDTO.getStatus() == SprayStatus.CONFIRMED), "The receptionist user is only allowed to create, cancel or confirm the order.");
                }
                break;
            case SPRAYER:
                if (sprayOrderDTO.getStatus() != null) {
                    validator.isTrue(
                            (sprayOrderDTO.getStatus() == SprayStatus.IN_PROGRESS || sprayOrderDTO.getStatus() == SprayStatus.SPRAY_COMPLETED), "The selected user is only allowed to set in progress or set completed for the order.");
                }
                break;
        }
    }

    @Override
    protected SprayOrderDTO executeUpdate(Integer userId, Integer orderId, SprayOrderDTO sprayOrderDTO, PersonRole personRole) {
        SprayOrder existingSprayOrder = sprayOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + sprayOrderDTO.getId()));

        SprayStatus newStatus = sprayOrderDTO.getStatus();
        SprayStatus oldStatus = existingSprayOrder.getStatus();

        // Notification messages
        String farmerNotificationMessage = "";
        String sprayerNotificationMessage = "";

        if ((personRole == PersonRole.FARMER || personRole == PersonRole.RECEPTIONIST) &&
                (oldStatus == SprayStatus.PENDING || oldStatus == null) && (newStatus == SprayStatus.PENDING)) {
            existingSprayOrder.setCropType(sprayOrderDTO.getCropType());
            existingSprayOrder.setArea(sprayOrderDTO.getArea());
            existingSprayOrder.setDateTime(sprayOrderDTO.getDateTime());
            existingSprayOrder.setLocation(sprayOrderDTO.getLocation());

            for (SprayerAssignment assignment : existingSprayOrder.getSprayerAssignments()) {
                notificationService.notifySprayer(assignment.getSprayer().getId(), "You have been assigned to a new spray order.");
            }

            SpraySession_2 updatedSpraySession = SpraySessionMapper.INSTANCE.toEntityUpdate(sprayOrderDTO.getSpraySession());
            if (updatedSpraySession != null && !existingSprayOrder.getSpraySession().equals(updatedSpraySession)) {
                existingSprayOrder.setSpraySession(updatedSpraySession);
                updatedSpraySession.setSprayOrder(existingSprayOrder);
            }
        }

        switch (personRole) {
            case RECEPTIONIST:
                if (oldStatus == SprayStatus.PENDING) {
                    if (newStatus == SprayStatus.CANCELLED) {
                        existingSprayOrder.setStatus(newStatus);
                        existingSprayOrder.setSpraySession(null);
                        farmerNotificationMessage = "Your order has been cancelled.";
                    }
                    if (newStatus == SprayStatus.CONFIRMED) {
                        existingSprayOrder.setStatus(newStatus);
                        farmerNotificationMessage = "Your order has been confirmed!";
                    }
                }
                if (oldStatus == SprayStatus.CONFIRMED) {
                    if (sprayOrderDTO.getSprayerAssignments().isEmpty()) {
                        throw new RuntimeException("No SPRAYERs assigned. Cannot set the status to ASSIGNED.");
                    }

                    List<Integer> sprayerIds = sprayOrderDTO.getSprayerAssignments()
                            .stream().map(sprayerAssignmentDTO -> sprayerAssignmentDTO.getSprayer().getId())
                            .collect(Collectors.toList());

                    List<Person> sprayers = personService.getUserByIds(sprayerIds);
                    if (sprayers.isEmpty()) {
                        throw new RuntimeException("SPRAYERs not found. Cannot set the status to ASSIGNED.");
                    }

                    boolean hasApprentice = sprayers.stream()
                            .anyMatch(sprayer -> sprayer.getExpertise() == PersonExpertise.APPRENTICE);

                    if (hasApprentice) {
                        throw new RuntimeException("Cannot assign an Apprentice SPRAYER. Please select an Adept or Expert SPRAYER.");
                    }
                    existingSprayOrder.getSprayerAssignments().clear();
                    List<SprayerAssignment> sprayerAssignments = sprayers.stream().map(sprayer -> {
                        SprayerAssignment assignment = SprayerAssignment.builder()
                                .sprayer(sprayer)
                                .isPrimary(true)
                                .sprayOrder(existingSprayOrder)
                                .build();
                        sprayer.getSprayerAssignments().add(assignment);
                        return assignment;
                    }).collect(Collectors.toList());
                    existingSprayOrder.getSprayerAssignments().addAll(sprayerAssignments);
                    existingSprayOrder.setStatus(SprayStatus.ASSIGNED);
                    farmerNotificationMessage = "Your order has been assigned!";
                    sprayerNotificationMessage = "You have been assigned to a new spray order.";
                }
                break;

            case SPRAYER:
                if (newStatus == SprayStatus.IN_PROGRESS && oldStatus == SprayStatus.ASSIGNED) {
                    existingSprayOrder.setStatus(SprayStatus.IN_PROGRESS);
                    farmerNotificationMessage = "Your spray order is now in progress.";
                }
                if (newStatus == SprayStatus.SPRAY_COMPLETED && oldStatus == SprayStatus.IN_PROGRESS) {
                    existingSprayOrder.setStatus(SprayStatus.SPRAY_COMPLETED);
                    farmerNotificationMessage = "Your spray order has been completed.";
                }
                break;

            default:
                throw new RuntimeException("Unsupported role: " + personRole);
        }

        // Handle payment and order completion
        if (oldStatus == SprayStatus.SPRAY_COMPLETED && personRole == PersonRole.SPRAYER) {
            BigDecimal paymentReceivedAmount = sprayOrderDTO.getPaymentReceivedAmount();
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
            existingSprayOrder.setStatus(SprayStatus.COMPLETED);
            farmerNotificationMessage = "Your spray order has been fully completed and paid.";
        }

        // Send notifications
        if (!farmerNotificationMessage.isEmpty()) {
            notificationService.notifyFarmer(existingSprayOrder.getFarmer().getId(), farmerNotificationMessage);
        }
        if (!sprayerNotificationMessage.isEmpty()) {
            for (SprayerAssignment assignment : existingSprayOrder.getSprayerAssignments()) {
                notificationService.notifySprayer(assignment.getSprayer().getId(), sprayerNotificationMessage);
            }
        }

        return SprayOrderMapper.INSTANCE.toDto(sprayOrderRepository.save(existingSprayOrder));
    }


    private void calculateCostPerArea(SprayOrder sprayOrder) {
        sprayOrder.setCost(sprayOrder.getArea().doubleValue() * COST_PER_DECARE);
    }

    @Override
    public List<SprayOrderDTO> getOrdersByUser(Integer id) {
        return sprayOrderRepository.getOrdersByUser(id).stream().map(SprayOrderMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public List<SprayOrderDTO> getOrdersBySprayer(Integer sprayerId) {
        return sprayOrderRepository.getOrdersBySprayer(sprayerId).stream().map(SprayOrderMapper.INSTANCE::toDto).collect(Collectors.toList());
    }
}
