package cn.promptness.calculus.utils;


import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.util.StringUtils;

import java.util.Objects;

public class ProgressUtil {

    private Stage stage;
    private Service<?> work;

    private ProgressUtil() {
    }

    public static ProgressUtil of(Stage parent, Service<?> work) {
        ProgressUtil ps = new ProgressUtil();
        ps.work = Objects.requireNonNull(work);
        ps.init(parent, "");
        return ps;
    }

    /**
     * 创建
     *
     * @param parent
     * @param work
     * @param ad
     * @return
     */
    public static ProgressUtil of(Stage parent, Service<?> work, String ad) {
        ProgressUtil ps = new ProgressUtil();
        ps.work = Objects.requireNonNull(work);
        ps.init(parent, ad);
        return ps;
    }

    /**
     * 显示
     */
    public void show() {
        work.start();
        stage.show();
    }

    private void init(Stage parent, String ad) {
        stage = new Stage();
        stage.initOwner(parent);
        // style
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);

        // message
        Label adLbl = new Label(ad);
        adLbl.setTextFill(Color.BLACK);

        // progress
        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setProgress(-1);
        indicator.progressProperty().bind(work.progressProperty());

        // pack
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setBackground(Background.EMPTY);
        vBox.getChildren().addAll(indicator, adLbl);

        // scene
        Scene scene = new Scene(vBox);
        scene.setFill(null);
        stage.setScene(scene);
        stage.setWidth(StringUtils.isEmpty(ad) ? 100 : ad.length() * 8d + 10d);
        stage.setHeight(100);

        // show center of parent
        double x = parent.getX() + (parent.getWidth() - stage.getWidth()) / 2;
        double y = parent.getY() + (parent.getHeight() - stage.getHeight()) / 2;
        stage.setX(x);
        stage.setY(y);

        // close if work finish
        EventHandler<WorkerStateEvent> onSucceeded = work.getOnSucceeded();
        work.setOnSucceeded(e -> {
            stage.close();
            if (onSucceeded != null) {
                onSucceeded.handle(e);
            }
        });
    }
}
