package com.fuber.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fuber.bean.Location;
import com.fuber.bean.Taxi;
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

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/booktaxi/{lat}/{long}")
	public Response bookTaxi(@PathParam("lat") Double pLat, @PathParam("long") Double pLong) throws FuberException {
		return bookTaxi(pLat, pLong, false);
	}

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/booktaxi/{lat}/{long}/{pink}")
	public Response bookTaxi(@PathParam("lat") Double pPickupLocationLat, @PathParam("long") Double pPickupLocationLong,
			@PathParam("pink") Boolean pIsPink) throws FuberException {

		// Find the nearest taxi if available
		Location pickupLocation = new Location(pPickupLocationLat, pPickupLocationLong);
		Taxi taxi = FuberUtility.getNearestTaxi(getTaxiListFromDB(true, pIsPink), pickupLocation, pIsPink);

		if (taxi == null) {
			return Response.status(200).entity("Taxi not available. Try again Later :(").build();
		}
		updateTaxiInDB(FuberUtility.updateTaxiAttributesForTripStart(taxi, pickupLocation));
		return Response.ok(taxi).build();
	}

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("/endtrip/{phone}/{lat}/{long}")
	public Response endTrip(@PathParam("phone") String pDriverPhoneNo, @PathParam("lat") Double pDropLocationLat,
			@PathParam("long") Double pDropLocationLong) throws FuberException {

		Taxi taxi = findTaxiInDatabaseByDriverPhoneNo(pDriverPhoneNo, false);
		if (taxi == null) {
			throw new FuberException(Response.Status.BAD_REQUEST.getStatusCode(), 400,
					"Taxi in-trip not found with phone no:" + pDriverPhoneNo,
					"Please verify taxi with phone no [" + pDriverPhoneNo + "] is in trip.");
		}
		Location tripEndLocation = new Location(pDropLocationLat, pDropLocationLat);
		Double totalTripCost = FuberUtility.calculateTripCharge(taxi.getTripStartLocation(), tripEndLocation,
				taxi.getTripStartTime(), taxi.getIsPink());
		Taxi taxiEndTrip = FuberUtility.updateTaxiAttributesForTripEnd(taxi, tripEndLocation);
		updateTaxiInDB(taxiEndTrip);
		return Response.status(200).entity("Trip Ended. Total Amount Payable = " + totalTripCost + " Dogecoin").build();

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
