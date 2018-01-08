package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

/**
 * The UI Test Class.
 * 
 * @author Mohid Makhdoomi
 */
public class Test {

	/**
	 * The main method, tests UI output with a hard coded input.
	 *
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("\t\t     HABITAT");
		Scanner reader = new Scanner(System.in);
		Integer[] prefs = { 7, 4, 9, 6, 10 };
		String[] cityList = { "Toronto,ON", "Winnipeg,MB", "Kingston,ON", "Edmonton,AB", "Hamilton,ON", "Montreal,QC",
				"Calgary,AB", "Halifax,NS", "Ottawa,ON", "Vancouver,BC" };
		reader.close();
		Rank.init(prefs, cityList);
		Integer[] multipliers = Rank.getFactorMult();
		Double[] cityWeight = Rank.getCityWeight();
		Double[][] weightPerFactor = Rank.getWeightPerFactor();
		LinkedHashMap<String, Double> rankedCities = Rank.getRankedCities();

		// Below prints out all user entered data and computed values -- mainly for debugging
		System.out.println();
		System.out.println("User Entered Values: " + (new ArrayList<Integer>(Arrays.asList(prefs))).toString());
		System.out
				.println("Calculated Multipliers: " + (new ArrayList<Integer>(Arrays.asList(multipliers))).toString());
		System.out.println("User Enter Cities: " + (new ArrayList<String>(Arrays.asList(cityList))).toString());
		System.out.println("Calculated QoL Totals: " + (new ArrayList<Double>(Arrays.asList(cityWeight))).toString());
		System.out.println("Descending QoL Order: " + rankedCities.toString());
		// Above prints out all user entered data and computed values -- mainly for debugging

		System.out.println("\n-- Quality of Life for Cities Ranked by Entered Preferences  --\n");
		System.out.println(
				"   City Name\t\t\t\tTotal QoL Weight\t\tMean Temp\t\tMean Precip\t\tCrime Rate\t\tIncome\t\t\tPopulation");
		System.out.print("===========================================================================================");
		System.out
				.println("===========================================================================================");
		int rank = 1;
		boolean needGraph = false;
		String centerCity = null;
		for (String tempCity : rankedCities.keySet()) {
			if (rank == 1) {
				if ((tempCity.split(",")[1]).equals("ON")) {
					needGraph = true;
					centerCity = tempCity.substring(0, 1)
							+ (tempCity.toLowerCase()).substring(1, tempCity.length()).split(",")[0];
				} else {
					centerCity = tempCity.substring(0, 1)
							+ (tempCity.toLowerCase()).substring(1, tempCity.length()).split(",")[0] + ","
							+ tempCity.split(",")[1];
				}
			}
			cityDisplay(tempCity, cityList, weightPerFactor, rankedCities, rank);
			rank++;
		}

		if (needGraph) {
			Graph.init();
			ArrayList<String> closeCities = new ArrayList<String>();
			ArrayList<String> newCityList = new ArrayList<String>();
			for (String i : Graph.adj(centerCity)) {
				closeCities.add(i + ",ON");
				newCityList.add((StringUtils.stripAccents(i + ",ON")).toUpperCase());
			}
			newCityList.add((centerCity + ",ON").toUpperCase());
			//			ArrayList<String> originalCities = new ArrayList<String>(Arrays.asList(cityList));
			//			for(String temp : originalCities) {
			//				if (!closeCities.contains(temp)) {
			//					closeCities.add(temp);
			//				}
			//			}
			//			System.out.println(newCityList.toString());
			String[] gCityList = newCityList.toArray(new String[newCityList.size()]);
			Rank.init(prefs, gCityList);
			//			Double[][] gWeightPerFactor = Rank.getWeightPerFactor();
			LinkedHashMap<String, Double> gRankedCities = Rank.getRankedCities();
			ArrayList<String> betterCities = new ArrayList<String>();
			for (String tempCity : gRankedCities.keySet()) {
				//				if (false) {
				if (tempCity.equalsIgnoreCase(centerCity + ",ON")) {
					break;
				} else {
					for (String original : closeCities) {
						if (tempCity.equalsIgnoreCase((StringUtils.stripAccents(original)))) {
							betterCities.add(original);
						}
					}
				}
			}
			//			boolean betterThanFirst = false;
			//			for (Double tempQoL : gRankedCities.values()) {
			//				if (tempQoL != null) {
			//					System.out.println(tempQoL);
			//					if (tempQoL > rankedCities.get(centerCity + ",ON")) {
			//						betterThanFirst = true;
			//					}
			//				}
			//			}
			if (betterCities.size() > 0) {
				//			if (betterThanFirst) {
				System.out.println("\n-- Below are Cities Within 75km of " + centerCity + ",ON"
						+ " that have a Higher Quality of Life Based on Entered Preferences  --\n");
				int gRank = 1;
				for (String tempCity : betterCities) {
					System.out.println(gRank + ": " + tempCity);
					//				for (String tempCity : gRankedCities.keySet()) {
					//					cityDisplay(tempCity, gCityList, gWeightPerFactor, gRankedCities, gRank);
					gRank++;
				}
			} else {
				System.out.println("\n-- There are no Better Cities within 75km of " + centerCity + ",ON" + " --\n");
			}
		} else {
			System.out.println("\n-- The Highest Rank City (" + centerCity + ") is not in Ontario --");
			System.out.println(
					"\n'Better Cities' functionality is Only Avaliable if The Highest Rank City is in Ontario\n");
		}
	}

	private static void cityDisplay(String tempCity, String[] cityList, Double[][] weightPerFactor,
			LinkedHashMap<String, Double> rankedCities, int rank) {
		System.out.print(rank + ": " + tempCity.substring(0, 1)
				+ (tempCity.toLowerCase()).substring(1, tempCity.length()).split(",")[0] + "," + tempCity.split(",")[1]
				+ "\t\t\t--\t");
		if (rankedCities.get(tempCity) == Double.NEGATIVE_INFINITY) {
			System.out.print("City is missing from one or more datasets");
		} else {
			System.out.print(rankedCities.get(tempCity) + "\t<--\t");
		}
		if (weightPerFactor[new ArrayList<String>(Arrays.asList(cityList)).indexOf(tempCity)] != null) {
			Double[] perFactor = weightPerFactor[new ArrayList<String>(Arrays.asList(cityList)).indexOf(tempCity)];
			for (int i = 0; i < perFactor.length; i++) {
				if (perFactor[i] == null || perFactor[i].equals(null)) {
					System.out.print("N/A");
				} else if (i == perFactor.length - 1) {
					System.out.print(perFactor[i]);
				} else {
					System.out.print(perFactor[i] + "  +  ");
				}
			}
		}
		System.out.println();
	}
}
