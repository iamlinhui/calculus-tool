package cn.promptness.calculus.pojo;

import cn.promptness.calculus.enums.FileRecordTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class LocalDbFile implements Serializable, Comparable<LocalDbFile> {

    private static final long serialVersionUID = 3765889654736572884L;

    /**
     * 渠道号
     */
    private Integer loanChannelId;

    /**
     * DB文件夹路径
     */
    private String basePath;

    /**
     * 文件日期
     */
    private Date businessDate;

    /**
     * 数据类型
     */
    private FileRecordTypeEnum fileRecordTypeEnum;

    /**
     * DB文件生成时间
     */
    private Date initTime;

    /**
     * 访问命中时间
     */
    private Date hitTime;

    /**
     * Table key
     *
     * @author lynn
     * @date 2021/9/20 0:36
     * @since v1.0.0
     */
    public String getKey() {
        return String.format("%s-%s", loanChannelId, fileRecordTypeEnum.name());
    }

    public String getDbPath() {
        return basePath + "/db";
    }

    public String getLockPath() {
        return basePath + "/lock.ok";
    }

    @Override
    public int compareTo(LocalDbFile o) {
        return this.getHitTime().compareTo(o.getHitTime());
    }
}
