package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import dataGetters.DataGetter;

/**
 * The {@code Rank} class implements an abstract object for calculating the
 * Quality of Life for a list of cities based on preferences on Climate, Crime,
 * Income and Population.
 * 
 * @author Mohid Makhdoomi
 */
public class Rank {
	private static Integer[] preferences;
	private static String[] cityList;
	private static Boolean[] notAvailable;
	private static Integer[] factorMult;
	private static Double[] avg;
	private static Double[] cityWeight;
	private static Double[][] weightPerFactor;
	private static LinkedHashMap<String, Double> rankedCities;

	/**
	 * Initializes the abstract object with a list of preferences and a list of
	 * cities. Using the {@code DataGetter} class, this class is implemented so
	 * that the abstract object contains data for which cities have no data, the
	 * average values for all 4 factors (Climate, Crime, Income, Population),
	 * the Quality of Life of all/each city as well as those cities ranked in
	 * order of highest QoL to lowest.
	 *
	 * @param prefs
	 *            list of user preferences on importance/value of all 4 factors
	 *            (Climate (Temperate, Precipitation), Crime, Income,
	 *            Population) Note: Climate is broken down into two sub
	 *            preferences, Temperature and Precipitation
	 * @param cityList
	 *            list of cities provided by the user
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void init(Integer[] prefs, String[] cityList) throws IOException {
		Rank.preferences = prefs.clone();
		Rank.cityList = cityList.clone();

		Rank.factorMult = convertToMultiplier(prefs);

		DataGetter.init(cityList);
		ArrayList<CityT> cities = DataGetter.get_allCity();

		Rank.notAvailable = new Boolean[cityList.length];
		Rank.avg = calcAvg(cities);
		// weightPerFactor are set in calcQOL
		Rank.weightPerFactor = new Double[cityList.length][];
		Rank.cityWeight = calcQOL(cities);

		Rank.rankedCities = finalRank(cityList);
	}

	/**
	 * Ranks list of cities in order of highest Quality of Life to lowest.
	 *
	 * @param cityList
	 *            list of cities provided by the user
	 * @return the ordered list of cities represented by a linked hash map where
	 *         cities are keys and theirs QoL 'weight' are values
	 */
	private static LinkedHashMap<String, Double> finalRank(String[] cityList) {
		TreeMap<String, Double> rank = new TreeMap<String, Double>();
		for (int i = 0; i < cityList.length; i++) {
			// if the city is missing from one or more datasets set its associated
			// weight to Double.NEGATIVE_INFINITY to represent it not being 
			// possible to rank this city
			if ((Rank.notAvailable[i]).booleanValue() == true) {
				rank.put(cityList[i], Double.NEGATIVE_INFINITY);
			} else {
				rank.put(cityList[i], Rank.cityWeight[i]);
			}
		}
		return rank.entrySet().stream().sorted(Collections.reverseOrder(Entry.comparingByValue()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	/**
	 * Calculates the Quality of Life value (weight) for each city using the
	 * percentage deviation of all the factors of each city from the average of
	 * all the cities.
	 *
	 * @param cities
	 *            list of cities and their data as an array of CityT instances
	 * @return the list of Quality of Life values for all the cities
	 */
	private static Double[] calcQOL(ArrayList<CityT> cities) {
		Double[] weight = new Double[cities.size()];
		Arrays.fill(weight, 0.0);
		for (int i = 0; i < cities.size(); i++) {
			// if the city is not missing from any dataset then calculate its QoL value
			if (!(Rank.notAvailable[i]).booleanValue()) {
				Double sum = 0.0;
				Double[] aux = climateHelper(cities.get(i));
				Double[] temp = new Double[5];
				temp[0] = (Rank.factorMult[0] * deviationHelper(aux[0], Rank.avg[0]));
				temp[1] = (Rank.factorMult[1] * deviationHelper(aux[1], Rank.avg[1]));
				// -1 for crime since crime rate > avg is worse, opposite of the rest of the data.
				temp[2] = (Rank.factorMult[2] * (-1) * deviationHelper((cities.get(i)).get_Crime(), Rank.avg[2]));
				temp[3] = (Rank.factorMult[3]
						* deviationHelper(((cities.get(i)).get_Income()).doubleValue(), Rank.avg[3]));
				temp[4] = (Rank.factorMult[4]
						* deviationHelper(((cities.get(i)).get_Population()).doubleValue(), Rank.avg[4]));
				sum += temp[0];
				sum += temp[1];
				sum += temp[2];
				sum += temp[3];
				sum += temp[4];
				// weight array gets the cities total QoL value
				weight[i] = sum;
				// weightPerFactor stores each factor of a cities QoL value
				Rank.weightPerFactor[i] = temp.clone();
			} else {
				// if the city is missing from one or more datasets set its weight to null 
				// to represent it not being possible to calculate the QoL of this city
				weight[i] = null;
				Rank.weightPerFactor[i] = null;
			}
		}
		return weight.clone();
	}

	/**
	 * Calculates the average values for all factors using the list of cities
	 * while taking into account cities that are missing from datasets.
	 *
	 * @param cities
	 *            list of cities and their data as an array of CityT instances
	 * @return the list of average values for all factors
	 */
	private static Double[] calcAvg(ArrayList<CityT> cities) {
		Double[] tempAvg = { 0.0, 0.0, 0.0, 0.0, 0.0 };
		for (CityT x : cities) {
			boolean missing = false;
			// if any dataset doesn't have data for the city, set missing to true
			if (x.get_Climate().get(0)[0] == null || (x.get_Climate().get(0)[0]).equals(null)) {
				missing = true;
			} else if (x.get_Crime() == null || (x.get_Crime()).equals(null)) {
				missing = true;
			} else if (x.get_Income() == null || (x.get_Income()).equals(null)) {
				missing = true;
			} else if (x.get_Population() == null || (x.get_Population()).equals(null)) {
				missing = true;
			} else {
				// add the data for each factor of the city to the temporary array
				Double[] aux = climateHelper(x);
				if (aux[0] == null || aux[1] == null || aux[0].equals(null) || aux[1].equals(null)) {
					missing = true;
				} else {
					tempAvg[0] += (aux[0]); // temperature
					tempAvg[1] += (aux[1]); // precipitation
					tempAvg[2] += x.get_Crime();
					tempAvg[3] += (x.get_Income()).doubleValue();
					tempAvg[4] += (x.get_Population()).doubleValue();
				}
			}
			// set the notAvailable value of this city to missing (will be false if 
			// the above if statements fail)
			Rank.notAvailable[cities.indexOf(x)] = missing;
		}
		// get the average of each factor by dividing the summed values by the
		// number of cities minus the number of cities missing data
		for (int i = 0; i < tempAvg.length; i++) {
			tempAvg[i] = ((tempAvg[i]) / (cities.size() - noDataCount()));
		}
		return tempAvg.clone();
	}

	/**
	 * Calculates the number of cities missing from one or more datasets.
	 *
	 * @return the number of missing cities
	 */
	private static int noDataCount() {
		int counter = 0;
		for (Boolean x : Rank.notAvailable) {
			if (x.booleanValue() == true) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Calculates the average values for Temperature and Precipitation for a
	 * city taking into account any missing values.
	 *
	 * @param city
	 *            the city
	 * @return the average values for Temperature and Precipitation
	 */
	private static Double[] climateHelper(CityT city) {
		Double[] aux = new Double[2];
		Arrays.fill(aux, 0.0);
		int tempMissing = 0; // number of empty temp vals
		int preMissing = 0; // number of empty precip vals
		// i < 4 because there is climate data for 4 months (4 seasons)
		// so we need to take the average of them
		for (int i = 0; i < 4; i++) {
			String temp = city.get_Climate().get(i)[2];
			String precip = city.get_Climate().get(i)[3];
			// if the temp value is "" or null increment the counter for missing temp
			// set temp = 0.0 to add nothing to the auxiliary array
			if (temp == null || temp.equals(null) || temp.equals("")) {
				tempMissing++;
				temp = "0.0";
			}
			// if the precip value is "" or null increment the counter for missing precip
			// set temp = 0.0 to add nothing to the auxiliary array
			if (precip == null || precip.equals(null) || precip.equals("")) {
				preMissing++;
				precip = "0.0";
			}
			aux[0] += Double.parseDouble(temp);
			aux[1] += Double.parseDouble(precip);
		}
		// Take the sum of the values for the 4 months and divide by 4 - the number of missing values
		if (4 - tempMissing == 0) {
			aux[0] = null;
		} else if (4 - preMissing == 0) {
			aux[1] = null;
		} else {
			aux[0] = (aux[0] / (4 - tempMissing));
			aux[1] = (aux[1] / (4 - preMissing));
		}
		return aux.clone();
	}

	/**
	 * Calculates the percentage deviation of a value from the average.
	 *
	 * @param val
	 *            a value
	 * @param avg
	 *            the average value
	 * @return the percentage deviation from average
	 */
	private static Double deviationHelper(Double val, Double avg) {
		return 100 * ((val - avg) / avg);
	}

	/**
	 * Takes a list of preferences for the factors (1 <= value <= 10) and
	 * converts them to a list of multiplier values.
	 *
	 * @param prefs
	 *            list of user preferences on importance/value of all 4 factors
	 *            (Climate (Temperate, Precipitation), Crime, Income,
	 *            Population) Note: Climate is broken down into two sub
	 *            preferences, Temperature and Precipitation
	 * @return returns the preferences as multipliers
	 */
	private static Integer[] convertToMultiplier(Integer[] prefs) {
		Integer[] mult = new Integer[prefs.length];
		for (int i = 0; i < prefs.length; i++) {
			// assumption is that no one prefers a high crime rate, so all values 1 to 10
			// represent only the importance of the crime rate being low but to make the scale
			// fair the value is divided by 2 to equalize this preference with the others.
			// E.g. Crime - 1 should be the same as Population - 6 as 6 is the lowest positive
			// value for Population, but 1 is the lowest positive value for Crime.
			if (i == 2) {
				mult[i] = ((prefs[i] + 1) / 2);
			} else {
				// if the number is less than 6 the multiplier is value - 5 - 1
				// E.g. value of 5 converts to a multiplier of -1
				if (Integer.signum(prefs[i] - 5) < 1) {
					mult[i] = (prefs[i] - 5 - 1);
				} else {
					// if the number is greater than 5 the multiplier is value - 5
					// E.g. value of 6 converts to a multiplier of 1
					mult[i] = (prefs[i] - 5);
				}
			}
		}
		return mult.clone();
	}

	/**
	 * Gets the list of user preferences on importance/value of all 4 factors
	 * (Climate (Temperate, Precipitation), Crime, Income, Population) Note:
	 * Climate is broken down into two sub preferences, Temperature and
	 * Precipitation.
	 *
	 * @return the list of user preferences
	 */
	public static Integer[] getPreferences() {
		return preferences;
	}

	/**
	 * Gets the list of cities provided by the user.
	 *
	 * @return the list of cities
	 */
	public static String[] getCityList() {
		return cityList;
	}

	/**
	 * Gets the list of boolean's indicating cities that are missing from one or
	 * more dataset. The index of this list aligns with the index of cityList.
	 *
	 * @return the list indicating cities that are missing data
	 */
	public static Boolean[] getNotAvailable() {
		return notAvailable;
	}

	/**
	 * Gets the list of multiplier values for Quality of Life calculations that
	 * are derived from the user provided preferences.
	 *
	 * @return the list of multiplier values
	 */
	public static Integer[] getFactorMult() {
		return factorMult;
	}

	/**
	 * Gets the list of average values for all factors of all the cities.
	 *
	 * @return the list of average values
	 */
	public static Double[] getAvg() {
		return avg;
	}

	/**
	 * Gets the list of Quality of Life values (weights) for all the cities.
	 *
	 * @return the list of weights for every city
	 */
	public static Double[] getCityWeight() {
		return cityWeight;
	}

	/**
	 * Gets the list of the Quality of Life values for each factor for all the
	 * cities.
	 *
	 * @return the list of per factor weights for every city
	 */
	public static Double[][] getWeightPerFactor() {
		return weightPerFactor;
	}

	/**
	 * Gets the ranked list of cities in order of highest Quality of Life to
	 * lowest.
	 *
	 * @return the ranked list of cities
	 */
	public static LinkedHashMap<String, Double> getRankedCities() {
		return rankedCities;
	}
}
