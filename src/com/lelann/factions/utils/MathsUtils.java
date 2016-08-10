package com.lelann.factions.utils;

public class MathsUtils {
	public static double addPercentage(double base, int percent){
		return (1 + percent / 100d) * base;
	}
	
	public static double addPercentage(double base, int percent, int n){
		return Math.pow(1 + percent / 100d, n) * base;
	}
	
	public static double round(double number, int dec){
		int div = (int) Math.pow(10, dec);
		return (double) ((int)(number * div)) / (double) div;
	}

	public static double min(double y, double y2) {
		return y < y2 ? y : y2;
	}
	
	public static double max(double y, double y2) {
		return y < y2 ? y2 : y;
	}
}
