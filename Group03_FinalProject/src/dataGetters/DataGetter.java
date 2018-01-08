package dataGetters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import main.CityT;

/**
 * The {@code DataGetter} class implements an abstract object for getting
 * Climate, Crime, Income and Population data for a list of cities.
 * 
 * @author Mohid Makhdoomi
 */
public class DataGetter {
	private static String[] cityList;
	private static ArrayList<ArrayList<String[]>> climate;
	private static ArrayList<Double> crime;
	private static ArrayList<Double> income;
	private static ArrayList<Integer> population;
	private static ArrayList<CityT> allCity;

	/**
	 * Initializes the abstract object with a list of cities. Using the modules
	 * in the {@code dataGetters} package, the class is implemented so that the
	 * abstract object contains data for the Climate, Crime, Income and
	 * Population of the user provided list of cities.
	 *
	 * @param fullCity
	 *            list of cities provided by the user
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static final void init(String[] fullCity) throws IOException {
		DataGetter.cityList = fullCity;
		DataGetter.climate = new ArrayList<ArrayList<String[]>>(0);
		DataGetter.crime = new ArrayList<Double>(0);
		DataGetter.income = new ArrayList<Double>(0);
		DataGetter.population = new ArrayList<Integer>(0);
		DataGetter.allCity = new ArrayList<CityT>(0);

		int cityListLength = fullCity.length;
		String[] cities = new String[cityListLength];
		String[] provinces = new String[cityListLength];

		// turns ["City1,Prov1","City2,Prov2",...] to a separate lists
		// ["City1","City2",...] and ["Prov1","Prov2",...]
		for (int i = 0; i < cityListLength; i++) {
			cities[i] = fullCity[i].split(",")[0];
			cities[i] = (cities[i].substring(0, 1)).toUpperCase()
					+ (cities[i].substring(1, cities[i].length())).toLowerCase();
			provinces[i] = (fullCity[i].split(",")[1]).toUpperCase();
		}

		// dataProv[0] is for climate
		// dataProv[1] is for crime
		// dataProv[2] is for income
		// dataProv[3] is for population
		String[][] dataProv = provHelper(provinces, cityListLength);

		DataGetter.climate = DataGetter.climateHelper(fullCity);
		DataGetter.crime = DataGetter.crimeHelper(cities, dataProv[1]);
		DataGetter.population = DataGetter.populationHelper(cities, dataProv[3]);
		DataGetter.income = DataGetter.incomeHelper(cities);
		// income data is collected after population as incomeHelper uses data
		// from DataGetter.population to convert dataset values to useable numbers.

		// Combines the individually collected data (Climate, Crime, Income, Population) 
		// by using the data to represent each city as an instance of CityT
		// allCity contains the list of cities and their data as an array of CityT instances
		for (int i = 0; i < cityListLength; i++) {
			DataGetter.allCity.add(new CityT(cityList[i], DataGetter.climate.get(i), DataGetter.crime.get(i),
					DataGetter.income.get(i), DataGetter.population.get(i)));
		}
	}

	/**
	 * Gets the climate data for all cities.
	 *
	 * @return the aggregate climate data
	 */
	public static final ArrayList<ArrayList<String[]>> get_climate() {
		return DataGetter.climate;
	}

	/**
	 * Gets the crime data for all cities.
	 *
	 * @return the aggregate crime data
	 */
	public static final ArrayList<Double> get_crime() {
		return DataGetter.crime;
	}

	/**
	 * Gets the income data for all cities.
	 *
	 * @return the aggregate income data
	 */
	public static final ArrayList<Double> get_income() {
		return DataGetter.income;
	}

	/**
	 * Gets the population data for all cities.
	 *
	 * @return the aggregate population data
	 */
	public static final ArrayList<Integer> get_population() {
		return DataGetter.population;
	}

	/**
	 * Gets all the data for all the cities
	 *
	 * @return the aggregate data of all cities
	 */
	public static final ArrayList<CityT> get_allCity() {
		return DataGetter.allCity;
	}

	/**
	 * Gets all the data for the given city
	 *
	 * @param city
	 *            the name of the city
	 * @return the aggregate data for the city
	 */
	public static final CityT get_City(String city) {
		for (int i = 0; i < cityList.length; i++) {
			if ((cityList[i]).equals(city)) {
				return DataGetter.allCity.get(i);
			}
		}
		return null;
	}

