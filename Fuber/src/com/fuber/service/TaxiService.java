package com.fuber.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.fuber.database.ConnectionFactory;
import com.fuber.util.Constants;
import com.fuber.util.DatabaseAdaptor;
import com.fuber.util.FuberUtility;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoException;

@Path("/taxiservice")
public class TaxiService {
	private final static Logger LOGGER = Logger.getLogger(TaxiService.class.getName());
	private DB fuberDatabase = ConnectionFactory.CONNECTION.getClient().getDB(Constants.DatabaseConstant.DATABASE_NAME);
	private DBCollection taxiCollection = fuberDatabase.getCollection(Constants.DatabaseConstant.COLLECTION_TAXI);

	@GET
	@Produces("application/json")
	@Path("/listtaxi")
	public Response listTaxi() {
		List<Taxi> taxiList = getTaxiListFromDB(false, false);
		GenericEntity<List<Taxi>> entity = new GenericEntity<List<Taxi>>(taxiList) {
		};
		return Response.ok(entity).build();
	}

	@PUT
	@Produces("application/json")
	@Path("/findtaxi/{lat}/{long}")
	public Response bookTaxi(@PathParam("lat") Double pLat, @PathParam("long") Double pLong) {
		return bookTaxi(pLat, pLong, false);
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/findtaxi/{lat}/{long}/{pink}")
	public Response bookTaxi(@PathParam("lat") Double pPickupLocationLat, @PathParam("long") Double pPickupLocationLong,
			@PathParam("pink") Boolean pIsPink) {

		// Find the nearest taxi if available
		Location pickupLocation = new Location(pPickupLocationLat, pPickupLocationLong);
		Taxi taxi = FuberUtility.getNearestTaxi(getTaxiListFromDB(true, pIsPink), pickupLocation, pIsPink);

		if (taxi != null) {
			Taxi taxiStartTrip = FuberUtility.updateTaxiAttributesForTripStart(taxi, pickupLocation);
			updateTaxiInDB(taxiStartTrip);
			return Response.status(200)
					.entity("Trip Started.\nDriver name : " + taxi.getName() + "\nPhone : " + taxi.getPhone()).build();
		}
		return Response.status(200).entity("Taxi not available").build();
	}

	@PUT
	@Produces("application/json")
	@Path("/endtrip/{phone}/{lat}/{long}")
	public Response endTrip(@PathParam("phone") String pDriverPhoneNo, @PathParam("lat") Double pDropLocationLat,
			@PathParam("long") Double pDropLocationLong) {

		Taxi taxi = findTaxiInDatabaseByDriverPhoneNo(pDriverPhoneNo, false);
		if (taxi != null) {
			Location tripEndLocation = new Location(pDropLocationLat, pDropLocationLat);
			Double totalTripCost = FuberUtility.calculateTripCharge(taxi.getTripStartLocation(), tripEndLocation,
					taxi.getTripStartTime(), taxi.getIsPink());
			Taxi taxiEndTrip = FuberUtility.updateTaxiAttributesForTripEnd(taxi, tripEndLocation);
			try {
				updateTaxiInDB(taxiEndTrip);
			} catch (MongoException e) {
				// TODO: handle exception
			}
			return Response.status(200).entity("Trip Ended. Total Amount Payable = " + totalTripCost + " Dogecoin")
					.build();
		}
		return Response.status(Status.NOT_FOUND).entity("Taxi in-trip not found with phone no: " + pDriverPhoneNo)
				.build();
	}

	private Taxi findTaxiInDatabaseByDriverPhoneNo(String pDriverPhoneNo, Boolean pIsAvailable) {
		Taxi taxi = null;
		BasicDBObject taxiDBObject = new BasicDBObject(Constants.DatabaseConstant.TAXI_COL_PHONE, pDriverPhoneNo)
				.append(Constants.DatabaseConstant.TAXI_COL_AVAILABLE, pIsAvailable);
		DBCursor cursor = taxiCollection.find(taxiDBObject);
		if (cursor.hasNext()) {
			taxi = DatabaseAdaptor.transformTaxiDBObjectToTaxi(cursor.one());
		}
		return taxi;
	}

	private void updateTaxiInDB(Taxi pTaxi) throws MongoException {
		taxiCollection.update(new BasicDBObject(Constants.DatabaseConstant.TAXI_COL_PHONE, pTaxi.getPhone()),
				DatabaseAdaptor.transformTaxiToDBObject(pTaxi));
	}

	/**
	 * Method to find list of taxis with the given parameters
	 * 
	 * @param pIsAvailable
	 *            Find only available taxis
	 * @param pIsPink
	 *            Find only pink taxis
	 * @return List of Taxi DBObjects
	 */
	private List<Taxi> getTaxiListFromDB(Boolean pIsAvailable, Boolean pIsPink) {
		return DatabaseAdaptor.transformTaxiDBObjectListToTaxiList(
				taxiCollection.find(DatabaseAdaptor.prepareTaxiSearchObject(pIsAvailable, pIsPink)).toArray());
	}

}
