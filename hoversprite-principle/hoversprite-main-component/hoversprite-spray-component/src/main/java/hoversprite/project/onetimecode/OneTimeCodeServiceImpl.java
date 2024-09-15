package hoversprite.project.onetimecode;

import hoversprite.project.onetimecode.request.OTCRequest;
import hoversprite.project.sprayOrder.SprayOrderDTO;
import hoversprite.project.sprayOrder.SprayOrderGlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
class OneTimeCodeServiceImpl implements OneTimeCodeService {

    private static final int OTP_LENGTH = 6;
    @Autowired
    private OneTimeCodeRepository oneTimeCodeRepository;

    @Autowired
    private SprayOrderGlobalService sprayOrderGlobalService;


    @Override
    public String createOtp(Long userId) {
        if (oneTimeCodeRepository.otpExistedForUser(userId)) {
            throw new RuntimeException("Code already existed");
        }

        return oneTimeCodeRepository.save(OneTimeCode.builder()
                .user(userId)
                .otpCode(generateOtp())
                .createdAt(LocalDateTime.now())
                .build()).getOtpCode();
    }

    @Override
    public boolean verifyOtp(Long userId, Long sprayOrderId) {
        return sprayOrderGlobalService.findById(sprayOrderId) != null;
    }

    public String generateOtp() {
        return String.valueOf((int)(Math.random() * 900000) + 100000); // 6-digit OTP
    }
}
