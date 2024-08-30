package com.example.project.controller;

import com.example.project.model.dto.SprayOrderDTO;
import com.example.project.model.entity.PersonRole;
import com.example.project.model.entity.SprayStatus;
import com.example.project.model.entity.SprayerAssignment;
import com.example.project.service.SprayOrderService;
import com.example.project.service.SprayerAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/{userId}/receptionist")
public class ReceptionistController {

    @Autowired
    private SprayOrderService sprayOrderService;

    @Autowired
    private SprayerAssignmentService sprayerAssignmentService;

    @PostMapping("/orders")
    public SprayOrderDTO createOrder(@PathVariable Long userId, @RequestBody SprayOrderDTO sprayOrderDTO) {
        return sprayOrderService.save(userId.intValue(), sprayOrderDTO, PersonRole.RECEPTIONIST);
    }

    @PatchMapping("/orders/{order_id}/assign")
    public SprayOrderDTO assignSprayer(@PathVariable Long userId, @PathVariable Long orderId, @RequestBody SprayerAssignment sprayerAssignment) {
        return sprayerAssignmentService.assignSprayers(orderId.intValue(), sprayerAssignment);
    }

    @PatchMapping("/orders/{order_id}/status")
    public SprayOrderDTO updateOrderStatus(@PathVariable Long userId, @PathVariable Long orderId, @RequestBody SprayStatus sprayStatus) {
        return sprayOrderService.updateOrderStatus(orderId.intValue(), sprayStatus);
    }

    @PutMapping("/orders/{orderId}")
    public SprayOrderDTO updateOrder(@PathVariable Long userId, @PathVariable Long orderId, @RequestBody SprayOrderDTO sprayOrderDTO) {
        return sprayOrderService.update(userId.intValue(), orderId.intValue(), sprayOrderDTO, PersonRole.RECEPTIONIST);
    }

    @GetMapping("/orders")
    public List<SprayOrderDTO> viewOrders(@PathVariable Long userId) {
        return sprayOrderService.findAll();
    }

    @GetMapping("/orders/{orderId}")
    public SprayOrderDTO viewOrder(@PathVariable Long userId, @PathVariable Long orderId) {
        return sprayOrderService.findById(orderId.intValue());
    }
}
