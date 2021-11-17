package cn.promptness.calculus.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class Room {

    @SerializedName("meeting_date")
    private String meetingDate;
    @SerializedName("meeting_id")
    private Integer meetingId;

    @SerializedName("start_time")
    private String startTime;
    @SerializedName("end_time")
    private String endTime;

    private Integer floor;
    @SerializedName("room_name")
    private String roomName;
    @SerializedName("room_id")
    private String roomId;
    private List<RoomTime> time;


    public String getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }

    public Integer getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Integer meetingId) {
        this.meetingId = meetingId;
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

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<RoomTime> getTime() {
        return time;
    }

    public void setTime(List<RoomTime> time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Room room = (Room) o;
        return Objects.equals(meetingDate, room.meetingDate) && Objects.equals(meetingId, room.meetingId) && Objects.equals(startTime, room.startTime) && Objects.equals(endTime, room.endTime) && Objects.equals(floor, room.floor) && Objects.equals(roomName, room.roomName) && Objects.equals(roomId, room.roomId) && Objects.equals(time, room.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meetingDate, meetingId, startTime, endTime, floor, roomName, roomId, time);
    }
}
