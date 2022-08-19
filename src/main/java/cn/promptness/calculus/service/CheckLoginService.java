package cn.promptness.calculus.service;

import cn.promptness.calculus.cache.AccountCache;
import cn.promptness.calculus.controller.LoginController;
import cn.promptness.calculus.pojo.Login;
import cn.promptness.httpclient.HttpClientUtil;
import cn.promptness.httpclient.HttpResult;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CheckLoginService extends BaseService<Boolean> {
    @Resource
    private LoginController loginController;
    @Resource
    private HttpClientUtil httpClientUtil;
    @Resource
    private ConfigurableApplicationContext applicationContext;
    private Stage loginStage;

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                while (loginStage.isShowing()) {
                    TimeUnit.SECONDS.sleep(3);
                    // 获取oa_token
                    HttpResult httpResult = httpClientUtil.doGet(String.format("https://passport.oa.fenqile.com/user/main/scan.json?token=%s&_=%s", loginController.getToken(), loginController.getCurrentTimeMillis()), AccountCache.getHeaderList());
                    Login login = httpResult.getContent(Login.class);
                    if (login.isSuccess()) {
                        AccountCache.flashHeader(httpResult.getHeaderList("Set-Cookie"));
                        applicationContext.getBean(CiaLoginService.class).start();
                        return true;
                    }
                }
                return Boolean.FALSE;
            }
        };
    }

    public CheckLoginService setStage(final Stage loginStage) {
        this.loginStage = loginStage;
        return this;
    }

    @Override
    public Service<Boolean> expect(Callback callback) {
        super.setOnSucceeded(event -> {
            if (Objects.equals(Boolean.TRUE, event.getSource().getValue())) {
                loginStage.close();
                applicationContext.getBean(ValidateUserService.class).expect(callback).start();
            }
        });
        return this;
    }
}
