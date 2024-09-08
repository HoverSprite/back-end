package hoversprite.project.sprayerAssignment;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class SprayerAssignmentRepositoryCustomImpl implements SprayerAssignmentRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<SprayerAssignment> findAllBySprayOrderIds(List<Long> ids) {
        return new JPAQuery<SprayerAssignment>(em)
                .from(QSprayerAssignment.sprayerAssignment)
                .where(QSprayerAssignment.sprayerAssignment.sprayOrder.in(ids))
                .fetch();
    }

    @Override
    public List<SprayerAssignment> findAllBySprayerAndSprayOrderIds(List<Long> sprayOrderIds, List<Long> sprayerIds) {
        return new JPAQuery<SprayerAssignment>(em)
                .from(QSprayerAssignment.sprayerAssignment)
                .where(QSprayerAssignment.sprayerAssignment.sprayOrder.in(sprayOrderIds)
                        .and(QSprayerAssignment.sprayerAssignment.sprayer.in(sprayerIds)))
                .fetch();
    }
}
