package com.example.Calendar.data;

public class DateInfo {
	private int date;
	private boolean isThisMonth;
	private boolean isWeekend;
	private boolean isHoliday;
	private String NongliDate;
	private String detailLunar;
	public int getDate() {
		return date;
	}
	public void setDate(int date) {
		this.date = date;
	}
	public String getNongliDate() {
		return NongliDate;
	}
	public void setNongliDate(String nongliDate) {
		NongliDate = nongliDate;
	}
	public boolean isThisMonth() {
		return isThisMonth;
	}
	public void setThisMonth(boolean isThisMonth) {
		this.isThisMonth = isThisMonth;
	}
	public boolean isHoliday() {
		return isHoliday;
	}
	public void setHoliday(boolean isHoliday) {
		this.isHoliday = isHoliday;
	}
	public boolean isWeekend() {
		return isWeekend;
	}
	public void setWeekend(boolean isWeekend) {
		this.isWeekend = isWeekend;
	}
	public String getDetailLunar(){
		return detailLunar;
	}
	public void setDetailLunar(String detailLunar){
		this.detailLunar = detailLunar;
	}
}
