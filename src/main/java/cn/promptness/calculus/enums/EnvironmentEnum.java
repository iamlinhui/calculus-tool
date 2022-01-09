package cn.promptness.calculus.enums;

import cn.promptness.calculus.data.Constant;

/**
 * 环境切换列表
 *
 * @author lynn
 * @date 2022/1/4 13:59
 * @since v1.0.0
 */
public enum EnvironmentEnum{

    /**
     *
     */
    STABLE            ("stable", "测试环境"),
    PRE               ("pre", "预发布环境"),
    POD               ("pod", "生产环境");

    EnvironmentEnum(String label, String desc) {
        this.label = label;
        this.desc = desc;
    }


    final String label;

    final String desc;

    public static EnvironmentEnum getInstance(String activeProfiles) {
        for (EnvironmentEnum environmentEnum : EnvironmentEnum.values()) {
            if (environmentEnum.getLabel().equals(activeProfiles)) {
                return environmentEnum;
            }
        }
        throw new RuntimeException("ERROR PROFILE");
    }

    public String getLabel() {
        return label;
    }

    public String getFullDesc() {
        return String.format("%s-%s", Constant.TITLE, desc);
    }

    public String getDesc() {
        return desc;
    }
}
