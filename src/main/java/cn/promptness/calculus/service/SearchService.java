package cn.promptness.calculus.service;

import cn.promptness.calculus.controller.SearchController;
import cn.promptness.calculus.data.Constant;
import cn.promptness.calculus.pojo.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SearchService extends BaseService<Void> {

    @Resource
    private AssetBillService assetBillService;

    @Resource
    private PlatformFileService platformFileService;

    private SearchController searchController;

    public SearchService setSearchController(SearchController searchController) {
        this.searchController = searchController;
        return this;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                searchController.ourBill.setText("");
                searchController.otherExpectBill.setText("");
                searchController.otherRealBill.setText("");
                String assetId = searchController.assetBillId.getText();

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
                searchController.ourBill.setText(JSON.toJSONStringWithDateFormat(assetBillList, Constant.DATE_FORMAT, SerializerFeature.PrettyFormat));

                CountDownLatch countDownLatch = new CountDownLatch(2);
                Constant.TASK_THREAD_POOL.execute(() -> getOtherExpectList(assetBillList, countDownLatch));
                Constant.TASK_THREAD_POOL.execute(() -> getOtherRealList(assetBillList, countDownLatch));
                countDownLatch.await();
                return null;
            }
        };
    }

    private void getOtherRealList(List<AssetBill> assetBillList, CountDownLatch countDownLatch) {
        try {
            AssetBill assetBill = assetBillList.get(0);
            Map<Date, List<AssetBill>> collect = assetBillList.stream().filter(x -> !Objects.equals("UNREPAY", x.getRealRepayType())).collect(Collectors.groupingBy(AssetBill::getRealRepayDate));

            CountDownLatch innerCountDownLatch = new CountDownLatch(collect.size());
            List<Future<List<RealSearchRsp>>> futureTaskList = Lists.newArrayList();
            for (Map.Entry<Date, List<AssetBill>> dateListEntry : collect.entrySet()) {
                Date key = dateListEntry.getKey();
                futureTaskList.add(Constant.SUB_TASK_THREAD_POOL.submit(() -> getSingleRealSearchResponse(assetBill, key, innerCountDownLatch)));
            }
            innerCountDownLatch.await();

            List<RealSearchRsp> result = Lists.newArrayList();
            for (Future<List<RealSearchRsp>> futureTask : futureTaskList) {
                result.addAll(futureTask.get());
            }
            Collections.sort(result);
            searchController.otherRealBill.setText(JSON.toJSONStringWithDateFormat(result, Constant.DATE_FORMAT, SerializerFeature.PrettyFormat));
        } catch (Exception e) {
            searchController.otherRealBill.setText(e.getMessage());
        } finally {
            countDownLatch.countDown();
        }
    }

    private List<RealSearchRsp> getSingleRealSearchResponse(AssetBill assetBill, Date key, CountDownLatch innerCountDownLatch) {
        try {
            List<RealSearchRsp> inner = Lists.newArrayList();
            if (Objects.equals(Constant.DEFAULT_DATE, key)) {
                return inner;
            }
            RealSearchReq.RealSearchParam realSearchParam = new RealSearchReq.RealSearchParam();
            realSearchParam.setAssetId(assetBill.getAssetId());
            // 合并最小期数对账 or 合并最大期数对账 这里先从实还文件里面全部取出来
            realSearchParam.setMinRepayTerm(0);
            realSearchParam.setMaxRepayTerm(assetBill.getLoanTerm());

            RealSearchReq realSearchReq = new RealSearchReq();
            realSearchReq.setLoanChannel(assetBill.getLoanChannelId());
            realSearchReq.setRealRepayDate(key);
            realSearchReq.setRealSearchParamList(Lists.newArrayList(realSearchParam));

            int count = 0;
            do {
                Map<String, List<RealSearchRsp>> stringListMap = platformFileService.searchCapitalRealRepay(realSearchReq);
                inner.addAll(Optional.ofNullable(stringListMap.get(assetBill.getAssetId())).orElse(Lists.newArrayList()));
                realSearchReq.setRealRepayDate(DateUtils.addDays(key, ++count));
            } while (Constant.ENHANCE_SWITCH.get() && count < Calendar.DAY_OF_WEEK && inner.isEmpty());
            return inner;
        } finally {
            innerCountDownLatch.countDown();
        }
    }

    private void getOtherExpectList(List<AssetBill> assetBillList, CountDownLatch countDownLatch) {
        try {
            AssetBill assetBill = assetBillList.get(0);

            ExpectSearchReq expectSearchReq = new ExpectSearchReq();
            expectSearchReq.setLoanChannel(assetBill.getLoanChannelId());
            expectSearchReq.setPaymentTime(assetBill.getPaymentTime());
            expectSearchReq.setExpectSearchParamList(Lists.newArrayList(new ExpectSearchReq.ExpectSearchParam(assetBill.getAssetId(), assetBill.getLoanTerm())));


            int count = 0;
            List<ExpectSearchRsp> expectSearchRsps = Lists.newArrayList();
            do {
                Map<String, List<ExpectSearchRsp>> capitalExpectRepay = platformFileService.searchCapitalExpectRepay(expectSearchReq);
                expectSearchRsps.addAll(Optional.ofNullable(capitalExpectRepay.get(assetBill.getAssetId())).orElse(Lists.newArrayList()));
                expectSearchReq.setPaymentTime(DateUtils.addDays(assetBill.getPaymentTime(), ++count));
            } while (Constant.ENHANCE_SWITCH.get() && count < Calendar.DAY_OF_WEEK && expectSearchRsps.isEmpty());

            Collections.sort(expectSearchRsps);
            searchController.otherExpectBill.setText(JSON.toJSONStringWithDateFormat(expectSearchRsps, Constant.DATE_FORMAT, SerializerFeature.PrettyFormat));

        } catch (Exception e) {
            searchController.otherExpectBill.setText(e.getMessage());
        } finally {
            countDownLatch.countDown();
        }
    }
}
