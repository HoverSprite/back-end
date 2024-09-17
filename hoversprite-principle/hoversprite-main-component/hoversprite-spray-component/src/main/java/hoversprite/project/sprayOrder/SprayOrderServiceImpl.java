package hoversprite.project.sprayOrder;

import com.mysema.commons.lang.Pair;
import hoversprite.project.EmailService;
import hoversprite.project.GeocodingUtil;
import hoversprite.project.notification.NotificationService;
import hoversprite.project.common.base.AbstractService;
import hoversprite.project.common.domain.*;
import hoversprite.project.common.validator.ValidationUtils;
import hoversprite.project.feedback.FeedbackGlobalService;
import hoversprite.project.mapper.PersonResponseMapper;
import hoversprite.project.mapper.SpraySessionResponseMapper;
import hoversprite.project.partner.PersonDTO;
import hoversprite.project.partner.PersonGlobalService;
import hoversprite.project.partner.PersonService;
import hoversprite.project.payment.PaymentGlobalService;
import hoversprite.project.payment.request.PaymentRequest;
import hoversprite.project.request.PersonRequest;
import hoversprite.project.request.SprayOrderRequest;
import hoversprite.project.request.SpraySessionRequest;
import hoversprite.project.request.SprayerAssignmentRequest;
import hoversprite.project.response.FeedBackReponse;
import hoversprite.project.response.SprayOrderResponse;
import hoversprite.project.response.SprayerAssignmentResponse;
import hoversprite.project.spraySession.SpraySession2GlobalService;
import hoversprite.project.spraySession.SpraySessionDTO;
import hoversprite.project.sprayerAssignment.SprayerAssignmentDTO;
import hoversprite.project.sprayerAssignment.SprayerAssignmentGlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import cn.hutool.core.date.ChineseDate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;

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
    private PaymentGlobalService paymentGlobalService;

    @Autowired
    private FeedbackGlobalService feedbackGlobalService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private GeocodingUtil geocodingUtil;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PersonService personService;


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

            // Check if the farmer exists, if not, create a new farmer
            PersonDTO farmer = personService.findFarmerByPhoneNumber(sprayOrderRequest.getFarmer().getPhoneNumber());
            if (farmer == null) {
                PersonRequest newFarmerRequest = new PersonRequest();
                newFarmerRequest.setFullName(sprayOrderRequest.getFarmer().getFullName());
                newFarmerRequest.setPhoneNumber(sprayOrderRequest.getFarmer().getPhoneNumber());
                newFarmerRequest.setHomeAddress(sprayOrderRequest.getLocation());
                if (newFarmerRequest.getEmailAddress() == null || newFarmerRequest.getEmailAddress().isEmpty()) {
                    newFarmerRequest.setEmailAddress("example@gmail.com");
                }

                farmer = personService.createFarmer(newFarmerRequest);
            }
            sprayOrder.setFarmer(farmer.getId());
        } else {
            sprayOrder.setFarmer(Long.valueOf(userid));
        }
        calculateCostPerArea(sprayOrder);
        sprayOrder.setLocation(sprayOrderRequest.getLocation());


        SprayOrder savedSprayOrder = sprayOrderRepository.save(sprayOrder);
        spraySessionRequest.setSprayOrder(savedSprayOrder.getId());
        SpraySessionDTO savedSpraySession = spraySession2GlobalService.save(userid, spraySessionRequest, personRole);
        savedSprayOrder.setSpraySession(savedSpraySession.getId());

//        // Get coordinates for the location
        if (sprayOrder.getLatitude() == null || sprayOrder.getLongitude() == null) {
            GeocodingUtil.LatLng coordinates = geocodingUtil.getCoordinates(sprayOrder.getLocation());
            if (coordinates != null) {
                sprayOrder.setLatitude(coordinates.lat);
                sprayOrder.setLongitude(coordinates.lng);
            }
        }
        // Save the order
        SprayOrderDTO savedOrderDTO = SprayOrderMapper.INSTANCE.toDto(sprayOrderRepository.save(savedSprayOrder));
        sendConfirmationEmail(savedOrderDTO);
