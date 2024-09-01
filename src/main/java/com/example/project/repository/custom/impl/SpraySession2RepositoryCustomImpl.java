package com.example.project.repository.custom.impl;

import com.example.project.model.entity.SpraySession_2;
import com.example.project.repository.custom.SpraySession2RepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public class SpraySession2RepositoryCustomImpl implements SpraySession2RepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<SpraySession_2> findSpraySessionByDate(LocalDate date, LocalTime startTime) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SpraySession_2> cq = cb.createQuery(SpraySession_2.class);
        Root<SpraySession_2> spraySession = cq.from(SpraySession_2.class);
        Predicate datePredicate = cb.equal(spraySession.get("date"), date);
        Predicate startTimePredicate = cb.greaterThanOrEqualTo(spraySession.get("startTime"), startTime);
        cq.where(cb.and(datePredicate, startTimePredicate));
        TypedQuery<SpraySession_2> query = em.createQuery(cq);
        return query.getResultList();
    }
}
