package hoversprite.project.qrcode;

import com.google.zxing.WriterException;

import java.io.IOException;

public interface QRCodeGlobalService {
    public byte[] generateQRCode(String content) throws WriterException, IOException;
}
