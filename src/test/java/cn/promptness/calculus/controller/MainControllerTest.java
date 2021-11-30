package cn.promptness.calculus.controller;


import cn.promptness.calculus.cache.AccountCache;
import cn.promptness.httpclient.HttpClientUtil;
import cn.promptness.httpclient.HttpResult;

public class MainControllerTest {

    public static void main(String[] args) throws Exception {

        AccountCache.read();

        HttpClientUtil httpClientUtil = new HttpClientUtil();

        HttpResult httpResult = httpClientUtil.doPostJson(
                "https://ciaapi.dszc-amc.com/cgi/CalculusAssetBillService_searchFileRecord",
                new String[]{"1087", "2021-08-31", "4"},
                AccountCache.getHeaderList()
        );

        System.out.println(httpResult.getMessage());


    }


}