package com.example.project.service.impl;

import com.example.project.base.AbstractService;
import com.example.project.mapper.SprayOrderMapper;
import com.example.project.mapper.SpraySessionMapper;
import com.example.project.model.dto.SprayOrderDTO;
import com.example.project.model.dto.SpraySessionDTO;
import com.example.project.model.entity.*;
import com.example.project.repository.PersonRepository;
import com.example.project.repository.SprayOrderRepository;
import com.example.project.service.EmailService;
import com.example.project.service.SprayOrderService;
import com.example.project.service.SpraySession2Service;
import com.example.project.validator.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SprayOrderServiceImpl extends AbstractService<SprayOrderDTO, Integer, PersonRole> implements SprayOrderService {

    private static final int MAX_SPRAYS_SESSIONS_IN_ONE_HOUR = 2;
    private static final int COST_PER_DECARE = 30000;
    private static final String NO_MORE_THAN_2_SESSIONS_ALLOWED_AT_THE_SAME_TIME = "Cannot create more than 2 sessions per time slot on a given day.";

    @Autowired
    private SprayOrderRepository sprayOrderRepository;

    @Autowired
    private SpraySession2Service spraySession2Service;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PersonRepository personRepository;

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
        List<SpraySession_2> existingSessions = spraySession2Service.findSpraySessionByDate(spraySessionDTO.getDate(), spraySessionDTO.getStartTime());
        validator.isTrue(existingSessions.size() < MAX_SPRAYS_SESSIONS_IN_ONE_HOUR, NO_MORE_THAN_2_SESSIONS_ALLOWED_AT_THE_SAME_TIME);
    }

    @Override
    protected SprayOrderDTO executeSave(Integer userId, SprayOrderDTO sprayOrderDTO, PersonRole personRole) {
        // Assuming you have a method to retrieve the farmer by ID
        Person farmer = personRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("Farmer not found with id: " + userId));

        SpraySessionDTO spraySessionDTO = sprayOrderDTO.getSpraySession();
        SprayOrder sprayOrder = SprayOrderMapper.INSTANCE.toEntitySave(sprayOrderDTO);
        SpraySession_2 spraySession = SpraySessionMapper.INSTANCE.toEntitySave(spraySessionDTO);

        // Set the farmer in the sprayOrder
        sprayOrder.setFarmer(farmer);

        if (personRole == PersonRole.RECEPTIONIST) {
            sprayOrder.setStatus(SprayStatus.CONFIRMED);
        }

        calculateCostPerArea(sprayOrder);
        spraySession.setSprayOrder(sprayOrder);
        sprayOrder.setSpraySession(spraySession);
        handleOrderConfirmed(sprayOrder);
        return SprayOrderMapper.INSTANCE.toDto(sprayOrderRepository.save(sprayOrder));
    }


    @Override
    protected void validateForUpdate(ValidationUtils validator, SprayOrderDTO sprayOrderDTO, PersonRole personRole) {
        verifySessionAvailable(validator, sprayOrderDTO);
        validator.isTrue(
                ((sprayOrderDTO.getStatus() == SprayStatus.CANCELLED ||
                sprayOrderDTO.getStatus() == SprayStatus.CONFIRMED) && personRole == PersonRole.RECEPTIONIST), "The selected user is not allowed to cancel or confirm the order.");
        validator.isTrue(
                (sprayOrderDTO.getStatus() == SprayStatus.IN_PROGRESS && personRole == PersonRole.SPRAYER), "The selected user is not allowed to set in progress for the order.");
    }

    @Override
    protected SprayOrderDTO executeUpdate(Integer userId, Integer orderId, SprayOrderDTO sprayOrderDTO, PersonRole personRole) {
        SprayOrder existingSprayOrder = sprayOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + sprayOrderDTO.getId()));

        existingSprayOrder.setCropType(sprayOrderDTO.getCropType());
        existingSprayOrder.setArea(sprayOrderDTO.getArea());
        existingSprayOrder.setDateTime(sprayOrderDTO.getDateTime());
        existingSprayOrder.setLocation(sprayOrderDTO.getLocation());
        existingSprayOrder.setStatus(SprayStatus.PENDING);

        SpraySession_2 updatedSpraySession = SpraySessionMapper.INSTANCE.toEntitySave(sprayOrderDTO.getSpraySession());
        if (updatedSpraySession != null && !existingSprayOrder.getSpraySession().equals(updatedSpraySession)) {
            existingSprayOrder.setSpraySession(updatedSpraySession);
            updatedSpraySession.setSprayOrder(existingSprayOrder);
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

    @Override
    public SprayOrderDTO updateOrderStatus(Integer orderId, SprayStatus newStatus) {
        SprayOrder sprayOrder = sprayOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        SprayStatus oldStatus = sprayOrder.getStatus();
        sprayOrder.setStatus(newStatus);

        switch (newStatus) {
            case CANCELLED:
                handleOrderCancelled(sprayOrder);
                break;

            case CONFIRMED:
                handleOrderConfirmed(sprayOrder);
                break;

            case ASSIGNED:
                handleOrderAssigned(sprayOrder);
                break;

            case IN_PROGRESS:
                handleOrderInProgress(sprayOrder);
                break;

            case COMPLETED:
                handleOrderCompleted(sprayOrder);
                break;

            default:
                throw new IllegalArgumentException("Unknown status: " + newStatus);
        }
        SprayOrder updatedOrder = sprayOrderRepository.save(sprayOrder);

        return SprayOrderMapper.INSTANCE.toDto(updatedOrder);
    }

    private void handleOrderCancelled(SprayOrder sprayOrder) {
        sendEmailToFarmer(sprayOrder, "Order Cancelled", "Your order has been cancelled.");
    }

    private void handleOrderConfirmed(SprayOrder sprayOrder) {
        String subject = "Order Confirmed";

        String lunarDate = getLunarDate(sprayOrder.getSpraySession().getDate());
        if (lunarDate == null) lunarDate = "N/A";  // Default to N/A if the Lunar date is not available

        String body = String.format(
                "Dear %s,\n\nYour order has been confirmed for %s (Lunar: %s) at %s. The total cost for the farmland size of %.2f decares is %.2f VND.\n\nThank you for trusting and choosing HoverSprite!",
                sprayOrder.getFarmer().getFirstName(),
                sprayOrder.getSpraySession().getDate(),
                lunarDate,
                sprayOrder.getLocation(),
                sprayOrder.getArea() != null ? sprayOrder.getArea().doubleValue() : 0.0,
                sprayOrder.getCost() != null ? sprayOrder.getCost() : 0.0
        );
        sendEmailToFarmer(sprayOrder, subject, body);
    }


    private void handleOrderAssigned(SprayOrder sprayOrder) {
        List<SprayerAssignment> assignments = sprayOrder.getSprayerAssignments();
        String sprayerNames = assignments.stream()
                .map(a -> a.getSprayer().getFirstName())
                .collect(Collectors.joining(", "));
        sendEmailToFarmer(sprayOrder, "Order Assigned", "Your order has been assigned to the following sprayer(s): " + sprayerNames);

        for (SprayerAssignment assignment : assignments) {
            Person sprayer = assignment.getSprayer();
            sendEmailToSprayer(sprayer, sprayOrder);
        }
    }

    private void handleOrderInProgress(SprayOrder sprayOrder) {
        // No additional actions are specified for this status update
    }

    private void handleOrderCompleted(SprayOrder sprayOrder) {
        sendEmailToFarmer(sprayOrder, "Order Completed", "Your order has been completed. The total cost is " + sprayOrder.getCost() + ".");
    }

    private void sendEmailToFarmer(SprayOrder sprayOrder, String subject, String body) {
        Person farmer = sprayOrder.getFarmer();
        String email = farmer.getEmailAddress(); // Assuming you have a getEmailAddress() method
        emailService.sendSimpleMessage(email, subject, body);
    }


    private void sendEmailToSprayer(Person sprayer, SprayOrder sprayOrder) {
        String email = sprayer.getEmailAddress(); // Assuming you have a getEmailAddress() method
        String subject = "New Spray Order Assigned";
        String body = String.format(
                "You have been assigned to a new spray order. Details: \nLocation: %s\nPhone: %s\nFarmer: %s",
                sprayOrder.getLocation(),
                sprayOrder.getFarmer().getPhoneNumber(),
                sprayOrder.getFarmer().getFirstName()
        );
        emailService.sendSimpleMessage(email, subject, body);
    }


    private String getLunarDate(LocalDate date) {
        // Implement your logic to convert Gregorian date to Lunar date
        return "Lunar Date"; // Placeholder
    }

}
