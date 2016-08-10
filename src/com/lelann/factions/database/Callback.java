package com.lelann.factions.database;

public interface Callback<T> {
	public void call(Throwable t, T result);
}
