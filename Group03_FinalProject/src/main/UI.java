package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

/**
 * The {@code UI} class implements a User Interface through the main method and
 * allows for a user to input their preferences on Climate, Crime, Income and
 * Population as well as a list of cities they are interested in.
 * 
 * @author Mohid Makhdoomi
 */
public class UI {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("\t\t     HABITAT\n\n\n");
		Scanner reader = new Scanner(System.in);
		Integer[] prefs = getInputRank(reader);
		String[] cityList = getInputCities(reader);
		reader.close();
		Rank.init(prefs, cityList);
		Integer[] multipliers = Rank.getFactorMult();
		Double[] cityWeight = Rank.getCityWeight();
		Double[][] weightPerFactor = Rank.getWeightPerFactor();
		LinkedHashMap<String, Double> rankedCities = Rank.getRankedCities();

		//		// Below prints out all user entered data and computed values -- mainly for debugging
		//		System.out.println();
		//		System.out.println("User Entered Values: " + (new ArrayList<Integer>(Arrays.asList(prefs))).toString());
		//		System.out
		//				.println("Calculated Multipliers: " + (new ArrayList<Integer>(Arrays.asList(multipliers))).toString());
		//		System.out.println("User Enter Cities: " + (new ArrayList<String>(Arrays.asList(cityList))).toString());
		//		System.out.println("Calculated QoL Totals: " + (new ArrayList<Double>(Arrays.asList(cityWeight))).toString());
		//		System.out.println("Descending QoL Order: " + rankedCities.toString());
		//		// Above prints out all user entered data and computed values -- mainly for debugging

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
			String[] gCityList = newCityList.toArray(new String[newCityList.size()]);
			Rank.init(prefs, gCityList);
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
			if (betterCities.size() > 0) {
				System.out.println("\n-- Below are Cities Within 75km of " + centerCity + ",ON"
						+ " that have a Higher Quality of Life Based on Entered Preferences  --\n");
				int gRank = 1;
				for (String tempCity : betterCities) {
					System.out.println(gRank + ": " + tempCity);
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

	/**
	 * Returns the input from the user for importance/value of factors (Climate
	 * (Temperature, Precipitation), Crime, Income, Population) that affect the
	 * Quality of Life of any city. Note: Climate is broken down into two sub
	 * preferences, Temperature and Precipitation
	 *
	 * @param reader
	 *            the text scanner to receive input from the user
	 * @return the user inputed list of preferences
	 */
	public static Integer[] getInputRank(Scanner reader) {
		Integer[] vals = new Integer[5];
		int temp = 0;
		System.out.println("Enter numbers between 1 and 10 below (including 1 and 10)");
		System.out.println("Closer to the ends of the spectrum == Higher importance.");
		System.out.println("1 and 10 indicate the highest importance, 5 and 6 indicate the least importance");
		System.out.println("Number less than or equal to 5 means lower value preferred");
		System.out.println("Number greater than or equal to 6 means higher value preferred");
		System.out.println("\nNote: For Crime Rate the number is still between 1 and 10 however it is");
		System.out.println("\tassumed that a lower crime rate is preferred, thus a number closer to 1");
		System.out.println("\tindicates lower importance and closer to 10 indicates higher importance\n");

		for (int i = 0; i < vals.length; i++) {
			if (i == 0) {
				System.out.print("Temperature : ");
			} else if (i == 1) {
				System.out.print("Precipitation : ");
			} else if (i == 2) {
				System.out.print("Crime Rate : ");
			} else if (i == 3) {
				System.out.print("Income : ");
			} else if (i == 4) {
				System.out.print("Population : ");
			}
			while (true) {
				try {
					temp = reader.nextInt();
					while ((1 > temp) || (10 < temp)) {
						System.out.print("Must be between 1 and 10, please try again: ");
						temp = reader.nextInt();
					}
					break;
				} catch (InputMismatchException e) {
					System.out.print("Must be an integer, please try again: ");
					reader.next();
				}
			}
			vals[i] = Integer.valueOf(temp);
		}
		return vals.clone();
	}

	/**
	 * Returns the input from the user for a list of cities in Canada
	 *
	 * @param reader
	 *            the text scanner to receive input from the user
	 * @return the user inputed list of cities
	 */
	public static String[] getInputCities(Scanner reader) {
		ArrayList<String> cities = new ArrayList<String>(0);
		String temp = "";
		System.out.println("\nEnter cities below (Canadian cities only)");
		System.out.println("Must be city name followed by a comma followed by the province initials");
		System.out.println("E.g. Toronto,ON or Winnipeg,MB\n");
		while (!temp.equalsIgnoreCase("Done")) {
			System.out.print("City name (enter Done to finish): ");
			temp = reader.next();
			if (cities.contains(temp.toUpperCase())) {
				System.out.println("Already entered this city, try again");
			} else if (!temp.equalsIgnoreCase("Done")) {
				cities.add((StringUtils.stripAccents(temp)).toUpperCase());
			}
		}
		return cities.toArray(new String[cities.size()]);
	}

	/**
	 * Displays the rank, name and quality of life weights of a given city
	 *
	 * @param tempCity
	 *            the name of the city to be displayed
	 * @param cityList
	 *            the list of cities provided by the user that make up
	 *            rankedCities
	 * @param weightPerFactor
	 *            the list of the Quality of Life values for each factor for all
	 *            the cities
	 * @param rankedCities
	 *            the ranked list of cities in order of highest Quality of Life
	 *            to lowest
	 * @param rank
	 *            the rank or position of tempCity in rankedCities
	 */
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
