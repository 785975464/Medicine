package com.jay.model;

import java.util.Date;

/**
 * Created by Jay on 2017/6/21.
 */
public class Inventory {
    private Integer id;

    private String goodsid;

    private Date createtime;

    private String creator;

    private String medicinename;

    private String producer;

    private String brand;

    private String pack;

    private String productid;

    private String price;

    private Integer amount;

    private Integer remain;

    private String totalquantity;

    private String notes;

    private String usequantity;

    private String recyclequantity;

    private String receivequantity;

    private String orderid;

    private String msds;

    private String location;

    private String cas;

    private Double molweight;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(String goodsid) {
        this.goodsid = goodsid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getMedicinename() {
        return medicinename;
    }

    public void setMedicinename(String medicinename) {
        this.medicinename = medicinename;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getRemain() {
        return remain;
    }

    public void setRemain(Integer remain) {
        this.remain = remain;
    }

    public String getTotalquantity() {
        return totalquantity;
    }

    public void setTotalquantity(String totalquantity) {
        this.totalquantity = totalquantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUsequantity() {
        return usequantity;
    }

    public void setUsequantity(String usequantity) {
        this.usequantity = usequantity;
    }

    public String getRecyclequantity() {
        return recyclequantity;
    }

    public void setRecyclequantity(String recyclequantity) {
        this.recyclequantity = recyclequantity;
    }

    public String getReceivequantity() {
        return receivequantity;
    }

    public void setReceivequantity(String receivequantity) {
        this.receivequantity = receivequantity;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getMsds() {
        return msds;
    }

    public void setMsds(String msds) {
        this.msds = msds;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCas() {
        return cas;
    }

    public void setCas(String cas) {
        this.cas = cas;
    }

    public Double getMolweight() {
        return molweight;
    }

    public void setMolweight(Double molweight) {
        this.molweight = molweight;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", goodsid='" + goodsid + '\'' +
                ", createtime=" + createtime +
                ", creator='" + creator + '\'' +
                ", medicinename='" + medicinename + '\'' +
                ", producer='" + producer + '\'' +
                ", brand='" + brand + '\'' +
                ", pack='" + pack + '\'' +
                ", productid='" + productid + '\'' +
                ", price='" + price + '\'' +
                ", amount=" + amount +
                ", remain=" + remain +
                ", totalquantity='" + totalquantity + '\'' +
                ", notes='" + notes + '\'' +
                ", usequantity='" + usequantity + '\'' +
                ", recyclequantity='" + recyclequantity + '\'' +
                ", receivequantity='" + receivequantity + '\'' +
                ", orderid='" + orderid + '\'' +
                ", MSDS='" + msds + '\'' +
                ", location='" + location + '\'' +
                ", CAS='" + cas + '\'' +
                ", molweight=" + molweight +
                '}';
    }
}
