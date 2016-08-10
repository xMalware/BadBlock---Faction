package com.lelann.factions.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends Database{
	private String hostname, port, username, password, database;

	public MySQL(String hostname, String port, String username, String password, String database){
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
		this.database = database;
	}
	public Connection openConnection() throws SQLException, ClassNotFoundException {
		if (checkConnection()) {
			return this.connection;
		}
		Class.forName("com.mysql.jdbc.Driver");
		this.connection = DriverManager.getConnection("jdbc:mysql://" + 
				this.hostname + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.username, this.password);
		
		return this.connection;
	}
}
