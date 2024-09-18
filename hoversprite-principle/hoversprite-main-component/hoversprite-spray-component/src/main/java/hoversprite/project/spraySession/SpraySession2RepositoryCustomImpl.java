package hoversprite.project.spraySession;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
class SpraySession2RepositoryCustomImpl implements SpraySession2RepositoryCustom {
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

    @Override
    public List<SpraySession_2> findSpraySessionByWeek(LocalDate date) {
        QSpraySession_2 spraySession = QSpraySession_2.spraySession_2;

        // Determine the start and end of the week for the given date
        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = date.with(DayOfWeek.SUNDAY);

        // Create a query to find spray sessions within the week
        JPAQuery<SpraySession_2> query = new JPAQuery<>(em);

        return query.select(spraySession)
                .from(spraySession)
                .where(spraySession.date.between(startOfWeek, endOfWeek))
                .fetch();
    }

    @Override
    public List<SpraySession_2> findSpraySessionsBetween(LocalDate startDate, LocalDate endDate) {
        QSpraySession_2 spraySession = QSpraySession_2.spraySession_2;

        JPAQuery<SpraySession_2> query = new JPAQuery<>(em);

        return query.select(spraySession)
                .from(spraySession)
                .where(spraySession.date.between(startDate, endDate))
                .fetch();
    }
}
