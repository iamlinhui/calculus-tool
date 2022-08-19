package cn.promptness.calculus.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderPool {

    private boolean isActive;
    private boolean isDeleted;
    private String groupId;
    private String createdBy;
    private String updatedBy;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;
    private Integer version;

    // 订单号
    private String orderId;

    // 资产ID（资管流水号）
    private String assetId;

    // 资产方订单ID
    private String cusOrderId;

    // 订单类型（1:自营 2:非自营 3:取现订单 5:账单类订单)
    private Short orderType;

    /**
     * 0 => 空订单类型 ,10=> 自营商品 ,20=> 取现 ,40=> 商家付款 //在商户消费 ,50=> 帐单分期 60=> 商户贷款
     * ,80=> pop第三方商品 ,90=> 账单延期还款订单 ,100=> 激活流程 ,110=> 全额还款信用卡 120=>
     * 使用信用钱包额度分期还款信用卡 ,130=> 乐助学学杂费 ,140=> 乐助学生活费 ,150=> 乐花订单 ,160=> 债务重组
     */
    private Integer actualOrderType;

    /**
     * 渠道
     */
    private Integer saleType;

    // 资金方返回流水号
    private String capitalOrderId;

    // 计划ID
    private String capitalPlanId;

    // 资金渠道
    private Integer loanChannelId;

    // 渠道名称
    private String loanChannelName;

    // 贷款金额
    private BigDecimal loanAmount;

    // 贷款状态（1:待付款 2:付款中 3:付款成功 4:付款失败）
    private Short loanStatus;

    // 收款人userId
    private Integer userId;

    // 收款人uid
    private Integer uid;

    // 收款人账号
    private String inAcctNo;

    // 收款人账户名称
    private String inAcctName;

    // 收款人开户行名称简写
    private String inAcctBank;

    // 付款人账号
    private String outAcctNo;

    // 付款人名称
    private String outAcctName;

    // 下单日期
    private Date orderDate;

    // 付款日期
    private Date paymentDate;

    /**
     * 请求支付次数
     */
    private Integer payReqTimes;

    /**
     * 鼎盛与资产方利率
     */
    private Integer assetRate;

    /**
     * 鼎盛与资金方利率
     */
    private Integer capitalRate;


    // 银行状态
    private String bankStatus;

    /**
     * 贷款期数
     */
    private Short loanTerm;

    /**
     * 是否特殊时效要求(0.否 1.是)
     */
    private Short isNeedQuick;

    /**
     * 实还账单数量
     */
    private Short realBillNum;

    /**
     * 预计账单数量
     */
    private Short expectBillNum;

    // 发起付款时间
    private Date applyPayTime;

    /**
     * paymentManager
     */
    // 商户简写
    private String name;

    /**
     * 协议通道（1：专用版通道 2：授权版通道）
     */
    private Short protocolChannelId;

    // 借款人姓名
    private String userName;

    // 借款人身份证
    private String identiNo;

    // 用户手机号
    private String mobileNo;

    // 开户行
    private String bankName;

    // 银行简写
    private String bankNameShort;

    // 联行号
    private String bankCode;

    // 计划名称
    private String capitalPlanName;

    // 是否重发
    private boolean repayApply;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    // 资金方用户账户用户编码（ID）
    private String loanAccount;

    /**
     * 付款失败拒绝码
     */
    private String refuseCode;

    /**
     * 修改时间 最小
     */
    private Date startModifyTime;

    /**
     * 修改时间 最大
     */

    private Date endModifyTime;


    /**
     * 付款时间 最小
     */
    private Date minPaymentDate;

    /**
     * 付款时间 最大
     */
    private Date maxPaymentDate;


    /**
     * 最小资产ID
     */
    private String sfMinAssetId;

    /**
     * 最大资产ID
     */
    private String sfMaxAssetId;

    /**
     * 资金渠道
     */
    private List<Integer> sfLoanChannelIds;

    /**
     * 排序字段
     */
    private String sfOrderField;

    /**
     * 担保模式字段（账单模式，0-旧模式，1-出表模式，2-出表模式，不接收平台账单） #3-桔子结算(作废)
     */
    private Integer postloanOrderType;

    /**
     * 担保模式查询条件字段
     */
    private List<Integer> postloanOrderTypes;

    /**
     * 首期还款日
     */
    private Date firstRepayDate;

    /**
     * 延迟审核类型
     */
    private Short delayType;

    /**
     * 外部错误码
     */
    private String outerErrorCode;

    /**
     * 平台错误码
     */
    private String platformErrorCode;

    /**
     * 标记字段
     */
    private Integer innerFlag;
}
