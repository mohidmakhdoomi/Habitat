package dataGetters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import edu.princeton.cs.algs4.BinarySearchST;
import edu.princeton.cs.algs4.Merge;

/**
 * The Class CrimeGetter.
 */
public class CrimeGetter {

	/**
	 * this function reads the csv file and takes each line as a list and adds
	 * it to an arraylist called data
	 * 
	 * @param line
	 *            is a string that will read be used to contain each line in csv
	 *            until its converted into a list
	 * @param count
	 *            is a counter variable for the for loop
	 * @param data
	 *            is an arraylist that holds strings . the string lists are the
	 *            lines in the csv file
	 * @param list
	 *            is a strings that holds a line in the csv file
	 * @return data
	 */
	private static ArrayList<String> getData() {
		String line = "";
		int count = 0;
		ArrayList<String> data = new ArrayList<String>();

		try (BufferedReader input = new BufferedReader(new FileReader("data/crime/02540004-eng.csv"))) {//try to access csv file
			while ((line = input.readLine()) != null) {
				if (count == 0) {
					line = input.readLine();
				} //ignore first line                
				data.add(count, line);//take in list line into 
				count++;//increment
			}
		} catch (IOException e) {//catch file error
			System.out.println("Error: File does not exist!");
		}
		data = dataClean(data);//clean data of unwanted information
		data = sorted(data);//sort the data by city
		return data;
	}

	/**
	 * this method uses merge sort to sort the data by city name in alphabetical
	 * order
	 * 
	 * @param toSort
	 *            is a comparable string list that holds lines from of data from
	 *            temp.
	 * @param temp
	 *            is a full array list the cleaned data in the csv file
	 * @param sorted
	 *            is an array list of strings that contains the cleaned csv data
	 *            sorted by city name
	 * @param t
	 *            is a temporary string variable used to load teh toSort
	 *            compareble list with temp's values
	 * @param d
	 *            is a temporary string variable used to hold toSorts data so it
	 *            can be inputted into the sorted array list
	 * @return sorted
	 */
	private static ArrayList<String> sorted(ArrayList<String> temp) {
		Comparable<String>[] toSort = new String[temp.size()];
		ArrayList<String> sorted = new ArrayList<String>();
		//fill the comoparable list toSort with the csv data from temp
		for (int i = 0; i < temp.size(); i++) {
			String t = temp.get(i); //creating array of cities to be sorted, this array is of type Comparable<String>
			toSort[i] = t;
		}

		Merge.sort(toSort); // sorting the array of data using merge sort

		//put sorted data into array list sorted
		for (int x = 0; x < toSort.length; x++) {
			String d = (String) toSort[x];
			sorted.add(d); //creating return array, an arrayList of Strings
		}
		return sorted;

	}

	/**
	 * this method removes all data that is not of year 2014 and is not crime
	 * severity index
	 * 
	 * @param data
	 *            is an arraylist that holds strings. the string are the lines
	 *            in the csv files that are wanted
	 * @param temp
	 *            is a arraylist of strings that holds all the data in the csv
	 *            file
	 * @param word
	 *            is a temporary variable to hold a string/line of csv data from
	 *            the temp list
	 * @return data
	 */
	private static ArrayList<String> dataClean(ArrayList<String> temp) {
		ArrayList<String> data = new ArrayList<String>();

		for (int i = 0; i < temp.size(); i++) {//for loop to run through each line in the data			
			if (temp.get(i).contains("2014")) {//check for year 2014
				if (temp.get(i).toLowerCase().contains("crime severity index")) {//check crime severity index
					String word = temp.get(i);
					data.add(word.replace("2014,", "").replace("\"", "")); //put into line of data arraylist. it is wanted info and remove year					
				}
			}
		}
		return data;
	}

