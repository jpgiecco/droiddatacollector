package ar.com.eurekaconsulting.elementControl.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;
import ar.com.eurekaconsulting.elementControl.model.Element;

public class RouteParser {
	
	private static final String ns = null;
	
	public static List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readRoute(parser);
        }
        catch (Exception ex){
        	return new ArrayList<Element>();
        } finally {
            in.close();
        }
    }
	
	private static List readRoute(XmlPullParser parser) throws XmlPullParserException, IOException {
	    List elements = new ArrayList();

	    parser.require(XmlPullParser.START_TAG, ns, "ruta");
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        // Starts by looking for the entry tag
	        if (name.equals("elemento")) {
	            elements.add(readElement(parser));
	        } else {
	            skip(parser);
	        }
	    }  
	    return elements;
	}
	
	private static Element readElement(XmlPullParser parser) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, ns, "elemento");
	    String codigo = null;
	    Integer idCliente = null;
	    String servicio = null;
	    String ubicacion = null;
	    Long medicionAnterior = null;
	    Integer consumoMaximo = null;
	    Boolean deuda = null;
	    String usuario = null;
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("idCliente")) {
	            idCliente = readCliente(parser);
	        } else if (name.equals("codigo")) {
	            codigo = readCodigo(parser);
	        } else if (name.equals("servicio")) {
	            servicio = readServicio(parser);
	        } else if (name.equals("ubicacion")) {
	            ubicacion = readUbicacion(parser);
	        } else if (name.equals("medicionAnterior")) {
	            medicionAnterior = readMedicionAnterior(parser);
	        } else if (name.equals("consumoExcesivo")) {
	            consumoMaximo = readConsumoMaximo(parser);
	        } else if (name.equals("deuda")) {
	            deuda = readDeuda(parser);
	        } else if (name.equals("usuario")){
	           usuario = readUsuario(parser);
	        } else {
	        	 skip(parser);
	        }
	    }
	    return new Element(idCliente, codigo, servicio, ubicacion, medicionAnterior, consumoMaximo,deuda,usuario);
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
	
	private static Integer readCliente(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "idCliente");
	    Integer idCliente = Integer.parseInt(readText(parser).trim());
	    parser.require(XmlPullParser.END_TAG, ns, "idCliente");
	    return idCliente;
	}
	
	private static String readCodigo(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "codigo");
	    String codigo = readText(parser).trim();
	    parser.require(XmlPullParser.END_TAG, ns, "codigo");
	    return codigo;
	}
	
	private static String readUbicacion(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "ubicacion");
	    String ubicacion = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "ubicacion");
	    return ubicacion;
	}
	
	private static String readServicio(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "servicio");
	    String servicio = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "servicio");
	    return servicio;
	}
	
	private static Long readMedicionAnterior(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "medicionAnterior");
	    Long medicionAnterior = Long.parseLong(readText(parser).trim());
	    parser.require(XmlPullParser.END_TAG, ns, "medicionAnterior");
	    return medicionAnterior;
	}
	
	private static Integer readConsumoMaximo(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "consumoExcesivo");
	    Integer consumoMaximo = Integer.parseInt(readText(parser).trim());
	    parser.require(XmlPullParser.END_TAG, ns, "consumoExcesivo");
	    return consumoMaximo;
	}
	
	private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}
	
	private static Boolean readDeuda(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "deuda");
	    Integer deudaInt = Integer.parseInt(readText(parser).trim());
	    parser.require(XmlPullParser.END_TAG, ns, "deuda");
	    if (deudaInt == 0) {
	    	return false;
	    } else {
	    	return true;
	    }
	}
	
	private static String readUsuario(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "usuario");
	    String usuario = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "usuario");
	    return usuario;
	}
}
