package ar.com.eurekaconsulting.elementControl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import ar.com.eurekaconsulting.elementControl.model.Element;
import ar.com.eurekaconsulting.elementControl.parser.RouteParser;

public class ElementListLoader {

	private static List<Element> pendingElements = new LinkedList<Element>();
	
	private static List<Element> takenElements = new LinkedList<Element>();
	
	private static int totalMediciones = 0;

	public static List<Element> getPendingElements() {
		return pendingElements;
	}
	
	public static List<Element> getTakenElements() {
		return takenElements;
	}

	public static void loadElements() {
		try {
			pendingElements.clear();
			takenElements.clear();
			//Ruta para dispositivos de Gas Junin - Galaxy Ace
			File ruta = new File("/mnt/sdcard/rutas/ruta.xml");
			//Ruta para dispositivo de desarrollo - Samsung Galaxy S4 Mini
			//File ruta = new File("/storage/extSdCard/rutas/ruta.xml");
			Date inputDate = new Date(ruta.lastModified());
			Store.checkInputDate(inputDate);
			FileInputStream fis;
			fis = new FileInputStream(ruta);
			pendingElements = RouteParser.parse(fis);
			//updateStoredElements(elements);
			updateStoredElements(pendingElements);
			totalMediciones = pendingElements.size();
			removeStoredElements();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void updateStoredElements(List<Element> elements) {
		for (Element element : elements) {
			Element storedElement = Store.restoreElement(element.getCode());
			if (storedElement != null) {
				element.setActualValue(storedElement.getActualValue());
				element.setNovedad(storedElement.getNovedad());
				element.setSaveOrder(storedElement.getSaveOrder());
				takenElements.add(element);
			}
		}
	}
	
	public static void removeStoredElements() {
		for (Element element : takenElements) {
			ElementListLoader.getPendingElements().remove(element);
		}
	}

	public static int getMedicionesRealizadas() {
		return takenElements.size();
	}

	public static int getMedicionesFaltantes() {
		return totalMediciones - takenElements.size();
	}
	
	public static int getTotalMediciones() {
		return totalMediciones;
	}

	public static boolean isFinRelevamiento() {
		return getMedicionesFaltantes() == 0;
	}

}
