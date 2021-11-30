package cn.promptness.calculus.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class ExpectSearchRsp implements Comparable<ExpectSearchRsp>{

    private String assetId;

    private Date paymentTime;

    /**
     * 应还款日
     */
    private Date expectRepayDate;

    private Integer loanChannelId;

    private Integer repayTerm;

    private Long expectRepayAmount;

    private Long expectRepayPrincipal;

    private Long expectRepayFee;

    @Override
    public int compareTo(ExpectSearchRsp o) {
        return this.getRepayTerm().compareTo(o.getRepayTerm());
    }
}
