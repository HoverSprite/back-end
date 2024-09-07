package hoversprite.project.project.repository;


import hoversprite.project.project.model.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    // Additional custom query methods can be defined here if needed

}
