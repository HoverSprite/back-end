package hoversprite.project.project.repository;

import hoversprite.project.project.model.entity.SprayOrder;
import hoversprite.project.project.repository.custom.SprayOrderRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprayOrderRepository extends JpaRepository<SprayOrder, Integer>, SprayOrderRepositoryCustom {
}
