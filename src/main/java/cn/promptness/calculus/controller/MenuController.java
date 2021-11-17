package cn.promptness.calculus.controller;

import cn.promptness.calculus.cache.AccountCache;
import cn.promptness.calculus.service.CheckLoginService;
import cn.promptness.calculus.service.ValidateUserService;
import cn.promptness.calculus.utils.SpringFxmlLoader;
import cn.promptness.calculus.utils.SystemTrayUtil;
import cn.promptness.calculus.utils.TooltipUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class MenuController {

    @FXML
    public Menu accountTitle;
    @FXML
    public MenuItem accountAction;
    @Resource
    private ConfigurableApplicationContext applicationContext;
    @Resource
    private SpringFxmlLoader springFxmlLoader;
    @Resource
    private MainController mainController;

    public void initialize() {
        AccountCache.read();
        applicationContext.getBean(ValidateUserService.class).expect(event -> {
            accountAction.setText("注销");
            accountTitle.setText(event.getSource().getValue().toString());
        }).start();
    }

    public void add(ActionEvent actionEvent) {

    }


    @FXML
    public void account() {
        // 有账户 点击时就是注销
        if (AccountCache.haveAccount()) {
            doLogout();
            return;
        }
        doLogin();
    }

    public void login() {
        doLogout();
        doLogin();
    }

    private void doLogin() {
        Stage primaryStage = SystemTrayUtil.getPrimaryStage();
        Parent root = primaryStage.getScene().getRoot();
        double x = TooltipUtil.getScreenX(root) + TooltipUtil.getWidth(root) / 3;
        double y = TooltipUtil.getScreenY(root) + TooltipUtil.getHeight(root) / 4;

        Scene scene = new Scene(springFxmlLoader.load("/fxml/login.fxml"));

        Stage loginStage = new Stage();
        loginStage.setTitle("MOA扫码登录");
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.initOwner(SystemTrayUtil.getPrimaryStage());
        loginStage.getIcons().add(new Image("/icon.png"));
        loginStage.setResizable(false);
        loginStage.setScene(scene);
        loginStage.setX(x);
        loginStage.setY(y);
        loginStage.show();

        applicationContext.getBean(CheckLoginService.class).setStage(loginStage).expect(event -> {
            accountAction.setText("注销");
            accountTitle.setText(event.getSource().getValue().toString());
        }).start();
    }

    private void doLogout() {
        AccountCache.logout();
        accountAction.setText("登录");
        accountTitle.setText("账户");
    }

    @FXML
    public void logger(ActionEvent actionEvent) {
        MenuItem menuItem = (MenuItem) actionEvent.getSource();
        LoggingSystem system = LoggingSystem.get(LoggingSystem.class.getClassLoader());
        system.setLogLevel("cn.promptness.calculus", LogLevel.valueOf(menuItem.getText().toUpperCase()));
    }

    public void close(ActionEvent actionEvent) {

    }

    public void instruction(ActionEvent actionEvent) {

    }

    public void about(ActionEvent actionEvent) {

    }
}
