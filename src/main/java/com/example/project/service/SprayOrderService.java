package com.example.project.service;

import com.example.project.base.BaseService;
import com.example.project.model.dto.SprayOrderDTO;
import com.example.project.model.entity.Order;
import com.example.project.model.entity.PersonRole;
import com.example.project.model.entity.SprayStatus;

import java.util.List;
import java.util.Optional;

public interface SprayOrderService  extends BaseService<SprayOrderDTO, Integer, PersonRole> {
    List<SprayOrderDTO> getOrdersByUser(Integer userId);

    List<SprayOrderDTO> getOrdersBySprayer(Integer sprayerId);
}
