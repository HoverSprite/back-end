package com.example.project.service;

import com.example.project.model.dto.SprayOrderDTO;
import com.example.project.model.entity.Order;

import java.util.List;
import java.util.Optional;

public interface SprayOrderService {
    SprayOrderDTO createSprayOrder(SprayOrderDTO sprayOrderDTO);
    List<SprayOrderDTO> findAll();

    SprayOrderDTO findSprayOrderById(Integer id);

    SprayOrderDTO updateSprayOrder(SprayOrderDTO updatedSprayOrderDTO);
    void deleteSprayOrder(Integer id);
}
