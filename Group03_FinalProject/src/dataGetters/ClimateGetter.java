package dataGetters;

/** @author Mohammad Kareem Khaled
 * 	ID: 400032153
 * 	Climate Data access methods for 4 seasons.
 *  For the use of the Habitat Project
 * 	Source: Environment Canada
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

/**
 * gets raw data of database in the ArrayList format [season, season, season,
 * season], where season is an arrayList of cities
 * (ArrayList<ArrayList<String[]>>).
 */
public class ClimateGetter {
	private static ArrayList<String[]> allSpring; // stores all matches for city name
	private static ArrayList<String[]> allSummer;
	private static ArrayList<String[]> allFall;
	private static ArrayList<String[]> allWinter;
	private static ArrayList<String[]> sortedSpring; // removes duplicate city names, ensures correct province, and only pulls cities with valid data
	private static ArrayList<String[]> sortedSummer;
	private static ArrayList<String[]> sortedFall;
	private static ArrayList<String[]> sortedWinter;
	private static ArrayList<String> checked; // contains a list of found cities for sort methods
	private static ArrayList<String> cities; // contains a list of cities to search for
	private static ArrayList<String> CP; // contains a list of cities and Provinces that user provides as an ArrayList

	/**
	 * initialize all the arrays from client provided data.
	 *
	 * @param oldCitiesP
	 *            the string provided by client program
	 */
	private static void setUpArrays(String[] oldCitiesP) {
		CP = new ArrayList<String>();

		String[] newCities = new String[oldCitiesP.length];
		for (int i = 0; i < oldCitiesP.length; i++) {
			CP.add(oldCitiesP[i].toUpperCase());
			newCities[i] = oldCitiesP[i].split(",")[0].toUpperCase();
		}
		cities = new ArrayList<String>(Arrays.asList(newCities));
		checked = new ArrayList<String>();
		sortedSpring = new ArrayList<String[]>();
		sortedSummer = new ArrayList<String[]>();
		sortedFall = new ArrayList<String[]>();
		sortedWinter = new ArrayList<String[]>();
		allSpring = new ArrayList<String[]>();
		allSummer = new ArrayList<String[]>();
		allFall = new ArrayList<String[]>();
		allWinter = new ArrayList<String[]>();
	}

