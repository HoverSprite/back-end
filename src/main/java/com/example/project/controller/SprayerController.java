package com.example.project.controller;

import com.example.project.model.dto.SprayOrderDTO;
import com.example.project.model.entity.PersonRole;
import com.example.project.model.entity.SprayStatus;
import com.example.project.service.SprayOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/{userId}/sprayer")
public class SprayerController {
    @Autowired
    private SprayOrderService sprayOrderService;

    @GetMapping("/orders")
    public List<SprayOrderDTO> viewAssignedOrders(@PathVariable Long userId) {
        return sprayOrderService.getOrdersBySprayer(userId.intValue());
    }

    @PutMapping("/orders/{orderId}")
    public SprayOrderDTO updateOrder(@PathVariable Long userId, @PathVariable Long orderId, @RequestBody SprayOrderDTO sprayOrderDTO) {
        return sprayOrderService.update(userId.intValue(), orderId.intValue(), sprayOrderDTO, PersonRole.SPRAYER);
    }

    @GetMapping("/orders/{orderId}")
    public SprayOrderDTO viewOrder(@PathVariable Long userId, @PathVariable Long orderId) {
        return sprayOrderService.findById(orderId.intValue());
    }
}
