package hoversprite.project.auth.onetimecode;

import hoversprite.project.auth.onetimecode.request.OTCRequest;
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
    public boolean verifyOtp(Long userId, Long sprayOrderId, OTCRequest otp) {
        SprayOrderDTO sprayOrder = sprayOrderGlobalService.findById(sprayOrderId);
        Optional<OneTimeCode> code = oneTimeCodeRepository.findByUserId(sprayOrder.getFarmer());
        return code.map(oneTimeCode -> oneTimeCode.getOtpCode().equals(otp.getOtp())).orElse(false);
    }

    public String generateOtp() {
        return String.valueOf((int)(Math.random() * 900000) + 100000); // 6-digit OTP
    }
}
