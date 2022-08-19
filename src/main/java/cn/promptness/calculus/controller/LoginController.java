package cn.promptness.calculus.controller;

import cn.promptness.calculus.data.Constant;
import cn.promptness.httpclient.HttpClientUtil;
import cn.promptness.httpclient.HttpResult;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.message.BasicHeader;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    @FXML
    public ImageView codeImageView;

    public BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    public byte[] getQrCode(String message) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Map<EncodeHintType, Object> hints = new HashMap<>(6);
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 1);
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bt = writer.encode(message, BarcodeFormat.QR_CODE, 240, 240, hints);
        BufferedImage bufferedImage = toBufferedImage(bt);
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    public void initialize() throws Exception {
        currentTimeMillis = String.valueOf(System.currentTimeMillis());
        token = DigestUtils.md5Hex(currentTimeMillis);
        String content = String.format("fenqile://oaWebLogin?platform=%s&data=%s", Constant.TITLE, token);
        byte[] qrCode = getQrCode(content);
        codeImageView.setImage(new Image(new ByteArrayInputStream(qrCode)));
    }


    /**
     * 生成二维码需要的客户端token的基准时间
     */
    private String currentTimeMillis;

    /**
     * 生成二维码需要的客户端token
     */
    private String token;

    public String getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public String getToken() {
        return token;
    }
}
