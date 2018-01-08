package dataGetters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import helpers.ModifiedMSD;

/**
 * This Module's sole purpose is to retrieve the value for neighborhood income
 * for a list of cities.
 * 
 * @author Abdul Moiz
 */
public class IncomeGetter {
	private String[] cities;
	private ArrayList<String[]> tempCities;
	private String[][] allCities;
	private Integer[] values;

	/**
	 * The constructor initializes all the state variables and by using a for
	 * loop finds the value for income for each element in the array of inputs.
	 * 
	 * @param input
	 *            - The string array containing the names of cities.
	 */
	public IncomeGetter(String[] input) {
		cities = input;
		values = new Integer[cities.length];
		tempCities = new ArrayList<String[]>();
		sortedArray();
		for (int i = 0; i < cities.length; i++) {
			values[i] = binarySearch(cities[i]);
		}
	}

	/**
	 * This method performs a binary search through the array of relevant lines
	 * from the data set after they have been sorted. This operation is done by
	 * first finding the middle point of the array and then checking whether the
	 * target string is either lexographically lower or higher than that middle
	 * element. If it is lower the first half of the array is taken recursively,
	 * if it is higher than the second half is used. The algorithm stops when
	 * the middle point is the string being searched for.
	 * 
	 * @param s
	 *            - The string being searched for.
	 * @return - The value for the income of the city being searched for is
	 *         returned.
	 */
	private Integer binarySearch(String s) {
		int lo = 0;
		int hi = allCities.length - 1;
		while (hi >= lo) {
			int mid = (lo + hi) / 2;
			if (allCities[mid][1].compareTo(s) == 0) {
				return Integer.parseInt(allCities[mid][allCities[mid].length - 1]);
			}
			if (allCities[mid][1].compareTo(s) < 0) {
				lo = mid + 1;
			}
			if (allCities[mid][1].compareTo(s) > 0) {
				hi = mid - 1;
			}
		}
		return null;
	}

	/**
	 * This method takes no input however goes through the data set and takes
	 * each line that is relevant to the problem. This is done by ensuring that
	 * the currently selected line meets the requirements through a set of if
	 * statements. If it does it is added to the array list. After the entire
	 * dataset is iterated through the array list is converted to a two
	 * dimensional array of string arrays due to the fact it is easier to work
	 * with them. The array is then sorted using a modified radix sort from the
	 * algorithms 4 textbook.
	 */
	private void sortedArray() {
		try {
			Scanner input = new Scanner(new File("data/income/01110044-eng.csv"));
			while (input.hasNextLine()) {
				String current = input.nextLine();
				String[] splitString = current.split(",");
				for (int i = 0; i < splitString.length; i++) {
					splitString[i] = splitString[i].replace("\"", "");
				}
				ArrayList<String> currentLine = new ArrayList<String>(Arrays.asList(splitString));
				if ((!currentLine.contains("Number of taxfilers and dependents") && currentLine.contains("Both sexes")
						&& currentLine.contains("All age groups") && currentLine.contains("Before-tax income")
						&& currentLine.contains("2014")) == true) {
					tempCities.add((String[]) currentLine.toArray(new String[currentLine.size()]));
				}
			}
			input.close();
			allCities = tempCities.toArray(new String[tempCities.size()][]);
			ModifiedMSD.sort(allCities);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This getter returns the extracted values in the form of an array to the
	 * user.
	 * 
	 * @return - The array of values for the cities.
	 */
	public Integer[] get() {
		return values;
	}
}
