package com.example.project.controller;

import com.example.project.model.dto.SprayOrderDTO;
import com.example.project.model.entity.PersonRole;
import com.example.project.service.SprayOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/{userId}/farmer")
public class FarmerController {

    @Autowired
    private SprayOrderService sprayOrderService;

    @PostMapping("/orders")
    public SprayOrderDTO createOrder(@PathVariable Long userId, @RequestBody SprayOrderDTO sprayOrderDTO) {
        return sprayOrderService.save(userId.intValue(), sprayOrderDTO, PersonRole.FARMER);
    }

    @GetMapping("/orders")
    public List<SprayOrderDTO> viewOrders(@PathVariable Long userId) {
        return sprayOrderService.getOrdersByUser(userId.intValue());
    }

    @GetMapping("/orders/{orderId}")
    public SprayOrderDTO viewOrder(@PathVariable Long userId, @PathVariable Long orderId) {
        return sprayOrderService.findById(orderId.intValue());
    }

    @PutMapping("/orders/{orderId}")
    public SprayOrderDTO updateOrder(@PathVariable Long userId, @PathVariable Long orderId, @RequestBody SprayOrderDTO sprayOrderDTO) {
        return sprayOrderService.update(userId.intValue(), orderId.intValue(), sprayOrderDTO, PersonRole.FARMER);
    }
}
