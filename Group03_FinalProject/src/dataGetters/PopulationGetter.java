package dataGetters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import edu.princeton.cs.algs4.BinarySearchST;
import edu.princeton.cs.algs4.Merge;

/**
 * The Class PopulationGetter.
 *
 * @author Jakub This module takes user input of Canadian cities and returns
 *         their populations. The city names are entered by the user in the
 *         cityReader() method, this returns an arrayList of city names as
 *         strings Note: the maximum number of cities is 10, type "Done" to
 *         finish The output is an arrayList of populations for the cities, "No
 *         Data" if city does not exist
 */
public class PopulationGetter {

	/**
	 * Reads csv file of data (city names and their populations) creates an
	 * array of data (lines of the csv) (City (prov.), pop) sorts the array
	 * using mergesort
	 *
	 * @return sorted arrayList of Strings (lines from csv)
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public ArrayList<String> citySorting() throws IOException {
		ArrayList<String> originalData = new ArrayList<String>();
		ArrayList<String> sortedData = new ArrayList<String>();

		File f = new File("data/population/dataset - Copy.csv"); //population dataset
		BufferedReader br = new BufferedReader(new FileReader(f));
		String data;
		while ((data = br.readLine()) != null) {
			originalData.add(data); //filling array of original data from csv file
		}
		Comparable<String>[] arrayToSort = new String[originalData.size()];
		for (int i = 0; i < originalData.size(); i++) {
			String t = originalData.get(i); //creating array of cities to be sorted, this array is of type Comparable<String>
			arrayToSort[i] = t;
		}

		Merge.sort(arrayToSort); // sorting the array of data using merge sort

		for (int x = 0; x < arrayToSort.length; x++) {
			String d = (String) arrayToSort[x];
			sortedData.add(d); //creating return array, an arrayList of Strings
		}
		br.close();
		return sortedData;
	}

	/**
	 * Data reader.
	 *
	 * @param cities
	 *            - arrayList of cities (Strings)
	 * @param arrayOfData
	 *            the array of data (arrayList of strings)
	 * @return populations - arrayList of populations matching the cities
	 *         inputed
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public ArrayList<Integer> dataReader(ArrayList<String> cities, ArrayList<String> arrayOfData) throws IOException {
		ArrayList<Integer> populations = new ArrayList<Integer>();
		Comparable<String>[] arrayToSearch = new String[arrayOfData.size()]; //create dummy array for searching
		for (int i = 0; i < arrayOfData.size(); i++) {
			String t = arrayOfData.get(i);
			arrayToSearch[i] = t;
		}

		//perform binary search
		BinarySearchST<String, String> binarySearch = new BinarySearchST<String, String>(arrayToSearch.length);
		for (Comparable<String> s : arrayToSearch) {
			String dataLine = s.toString();
			int y = dataLine.indexOf(',');

			String key = StringUtils.stripAccents(dataLine.substring(0, y));
			String value = dataLine.substring(y + 1);
			binarySearch.put(key, value);
		}

		for (String city : cities) {

			if (binarySearch.contains(city)) {
				populations.add(Integer.valueOf(binarySearch.get(city)));
			} else {
				populations.add(null);
			}
		}
		return populations;
	}
}
