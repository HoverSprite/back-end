package hoversprite.project.project.repository;

import hoversprite.project.project.model.entity.SprayerAssignment;
import hoversprite.project.project.repository.custom.SprayerAssignmentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprayerAssignmentRepository extends JpaRepository<SprayerAssignment, Integer>, SprayerAssignmentRepositoryCustom {
}
