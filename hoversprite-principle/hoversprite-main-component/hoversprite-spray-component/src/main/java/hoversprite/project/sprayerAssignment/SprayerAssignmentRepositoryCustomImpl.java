package hoversprite.project.sprayerAssignment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
class SprayerAssignmentRepositoryCustomImpl implements SprayerAssignmentRepositoryCustom {
    @PersistenceContext
    private EntityManager em;
}
