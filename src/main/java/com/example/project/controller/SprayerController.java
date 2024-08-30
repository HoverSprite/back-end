package com.example.project.controller;

import com.example.project.model.dto.SprayOrderDTO;
import com.example.project.model.entity.SprayStatus;
import com.example.project.service.SprayOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/{userId}/sprayer")
public class SprayerController {
    @Autowired
    private SprayOrderService sprayOrderService;

    @PatchMapping("/orders/{order_id}/status")
    public SprayOrderDTO updateOrderStatus(@PathVariable Long userId, @PathVariable Long orderId, @RequestBody SprayStatus sprayStatus) {
        return sprayOrderService.updateOrderStatus(orderId.intValue(), sprayStatus);
    }

    @GetMapping("/orders")
    public List<SprayOrderDTO> viewAssignedOrders(@PathVariable Long userId) {
        return sprayOrderService.getOrdersBySprayer(userId.intValue());
    }

    @GetMapping("/orders/{orderId}")
    public SprayOrderDTO viewOrder(@PathVariable Long userId, @PathVariable Long orderId) {
        return sprayOrderService.findById(orderId.intValue());
    }
}
