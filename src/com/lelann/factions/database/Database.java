package com.lelann.factions.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;

import com.google.common.collect.Queues;
import com.lelann.factions.FactionObject;

public abstract class Database extends FactionObject{
	protected Connection connection;
	protected Thread saving = new Thread(){
		@Override
		public void run(){
			while(true){
				String update = null;
				
				while((update = toSave.poll()) != null){
					try {
						updateSQL(update);
					} catch(SQLException | ClassNotFoundException e){
						e.printStackTrace();
					}
				}
				
				try {
					Thread.sleep(500L);
				} catch (InterruptedException e){}
			}
		}
	};
	
	public Database(){
		saving.start();
	}
	
	protected Queue<String> toSave = Queues.newLinkedBlockingDeque();
	
	public abstract Connection openConnection() throws SQLException, ClassNotFoundException;

	public boolean checkConnection() throws SQLException {
		return (this.connection != null) && (!this.connection.isClosed());
	}

	public Connection getConnection() {
		return this.connection;
	}

	public boolean closeConnection() throws SQLException {
		if (this.connection == null) {
			return false;
		}
		this.connection.close();
		return true;
	}

	public ResultSet querySQL(String query) throws SQLException, ClassNotFoundException {
		if (!checkConnection()) {
			openConnection();
		}
		Statement statement = this.connection.createStatement();

		ResultSet result = statement.executeQuery(query);

		return result;
	}

	public int updateSQL(String query) throws SQLException, ClassNotFoundException {
		if (!checkConnection()) {
			openConnection();
		}
		Statement statement = this.connection.createStatement();

		int result = statement.executeUpdate(query);

		return result;
	}

	public void updateAsynchrounously(final String line){
		toSave.add(line);
	}
}
