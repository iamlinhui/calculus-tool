package cn.promptness.calculus.enums;

import java.io.Serializable;

/**
 * @author lynn
 * @date 2020/9/14 14:51
 * @since v1.0.0
 */
public interface BaseEnum extends Serializable {

    /**
     * code 码
     *
     * @return code
     * @date 2020/9/14 14:51
     * @since v1.0.0
     */
    int getCode();


    /**
     * 描述
     *
     * @return desc
     * @author lynn
     * @date 2020/9/14 14:51
     * @since v1.0.0
     */
    String getDesc();

}
