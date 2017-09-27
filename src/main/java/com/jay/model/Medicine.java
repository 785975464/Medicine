package com.jay.model;

/**
 * Created by Jay on 2017/6/21.
 */
public class Medicine {
    private Integer id;

    private String locationid;

    private String name;

    private String cas;

    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocationid() {
        return locationid;
    }

    public void setLocationid(String locationid) {
        this.locationid = locationid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCas() {
        return cas;
    }

    public void setCas(String cas) {
        this.cas = cas;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + id +
                ", locationid='" + locationid + '\'' +
                ", name='" + name + '\'' +
                ", cas='" + cas + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
