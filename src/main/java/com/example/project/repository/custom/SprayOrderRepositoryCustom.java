package com.example.project.repository.custom;

import com.example.project.model.entity.SprayOrder;

import java.util.List;

public interface SprayOrderRepositoryCustom {
    List<SprayOrder> getOrdersByUser(Integer id);

    List<SprayOrder> getOrdersBySprayer(Integer id);
}
