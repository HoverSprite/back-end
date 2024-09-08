package hoversprite.project.sprayOrder;

import com.querydsl.jpa.impl.JPAQuery;
import hoversprite.project.common.domain.SprayStatus;
import hoversprite.project.sprayOrder.QSprayOrder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
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
    public List<SprayOrder> getUnAssignedSprayOrders() {
        return new JPAQuery<SprayOrder>(em)
                .from(QSprayOrder.sprayOrder)
                .where(QSprayOrder.sprayOrder.status.eq(SprayStatus.CONFIRMED))
                .fetch();
    }

    @Override
    public List<SprayOrder> findAllAvailableSprayOrderForSprayerByIds(List<Long> ids) {
        return new JPAQuery<SprayOrder>(em)
                .from(QSprayOrder.sprayOrder)
                .where(QSprayOrder.sprayOrder.status.eq(SprayStatus.ASSIGNED)
                        .and(QSprayOrder.sprayOrder.id.in(ids)))
                .fetch();
    }

}
