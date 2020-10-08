package ar.com.eurekaconsulting.elementControl.util;

import java.util.Comparator;

import ar.com.eurekaconsulting.elementControl.model.Element;

public class ElementComparator implements Comparator<Element>{

	@Override
	public int compare(Element arg0, Element arg1) {
		return arg0.getCode().compareTo(arg1.getCode());
	}

}