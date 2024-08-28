package com.example.project.repository.custom.impl;

import com.example.project.model.entity.SpraySession_2;
import com.example.project.repository.custom.SpraySession2RepositoryCustom;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
