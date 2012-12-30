package com.kikbak.location;

public class GeoBoundaries {
	
	private static final double MIN_LAT = Math.toRadians(-90d);  // -PI/2
	private static final double MAX_LAT = Math.toRadians(90d);   //  PI/2
	private static final double MIN_LON = Math.toRadians(-180d); // -PI
	private static final double MAX_LON = Math.toRadians(180d);
	
	//earh radius
	// 6371.01 km
	// 3958.76 Miles
	private static final double earthRadius = 3958.76;

	public static GeoFence getGeoFence(Coordinate origin, Double distanceMiles){
	
		if( distanceMiles < 0d)
			throw new IllegalArgumentException();

		// angular distance in radians on a great circle
		double radDist = distanceMiles / earthRadius;

		double minLat = origin.getLatRad() - radDist;
		double maxLat = origin.getLatRad() + radDist;

		double minLon, maxLon;
		if (minLat > MIN_LAT && maxLat < MAX_LAT) {
			double deltaLon = Math.asin(Math.sin(radDist) /
				Math.cos(origin.getLatRad()));
			minLon = origin.getLongRad() - deltaLon;
			if (minLon < MIN_LON) minLon += 2d * Math.PI;
			maxLon = origin.getLongRad() + deltaLon;
			if (maxLon > MAX_LON) maxLon -= 2d * Math.PI;
		} else {
			// a pole is within the distance
			minLat = Math.max(minLat, MIN_LAT);
			maxLat = Math.min(maxLat, MAX_LAT);
			minLon = MIN_LON;
			maxLon = MAX_LON;
		}

		return new GeoFence(Math.toDegrees(maxLat), Math.toDegrees(minLat), Math.toDegrees(maxLon), Math.toDegrees(minLon));
	}
	
}
