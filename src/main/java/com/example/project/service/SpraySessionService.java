package com.example.project.service;

import com.example.project.model.entity.SpraySession;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpraySessionService {
    List<SpraySession> findAllSpraySessions();
    Optional<SpraySession> findSpraySessionById(Long id);
    List<SpraySession> findAvailableSpraySessions();
    SpraySession createSpraySession(Long timeSlotId);
    SpraySession updateSpraySession(Long id, SpraySession updatedSpraySession);
    void deleteSpraySession(Long id);
    boolean checkAvailability(Long timeSlotId, LocalDate date);}
