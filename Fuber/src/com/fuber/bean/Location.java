package com.fuber.bean;

public class Location {
	private Double lat;
	private Double longi;

	public Location(Double pLat, Double pLongi) {
		lat = pLat;
		longi = pLongi;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLongi() {
		return longi;
	}

	public void setLongi(Double longi) {
		this.longi = longi;
	}
}
