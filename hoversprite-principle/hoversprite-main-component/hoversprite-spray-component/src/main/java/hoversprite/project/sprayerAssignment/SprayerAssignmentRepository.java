package hoversprite.project.sprayerAssignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface SprayerAssignmentRepository extends JpaRepository<SprayerAssignment, Long>, SprayerAssignmentRepositoryCustom {
}
