package com.fuber.util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fuber.bean.Location;
import com.fuber.bean.Taxi;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DatabaseAdaptor {
	public static DBObject prepareTaxiSearchObject(Boolean pTaxiAvailable, Boolean pIsPinkTaxiRequested) {
		DBObject taxiObject = new BasicDBObject();
		if (pTaxiAvailable) {
			((BasicDBObject) taxiObject).append(Constants.DatabaseConstant.TAXI_COL_AVAILABLE, true);
		}
		if (pIsPinkTaxiRequested) {
			((BasicDBObject) taxiObject).append(Constants.DatabaseConstant.TAXI_COL_PINK, true);
		}
		return taxiObject;
	}

	public static final DBObject transformTaxiToDBObject(Taxi pTaxi) {
		DBObject taxiObject = new BasicDBObject(Constants.DatabaseConstant.TAXI_COL_NAME, pTaxi.getName())
				.append(Constants.DatabaseConstant.TAXI_COL_PINK, pTaxi.getIsPink())
				.append(Constants.DatabaseConstant.TAXI_COL_TAXI_LAT, pTaxi.getTaxiLocation().getLat())
				.append(Constants.DatabaseConstant.TAXI_COL_TAXI_LONG, pTaxi.getTaxiLocation().getLongi())
				.append(Constants.DatabaseConstant.TAXI_COL_PHONE, pTaxi.getPhone())
				.append(Constants.DatabaseConstant.TAXI_COL_AVAILABLE, pTaxi.getIsAvailable());
		if (!pTaxi.getIsAvailable()) {
			((BasicDBObject) taxiObject)
					.append(Constants.DatabaseConstant.TAXI_COL_TRIP_START, pTaxi.getTripStartTime().toString())
					.append(Constants.DatabaseConstant.TAXI_COL_TRIP_START_LAT, pTaxi.getTripStartLocation().getLat())
					.append(Constants.DatabaseConstant.TAXI_COL_TRIP_START_LONG, pTaxi.getTripStartLocation().getLongi());
		} else {
			((BasicDBObject) taxiObject).append(Constants.DatabaseConstant.TAXI_COL_TRIP_START, null)
					.append(Constants.DatabaseConstant.TAXI_COL_TRIP_START_LAT, null)
					.append(Constants.DatabaseConstant.TAXI_COL_TRIP_START_LONG, null);
		}
		return taxiObject;
	}

	public static List<Taxi> transformTaxiDBObjectListToTaxiList(List<DBObject> pTaxiDBObjectList) {
		List<Taxi> taxiList = new ArrayList<Taxi>();
		for (DBObject dbObject : pTaxiDBObjectList) {
			taxiList.add(transformTaxiDBObjectToTaxi(dbObject));
		}
		return taxiList;
	}

	public static Taxi transformTaxiDBObjectToTaxi(DBObject taxiDBObject) {
		Taxi taxi = new Taxi();
		taxi.setName((String) taxiDBObject.get(Constants.DatabaseConstant.TAXI_COL_NAME));
		taxi.setTaxiLocation(new Location((Double) taxiDBObject.get(Constants.DatabaseConstant.TAXI_COL_TAXI_LAT),
				(Double) taxiDBObject.get(Constants.DatabaseConstant.TAXI_COL_TAXI_LONG)));
		taxi.setPhone((String) taxiDBObject.get(Constants.DatabaseConstant.TAXI_COL_PHONE));
		taxi.setIsPink((Boolean) taxiDBObject.get(Constants.DatabaseConstant.TAXI_COL_PINK));
		taxi.setIsAvailable((Boolean) taxiDBObject.get(Constants.DatabaseConstant.TAXI_COL_AVAILABLE));
		if (!taxi.getIsAvailable()) {
			taxi.setTripStartLocation(
					new Location((Double) taxiDBObject.get(Constants.DatabaseConstant.TAXI_COL_TRIP_START_LAT),
							(Double) taxiDBObject.get(Constants.DatabaseConstant.TAXI_COL_TRIP_START_LONG)));
			taxi.setTripStartTime(
					Instant.parse((String) taxiDBObject.get(Constants.DatabaseConstant.TAXI_COL_TRIP_START)));
		}
		return taxi;
	}
}
