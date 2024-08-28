package com.example.project.service.impl;

import com.example.project.model.dto.SpraySessionDTO;
import com.example.project.model.entity.SpraySession_2;
import com.example.project.repository.SpraySession2Repository;
import com.example.project.service.SpraySession2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class SpraySession2ServiceImpl implements SpraySession2Service {

    @Autowired
    private SpraySession2Repository spraySession2Repository;

    @Override
    public List<SpraySession_2> findSpraySessionByDate(LocalDate date, LocalTime startTime) {
        return spraySession2Repository.findSpraySessionByDate(date, startTime);
    }

    @Override
    public SpraySession_2 createSpraySession(SpraySession_2 spraySession) {
        return spraySession2Repository.save(spraySession);
    }
}
