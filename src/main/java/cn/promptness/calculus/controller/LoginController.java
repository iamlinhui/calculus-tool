package cn.promptness.calculus.controller;

import cn.promptness.httpclient.HttpClientUtil;
import cn.promptness.httpclient.HttpResult;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.message.BasicHeader;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;

@Controller
public class LoginController {

    @Resource
    private HttpClientUtil httpClientUtil;

    @FXML
    public ImageView codeImageView;

    public void initialize() {
        currentTimeMillis = String.valueOf(System.currentTimeMillis());
        token = DigestUtils.md5Hex(currentTimeMillis);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            // fix 2022/5/9 增加Referer
            HttpResult httpResult = httpClientUtil.doGet("https://passport.oa.fenqile.com/user/main/qrcode.png?token=" + getToken(), byteArrayOutputStream, Collections.singletonList(new BasicHeader("Referer", "https://passport.oa.fenqile.com/")));
            codeSuccess = httpResult.isSuccess();
            codeImageView.setImage(new Image(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())));
        } catch (Exception e) {
            codeSuccess = false;
        }
    }


    /**
     * 生成二维码需要的客户端token的基准时间
     */
    private String currentTimeMillis;

    /**
     * 生成二维码需要的客户端token
     */
    private String token;

    /**
     * 二维码是否加载成功
     */
    private boolean codeSuccess;

    public String getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public String getToken() {
        return token;
    }

    public boolean isCodeSuccess() {
        return codeSuccess;
    }
}
