package com.fuber.database;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.MongoClient;

public enum ConnectionFactory {
	CONNECTION;
	private MongoClient client = null;

	private ConnectionFactory() {
		try {
			client = new MongoClient();
		} catch (Exception e) {
			Logger.getLogger(ConnectionFactory.class.getName()).log(Level.SEVERE,
					"Error occured. Unable to instanciate MongoClient", e);
		}
	}

	public MongoClient getClient() {
		if (client == null)
			throw new RuntimeException();
		return client;
	}
}