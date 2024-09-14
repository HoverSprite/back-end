package hoversprite.project.qrcode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
class QRCodeServiceImpl implements QRCodeService {
    @Override
    public byte[] generateQRCode(String content) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300);

        // Convert BitMatrix to BufferedImage with color support
        BufferedImage qrImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = qrImage.createGraphics();
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, qrImage.getWidth(), qrImage.getHeight());
        graphics.setColor(Color.BLACK);

        for (int y = 0; y < bitMatrix.getHeight(); y++) {
            for (int x = 0; x < bitMatrix.getWidth(); x++) {
                if (bitMatrix.get(x, y)) {
                    graphics.fillRect(x, y, 1, 1);
                }
            }
        }
        graphics.dispose();

        // Load logo from classpath
        InputStream logoStream = getClass().getClassLoader().getResourceAsStream("hoversprite-icon.png");
        if (logoStream == null) {
            throw new IOException("Logo image not found");
        }
        BufferedImage logo = ImageIO.read(logoStream);

        // Calculate the scaling factor to resize the logo
        int logoWidth = 40;
        int logoHeight = 40;
        Image scaledLogo = logo.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);

        // Draw logo on QR code
        graphics = qrImage.createGraphics();
        int centerX = (qrImage.getWidth() - logoWidth) / 2;
        int centerY = (qrImage.getHeight() - logoHeight) / 2;
        graphics.drawImage(scaledLogo, centerX, centerY, null);
        graphics.dispose();

        // Convert BufferedImage to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", baos);
        return baos.toByteArray();
    }
}
