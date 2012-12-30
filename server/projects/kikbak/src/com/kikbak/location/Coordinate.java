package com.kikbak.location;

public class Coordinate {
	final private double latDeg;
	final private double longDeg;
	final private double latRad;
	final private double longRad;
	
	public Coordinate(final double latitude, final double longitude){
		this.latDeg = latitude;
		this.longDeg = longitude;
		this.latRad = Math.toRadians(latitude);
		this.longRad = Math.toRadians(longitude);
	}

	public double getLatDeg() {
		return latDeg;
	}

	public double getLongDeg() {
		return longDeg;
	}

	public double getLatRad() {
		return latRad;
	}

	public double getLongRad() {
		return longRad;
	}	

	
}
