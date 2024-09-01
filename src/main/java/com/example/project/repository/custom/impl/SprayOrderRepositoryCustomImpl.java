package com.example.project.repository.custom.impl;

import com.example.project.model.entity.Person;
import com.example.project.model.entity.SprayOrder;
import com.example.project.model.entity.SpraySession_2;
import com.example.project.model.entity.SprayerAssignment;
import com.example.project.repository.custom.SprayOrderRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SprayOrderRepositoryCustomImpl implements SprayOrderRepositoryCustom {

    @PersistenceContext
    private EntityManager em;


    @Override
    public List<SprayOrder> getOrdersByUser(Integer id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SprayOrder> cq = cb.createQuery(SprayOrder.class);
        Root<SprayOrder> sprayOrderRoot = cq.from(SprayOrder.class);

        // Perform a fetch join to eagerly load 'farmer' and 'spraySession'
        sprayOrderRoot.fetch("farmer", JoinType.LEFT);
        sprayOrderRoot.fetch("spraySession", JoinType.LEFT);

        // Create predicate to filter by farmer ID
        Predicate farmerPredicate = cb.equal(sprayOrderRoot.get("farmer").get("id"), id);
        cq.where(farmerPredicate);
        cq.distinct(true);

        TypedQuery<SprayOrder> query = em.createQuery(cq);
        return query.getResultList();
    }



    @Override
    public List<SprayOrder> getOrdersBySprayer(Integer id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SprayOrder> cq = cb.createQuery(SprayOrder.class);
        Root<SprayOrder> sprayOrderRoot = cq.from(SprayOrder.class);
        Join<SprayOrder, SprayerAssignment> sprayerAssignmentJoin = sprayOrderRoot.join("sprayerAssignments");
        Predicate sprayerPredicate = cb.equal(sprayerAssignmentJoin.get("sprayer").get("id"), id);
        cq.where(sprayerPredicate);
        return em.createQuery(cq).getResultList();
    }
}
