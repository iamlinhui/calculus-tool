package cn.promptness.calculus.task;

import cn.promptness.calculus.cache.AccountCache;
import cn.promptness.calculus.service.CiaLoginService;
import cn.promptness.calculus.service.ValidateUserService;
import javafx.application.Platform;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 检查登录状态
 *
 * @author lynn
 * @date 2021/1/8 14:18
 * @since v1.0.0
 */
@Component
public class ContinuationTask {

    @Resource
    private ConfigurableApplicationContext applicationContext;

    @Scheduled(initialDelay = 60000, fixedRate = 60000)
    public void continuation() {
        if (AccountCache.haveAccount()) {
            Platform.runLater(() -> {
                applicationContext.getBean(ValidateUserService.class).expect(null).start();
                applicationContext.getBean(CiaLoginService.class).start();
            });
        }
    }
}
