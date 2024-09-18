package hoversprite.project.sprayOrder;

import com.querydsl.jpa.impl.JPAQuery;
import hoversprite.project.common.domain.SprayStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.support.PageableExecutionUtils.getPage;

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
    public Page<SprayOrder> getOrdersByUser(long id, Pageable pageable) {
        JPAQuery<SprayOrder> query = new JPAQuery<SprayOrder>(em)
                .from(QSprayOrder.sprayOrder)
                .where(QSprayOrder.sprayOrder.farmer.eq(id));

        long total = query.fetchCount();

        List<SprayOrder> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<SprayOrder> getOrdersByUserAndStatus(long id, SprayStatus status, Pageable pageable) {
        JPAQuery<SprayOrder> query = new JPAQuery<SprayOrder>(em)
                .from(QSprayOrder.sprayOrder)
                .where(QSprayOrder.sprayOrder.farmer.eq(id)
                        .and(QSprayOrder.sprayOrder.status.eq(status)));

        long total = query.fetchCount();

        List<SprayOrder> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<SprayOrder> getOrdersBySprayer(List<Long> sprayOrderIds, Pageable pageable) {
        JPAQuery<SprayOrder> query = new JPAQuery<SprayOrder>(em)
                .from(QSprayOrder.sprayOrder)
                .where(QSprayOrder.sprayOrder.id.in(sprayOrderIds));

        long total = query.fetchCount();

        List<SprayOrder> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<SprayOrder> getOrdersBySprayerAndStatus(List<Long> sprayOrderIds, SprayStatus status, Pageable pageable) {
        JPAQuery<SprayOrder> query = new JPAQuery<SprayOrder>(em)
                .from(QSprayOrder.sprayOrder)
                .where(QSprayOrder.sprayOrder.id.in(sprayOrderIds)
                        .and(QSprayOrder.sprayOrder.status.eq(status)));

        long total = query.fetchCount();

        List<SprayOrder> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<SprayOrder> findAllByStatus(SprayStatus status, Pageable pageable) {
        JPAQuery<SprayOrder> query = new JPAQuery<SprayOrder>(em)
                .from(QSprayOrder.sprayOrder)
                .where(QSprayOrder.sprayOrder.status.eq(status));

        long total = query.fetchCount();

        List<SprayOrder> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<SprayOrder> getUnAssignedSprayOrders() {
        return new JPAQuery<SprayOrder>(em)
                .from(QSprayOrder.sprayOrder)
                .where(QSprayOrder.sprayOrder.status.eq(SprayStatus.CONFIRMED)

                        .and(QSprayOrder.sprayOrder.autoAssign.eq(true)))
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
