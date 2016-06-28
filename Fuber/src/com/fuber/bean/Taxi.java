package com.fuber.bean;

import java.time.Instant;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Taxi {
	private String id;
	private String name;
	private String phone;
	private Double longi;
	private Double lat;
	private Boolean isPink;
	private Boolean isAvailable;
	private Double customerLat;
	private Double customerLong;

	public Double getCustomerLat() {
		return customerLat;
	}

	public void setCustomerLat(Double customerLat) {
		this.customerLat = customerLat;
	}

	public Double getCustomerLong() {
		return customerLong;
	}

	public void setCustomerLong(Double customerLong) {
		this.customerLong = customerLong;
	}

	private Instant tripStart;
	private Instant tripEnd;

	public Instant getTripStart() {
		return tripStart;
	}

	public void setTripStart(Instant tripStart) {
		this.tripStart = tripStart;
	}

	public Instant getTripEnd() {
		return tripEnd;
	}

	public void setTripEnd(Instant tripEnd) {
		this.tripEnd = tripEnd;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Double getLongi() {
		return longi;
	}

	public void setLongi(Double longi) {
		this.longi = longi;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
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
