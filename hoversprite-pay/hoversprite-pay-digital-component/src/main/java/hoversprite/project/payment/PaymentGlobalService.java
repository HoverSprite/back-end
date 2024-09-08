package hoversprite.project.payment;

import hoversprite.project.common.base.BaseService;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.payment.request.PaymentRequest;

public interface PaymentGlobalService  extends BaseService<PaymentDTO, PaymentRequest, Long, PersonRole> {

}
