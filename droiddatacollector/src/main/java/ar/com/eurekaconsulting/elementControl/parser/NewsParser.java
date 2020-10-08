package ar.com.eurekaconsulting.elementControl.parser;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ar.com.eurekaconsulting.elementControl.model.New;

public class NewsParser {
	
	private static final String ns = null;
	
	public static List<New> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, "iso-8859-1");
            parser.nextTag();
            return readRoute(parser);
        }
        catch (Exception ex){
        	return new ArrayList<New>();
        } finally {
            in.close();
        }
    }
	
	private static List readRoute(XmlPullParser parser) throws XmlPullParserException, IOException {
	    List elements = new ArrayList();

	    parser.require(XmlPullParser.START_TAG, ns, "novedades");
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        // Starts by looking for the entry tag
	        if (name.equals("novedad")) {
	            elements.add(readNew(parser));
	        } else {
	            skip(parser);
	        }
	    }  
	    return elements;
	}
	
	private static New readNew(XmlPullParser parser) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, ns, "novedad");
	    Integer codigo = null;
	    String servicio = null;
	    String ubicacion = null;
	    Long medicionAnterior = null;
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("codigo")) {
	            codigo = readCodigo(parser);
	        } else if (name.equals("descripcion")) {
	            servicio = readDescripcion(parser);
	        } else {
	            skip(parser);
	        }
	    }
	    return new New(codigo, servicio);
	}

	private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }
	
	private static Integer readCodigo(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "codigo");
	    Integer codigo = Integer.parseInt(readText(parser));
	    parser.require(XmlPullParser.END_TAG, ns, "codigo");
	    return codigo;
	}	
	
	private static String readDescripcion(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "descripcion");
	    String descripcion = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "descripcion");
	    return descripcion;
	}
	
	private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}
}
