package cn.promptness.calculus.pojo;


import lombok.Data;

import java.util.Date;

@Data
public class RealSearchRsp implements Comparable<RealSearchRsp>{

    private Integer loanChannelId;

    private String assetId;

    /**
     * 结算日
     */
    private Date realRepayDate;

    private Integer repayTerm;

    private Long realRepayAmount;

    private Long realRepayPrincipal;

    private Long realRepayFee;

    private Long realRepayMulct;

    private Integer realRepayType;

    @Override
    public int compareTo(RealSearchRsp o) {
        return this.getRepayTerm().compareTo(o.getRepayTerm());
    }
}
