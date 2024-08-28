package com.example.project.repository;

import com.example.project.model.entity.SprayOrder;
import com.example.project.repository.custom.SprayOrderRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SprayOrderRepository extends JpaRepository<SprayOrder, Integer>, SprayOrderRepositoryCustom {
}
