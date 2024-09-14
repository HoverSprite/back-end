package hoversprite.project.feedback;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
interface FeedbackRepository extends JpaRepository<Feedback, Long>, FeedbackRepositoryCustom {
}