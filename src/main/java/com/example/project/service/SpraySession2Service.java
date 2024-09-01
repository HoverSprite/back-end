package com.example.project.service;

import com.example.project.base.BaseService;
import com.example.project.model.dto.SpraySessionDTO;
import com.example.project.model.entity.PersonRole;
import com.example.project.model.entity.SpraySession_2;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SpraySession2Service extends BaseService<SpraySessionDTO, Integer, PersonRole> {

    List<SpraySession_2> findSpraySessionByDate(LocalDate date, LocalTime startTime);

    SpraySessionDTO createOrFindSpraySession(SpraySessionDTO spraySessionDTO);
}
