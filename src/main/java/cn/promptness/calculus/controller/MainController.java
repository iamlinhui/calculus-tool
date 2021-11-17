package cn.promptness.calculus.controller;

import cn.promptness.httpclient.HttpClientUtil;
import cn.promptness.httpclient.HttpResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    public TabPane tabPane;

    @FXML
    private Button bt;

    public void initialize() {



    }

    @FXML
    public void click(ActionEvent actionEvent) throws Exception {
        Cookie cookie = new BasicClientCookie("sessionId", "A2BD540EB9BA7335504F6A91E1B9223A");

        List<Cookie> cookieList = new ArrayList<>();
        cookieList.add(cookie);

        HttpClientUtil httpClientUtil = new HttpClientUtil();
        HttpResult httpResult = httpClientUtil.doPostJson(
                "https://ciaapi.dszc-amc.com/cgi/CalculusAssetBillService_selectAssetBill",
                new String[]{"1120041311454848607348"},
                cookieList
        );
        System.out.println(httpResult.getMessage());


        HttpResult httpResult2 = httpClientUtil.doPostJson(
                "https://ciaapi.dszc-amc.com/cgi/CalculusAssetBillService_searchFileRecord",
                new String[]{"1087", "2021-08-31", "4"},
                cookieList
        );
        System.out.println(httpResult2.getMessage());


        HttpResult httpResult1 = httpClientUtil.doGet("http://img1.fenqile.com/dszgloan200/M00/ex/20210831231017-a7b70186-f5f7-42c0-9cac-a9dd7099afaa.zip", new FileOutputStream(new File("d:/a.zip")));
        System.out.println(httpResult1);
    }
}
