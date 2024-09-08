package hoversprite.project.payment;

import hoversprite.project.common.base.AbstractService;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.common.validator.ValidationUtils;
import hoversprite.project.payment.request.PaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
class PaymentServiceImpl extends AbstractService<PaymentDTO, PaymentRequest, Long, PersonRole> implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public PaymentDTO findById(Long aLong) {
        return paymentRepository.findById(aLong).map(PaymentMapper.INSTANCE::toDto).orElse(null);
    }

    @Override
    public List<PaymentDTO> findAll() {
        return paymentRepository.findAll().stream().map(PaymentMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long aLong) {
        paymentRepository.deleteById(aLong);
    }

    @Override
    protected void validateForSave(ValidationUtils validator, PaymentRequest dto, PersonRole personRole) {

    }

    @Override
    protected void validateForUpdate(ValidationUtils validator, PaymentRequest dto, PersonRole personRole) {

    }

    @Override
    protected PaymentDTO executeSave(Long userId, PaymentRequest dto, PersonRole personRole) {
        return PaymentMapper.INSTANCE.toDto(paymentRepository.save(PaymentMapper.INSTANCE.toEntitySave(dto)));
    }

    @Override
    protected PaymentDTO executeUpdate(Long userId, Long aLong, PaymentRequest dto, PersonRole personRole) {
        return null;
    }
}
