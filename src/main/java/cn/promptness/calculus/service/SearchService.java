package cn.promptness.calculus.service;

import cn.promptness.calculus.controller.BillController;
import cn.promptness.calculus.pojo.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SearchService extends BaseService<Void> {

    @Resource
    private AssetBillService assetBillService;

    @Resource
    private PlatformFileService platformFileService;

    private BillController billController;

    public SearchService setBillController(BillController billController) {
        this.billController = billController;
        return this;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String assetId = billController.assetBillId.getText();

                if (StringUtils.isEmpty(assetId)) {
                    return null;
                }
                String maybeAssetId = StringUtils.trimAllWhitespace(assetId.split("-")[0]);
                if (StringUtils.isEmpty(maybeAssetId)) {
                    return null;
                }

                List<AssetBill> assetBillList = assetBillService.selectAssetBill(maybeAssetId);
                if (assetBillList.isEmpty()) {
                    return null;
                }
                Collections.sort(assetBillList);
                billController.ourBill.setText(JSON.toJSONStringWithDateFormat(assetBillList, "yyyy-MM-dd", SerializerFeature.PrettyFormat));

                AssetBill assetBill = assetBillList.get(0);

                ExpectSearchReq expectSearchReq = new ExpectSearchReq();
                expectSearchReq.setLoanChannel(assetBill.getLoanChannelId());
                expectSearchReq.setPaymentTime(assetBill.getPaymentTime());
                expectSearchReq.setExpectSearchParamList(Lists.newArrayList(new ExpectSearchReq.ExpectSearchParam(assetBill.getAssetId(), assetBill.getLoanTerm())));


                Map<String, List<ExpectSearchRsp>> capitalExpectRepay = platformFileService.searchCapitalExpectRepay(expectSearchReq);
                List<ExpectSearchRsp> expectSearchRsps = Optional.ofNullable(capitalExpectRepay.get(assetBill.getAssetId())).orElse(Lists.newArrayList());
                Collections.sort(expectSearchRsps);

                billController.otherExpectBill.setText(JSON.toJSONStringWithDateFormat(expectSearchRsps, "yyyy-MM-dd", SerializerFeature.PrettyFormat));

                Map<Date, List<AssetBill>> collect = assetBillList.stream().filter(x -> !Objects.equals("UNREPAY", x.getRealRepayType())).collect(Collectors.groupingBy(AssetBill::getRealRepayDate));

                List<RealSearchRsp> result = Lists.newArrayList();
                for (Map.Entry<Date, List<AssetBill>> dateListEntry : collect.entrySet()) {
                    if (dateListEntry.getKey().getTime() == -28800000) {
                        continue;
                    }

                    RealSearchReq.RealSearchParam realSearchParam = new RealSearchReq.RealSearchParam();
                    realSearchParam.setAssetId(assetBill.getAssetId());
                    // 合并最小期数对账 or 合并最大期数对账 这里先从实还文件里面全部取出来
                    realSearchParam.setMinRepayTerm(0);
                    realSearchParam.setMaxRepayTerm(36);


                    RealSearchReq realSearchReq = new RealSearchReq();
                    realSearchReq.setLoanChannel(assetBill.getLoanChannelId());
                    realSearchReq.setRealRepayDate(dateListEntry.getKey());
                    realSearchReq.setRealSearchParamList(Lists.newArrayList(realSearchParam));

                    Map<String, List<RealSearchRsp>> stringListMap = platformFileService.searchCapitalRealRepay(realSearchReq);
                    if (stringListMap.isEmpty()) {
                        log.error("{}实还资金方数据不存在{}", dateListEntry.getKey(), assetBill.getAssetId());
                        continue;
                    }
                    result.addAll(stringListMap.get(assetBill.getAssetId()));
                }
                Collections.sort(result);
                billController.otherRealBill.setText(JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd", SerializerFeature.PrettyFormat));

                return null;
            }
        };
    }
}
