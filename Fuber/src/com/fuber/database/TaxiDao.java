package com.fuber.database;

import com.fuber.errorhandling.FuberException;
import com.fuber.util.Constants;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public enum TaxiDao {
	Taxi;
	private DBCollection taxiCollection = null;

	private TaxiDao() {
		final DB fuberDatabase = ConnectionFactory.CONNECTION.getClient()
				.getDB(Constants.DatabaseConstant.DATABASE_NAME);
		taxiCollection = fuberDatabase.getCollection(Constants.DatabaseConstant.COLLECTION_TAXI);
	}

	public DBCollection getTaxiCollection() throws FuberException {
		if (taxiCollection == null)
			throw new FuberException(400, 400, "Taxi collection not found in database",
					String.format("Check DB for collection named %s.", Constants.DatabaseConstant.COLLECTION_TAXI));
		return taxiCollection;
	}
}
