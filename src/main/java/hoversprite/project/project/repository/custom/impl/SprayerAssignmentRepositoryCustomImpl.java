package hoversprite.project.project.repository.custom.impl;

import hoversprite.project.project.repository.custom.SprayerAssignmentRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class SprayerAssignmentRepositoryCustomImpl implements SprayerAssignmentRepositoryCustom {
    @PersistenceContext
    private EntityManager em;
}
