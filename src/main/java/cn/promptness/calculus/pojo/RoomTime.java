package cn.promptness.calculus.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class RoomTime {

    /**
     * 1  时间段可用 0 此时间段不可用
     */
    private Integer flag;
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("end_time")
    private String endTime;

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoomTime roomTime = (RoomTime) o;
        return Objects.equals(flag, roomTime.flag) && Objects.equals(startTime, roomTime.startTime) && Objects.equals(endTime, roomTime.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flag, startTime, endTime);
    }
}
