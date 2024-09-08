package hoversprite.project.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryCustom {

}
