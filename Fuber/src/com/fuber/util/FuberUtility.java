package com.fuber.util;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.fuber.bean.Location;
import com.fuber.bean.Taxi;

public class FuberUtility {
	/**
	 * Method to calculate distance between two latitude/longitude using
	 * pythagoras theorem
	 * 
	 * @param pSourceLat
	 *            Source latitude
	 * @param pSourceLongi
	 *            Source longitude
	 * @param pDestinationLat
	 *            Destination latitude
	 * @param pDestinationLongi
	 *            Destination longitude
	 * @return distance
	 */
	public static Double getDistanceBetweenLocation(Location pSource, Location pDestination) {
		Double latDiff = pSource.getLat() - pDestination.getLat();
		Double longiDiff = pSource.getLongi() - pDestination.getLongi();
		return Math.sqrt(latDiff * latDiff + longiDiff * longiDiff);
	}

	/**
	 * From the given list of taxi objects, find the one closest to customer
	 * location.
	 * 
	 * @param pTaxiObjectList
	 *            List of Taxi DB Objects
	 * @param pCustomerLatitude
	 *            Customer Location Latitude
	 * @param pCustomerLongitude
	 *            Customer Location Longitude
	 * @param pIsPinkTaxiRequested
	 *            Does the customer request for a pink taxi only
	 * @return
	 */
	public static Taxi getNearestTaxi(List<Taxi> pTaxiObjectList, Location pCustomerLocation,
			Boolean pIsPinkTaxiRequested) {
		Taxi nearestTaxi = null;
		Double minDistance = Constants.MAX_DISTANCE_THRESHOLD;
		for (Taxi taxi : pTaxiObjectList) {
			Boolean isTaxiPink = (Boolean) taxi.getIsPink();
			if (!pIsPinkTaxiRequested || pIsPinkTaxiRequested && isTaxiPink) {
				Double distance = FuberUtility.getDistanceBetweenLocation(taxi.getTaxiLocation(), pCustomerLocation);
				if (distance < minDistance) {
					minDistance = distance;
					nearestTaxi = taxi;
				}
			}
		}
		return nearestTaxi;
	}

	public static Double calculateTripCharge(Location pTripStartLocation, Location pTripEndLocation,
			Instant pTripStartTime, Boolean pIsTaxiPink) {
		Double distanceTravelled = getDistanceBetweenLocation(pTripStartLocation, pTripEndLocation);
		Duration tripDuration = Duration.between(pTripStartTime, Instant.now());
		Double tripCharge = tripDuration.toMinutes() + distanceTravelled * 2;
		if (pIsTaxiPink) {
			tripCharge += 5.0;
		}
		return tripCharge;
	}

	public static Taxi updateTaxiAttributesForTripEnd(final Taxi pTaxi, Location pCustomerDropLocation) {
		Taxi taxi = new Taxi(pTaxi);
		taxi.setTaxiLocation(pCustomerDropLocation);
		taxi.setIsAvailable(true);
		taxi.setTripStartTime(null);
		return taxi;
	}

	public static Taxi updateTaxiAttributesForTripStart(final Taxi pTaxi, Location pCustomerPickupLocation) {
		Taxi taxi = new Taxi(pTaxi);
		taxi.setTripStartTime(Instant.now());
		taxi.setIsAvailable(false);
		taxi.setTripStartLocation(pCustomerPickupLocation);
		return taxi;
	}
}
