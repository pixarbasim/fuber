package com.fuber.bean;

import java.time.Instant;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Taxi {
	private String name;
	private String phone;
	private Location taxiLocation;
	private Boolean isPink;
	private Boolean isAvailable;
	private Location tripStartLocation;
	private Instant tripStartTime;

	public Taxi() {

	}

	public Taxi(String name, String phone, Location taxiLocation, Boolean isPink, Boolean isAvailable) {
		super();
		this.name = name;
		this.phone = phone;
		this.taxiLocation = taxiLocation;
		this.isPink = isPink;
		this.isAvailable = isAvailable;
	}

	public Taxi(Taxi pTaxi) {
		name = pTaxi.getName();
		phone = pTaxi.getPhone();
		taxiLocation = pTaxi.getTaxiLocation();
		isPink = pTaxi.getIsPink();
		isAvailable = pTaxi.getIsAvailable();
		tripStartLocation = pTaxi.getTripStartLocation();
		tripStartTime = pTaxi.getTripStartTime();
	}

	public Instant getTripStartTime() {
		return tripStartTime;
	}

	public void setTripStartTime(Instant tripStart) {
		this.tripStartTime = tripStart;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Location getTaxiLocation() {
		return taxiLocation;
	}

	public void setTaxiLocation(Location taxiLocation) {
		this.taxiLocation = taxiLocation;
	}

	public Location getTripStartLocation() {
		return tripStartLocation;
	}

	public void setTripStartLocation(Location tripStartLocation) {
		this.tripStartLocation = tripStartLocation;
	}

	public Boolean getIsPink() {
		return isPink;
	}

	public void setIsPink(Boolean isPink) {
		this.isPink = isPink;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
}