	/**
	 * This method uses Binary Search to go through the data and find the crime
	 * rate data for the cities the user entered
	 * 
	 * @param cr
	 *            is the array list of Strings of crime rate values for each
	 *            city
	 * @param data
	 *            is an arraylist that holds strings. the string are the lines
	 *            in the csv file
	 * @param dataSearch
	 *            is an comparable string list that holds the csv data from the
	 *            data arraylist
	 * @param t
	 *            is a temporary string used to hold the csv line in teh data
	 *            Array list to put it into dataSearch
	 * @param binarySearch
	 *            is an object form BinarySearchST.java
	 * @param listCit
	 *            is the input list of "city, province" names
	 * @param dataLine
	 *            is a string variable that holds a value in dataSearch
	 * @param list
	 *            is a string list that splits the dataLine at the ',' so I can
	 *            access the city, province, crime severity index
	 * @param key
	 *            is a string value that holds the "city, province" for each
	 *            line in dataSearch
	 * @param value
	 *            is a strin value that holds the keys corresponding crime
	 *            severity index
	 * @param
	 * @return cr
	 */
	private static ArrayList<String> searcher(String[] listCit, ArrayList<String> data) {
		ArrayList<String> cr = new ArrayList<String>();
		Comparable<String>[] dataSearch = new String[data.size()];
		//load dataSearch with the values in data
		for (int i = 0; i < data.size(); i++) {
			String t = data.get(i);
			dataSearch[i] = t;
		}
		HashMap<String, ArrayList<Double>> dups = new HashMap<String, ArrayList<Double>>();

		BinarySearchST<String, String> binarySearch = new BinarySearchST<String, String>(dataSearch.length);
		//assign key and values into binary search
		for (Comparable<String> s : dataSearch) {
			String dataLine = s.toString();
			String[] list = dataLine.split(",");
			//System.out.println(list[0]);//city
			String key = StringUtils.stripAccents(list[0]) + "," + list[1];//city, province string
			String value = list[list.length - 1];//crime rate string
			if (!(dups.containsKey(key))) {
				dups.put(key, new ArrayList<Double>());
			}
			if (!(value.equals(".."))) {
				dups.get(key).add(Double.parseDouble(value));
			}
		}

		for (String tempKey : dups.keySet()) {
			Double[] valueArr = (dups.get(tempKey)).toArray(new Double[(dups.get(tempKey)).size()]);
			Arrays.sort(valueArr);
			Double newVal;
			if (valueArr.length >= 2) {
				if (valueArr.length % 2 == 0) {
					newVal = ((Double) valueArr[valueArr.length / 2] + (Double) valueArr[valueArr.length / 2 - 1]) / 2;
				} else {
					newVal = (Double) valueArr[valueArr.length / 2];
				}
			} else if (valueArr.length == 1) {
				newVal = dups.get(tempKey).get(0);
			} else {
				continue;
			}
			binarySearch.put(tempKey, String.valueOf(newVal));
		}

		//go through the city names in the input list and use binary search to find the crime severity for it
		//add the crime severity to the arraylist cr and return cr when finished
		for (String city : listCit) {
			//String[] list=city.split(",");
			if (binarySearch.contains(city)) { // && !((binarySearch.get(city)).equals(".."))
				cr.add(binarySearch.get(city));
				continue;
			} else {
				cr.add(null);
			}
		}
		return cr;
	}

	/**
	 * this function goes through the inputted list and finds special cased
	 * cities that have complicated names in the csv file for example burlington
	 * and oakville are considered one city in the csv so I take that into
	 * account here by replacing the city name "Burlington" with the name it has
	 * for burlington in my csv which is"Halton Region (Oakville/Burlington)"
	 * 
	 * @param listCit
	 *            is string list of city adn province names
	 * @param list
	 *            is a string list that holds the [city,province] of one if the
	 *            inputs from the user
	 * @return listCit
	 */
	private static String[] cleanList(String[] listCit) {
		for (int i = 0; i < listCit.length; i++) {
			String[] list = listCit[i].split(",");//make[ciy,province] list
			if (list[0].equalsIgnoreCase("burlington") || list[0].equalsIgnoreCase("oakville")) {
				listCit[i] = "Halton Region (Oakville/Burlington)";
			}

			if (list[0].equalsIgnoreCase("Collingwood")) {
				listCit[i] = "Collingwood (The Blue Mountains)";
			}

			if (list[0].equalsIgnoreCase("Markham") || list[0].equalsIgnoreCase("Vaughn")) {
				listCit[i] = "York Region (Markham/Vaughn)";
			}
		}
		return listCit;
	}

	/**
	 * this function returns an array list containing double values that
	 * correspond to the crime rate of each city
	 * 
	 * @param listCit
	 *            is the input list of city names
	 * @param data
	 *            is the csv data put into an arrayList string
	 * @param crString
	 *            is an arraylist of crime rates for each city in string form
	 * @param cr
	 *            is an arrayList Double who has crStrings values converted to
	 *            doubles
	 * @return cr
	 */
	public static ArrayList<Double> get_CrimeRate(String[] listCit) {
		listCit = cleanList(listCit);
		ArrayList<String> data = getData();//get clean data
		ArrayList<String> crString = searcher(listCit, data);
		ArrayList<Double> cr = new ArrayList<Double>();
		for (int i = 0; i < crString.size(); i++) {
			if (crString.get(i) != null)
				cr.add(Double.parseDouble(crString.get(i)));
			else
				cr.add(null);
		}

		//convert to double values
		return cr;
		//,String[] listProv
	}
}