package com.jay.model;

/**
 * Created by Jay on 2017/6/21.
 */
public class Medicine {
    private Integer id;

    private String medicineid;

    private String name;

    private String cas;

    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMedicineid() {
        return medicineid;
    }

    public void setMedicineid(String medicineid) {
        this.medicineid = medicineid;
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
                ", medicineid='" + medicineid + '\'' +
                ", name='" + name + '\'' +
                ", cas='" + cas + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
