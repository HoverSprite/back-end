package hoversprite.project.project.service.impl;

import hoversprite.project.project.model.entity.Order;
import hoversprite.project.project.model.entity.SpraySession;
import hoversprite.project.project.model.entity.TimeSlot;
import hoversprite.project.project.repository.OrderRepository;
import hoversprite.project.project.repository.SpraySessionRepository;
import hoversprite.project.project.repository.TimeSlotRepository;
import hoversprite.project.project.service.OrderService;
import hoversprite.project.project.service.SpraySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        LocalDate serviceDate = order.getServiceDate();
        SpraySession spraySession = order.getSpraySession();
        TimeSlot timeSlot = spraySession.getTimeSlot(); // Ensure the TimeSlot is correctly retrieved

        if (timeSlot == null || timeSlot.getId() == null) {
            throw new RuntimeException("TimeSlot is not set or is invalid.");
        }

        // Retrieve all SpraySessions for the given TimeSlot on the service date
        List<SpraySession> existingSessions = spraySessionRepository.findByTimeSlotAndDate(timeSlot, serviceDate);

        // Check if there are any existing sessions on the same service date
        if (!existingSessions.isEmpty()) {
            for (SpraySession session : existingSessions) {
                // Check if any session is available
                if (session.getIsAvailable()) {
                    // Mark session as unavailable and assign to order
                    session.setIsAvailable(false);
                    spraySessionRepository.save(session);
                    order.setSpraySession(session);
                    return saveOrderWithCostCalculation(order);
                }
            }
            // If no available sessions are found, log a message
            System.out.println("No available SpraySessions for the given time slot on " + serviceDate);
        }

        // If no matching date or only one session exists, create a new SpraySession
        if (existingSessions.size() < 2) {
            SpraySession newSession = new SpraySession();
            newSession.setTimeSlot(timeSlot); // Ensure the TimeSlot is set
            newSession.setIsAvailable(false); // Mark as unavailable since it's being assigned to this order
            newSession.setDate(serviceDate);
            newSession = spraySessionRepository.save(newSession);

            // Assign the new session to the order
            order.setSpraySession(newSession);
            return saveOrderWithCostCalculation(order);
        } else {
            // If two sessions already exist, log a message
            System.out.println("Maximum number of SpraySessions reached for TimeSlot on " + serviceDate);
            throw new RuntimeException("Cannot create more than 2 sessions per time slot on a given day.");
        }
    }

    // Helper method to save the order with cost calculation
    private Order saveOrderWithCostCalculation(Order order) {
        // Calculate the total cost based on the area
        double costPerDecare = 30000;
        double totalCost = order.getArea() * costPerDecare;
        order.setTotalCost(totalCost);

        // Save the Order
        return orderRepository.save(order);
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