//         Notify the farmer about the new order
        // Notify the farmer about the new order
        if (personRole.hasRole(PersonRole.FARMER)) {
            notificationService.notifyUser(savedOrderDTO.getFarmer(), PersonRole.FARMER.toString(),
                    "Your spray order has been created successfully and is pending confirmation.");
        } else if (personRole.hasRole(PersonRole.RECEPTIONIST)) {
            notificationService.notifyUser(savedOrderDTO.getFarmer(), PersonRole.FARMER.toString(),
                    "A new spray order has been created and confirmed for you by a receptionist.");
        }
        return savedOrderDTO;
    }


    @Override
    protected void validateForUpdate(ValidationUtils validator, SprayOrderRequest sprayOrder, PersonRole personRole) {
        switch (personRole) {
//            case RECEPTIONIST:
//                if (sprayOrder.getStatus() != null) {
//                    validator.isTrue(
//                            (sprayOrder.getStatus() == SprayStatus.CANCELLED || sprayOrder.getStatus() == SprayStatus.PENDING ||
//                                    sprayOrder.getStatus() == SprayStatus.CONFIRMED), "The receptionist user is only allowed to create, cancel or confirm the order.");
//                }
//                break;
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


        if ((personRole.hasRole(PersonRole.FARMER) || personRole.hasRole(PersonRole.RECEPTIONIST)) &&
                (oldStatus == SprayStatus.PENDING || oldStatus == null) && (newStatus == SprayStatus.PENDING)) {
            existingSprayOrder.setCropType(sprayOrder.getCropType());
            existingSprayOrder.setArea(sprayOrder.getArea());
            existingSprayOrder.setDateTime(sprayOrder.getDateTime());
            existingSprayOrder.setLocation(sprayOrder.getLocation());
            GeocodingUtil.LatLng coordinates = geocodingUtil.getCoordinates(sprayOrder.getLocation());
            if (coordinates != null) {
                sprayOrder.setLatitude(coordinates.lat);
                sprayOrder.setLongitude(coordinates.lng);
            }

//            for (SprayerAssignment assignment : existingSprayOrder.getSprayerAssignments()) {
//                notificationService.notifySprayer(assignment.getSprayer().getId(), "You have been assigned to a new spray order.");
//            }
            sprayOrder.getSpraySession().setSprayOrder(existingSprayOrder.getId());
            SpraySessionDTO savedSpraySession = spraySession2GlobalService.update(userId, existingSprayOrder.getSpraySession(), sprayOrder.getSpraySession(), personRole);
            existingSprayOrder.setSpraySession(savedSpraySession.getId());
            notificationService.notifyUser(existingSprayOrder.getFarmer(), PersonRole.FARMER.toString(),
                    "Your spray order details have been updated.");
        }

        if (personRole == PersonRole.ADMIN) {
            // ADMIN can perform both RECEPTIONIST and SPRAYER actions
            handleReceptionistActions(userId, oldStatus, newStatus, sprayOrder, existingSprayOrder, personRole);
            handleSprayerActions(oldStatus, newStatus, existingSprayOrder);
        } else {
            switch (personRole) {
                case RECEPTIONIST:
                    handleReceptionistActions(userId, oldStatus, newStatus, sprayOrder, existingSprayOrder, personRole);
                    handleSprayerActions(oldStatus, newStatus, existingSprayOrder);
                    break;

                case SPRAYER:
                    handleSprayerActions(oldStatus, newStatus, existingSprayOrder);
                    break;

                default:
                    break;
            }
        }

        // Handle payment and order completion
        if (oldStatus == SprayStatus.SPRAY_COMPLETED) {
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
            notificationService.notifyUser(existingSprayOrder.getFarmer(), PersonRole.FARMER.toString(),
                    "Your spray order has been fully completed and paid.");
        }


        return SprayOrderMapper.INSTANCE.toDto(sprayOrderRepository.save(existingSprayOrder));
    }

    private void handleReceptionistActions(Long userId, SprayStatus oldStatus, SprayStatus newStatus, SprayOrderRequest sprayOrder, SprayOrder existingSprayOrder, PersonRole personRole) {
        if (sprayOrder.getAutoAssign() != null) {
            existingSprayOrder.setAutoAssign(sprayOrder.getAutoAssign());
        }
        if (oldStatus == SprayStatus.PENDING) {
            if (newStatus == SprayStatus.CANCELLED) {
                existingSprayOrder.setStatus(newStatus);
                spraySession2GlobalService.deleteById(existingSprayOrder.getSpraySession());
                existingSprayOrder.setSpraySession(null);
                notificationService.notifyUser(existingSprayOrder.getFarmer(), PersonRole.FARMER.toString(),
                        "Your spray order has been cancelled.");
            }
            if (newStatus == SprayStatus.CONFIRMED) {
                existingSprayOrder.setStatus(newStatus);
                notificationService.notifyUser(existingSprayOrder.getFarmer(), PersonRole.FARMER.toString(),
                        "Your spray order has been confirmed!");
            }
        }
        handleConfirmed(oldStatus, sprayOrder, existingSprayOrder, userId, personRole);
        handleAssigned(oldStatus, sprayOrder, existingSprayOrder, userId, personRole);
    }

    private void handleAssigned(SprayStatus oldStatus, SprayOrderRequest sprayOrder, SprayOrder existingSprayOrder, Long userId, PersonRole personRole) {
        if (oldStatus == SprayStatus.ASSIGNED) {
            Long sprayOrderId = existingSprayOrder.getId();

            List<SprayerAssignmentDTO> existingAssignments = sprayerAssignmentGlobalService
                    .findAllBySprayOrderIds(Collections.singletonList(sprayOrderId));

            List<Long> existingSprayerIds = existingAssignments.stream()
                    .map(SprayerAssignmentDTO::getSprayer)
                    .collect(Collectors.toList());

            if (sprayOrder.getSprayerAssignments().isEmpty()) {
                sprayerAssignmentGlobalService.deleteAllByIds(existingAssignments.stream().map(SprayerAssignmentDTO::getId).collect(Collectors.toList()));
                existingSprayOrder.setStatus(SprayStatus.CONFIRMED);
                return;
            }

            for (SprayerAssignmentRequest currentAssignment : sprayOrder.getSprayerAssignments()) {
                Long currentSprayerId = currentAssignment.getSprayer().getId();

                if (!existingSprayerIds.contains(currentSprayerId)) {
                    currentAssignment.setSprayOrder(sprayOrderId);
                    sprayerAssignmentGlobalService.save(userId, currentAssignment, personRole);
                }
            }

            for (SprayerAssignmentDTO existingAssignment : existingAssignments) {
                boolean isInNewAssignments = sprayOrder.getSprayerAssignments().stream()
                        .anyMatch(newAssignment -> Objects.equals(newAssignment.getSprayer().getId(), existingAssignment.getSprayer()));

                if (!isInNewAssignments) {
                    sprayerAssignmentGlobalService.deleteById(existingAssignment.getId());
                }
            }
        }
    }

    private void handleConfirmed(SprayStatus oldStatus, SprayOrderRequest sprayOrder, SprayOrder existingSprayOrder, Long userId, PersonRole personRole) {
        if (oldStatus == SprayStatus.CONFIRMED) {
            if (sprayOrder.getSprayerAssignments().isEmpty()) {
                return;
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
                        SprayerAssignmentDTO assignment = sprayerAssignmentGlobalService.save(userId, assignmentRequest, personRole);
                        // Notify the assigned sprayer
                        notificationService.notifyUser(assignment.getSprayer(), PersonRole.SPRAYER.toString(),
                                "You have been assigned to a new spray order. Order ID: " + existingSprayOrder.getId());                        return assignment;
                    })
                    .collect(Collectors.toList());

            existingSprayOrder.setStatus(SprayStatus.ASSIGNED);
            notificationService.notifyUser(existingSprayOrder.getFarmer(), PersonRole.FARMER.toString(),
                    "Sprayers have been assigned to your order.");
        }
    }

    private void handleSprayerActions(SprayStatus oldStatus, SprayStatus newStatus, SprayOrder existingSprayOrder) {
        if (newStatus == SprayStatus.IN_PROGRESS && oldStatus == SprayStatus.ASSIGNED) {
            existingSprayOrder.setStatus(SprayStatus.IN_PROGRESS);
            notificationService.notifyUser(existingSprayOrder.getFarmer(), PersonRole.FARMER.toString(),
                    "Your spray order is now in progress.");
        }
        if (newStatus == SprayStatus.SPRAY_COMPLETED && oldStatus == SprayStatus.IN_PROGRESS) {
            existingSprayOrder.setStatus(SprayStatus.SPRAY_COMPLETED);
            notificationService.notifyUser(existingSprayOrder.getFarmer(), PersonRole.FARMER.toString(),
                    "Spraying for your order has been completed. Please proceed with payment.");
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
        List<SprayerAssignmentDTO> sprayerAssignmentDTOs = sprayerAssignmentGlobalService.findAssignmentsForSprayer(sprayerId);
        List<Long> sprayOrderIds = sprayerAssignmentDTOs.stream().map(SprayerAssignmentDTO::getSprayOrder).collect(Collectors.toList());
        return sprayOrderRepository.findAllById(sprayOrderIds).stream().map(SprayOrderMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public List<SprayOrderDTO> getOrdersBySprayerEmail(String emailId) {
        // Fetch the sprayer assignments using the emailId
        List<SprayerAssignmentDTO> sprayerAssignmentDTOs = sprayerAssignmentGlobalService.findAssignmentsForSprayerByEmail(emailId);

        // Get the spray order IDs from the assignments
        List<Long> sprayOrderIds = sprayerAssignmentDTOs.stream()
                .map(SprayerAssignmentDTO::getSprayOrder)
                .collect(Collectors.toList());

        // Retrieve the spray orders by the IDs and map them to DTOs
        return sprayOrderRepository.findAllById(sprayOrderIds).stream()
                .map(SprayOrderMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<SprayOrderDTO> getAvailableSprayOrdersBySprayer(Long sprayerId) {
        List<SprayerAssignmentDTO> sprayerAssignmentDTOs = sprayerAssignmentGlobalService.findAssignmentsForSprayer(sprayerId);
        List<Long> sprayOrderIds = sprayerAssignmentDTOs.stream().map(SprayerAssignmentDTO::getSprayOrder).collect(Collectors.toList());
        return sprayOrderRepository.findAllAvailableSprayOrderForSprayerByIds(sprayOrderIds).stream().map(SprayOrderMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public List<SprayOrderDTO> getUnAssignedSprayOrders() {
        return sprayOrderRepository.getUnAssignedSprayOrders().stream().map(SprayOrderMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public SprayOrderResponse findSprayOrderDetails(Long sprayOrderId) {
        SprayOrderDTO sprayOrderDTO = findById(sprayOrderId);
        SpraySessionDTO spraySessionDTO = spraySession2GlobalService.findById(sprayOrderDTO.getSpraySession());
        List<SprayerAssignmentDTO> sprayerAssignmentDTOS = sprayerAssignmentGlobalService.findAllBySprayOrderIds(Collections.singletonList(sprayOrderId));

        List<SprayerAssignmentResponse> sprayerAssignmentResponses = sprayerAssignmentDTOS.stream()
                .map(sprayerAssignmentDTO -> {
                    return SprayerAssignmentResponse.builder()
                            .id(sprayerAssignmentDTO.getId())
                            .isPrimary(sprayerAssignmentDTO.getIsPrimary())
                            .sprayer(PersonResponseMapper.INSTANCE.toReponse(personGlobalService.findById(sprayerAssignmentDTO.getSprayer())))
                            .build();
                }).collect(Collectors.toList());

        List<FeedBackReponse> feedBackReponses = feedbackGlobalService.getFeedbacksBySprayOrderId(sprayOrderId);

        return SprayOrderResponse.builder()
                .farmer(PersonResponseMapper.INSTANCE.toReponse(personGlobalService.findById(sprayOrderDTO.getFarmer())))
                .cropType(sprayOrderDTO.getCropType())
                .area(sprayOrderDTO.getArea())
                .cost(sprayOrderDTO.getCost())
                .dateTime(sprayOrderDTO.getDateTime())
                .latitude(sprayOrderDTO.getLatitude())
                .location(sprayOrderDTO.getLocation())
                .longitude(sprayOrderDTO.getLongitude())
                .sprayerAssignments(sprayerAssignmentResponses)
                .changeAmount(sprayOrderDTO.getChangeAmount())
                .status(sprayOrderDTO.getStatus())
                .spraySession(SpraySessionResponseMapper.INSTANCE.toResponse(spraySessionDTO))
                .autoAssign(sprayOrderDTO.getAutoAssign())
                .id(sprayOrderDTO.getId())
                .feedBacks(CollectionUtils.isEmpty(feedBackReponses) ? null : feedBackReponses)
                .build();
    }

    @Override
    public void automateSprayerSelection(SprayOrderDTO sprayOrder, SprayStatus previousStatus) {
        boolean assignmentsMade = automateSprayerSelected(sprayOrder);
        if (assignmentsMade) {
            lockAndUnlockStatus(sprayOrder, SprayStatus.ASSIGNED);


        } else {
            lockAndUnlockStatus(sprayOrder, previousStatus);
        }
    }

    @Override
    public SprayOrderDTO lockAndUnlockStatus(SprayOrderDTO sprayOrder, SprayStatus status) {
        SprayOrder existingSprayOrder = sprayOrderRepository.findById(sprayOrder.getId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + sprayOrder.getId()));

        existingSprayOrder.setStatus(status);
        return SprayOrderMapper.INSTANCE.toDto(sprayOrderRepository.save(existingSprayOrder));
    }

    private void sendConfirmationEmail(SprayOrderDTO order) {
        PersonDTO farmer = personGlobalService.getUserByIds(List.of(order.getFarmer())).get(0);
        String farmerEmail = farmer.getEmailAddress();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String gregorianDate = order.getDateTime().format(formatter);
        String lunarDate = convertToLunarDate(order.getDateTime().toLocalDate());
        String emailSubject = "HoverSprite - Spray Order Confirmation";
        String emailBody = String.format(
                "Dear %s,\n\n" +
                        "Thank you for choosing HoverSprite for your spraying needs. Your order has been successfully booked.\n\n" +
                        "Order Details:\n" +
                        "Date (Gregorian): %s\n" +
                        "Date (Lunar): %s\n" +
                        "Time: %s\n" +
                        "Location: %s\n" +
                        "Farmland Size: %.2f decares\n" +
                        "Total Cost: %.2f VND\n\n" +
                        "We appreciate your trust in HoverSprite. Our team is committed to providing you with the best service possible.\n\n" +
                        "If you have any questions or need to make changes to your order, please don't hesitate to contact us.\n\n" +
                        "Best regards,\n" +
                        "The HoverSprite Team",
                farmer.getFullName(),  // farmer name
                gregorianDate,         // gregorian date
                lunarDate,             // lunar date
                order.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm")), // time
                order.getLocation(),   // location
                order.getArea().doubleValue(),  // area (double)
                order.getCost()        // cost (double)
        );


        emailService.sendSimpleMessage(farmerEmail, emailSubject, emailBody);
    }

    private void notifyFarmerAboutSprayerAssignment(SprayOrderDTO sprayOrderDTO, List<PersonDTO> assignedSprayers) {
        PersonDTO farmer = personGlobalService.getUserByIds(List.of(sprayOrderDTO.getFarmer())).get(0);
        String farmerEmail = farmer.getEmailAddress();

        String sprayersNames = assignedSprayers.stream()
                .map(PersonDTO::getFullName)
                .collect(Collectors.joining(", "));

        String emailSubject = "HoverSprite - Spray Order Assigned to Sprayer(s)";
        String emailBody = String.format(
                "Dear %s,\n\n" +
                        "Your spray order has been assigned to the following sprayer(s): %s.\n" +
                        "They will be conducting the spraying service at your location.\n\n" +
                        "Best regards,\n" +
                        "The HoverSprite Team",
                farmer.getFullName(),
                sprayersNames
        );

        emailService.sendSimpleMessage(farmerEmail, emailSubject, emailBody);
    }

    private void sendSprayerAssignmentEmail(Long sprayerId, PersonDTO farmer, SprayOrderDTO order) {
        PersonDTO sprayer = personGlobalService.findById(sprayerId);
        String sprayerEmail = sprayer.getEmailAddress();

        String emailSubject = "HoverSprite - New Spray Order Assignment";
        String emailBody = String.format(
                "Dear %s,\n\n" +
                        "You have been assigned to a new spray order.\n\n" +
                        "Farmer Details:\n" +
                        "Full Name: %s\n" +
                        "Location: %s\n" +
                        "Phone Number: %s\n\n" +
                        "Please make the necessary preparations.\n\n" +
                        "Best regards,\n" +
                        "The HoverSprite Team",
                sprayer.getFullName(),
                farmer.getFullName(),
                order.getLocation(),
                farmer.getPhoneNumber()
        );

        emailService.sendSimpleMessage(sprayerEmail, emailSubject, emailBody);
    }




    @Override
    public Map<PersonExpertise, List<Pair<PersonDTO, Integer>>> getSortedAvailableSprayers(Long sprayOrderId) {
        SprayOrderDTO sprayOrderDTO = findById(sprayOrderId);
        List<Long> unAvailableSprayersId = sprayerAssignmentGlobalService.findUnAvailableSprayerIds(sprayOrderDTO);
        Map<PersonExpertise, List<PersonDTO>> availableSprayers = personGlobalService.getSprayersGroupedByExpertise(unAvailableSprayersId);

        SpraySessionDTO session = spraySession2GlobalService.findById(sprayOrderDTO.getSpraySession());
        List<SpraySessionDTO> spraySessionByWeek = spraySession2GlobalService.findSpraySessionByWeek(session.getDate());
        List<Long> sprayOrderIdsByWeek = spraySessionByWeek.stream().map(SpraySessionDTO::getSprayOrder).collect(Collectors.toList());

        Map<PersonExpertise, List<Pair<PersonDTO, Integer>>> sortedAvailableSprayers = new HashMap<>();

        for (Map.Entry<PersonExpertise, List<PersonDTO>> entry : availableSprayers.entrySet()) {
            PersonExpertise expertise = entry.getKey();
            List<PersonDTO> personDTOs = entry.getValue();

            List<Pair<PersonDTO, Integer>> personWithAssignmentCount = new ArrayList<>();

            for (PersonDTO personDTO : personDTOs) {
                List<SprayerAssignmentDTO> assignments = sprayerAssignmentGlobalService.findSprayerAssignmentInTheWeek(sprayOrderIdsByWeek, Collections.singletonList(personDTO.getId()));
                int assignmentCount = assignments.size();
                personWithAssignmentCount.add(new Pair<>(personDTO, assignmentCount));
            }

            List<Pair<PersonDTO, Integer>> sortedPersonWithAssignmentCount = personWithAssignmentCount.stream()
                    .sorted(Comparator.comparingInt(Pair::getSecond))
                    .collect(Collectors.toList());

            sortedAvailableSprayers.put(expertise, sortedPersonWithAssignmentCount);
        }

        return sortedAvailableSprayers;
    }

    @Override
    public boolean automateSprayerSelected(SprayOrderDTO sprayOrderDTO) {
        Map<PersonExpertise, List<Pair<PersonDTO, Integer>>> sortedAvailableSprayers = getSortedAvailableSprayers(sprayOrderDTO.getId());

        List<PersonDTO> selectedSprayers = new ArrayList<>();
        List<PersonDTO> noAssignmentsSprayersApprentice = getFilteredSprayers(sortedAvailableSprayers, PersonExpertise.APPRENTICE, false);
        List<PersonDTO> someAssignmentsSprayersApprentice = getFilteredSprayers(sortedAvailableSprayers, PersonExpertise.APPRENTICE, true);
        List<PersonDTO> noAssignmentsSprayersAdept = getFilteredSprayers(sortedAvailableSprayers, PersonExpertise.ADEPT, false);
        List<PersonDTO> someAssignmentsSprayersAdept = getFilteredSprayers(sortedAvailableSprayers, PersonExpertise.ADEPT, true);
        List<PersonDTO> noAssignmentsSprayersExpert = getFilteredSprayers(sortedAvailableSprayers, PersonExpertise.EXPERT, false);
        List<PersonDTO> someAssignmentsSprayersExpert = getFilteredSprayers(sortedAvailableSprayers, PersonExpertise.EXPERT, true);


        // START ANALYZING
        addMultipleListToSelectedSprayer(selectedSprayers, noAssignmentsSprayersApprentice,
                noAssignmentsSprayersAdept, noAssignmentsSprayersExpert);

        if (selectedSprayers.size() > 1) {
            handleSprayerSelection(selectedSprayers, sprayOrderDTO);
            return true;
        }

        if (selectedSprayers.size() == 1) {
            if (selectedSprayers.get(0).getExpertise() == PersonExpertise.APPRENTICE) {
                addSprayersWithNoAssignments(selectedSprayers, someAssignmentsSprayersAdept);
                addSprayersWithNoAssignments(selectedSprayers, someAssignmentsSprayersExpert);
                if (selectedSprayers.size() > 1) {
                    handleSprayerSelection(selectedSprayers, sprayOrderDTO);
                    return true;
                }
            }
            if (Arrays.asList(PersonExpertise.ADEPT, PersonExpertise.EXPERT).contains(selectedSprayers.get(0).getExpertise())) {
                if (selectedSprayers.get(0).getExpertise() == PersonExpertise.EXPERT) {
                    handleSprayerSelection(selectedSprayers, sprayOrderDTO);
                    return true;
                }
                addMultipleListToSelectedSprayer(selectedSprayers, someAssignmentsSprayersApprentice,
                        someAssignmentsSprayersAdept, someAssignmentsSprayersExpert);
                if (selectedSprayers.size() > 1) {
                    handleSprayerSelection(selectedSprayers, sprayOrderDTO);
                    return true;
                }
            }
        }

        if (selectedSprayers.isEmpty()) {
            addMultipleListToSelectedSprayer(selectedSprayers, someAssignmentsSprayersApprentice,
                    someAssignmentsSprayersAdept, someAssignmentsSprayersExpert);

            if (selectedSprayers.size() > 1) {
                handleSprayerSelection(selectedSprayers, sprayOrderDTO);
                return true;
            }
            if (selectedSprayers.size() == 1 && selectedSprayers.get(0).getExpertise() == PersonExpertise.EXPERT) {
                handleSprayerSelection(selectedSprayers, sprayOrderDTO);
                return true;
            }
        }

        return false;
    }

    @SafeVarargs
    private void addMultipleListToSelectedSprayer(List<PersonDTO> selectedSprayers, List<PersonDTO>... multipleList) {
        for (List<PersonDTO> list : multipleList) {
            addSprayersWithNoAssignments(selectedSprayers, list);
        }
    }

    private List<PersonDTO> limitToTwoSprayers(List<PersonDTO> sprayers) {
        return sprayers.size() > 2 ? sprayers.subList(0, 2) : sprayers;
    }

    // Method to add sprayers based on specific conditions and create assignments
    private void handleSprayerSelection(List<PersonDTO> selectedSprayers, SprayOrderDTO sprayOrderDTO) {
        selectedSprayers = limitToTwoSprayers(selectedSprayers);
        createAssigments(selectedSprayers, sprayOrderDTO.getId());
        notifyFarmerAboutSprayerAssignment(sprayOrderDTO, selectedSprayers); // Send notification to farmer
    }


    private void createAssigments(List<PersonDTO> selectedSprayers, Long sprayOrderId) {
        SprayOrderDTO sprayOrderDTO = findById(sprayOrderId);
        PersonDTO farmer = personGlobalService.findById(sprayOrderDTO.getFarmer());
        List<SprayerAssignmentRequest> assignmentRequests = selectedSprayers.stream()
                .map(selectedSprayer -> SprayerAssignmentRequest.builder()
                        .sprayOrder(sprayOrderId)
                        .sprayer(PersonRequest.builder().id(selectedSprayer.getId()).build())
                        .isPrimary(true).build())
                .collect(Collectors.toList());

        assignmentRequests.forEach(sprayerAssignmentRequest -> {
            SprayerAssignmentDTO assignment = sprayerAssignmentGlobalService.save(null, sprayerAssignmentRequest, PersonRole.ADMIN);

            sendSprayerAssignmentEmail(assignment.getSprayer(), farmer, sprayOrderDTO);
            // Notify the assigned sprayer
            notificationService.notifyUser(assignment.getSprayer(), PersonRole.SPRAYER.toString(),
                    "You have been assigned to a new spray order. Order ID: " + sprayOrderId);
        });
    }

    private void addSprayersWithNoAssignments(List<PersonDTO> selectedSprayers, List<PersonDTO> noAssignedSprayers) {
        if (noAssignedSprayers != null && !noAssignedSprayers.isEmpty()) {
            selectedSprayers.add(noAssignedSprayers.get(0));
        }
    }

    private List<PersonDTO> getFilteredSprayers(Map<PersonExpertise, List<Pair<PersonDTO, Integer>>> sortedAvailableSprayers, PersonExpertise expertise, boolean biggerThan0) {
        List<Pair<PersonDTO, Integer>> sprayers = sortedAvailableSprayers.get(expertise);
        if (sprayers == null) return Collections.emptyList();
        return sprayers.stream()
                .filter(pair -> biggerThan0 ? pair.getSecond() > 0 : pair.getSecond() == 0)
                .map(Pair::getFirst)
                .collect(Collectors.toList());
    }

    public String convertToLunarDate(LocalDate gregorianDate) {
        ChineseDate lunarDate = new ChineseDate(Date.from(gregorianDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        // Extract year, month, and day as numbers
        int lunarYear = lunarDate.getChineseYear();
        int lunarMonth = lunarDate.getMonth();  // The month as a number (1-12)
        int lunarDay = lunarDate.getDay();  // The day as a number (1-30)

        // Return in the format MM/DD/YYYY
        return String.format("%d/%d/%d", lunarMonth, lunarDay, lunarYear);
    }
}