package cn.promptness.calculus.pojo;

import java.util.Date;

public class FileRecord {

    private Integer fid;

    private Integer ftype;

    private String fplatformFileUrl;

    private String fplatformFileMd5;

    private String fcapitalFileUrl;

    private String fcapitalFileMd5;

    private Date fdate;

    private Integer floanChannelId;

    private Integer fbatchNo;

    private Integer fversion;

    private Date fcreateTime;

    private Date fmodifyTime;

    private Integer fstatus;

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public Integer getFtype() {
        return ftype;
    }

    public void setFtype(Integer ftype) {
        this.ftype = ftype;
    }

    public String getFplatformFileUrl() {
        return fplatformFileUrl;
    }

    public void setFplatformFileUrl(String fplatformFileUrl) {
        this.fplatformFileUrl = fplatformFileUrl;
    }

    public String getFplatformFileMd5() {
        return fplatformFileMd5;
    }

    public void setFplatformFileMd5(String fplatformFileMd5) {
        this.fplatformFileMd5 = fplatformFileMd5;
    }

    public String getFcapitalFileUrl() {
        return fcapitalFileUrl;
    }

    public void setFcapitalFileUrl(String fcapitalFileUrl) {
        this.fcapitalFileUrl = fcapitalFileUrl;
    }

    public String getFcapitalFileMd5() {
        return fcapitalFileMd5;
    }

    public void setFcapitalFileMd5(String fcapitalFileMd5) {
        this.fcapitalFileMd5 = fcapitalFileMd5;
    }

    public Date getFdate() {
        return fdate;
    }

    public void setFdate(Date fdate) {
        this.fdate = fdate;
    }

    public Integer getFloanChannelId() {
        return floanChannelId;
    }

    public void setFloanChannelId(Integer floanChannelId) {
        this.floanChannelId = floanChannelId;
    }

    public Integer getFbatchNo() {
        return fbatchNo;
    }

    public void setFbatchNo(Integer fbatchNo) {
        this.fbatchNo = fbatchNo;
    }

    public Integer getFversion() {
        return fversion;
    }

    public void setFversion(Integer fversion) {
        this.fversion = fversion;
    }

    public Date getFcreateTime() {
        return fcreateTime;
    }

    public void setFcreateTime(Date fcreateTime) {
        this.fcreateTime = fcreateTime;
    }

    public Date getFmodifyTime() {
        return fmodifyTime;
    }

    public void setFmodifyTime(Date fmodifyTime) {
        this.fmodifyTime = fmodifyTime;
    }

    public Integer getFstatus() {
        return fstatus;
    }

    public void setFstatus(Integer fstatus) {
        this.fstatus = fstatus;
    }
}
