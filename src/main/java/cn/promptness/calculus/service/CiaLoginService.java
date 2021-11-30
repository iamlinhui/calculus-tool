package cn.promptness.calculus.service;

import cn.promptness.calculus.cache.AccountCache;
import cn.promptness.calculus.pojo.CiaResponse;
import cn.promptness.httpclient.HttpClientUtil;
import cn.promptness.httpclient.HttpResult;
import javafx.concurrent.Task;
import org.apache.http.Header;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CiaLoginService extends BaseService<Boolean> {

    @Resource
    private HttpClientUtil httpClientUtil;

    @Override
    protected Task<Boolean> createTask() {

        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                // 获取重定向中的ticket
                List<Header> location = httpClientUtil.doGet("https://passport.oa.fenqile.com/?url=https://cia.dszc-amc.com", AccountCache.getHeaderList()).getHeaderList("Location");
                if (location.isEmpty()) {
                    return false;
                }
                String ticket = location.get(0).getValue().split("=")[1];
                if (StringUtils.isEmpty(ticket)) {
                    return false;
                }

                // 获取cia sessionId
                List<Header> dsHeadList = httpClientUtil.doPost(String.format("https://ciaapi.dszc-amc.com/users/login?ticket=%s", ticket), AccountCache.getHeaderList()).getHeaderList("Set-Cookie");
                AccountCache.addHeader(dsHeadList);

                // 查询环境信息
                HttpResult ciaHttpResult = httpClientUtil.doPost("https://ciaapi.dszc-amc.com/cgi/env", AccountCache.getHeaderList());
                // {"code":"0","data":{"env":"pre"},"msg":"SUCCESS"}
                return ciaHttpResult.getContent(CiaResponse.class).isSuccess();
            }
        };
    }
}
