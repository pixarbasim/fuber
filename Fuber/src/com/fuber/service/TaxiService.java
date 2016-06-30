package com.fuber.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fuber.bean.Location;
import com.fuber.bean.Taxi;
import com.fuber.bean.Trip;
import com.fuber.database.TaxiDao;
import com.fuber.errorhandling.FuberException;
import com.fuber.util.Constants;
import com.fuber.util.DatabaseAdaptor;
import com.fuber.util.FuberUtility;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoException;

@Path("/taxiservice")
public class TaxiService {

	private static final int STATUS_NOT_FOUND = 304;

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/listtaxi")
	public Response listTaxi() throws FuberException {
		List<Taxi> taxiList = getTaxiListFromDB(false, false);
		GenericEntity<List<Taxi>> entity = new GenericEntity<List<Taxi>>(taxiList) {
		};
		return Response.ok(entity).build();
	}

	@POST
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Path("/booktaxi")
	public Response bookTaxi(@FormParam("lat") Double pPickupLocationLat, @FormParam("long") Double pPickupLocationLong,
			@FormParam("pink") boolean pIsPink) throws FuberException {
		if (pPickupLocationLat == null || pPickupLocationLong == null) {
			throw new FuberException(400, 400, "Insufficient data", "Check headers and http request format");
		}
		// Find the nearest taxi if available
		Location pickupLocation = new Location(pPickupLocationLat, pPickupLocationLong);
		Taxi taxi = FuberUtility.getNearestTaxi(getTaxiListFromDB(true, pIsPink), pickupLocation, pIsPink);

		if (taxi == null) {
			throw new FuberException(204, 204, "Taxi not available", "Try Later :(");
		}
		updateTaxiInDB(FuberUtility.updateTaxiAttributesForTripStart(taxi, pickupLocation));
		return Response.ok(taxi).build();
	}

	@POST
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/endtrip")
	public Response endTrip(@FormParam("phone") String pDriverPhoneNo, @FormParam("lat") Double pDropLocationLat,
			@FormParam("long") Double pDropLocationLong) throws FuberException {

		if (pDriverPhoneNo == null || pDropLocationLat == null || pDropLocationLong == null) {
			throw new FuberException(400, 400, "Insufficient data.", "Check headers and http request format");
		}
		Taxi taxi = findTaxiInDatabaseByDriverPhoneNo(pDriverPhoneNo, false);
		if (taxi == null) {
			throw new FuberException(204, 204, "Taxi in-trip not found with phone no:" + pDriverPhoneNo,
					"Please verify taxi with phone no [" + pDriverPhoneNo + "] is in trip.");
		}
		Location tripEndLocation = new Location(pDropLocationLat, pDropLocationLong);
		Double totalTripCost = FuberUtility.calculateTripCharge(taxi.getTripStartLocation(), tripEndLocation,
				taxi.getTripStartTime(), taxi.getIsPink());
		Taxi targetTaxi = FuberUtility.updateTaxiAttributesForTripEnd(taxi, tripEndLocation);
		updateTaxiInDB(targetTaxi);
		return Response.status(200).entity(new Trip(taxi.getName(), taxi.getPhone(), tripEndLocation, taxi.getIsPink(),
				taxi.getTripStartLocation(), totalTripCost)).build();

	}

	private Taxi findTaxiInDatabaseByDriverPhoneNo(String pDriverPhoneNo, Boolean pIsAvailable) throws FuberException {
		Taxi taxi = null;
		BasicDBObject taxiDBObject = new BasicDBObject(Constants.DatabaseConstant.TAXI_COL_PHONE, pDriverPhoneNo)
				.append(Constants.DatabaseConstant.TAXI_COL_AVAILABLE, pIsAvailable);
		DBCursor cursor = TaxiDao.Taxi.getTaxiCollection().find(taxiDBObject);
		if (cursor.hasNext()) {
			taxi = DatabaseAdaptor.transformTaxiDBObjectToTaxi(cursor.one());
		}
		return taxi;
	}

	private void updateTaxiInDB(Taxi pTaxi) throws FuberException {
		try {
			TaxiDao.Taxi.getTaxiCollection().update(
					new BasicDBObject(Constants.DatabaseConstant.TAXI_COL_PHONE, pTaxi.getPhone()),
					DatabaseAdaptor.transformTaxiToDBObject(pTaxi));
		} catch (MongoException e) {
			throw new FuberException(STATUS_NOT_FOUND, STATUS_NOT_FOUND,
					"Failed to update trip. Check taxi with phone " + pTaxi.getPhone() + " exists", "Check DB");
		}
	}

	/**
	 * Method to find list of taxis with the given parameters
	 * 
	 * @param pIsAvailable
	 *            Find only available taxis
	 * @param pIsPink
	 *            Find only pink taxis
	 * @return List of Taxi DBObjects
	 * @throws FuberException
	 */
	private List<Taxi> getTaxiListFromDB(Boolean pIsAvailable, Boolean pIsPink) throws FuberException {
		return DatabaseAdaptor.transformTaxiDBObjectListToTaxiList(TaxiDao.Taxi.getTaxiCollection()
				.find(DatabaseAdaptor.prepareTaxiSearchObject(pIsAvailable, pIsPink)).toArray());
	}

}
