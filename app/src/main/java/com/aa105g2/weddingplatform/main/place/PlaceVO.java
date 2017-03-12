package com.aa105g2.weddingplatform.main.place;


import java.io.Serializable;


public class PlaceVO implements Serializable  {
    private String place_no;
    private String sup_no;
    private Integer place_type;
    private String place_name;
    private String place_area;
    private String place_adds;
    private byte[] place_pic;
    private String place_note;
    private Integer place_status;

    public String getPlace_no() {
        return place_no;
    }
    public void setPlace_no(String place_no) {
        this.place_no = place_no;
    }
    public String getSup_no() {
        return sup_no;
    }
    public void setSup_no(String sup_no) {
        this.sup_no = sup_no;
    }
    public Integer getPlace_type() {
        return place_type;
    }
    public void setPlace_type(Integer place_type) {
        this.place_type = place_type;
    }
    public String getPlace_name() {
        return place_name;
    }
    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }
    public String getPlace_area() {
        return place_area;
    }
    public void setPlace_area(String place_area) {
        this.place_area = place_area;
    }
    public String getPlace_adds() {
        return place_adds;
    }
    public void setPlace_adds(String place_adds) {
        this.place_adds = place_adds;
    }
    public byte[] getPlace_pic() {
		return place_pic;
	}
	public void setPlace_pic(byte[] place_pic) {
		this.place_pic = place_pic;
	}
    public String getPlace_note() {
        return place_note;
    }
    public void setPlace_note(String place_note) {
        this.place_note = place_note;
    }
    public Integer getPlace_status() {
        return place_status;
    }
    public void setPlace_status(Integer place_status) {
        this.place_status = place_status;
    }
}

