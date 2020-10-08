package ar.com.eurekaconsulting.elementControl.util;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;

import ar.com.eurekaconsulting.elementControl.model.Element;

public class ElementsReadExporter {

	public static File exportReadElements(String filePath, Context context) {
		try {
			File exportFile = new File(filePath, "controles_" + DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()).replace("/", "_") + ".txt");
			exportFile.createNewFile();
			FileWriter writer = new FileWriter(exportFile);

			// Export stored elements
			Collection<Element> storedElements = ElementListLoader.getTakenElements().values();
			for (Element element : storedElements) {
				writer.write(toString(element));
				writer.write("\n");
			}

			writer.flush();
			writer.close();

			Toast.makeText(context, "Elementos Exportados a " + exportFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();

			return exportFile;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String toString(Element element) {
		String clientCode = element.getClientId().toString();
		String elementCode = element.getCode().toString();
		String elementValue = element.getActualValue().toString();
		String novedad = "";
		if (element.getNovedad() != null) {
			novedad = element.getNovedad().getCode().toString();
		}
		String saveOrder = element.getSaveOrder().toString();
		return padRight(clientCode, 10) + padRight(elementCode, 10) + padRight(elementValue, 10) + padRight(novedad, 5) + padRight(saveOrder, 5);
	}

	// pad with " " to the right to the given length (n)
	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

}
