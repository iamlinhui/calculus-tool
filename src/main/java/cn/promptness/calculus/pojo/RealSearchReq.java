package cn.promptness.calculus.pojo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RealSearchReq {

    private Integer loanChannel;

    /**
     * 结算日期
     */
    private Date realRepayDate;

    private List<RealSearchParam> realSearchParamList;

    @Data
    public static class RealSearchParam {

        /**
         * 资产号
         */
        private String assetId;

        /**
         * 期数
         */
        private Integer minRepayTerm;

        private Integer maxRepayTerm;

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            final RealSearchParam realSearchParam = (RealSearchParam) obj;
            if (this == realSearchParam) {
                return true;
            } else {
                return this.assetId.equals(realSearchParam.assetId);
            }
        }

        @Override
        public int hashCode() {
            return assetId.hashCode();
        }
    }
}
