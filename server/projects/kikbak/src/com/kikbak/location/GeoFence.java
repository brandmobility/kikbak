package com.kikbak.location;

public class GeoFence {

	final Coordinate northWest;
	final Coordinate northEast;
	final Coordinate southWest;
	final Coordinate southEast;
	
	public GeoFence(double maxLatitude, double minLatitude, double maxLongitude, double minLongitude){
		northWest = new Coordinate(maxLatitude, maxLongitude);
		northEast = new Coordinate(maxLatitude, minLongitude);
		southWest = new Coordinate(minLatitude, maxLongitude);
		southEast = new Coordinate(minLatitude, minLongitude);
	}
	
	public Coordinate getFenceCoordinate(GeoFenceCoordinateType type){
		
		if(type == GeoFenceCoordinateType.NorthEast){
			return northEast;
		}
		else if( type == GeoFenceCoordinateType.NorthWest){
			return northWest;
		}
		else if( type == GeoFenceCoordinateType.SouthEast){
			return southEast;
		}
		
		return southWest;
	}
	
	
}
