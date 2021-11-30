package cn.promptness.calculus.enums;

/**
 * 差账文件类型
 *
 * @author lynn
 * @date 2021/8/15 15:25
 * @since v1.0.0
 */
public enum FileRecordTypeEnum implements BaseEnum {

    /**
     * 文件类型 0 其他,1 还款计划,2 实际还款
     */
    UNKNOWN            (0, "其他"),
    EXPECT             (1, "还款计划"),
    REAL               (2, "实际还款");


    FileRecordTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    int code;

    String desc;


    @Override
    public int getCode() {

        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }


    public static FileRecordTypeEnum getInstance(int code) {
        for (FileRecordTypeEnum value : FileRecordTypeEnum.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }
}
