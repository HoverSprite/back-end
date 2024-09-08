package hoversprite.project.payment;

import hoversprite.project.payment.request.PaymentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    Payment toEntity(PaymentDTO dto);
    PaymentDTO toDto(Payment entity);

    Payment toEntitySave(PaymentRequest request);
}
