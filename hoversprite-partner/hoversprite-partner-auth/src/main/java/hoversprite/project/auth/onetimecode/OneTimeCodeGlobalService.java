package hoversprite.project.auth.onetimecode;

import hoversprite.project.auth.onetimecode.request.OTCRequest;
import hoversprite.project.common.domain.PersonExpertise;

import java.util.List;
import java.util.Map;

public interface OneTimeCodeGlobalService {
    String createOtp(Long userId);

    boolean verifyOtp(Long sprayOrderId, OTCRequest otp);
}
