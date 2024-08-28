package com.example.project.repository.custom.impl;

import com.example.project.repository.custom.SprayOrderRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class SprayOrderRepositoryCustomImpl implements SprayOrderRepositoryCustom {

    @PersistenceContext
    private EntityManager em;



}
