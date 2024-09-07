package hoversprite.project.project.repository;

import hoversprite.project.project.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Additional custom query methods can be defined here if needed
}
