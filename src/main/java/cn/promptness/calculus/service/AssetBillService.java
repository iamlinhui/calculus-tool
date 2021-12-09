package cn.promptness.calculus.service;

import cn.promptness.calculus.cache.AccountCache;
import cn.promptness.calculus.pojo.AssetBill;
import cn.promptness.calculus.pojo.CiaResponse;
import cn.promptness.calculus.pojo.FileRecord;
import cn.promptness.calculus.task.ContinuationTask;
import cn.promptness.httpclient.HttpClientUtil;
import cn.promptness.httpclient.HttpResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AssetBillService {

    @Resource
    private HttpClientUtil httpClientUtil;
    @Resource
    private ContinuationTask continuationTask;

    /**
     * 获取标准回盘文件下载地址
     *
     * @param fileType 1 还款计划 4 实际还款
     * @return 文件信息
     * @throws
     * @author lynn
     * @date 2021/11/19 16:43
     * @since v1.0.0
     */
    public FileRecord searchFileRecord(Integer loanChannelId, Date businessDate, Integer fileType) throws Exception {
        String format = DateFormatUtils.format(businessDate, "yyyy-MM-dd");
        HttpResult httpResult = httpClientUtil.doPostJson(
                "https://ciaapi.dszc-amc.com/cgi/CalculusAssetBillService_searchFileRecord",
                new String[]{loanChannelId.toString(), format, fileType.toString()},
                AccountCache.getHeaderList()
        );
        if (httpResult.isFailed()) {
            return null;
        }
        CiaResponse<FileRecord> ciaResponse = JSON.parseObject(httpResult.getMessage(), new TypeReference<CiaResponse<FileRecord>>() {}.getType());
        if (ciaResponse.isSuccess()) {
            return ciaResponse.getData();
        }
        if (ciaResponse.getCode() == 1) {
            continuationTask.continuation();
        }
        return null;
    }


    public List<AssetBill> selectAssetBill(String assetId) throws Exception {
        HttpResult httpResult = httpClientUtil.doPostJson(
                "https://ciaapi.dszc-amc.com/cgi/CalculusAssetBillService_selectAssetBill",
                new String[]{assetId},
                AccountCache.getHeaderList()
        );
        if (httpResult.isFailed()) {
            return Lists.newArrayList();
        }
        CiaResponse<List<AssetBill>> ciaResponse = JSON.parseObject(httpResult.getMessage()
                .replace("realRepayMuclt", "realRepayMulct")
                .replace("\"[", "[")
                .replace("]\"", "]")
                .replace("\"{", "{")
                .replace("}\"", "}"), new TypeReference<CiaResponse<List<AssetBill>>>() {
        }.getType());

        if (ciaResponse.isSuccess()) {
            return ciaResponse.getData();
        }
        if (ciaResponse.getCode() == 1) {
            continuationTask.continuation();
        }
        // {"code":"0001","msg":"登陆状态异常，请重新登录！"}
        log.error(httpResult.getMessage());
        return Lists.newArrayList();
    }

    public File downloadFile(String path, String fileName) throws Exception {
        File file = new File(fileName);
        HttpResult httpResult = httpClientUtil.doGet(path, new FileOutputStream(file));
        if (httpResult.isSuccess()) {
            return file;
        }
        return null;
    }
}
