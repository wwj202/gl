package com.gl.subsidy.entity;

public class Subsidy {
	
	private int id;
	private String fldDate;
	private String fldCustomer;
	private int fldCount;
	private float fldPrice;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFldDate() {
		return fldDate;
	}
	public void setFldDate(String fldDate) {
		this.fldDate = fldDate;
	}
	public String getFldCustomer() {
		return fldCustomer;
	}
	public void setFldCustomer(String fldCustomer) {
		this.fldCustomer = fldCustomer;
	}
	public int getFldCount() {
		return fldCount;
	}
	public void setFldCount(int fldCount) {
		this.fldCount = fldCount;
	}
	public float getFldPrice() {
		return fldPrice;
	}
	public void setFldPrice(float fldPrice) {
		this.fldPrice = fldPrice;
	}

}
