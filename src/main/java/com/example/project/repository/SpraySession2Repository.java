package com.example.project.repository;

import com.example.project.model.entity.SpraySession_2;
import com.example.project.repository.custom.SpraySession2RepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpraySession2Repository extends JpaRepository<SpraySession_2, Integer>, SpraySession2RepositoryCustom {
}
