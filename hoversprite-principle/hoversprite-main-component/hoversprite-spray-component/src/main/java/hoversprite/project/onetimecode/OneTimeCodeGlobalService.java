package hoversprite.project.onetimecode;

import hoversprite.project.onetimecode.request.OTCRequest;

public interface OneTimeCodeGlobalService {
    String createOtp(Long userId);

    boolean verifyOtp(Long userId, Long sprayOrderId);
}
