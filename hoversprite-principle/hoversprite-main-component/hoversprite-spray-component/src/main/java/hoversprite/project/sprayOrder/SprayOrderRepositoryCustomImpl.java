package hoversprite.project.sprayOrder;

import com.querydsl.jpa.impl.JPAQuery;
import hoversprite.project.sprayOrder.QSprayOrder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class SprayOrderRepositoryCustomImpl implements SprayOrderRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<SprayOrder> getOrdersByUser(long id) {
        return new JPAQuery<SprayOrder>(em)
                .from(QSprayOrder.sprayOrder)
                .where(QSprayOrder.sprayOrder.farmer.eq(id))
                .fetch();
    }



    @Override
    public List<SprayOrder> getOrdersBySprayer(long id) {
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
}
