package com.example.project.service.impl;

import com.example.project.model.entity.Order;
import com.example.project.model.entity.SpraySession;
import com.example.project.model.entity.TimeSlot;
import com.example.project.repository.OrderRepository;
import com.example.project.repository.SpraySessionRepository;
import com.example.project.repository.TimeSlotRepository;
import com.example.project.service.OrderService;
import com.example.project.service.SpraySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final SpraySessionRepository spraySessionRepository;
    private final TimeSlotRepository timeSlotRepository;
    private SpraySessionService spraySessionService;
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, SpraySessionRepository spraySessionRepository, TimeSlotRepository timeSlotRepository) {
        this.orderRepository = orderRepository;
        this.spraySessionRepository = spraySessionRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    public Order createOrder(Order order) {
        // Ensure the TimeSlot exists
        Long timeSlotId = order.getSpraySession().getTimeSlot().getId();
        Optional<TimeSlot> timeSlot = timeSlotRepository.findById(timeSlotId);

        if (timeSlot.isPresent()) {
            // Create the SpraySession if it does not exist
            SpraySession spraySession = new SpraySession();
            spraySession.setTimeSlot(timeSlot.get());
            spraySession.setIsAvailable(true);

            spraySession = spraySessionRepository.save(spraySession);

            // Link the SpraySession to the Order
            order.setSpraySession(spraySession);

            // Calculate the total cost
            double costPerDecare = 30000;
            double totalCost = order.getArea() * costPerDecare;
            order.setTotalCost(totalCost);

            // Save and return the Order
            return orderRepository.save(order);
        } else {
            throw new RuntimeException("TimeSlot not found with id: " + timeSlotId);
        }
    }


    @Override
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public Order updateOrder(Long id, Order updatedOrder) {
        Optional<Order> existingOrder = orderRepository.findById(id);
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            order.setCropType(updatedOrder.getCropType());
            order.setArea(updatedOrder.getArea());
            order.setServiceDate(updatedOrder.getServiceDate());
            order.setSpraySession(updatedOrder.getSpraySession());

            // Recalculate total cost in case area or other parameters have changed
            double costPerDecare = 30000;
            double totalCost = updatedOrder.getArea() * costPerDecare;
            order.setTotalCost(totalCost);

            return orderRepository.save(order);
        } else {
            throw new RuntimeException("Order not found with id: " + id);
        }
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
