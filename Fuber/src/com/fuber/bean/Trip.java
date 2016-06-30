package com.fuber.bean;

import java.time.Duration;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Trip {
	private String driverName;
	private String phone;
	private Location tripEndLocation;
	private Boolean isPink;
	private Location tripStartLocation;
	private Double tripCost;

	public Trip() {

	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Location getTripEndLocation() {
		return tripEndLocation;
	}

	public void setTripEndLocation(Location tripEndLocation) {
		this.tripEndLocation = tripEndLocation;
	}

	public Boolean getIsPink() {
		return isPink;
	}

	public void setIsPink(Boolean isPink) {
		this.isPink = isPink;
	}

	public Location getTripStartLocation() {
		return tripStartLocation;
	}

	public void setTripStartLocation(Location tripStartLocation) {
		this.tripStartLocation = tripStartLocation;
	}


	public Double getTripCost() {
		return tripCost;
	}

	public void setTripCost(Double tripCost) {
		this.tripCost = tripCost;
	}

	public Trip(String driverName, String phone, Location tripEndLocation, Boolean isPink, Location tripStartLocation,
			 Double tripCost) {
		this.driverName = driverName;
		this.phone = phone;
		this.tripEndLocation = tripEndLocation;
		this.isPink = isPink;
		this.tripStartLocation = tripStartLocation;
		this.tripCost = tripCost;
	}

}
