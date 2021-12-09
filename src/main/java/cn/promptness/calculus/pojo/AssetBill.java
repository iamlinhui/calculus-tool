package cn.promptness.calculus.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class AssetBill implements Comparable<AssetBill> {

    /**
     * 账单ID
     */
    private String billId;
    /**
     * 资产ID
     */
    private String assetId;

    /**
     * 支付金额（毫）
     */
    private Long paymentAmount;

    /**
     * 前端订单号
     */
    private String cusOrderId;
    /**
     * 资金方渠道号
     */
    private Integer loanChannelId;
    /**
     * 资金方订单号
     */
    private String capitalOrderId;
    /**
     * 资产贷款金额
     */
    private Long loanAmount;
    /**
     * 资产期数
     */
    private Integer loanTerm;
    /**
     * 资产放款时间
     */
    private Date paymentTime;
    /**
     * 账单期数
     */
    private Integer repayTerm;
    /**
     * 账单应还金额（所有资金账户）
     */
    private Long expectRepayAmount;
    /**
     * 账单应还款日
     */
    private Date expectRepayDate;
    /**
     * 账单应还本金
     */
    private Long expectRepayPrincipal;
    /**
     * 账单应还利息
     */
    private Long expectRepayFee;


    /**
     * 账单实还金额（所有资金账户）
     */
    private Long realRepayAmount;
    /**
     * 账单实还款日
     */
    private Date realRepayDate;
    /**
     * 账单实还本金
     */
    private Long realRepayPrincipal;
    /**
     * 账单实还利息
     */
    private Long realRepayFee;
    /**
     * 账单实还罚息
     */
    private Long realRepayMulct;

    /**
     * 账单实还款类型（0-未还，10-正常还款，20-部分提前还，30-全部提前还，40-逾期还款，50-坏账代偿）
     */
    private String realRepayType;


    /**
     * 用户实还金额（所有资金账户）
     */
    private Long userRepayAmount;
    /**
     * 用户实还款日
     */
    private Date userRepayDate;
    /**
     * 用户实还本金
     */
    private Long userRepayPrincipal;
    /**
     * 用户实还利息
     */
    private Long userRepayFee;
    /**
     * 用户实还利息
     */
    private Long userRepayMuclt;

    /**
     * 用户实还款类型（0-未还，10-正常还款，20-部分提前还，30-全部提前还，40-逾期还款，50-坏账代偿）
     */
    private String userRepayType;


    /**
     * 年利率(基数:1000000)
     */
    private Long repayRate;
    /**
     * 罚息利率(基数:1000000)
     */

    private Long mulctRate;

    private Integer repaySourceType;

    private String repaySourceId;

    private Integer orderType;

    private Integer subOrderType;

    @Override
    public int compareTo(AssetBill o) {
        return this.getRepayTerm().compareTo(o.getRepayTerm());
    }
}
