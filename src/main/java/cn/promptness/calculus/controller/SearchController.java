package cn.promptness.calculus.controller;

import cn.promptness.calculus.service.SearchService;
import cn.promptness.calculus.utils.ProgressUtil;
import cn.promptness.calculus.utils.SystemTrayUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SearchController {

    @FXML
    public TextField assetBillId;
    @FXML
    public TextArea ourBill;
    @FXML
    public TextArea otherExpectBill;
    @FXML
    public TextArea otherRealBill;

    @Resource
    private ConfigurableApplicationContext applicationContext;

    public void initialize() {

    }

    @FXML
    public void search() {
        SearchService searchService = applicationContext.getBean(SearchService.class).setSearchController(this);
        ProgressUtil.of(SystemTrayUtil.getPrimaryStage(), searchService).show();
    }
}
