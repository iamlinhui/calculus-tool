package cn.promptness.calculus.enums;

import cn.promptness.calculus.data.Constant;

public enum EnvironmentEnum{

    /**
     *
     */
    STABLE            ("stable", "测试环境"),
    PRE               ("pre", "预发布环境"),
    POD               ("pod", "生产环境");

    EnvironmentEnum(String code, String desc) {
        this.label = code;
        this.desc = desc;
    }


    final String label;

    final String desc;

    public String getLabel() {
        return label;
    }

    public String getDesc() {
        return String.format("%s-%s", Constant.TITLE, desc);
    }
}
