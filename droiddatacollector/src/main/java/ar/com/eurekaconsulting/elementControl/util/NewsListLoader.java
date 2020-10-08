package ar.com.eurekaconsulting.elementControl.util;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.os.Environment;
import ar.com.eurekaconsulting.elementControl.model.New;
import ar.com.eurekaconsulting.elementControl.parser.NewsParser;

public class NewsListLoader {
	
	private static List<New> news = new LinkedList<New>();

	public static void loadNews(){
				//Ruta para dispositivos de Gas Junín - Galaxy Ace
				//File ruta = new File("/mnt/sdcard/rutas/novedades.xml");
				//Ruta para dispositivo de desarrollo - Samsung Galaxy S4 Mini
				//File ruta = new File("/storage/extSdCard/rutas/novedades.xml");
				//Obtiene el archivo de novedades de la memoria externa
				File ruta = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/rutas/novedades.xml");
				System.out.println(ruta.getAbsolutePath());
				FileInputStream fis;
				try {
					fis = new FileInputStream(ruta);
					news = NewsParser.parse(fis);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	}

	public static List<New> getNews() {
		return news;
	}

	public static void setNews(List<New> news) {
		NewsListLoader.news = news;
	}

	public static New getNewByCode(Integer codigoNovedad) {
		for (New newObj : news) {
			if (newObj.getCode().equals(codigoNovedad)) {
				return newObj;
			}
		}
		return null;
	}

}