	/**
	 * Getter method! Blank string "" returned if no data present for
	 * precip/temp.
	 *
	 * @param userCities
	 *            client provided string
	 * @return ArrayList<ArrayList<String[]>> containing climate data in the
	 *         ArrayList format [season, season, season, season], where season
	 *         contains the climate data for every city
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static ArrayList<ArrayList<String[]>> getClimate(String[] userCities) throws IOException { // access method, use this! 
		//NOTE: blank string "" returned if no data present for precip/temp.
		setUpArrays(userCities);
		setUpSeasons();
		sortSeasons();

		ArrayList<ArrayList<String[]>> output = new ArrayList<ArrayList<String[]>>();
		output.add(sortedSpring);
		output.add(sortedSummer);
		output.add(sortedFall);
		output.add(sortedWinter);

		// Below rearranges the collected climate data so each element of the outer
		// ArrayList is a city, and the inner ArrayList elements are the 4 months
		ArrayList<ArrayList<String[]>> fin = new ArrayList<ArrayList<String[]>>(0);
		ArrayList<String[]> iter = new ArrayList<String[]>(0);
		for (int i = 0; i < userCities.length; i++) {
			iter = new ArrayList<String[]>(0);
			iter.add((output.get(0)).get(i));
			iter.add((output.get(1)).get(i));
			iter.add((output.get(2)).get(i));
			iter.add((output.get(3)).get(i));
			fin.add(iter);
		}
		return fin;
	}

	/**
	 * build data for each of the seasons.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void setUpSeasons() throws IOException {
		setUpSeason("data/climate/MARCH_CLIMATE_OR.csv", allSpring);
		setUpSeason("data/climate/JUNE_CLIMATE_OR.csv", allSummer);
		setUpSeason("data/climate/SEPTEMBER_CLIMATE_OR.csv", allFall);
		setUpSeason("data/climate/DECEMBER_CLIMATE_OR.csv", allWinter);
	}

	/**
	 * sort data for each of the seasons.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void sortSeasons() throws IOException {
		sortSeason(allSpring, sortedSpring);
		sortSeason(allSummer, sortedSummer);
		sortSeason(allFall, sortedFall);
		sortSeason(allWinter, sortedWinter);
	}

	/**
	 * gathers city climate data.
	 *
	 * @param filename
	 *            csv filename for the same season
	 * @param allSeason
	 *            ArrayList for a season
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void setUpSeason(String filename, ArrayList<String[]> allSeason) throws IOException {
		//Justification for linear search: Weather station names do not always start with the city name. 
		//Even if we alphabetize the data (which a binary search would require), many matches for a city are 
		//far apart. In order to ensure all results are caught, we use a linear search. 
		//NOTE: Linear search is O(n) whereas we would need over O(nlogn) for a binary search in this scenario. (to insert into an array, sort, and find cities)
		String csvFile = filename;
		String line = "";
		String csvSplitBy = ",";
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		try {
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] city = line.split(csvSplitBy); // array to split line by comma
				if (existsInName(city[0])) { // checks if city within user provided string
					String name = city[0].replaceAll("^\"|\"$", "");
					String prov = city[3].replaceAll("^\"|\"$", "");
					String temp = city[4].replaceAll("^\"|\"$", "");
					String precip = city[14].replaceAll("^\"|\"$", "");
					allSeason.add(new String[] { name, prov, temp, precip });
				}
			}

		} finally {
			br.close();
		}
	}

	/**
	 * returns true if city is already checked. If not checked, then it is added
	 * to the checked ArrayList
	 * 
	 * @param lookup
	 *            city to look up (check to see if it exists)
	 * @return boolean if city was already added to ArrayList
	 */
	private static boolean notChecked(String lookup) {
		lookup = lookup.replaceAll("^\"|\"$", ""); // remove quotes for compatison using regex

		for (int i = 0; i < checked.size(); i++) { // iterate through checked arraylist, which contains cities we already checked to avoid duplicates
			if (lookup.contains(checked.get(i))) { // check if saved string is a substring of lookup.

				return false; // return false because we already looked at the value (We don't want duplicates)
			}
		}
		return true;
	}

	/**
	 * Exists in name.
	 *
	 * @param lookup
	 *            city name substring to look for in database
	 * @return Boolean true if city exists in name
	 */
	private static boolean existsInName(String lookup) {
		lookup = lookup.replaceAll("^\"|\"$", ""); // remove quotes for comparison using regex
		for (int i = 0; i < cities.size(); i++) { // iterate through cities ArrayList to check if dataset's city matches with our city
			if ((StringUtils.stripAccents(lookup)).contains(cities.get(i))) { // match found
				return true;
			}
		}
		return false;
	}

	/**
	 * Sort season.
	 *
	 * @param allSeason
	 *            season to sort
	 * @param sortedSeason
	 *            sorted list for season
	 */
	private static void sortSeason(ArrayList<String[]> allSeason, ArrayList<String[]> sortedSeason) {
		for (String s : CP) { // for all strings in arraylist containing cities and provinces
			for (int j = 0; j < allSeason.size(); j++) {
				if (allSeason.get(j)[0].contains(s.split(",")[0])) { // look at first value in string, the name of the city
					String temp = allSeason.get(j)[1].replaceAll("^\"|\"$", ""); // remove quotes on province in data (allSpring)
					if (temp.equals(s.split(",")[1])) { // if the province matches (to ensure correct province goes through)
						if (notChecked(s.split(",")[0])) { // if city not already added
							if (!allSeason.get(j)[2].equals("")) { //weeds out city entries without temperature
								sortedSeason.add(allSeason.get(j)); // add the city as a String[] into sortedSeason
								checked.add(s.split(",")[0]); // add the city to checked to ensure we don't duplicate data
								break;
							}
						}
					}
				}
				// if a city is not added to that ordered index, an error will be
				// thrown. To retain order, we add null values (so you can still
				// iterate through)
			}
			try {
				sortedSeason.get(CP.indexOf(s));
			} catch (IndexOutOfBoundsException e) {
				sortedSeason.add(CP.indexOf(s), new String[] { null, null, null, null });
			}
			checked = new ArrayList<String>(); // reset checked list
		}
	}
}
