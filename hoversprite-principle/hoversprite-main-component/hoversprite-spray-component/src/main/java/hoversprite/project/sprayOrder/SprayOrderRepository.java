package hoversprite.project.sprayOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface SprayOrderRepository extends JpaRepository<SprayOrder, Long>, SprayOrderRepositoryCustom {
}
