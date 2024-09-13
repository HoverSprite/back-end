package hoversprite.project.image;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class ImageRepositoryCustomImpl implements ImageRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Image> getImagesByFeedbackIds(List<Long> feedbackIds) {
        return new JPAQuery<Image>(em)
                .from(QImage.image)
                .where(QImage.image.feedback.in(feedbackIds))
                .fetch();
    }
}
