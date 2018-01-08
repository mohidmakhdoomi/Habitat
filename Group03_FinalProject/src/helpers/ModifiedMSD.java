package helpers;

/**
 * This class performs a sort on strings of varying lengths using the most
 * significant digit (or character) first. This is a modified version to work
 * with two dimensional arrays, however the original was covered in 2C03 lecture
 * and the Algorithms 4 textbook.
 * 
 * @author Abdul Moiz
 */
public class ModifiedMSD {
	private static final int R = 256;
	private static final int CUTOFF = 15;

	/**
	 * This method initializes the local variables needed to perform the sort.
	 * 
	 * @param a
	 *            - The two dimensional array to be sorted.
	 */
	public static void sort(String[][] a) {
		int n = a.length;
		String[][] aux = new String[n][];
		sort(a, 0, n - 1, 0, aux);
	}

	/**
	 * This method returns the ASCII integer value of a selected character from
	 * a string. The selection is done by choosing the character's index in the
	 * string.
	 * 
	 * @param s
	 *            - The string that is inputed.
	 * @param d
	 *            - The index of the character.
	 * @return - The ASCII value of the selected character at index d in string
	 *         s.
	 */
	private static int charAt(String s, int d) {
		assert d >= 0 && d <= s.length();
		if (d == s.length())
			return -1;
		return s.charAt(d);
	}

	/**
	 * This method performs the main radix sort recursively, The procedure is
	 * done by first sorting the entire array using the most significant digit
	 * (left most character), then the array is divided into sub arrays and the
	 * second most significant digit is sorted one sub array at a time.
	 * 
	 * @param a
	 *            - The array being sorted.
	 * @param lo
	 *            - The beginning of the sub array.
	 * @param hi
	 *            - The ending index of the sub array.
	 * @param d
	 *            - The current digit being sorted by.
	 * @param aux
	 *            - An auxiliary array for copying the sorted count array into.
	 */
	private static void sort(String[][] a, int lo, int hi, int d, String[][] aux) {

		if (hi <= lo + CUTOFF) {
			insertion(a, lo, hi, d);
			return;
		}

		int[] count = new int[R + 2];
		for (int i = lo; i <= hi; i++) {
			int c = charAt(a[i][1], d);
			count[c + 2]++;
		}

		for (int r = 0; r < R + 1; r++)
			count[r + 1] += count[r];

		for (int i = lo; i <= hi; i++) {
			int c = charAt(a[i][1], d);
			aux[count[c + 1]++] = a[i];
		}

		for (int i = lo; i <= hi; i++)
			a[i] = aux[i - lo];

		for (int r = 0; r < R; r++)
			sort(a, lo + count[r], lo + count[r + 1] - 1, d + 1, aux);
	}

	/**
	 * For sub arrays that are smaller than a selected cutoff a simple insertion
	 * sort is used to sort the array.
	 * 
	 * @param a
	 *            - The array of string arrays being sorted.
	 * @param lo
	 *            - The beginning of the sub array.
	 * @param hi
	 *            - The ending index of the sub array.
	 * @param d
	 *            - The current digit being sorted by.
	 */
	private static void insertion(String[][] a, int lo, int hi, int d) {
		for (int i = lo; i <= hi; i++)
			for (int j = i; j > lo && less(a[j][1], a[j - 1][1], d); j--)
				exch(a, j, j - 1);
	}

	/**
	 * This method simply swaps two given indexes in the array.
	 * 
	 * @param a
	 *            - The array containing the elements to be swapped.
	 * @param i
	 *            - The first index.
	 * @param j
	 *            - The second index.
	 */
	private static void exch(String[][] a, int i, int j) {
		String[] temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}

	/**
	 * This method checks which if a given string is lexographically lower than
	 * another given string.
	 * 
	 * @param v
	 *            - The first string.
	 * @param w
	 *            - The second string.
	 * @param d
	 *            - The current significant digit of the sub array.
	 * @return - A boolean is returned indicating whether the first string is
	 *         lexographically lower than the second.
	 */
	private static boolean less(String v, String w, int d) {
		for (int i = d; i < Math.min(v.length(), w.length()); i++) {
			if (v.charAt(i) < w.charAt(i))
				return true;
			if (v.charAt(i) > w.charAt(i))
				return false;
		}
		return v.length() < w.length();
	}
}
