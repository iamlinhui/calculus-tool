package cn.promptness.calculus;

import cn.promptness.calculus.cache.AccountCache;
import cn.promptness.calculus.data.Constant;
import cn.promptness.calculus.utils.SpringFxmlLoader;
import cn.promptness.calculus.utils.SystemTrayUtil;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.Style;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.awt.*;

@SpringBootApplication
@EnableScheduling
public class CalculusToolApplication extends Application implements ApplicationListener<ContextClosedEvent> {

    private ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        if (!SystemTray.isSupported()) {
            System.exit(1);
        }
        Application.launch(CalculusToolApplication.class, args);
    }

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder().sources(CalculusToolApplication.class).bannerMode(Banner.Mode.OFF).web(WebApplicationType.NONE).run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) {
        SystemTrayUtil.systemTray(primaryStage, Constant.TITLE);
        Parent root = applicationContext.getBean(SpringFxmlLoader.class).load("/fxml/main.fxml");
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Style.LIGHT.getStyleStylesheetURL());
        primaryStage.setTitle(Constant.TITLE);
        primaryStage.getIcons().add(new Image("/icon.png"));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        AccountCache.cache();
    }

}