	/**
	 * Takes a list of provinces and returns customized names of the provinces
	 * specifically for each dataset.
	 *
	 * @param provinces
	 *            list of provinces for the list of cities provided by the user
	 * @param numCities
	 *            the number of cities provided by the user
	 * @return dataset specific province names
	 */
	private static String[][] provHelper(String[] provinces, int numCities) {
		String[][] temp = new String[4][numCities];
		temp[0] = provinces.clone(); // Climate takes provinces in the same format
		temp[2] = provinces.clone(); // Income does not need province names
		for (int i = 0; i < temp[1].length; i++) {
			if (provinces[i].equals("AB")) {
				temp[1][i] = "Alberta";
				temp[3][i] = "(Alta.)";
			} else if (provinces[i].equals("BC")) {
				temp[1][i] = "British Columbia";
				temp[3][i] = "(B.C.)";
			} else if (provinces[i].equals("MB")) {
				temp[1][i] = "Manitoba";
				temp[3][i] = "(Man.)";
			} else if (provinces[i].equals("NB")) {
				temp[1][i] = "New Brunswick";
				temp[3][i] = "(N.B.)";
			} else if (provinces[i].equals("NL")) {
				temp[1][i] = "Newfoundland and Labrador";
				temp[3][i] = "(N.L.)";
			} else if (provinces[i].equals("NT")) {
				temp[1][i] = "Northwest Territories";
				temp[3][i] = "(N.W.T.)";
			} else if (provinces[i].equals("NS")) {
				temp[1][i] = "Nova Scotia";
				temp[3][i] = "(N.S.)";
			} else if (provinces[i].equals("NU")) {
				temp[1][i] = "Nunavut";
				temp[3][i] = "(Nvt.)";
			} else if (provinces[i].equals("ON")) {
				temp[1][i] = "Ontario";
				temp[3][i] = "(Ont.)";
			} else if (provinces[i].equals("PE")) {
				temp[1][i] = "Prince Edward Island";
				temp[3][i] = "(P.E.I.)";
			} else if (provinces[i].equals("QC")) {
				temp[1][i] = "Quebec";
				temp[3][i] = "(Que.)";
			} else if (provinces[i].equals("SK")) {
				temp[1][i] = "Saskatchewan";
				temp[3][i] = "(Sask.)";
			} else if (provinces[i].equals("YT")) {
				temp[1][i] = "Yukon";
				temp[3][i] = "(Y.T.)";
			} else {
				temp[1][i] = provinces[i];
				temp[3][i] = provinces[i];
			}
		}
		return temp;
	}

	/**
	 * Takes the users list of cities and returns the climate data using
	 * {@code ClimateGetter}.
	 *
	 * @param fullCity
	 *            list of cities provided by the user
	 * @return the climate data for all cities
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static final ArrayList<ArrayList<String[]>> climateHelper(String[] fullCity) throws IOException {
		ArrayList<ArrayList<String[]>> temp = ClimateGetter.getClimate(fullCity);
		return temp;
	}

	/**
	 * Takes the list of city names and provinces separately and returns the
	 * crime data using {@code CrimeGetter}.
	 *
	 * @param cities
	 *            the list of city names
	 * @param provs
	 *            the list of city provinces
	 * @return the crime data for all cities
	 */
	private static final ArrayList<Double> crimeHelper(String[] cities, String[] provs) {
		String[] temp = new String[cities.length];
		for (int i = 0; i < temp.length; i++) {
			temp[i] = cities[i] + ", " + provs[i];
		}
		return CrimeGetter.get_CrimeRate(temp.clone());
	}

	/**
	 * Takes the list of city names and returns the income data using
	 * {@code IncomeGetter}. The income dataset requires that the value for each
	 * city be divided by its population and then multiplied by 1000 to get the
	 * accurate value and so this is done after using {@code IncomeGetter} to
	 * get the data from the income dataset.
	 *
	 * @param cities
	 *            the list of city names
	 * @return the income data for all cities
	 */
	private static final ArrayList<Double> incomeHelper(String[] cities) {
		ArrayList<Integer> temp = new ArrayList<Integer>(Arrays.asList((new IncomeGetter(cities)).get()));
		ArrayList<Double> ret = new ArrayList<Double>(temp.size());
		for (int i = 0; i < temp.size(); i++) {
			if (temp.get(i) == null || (temp.get(i)).equals(null) || ((DataGetter.population).get(i)) == null
					|| ((DataGetter.population).get(i)).equals(null)) {
				ret.add(null);
			} else {
				ret.add((((temp.get(i)).doubleValue()) / ((DataGetter.population).get(i))) * 1000.0);
			}
		}
		return ret;
	}

	/**
	 * Takes the list of city names and provinces separately and returns the
	 * population data using {@code CrimeGetter}.
	 *
	 * @param cities
	 *            the list of city names
	 * @param provs
	 *            the list of city provinces
	 * @return the population data for all cities
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static final ArrayList<Integer> populationHelper(String[] cities, String[] provs) throws IOException {
		String[] temp = new String[cities.length];
		for (int i = 0; i < temp.length; i++) {
			temp[i] = (cities[i] + " " + provs[i]);
		}
		return (new PopulationGetter()).dataReader((new ArrayList<String>(Arrays.asList(temp))),
				(new PopulationGetter()).citySorting());
	}
}
