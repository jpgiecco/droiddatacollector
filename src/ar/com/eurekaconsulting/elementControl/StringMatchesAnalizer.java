package ar.com.eurekaconsulting.elementControl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

public class StringMatchesAnalizer {
	
	private static StringMatchesAnalizer instance;
	
	public static StringMatchesAnalizer getInstance() {
		if (instance == null) {
			instance = new StringMatchesAnalizer();
		}
		return instance;
	}
	
	public String getBestMatch(ArrayList<String> matches){
		for (String string : matches) {
			String stringWithEndSpace = string + " ";
			if (stringWithEndSpace.matches("([0-9]{1}\\s)+")) {
				return string.replace(" ", "");
			}
		}
		return null;
	}
	
	public ArrayList<String> orderMatchesByLength(ArrayList<String> matches){
		Collections.sort(matches, new LengthComparator());
		return matches;
	}
	
	private class LengthComparator implements Comparator<String>{

		@Override
		public int compare(String lhs, String rhs) {
			return Integer.valueOf(rhs.length()).compareTo(Integer.valueOf(lhs.length()));
		}
		
	}
}
