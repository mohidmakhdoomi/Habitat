package main;

import java.util.ArrayList;

/**
 * The {@code CityT} class provides an Abstract Data Type (ADT) for representing
 * cities.
 * 
 * @author Mohid Makhdoomi
 */

public class CityT {
	private String name;
	private ArrayList<String[]> climate;
	private Double crime;
	private Double income;
	private Integer population;

	/**
	 * Instantiates a new CityT object.
	 *
	 * @param city
	 *            the name of the city
	 * @param climate
	 *            the climate data for the city
	 * @param crime
	 *            the crime data for the city
	 * @param income
	 *            the income data for the city
	 * @param population
	 *            the population data for the city
	 */
	public CityT(String city, ArrayList<String[]> climate, Double crime, Double income, Integer population) {
		this.name = city;
		this.climate = climate;
		this.crime = crime;
		this.income = income;
		this.population = population;
	}

	/**
	 * Gets the name of the city.
	 *
	 * @return the name
	 */
	public String name() {
		return this.name;
	}

	/**
	 * Gets the climate data for the city.
	 *
	 * @return the climate data
	 */
	public ArrayList<String[]> get_Climate() {
		return this.climate;
	}

	/**
	 * Gets the crime data for the city.
	 *
	 * @return the crime data
	 */
	public Double get_Crime() {
		return this.crime;
	}

	/**
	 * Gets the income data for the city.
	 *
	 * @return the income data
	 */
	public Double get_Income() {
		return this.income;
	}

	/**
	 * Gets the population data for the city.
	 *
	 * @return the population data
	 */
	public Integer get_Population() {
		return this.population;
	}
}
