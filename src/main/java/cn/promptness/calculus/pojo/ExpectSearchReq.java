package cn.promptness.calculus.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
public class ExpectSearchReq {

    private Integer loanChannel;

    /**
     * 放款日期
     */
    private Date paymentTime;

    private List<ExpectSearchParam> expectSearchParamList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExpectSearchParam {
        /**
         * 资产号
         */
        private String assetId;

        /**
         * 期数
         */
        private Integer loanTerm;
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            final ExpectSearchParam expectSearchParam = (ExpectSearchParam) obj;
            if (this == expectSearchParam) {
                return true;
            } else {
                return this.assetId.equals(expectSearchParam.assetId);
            }
        }
        @Override
        public int hashCode() {
            return assetId.hashCode();
        }

    }
}
