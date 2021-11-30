package cn.promptness.calculus.controller;

import cn.promptness.calculus.utils.SpringFxmlLoader;
import cn.promptness.calculus.utils.SystemTrayUtil;
import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import com.sun.javafx.scene.control.skin.TabPaneSkin;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class MainController {

    @Resource
    private SpringFxmlLoader springFxmlLoader;

    @FXML
    public TabPane tabPane;

    public void initialize() {
        addTab();
    }

    public void addTab() {
        FXMLLoader loader = springFxmlLoader.getLoader("/fxml/bill.fxml");
        Parent load = springFxmlLoader.load(loader);
        Tab tab = new Tab("控制台", load);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
        tab.setOnClosed(event -> tabPane.setTabClosingPolicy(tabPane.getTabs().size() == 1 ? TabPane.TabClosingPolicy.UNAVAILABLE : TabPane.TabClosingPolicy.SELECTED_TAB));
        tabPane.setTabClosingPolicy(tabPane.getTabs().size() > 1 ? TabPane.TabClosingPolicy.SELECTED_TAB : TabPane.TabClosingPolicy.UNAVAILABLE);
    }

    @EventListener(value = Class.class)
    public void addCtrlClose() {
        // SHORTCUT在windows会处理成ctrl,在苹果上会处理成Command
        SystemTrayUtil.getPrimaryStage().getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN),
                () -> {
                    if (tabPane.getTabs().size() > 1) {
                        TabPaneBehavior tabPaneBehavior = ((TabPaneSkin) tabPane.getSkin()).getBehavior();
                        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
                        if (tabPaneBehavior.canCloseTab(selectedTab)) {
                            tabPaneBehavior.closeTab(selectedTab);
                        }
                    }
                });
    }
}
