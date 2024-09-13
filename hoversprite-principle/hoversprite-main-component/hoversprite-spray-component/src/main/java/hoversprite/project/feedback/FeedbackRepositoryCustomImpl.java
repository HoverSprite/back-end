package hoversprite.project.feedback;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class FeedbackRepositoryCustomImpl implements FeedbackRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Feedback> getFeedbacksBySprayOrderId(Long sprayOrderId) {
        return new JPAQuery<Feedback>(em)
                .from(QFeedback.feedback)
                .where(QFeedback.feedback.sprayOrder.eq(sprayOrderId))
                .fetch();
    }
}
