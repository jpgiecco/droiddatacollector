package ar.com.eurekaconsulting.elementControl.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import java.util.Date;

import ar.com.eurekaconsulting.elementControl.model.Element;

public class Store {	

	private static Context context;

	public static void init(Context ctx) {
		context = ctx;
	}
	
	public static boolean saveElement(Element element) {
		try{
			SharedPreferences prefs =  context.getSharedPreferences("CONTROL_APP", Context.MODE_PRIVATE);
			Gson serializer = new Gson();
			if (element.getActualValue().equals(0L)) {
				element.setActualValue(-1L);
				ElementListLoader.getTakenElements().put("ELEMENT_"+element.getCode(),element);
			} else if (element.getActualValue() != -1L || element.getNovedad() != null) {
				element.setSaveOrder(Store.getSaveOrderCounter());
				Store.incrementSaveOrderNumber();
				ElementListLoader.getTakenElements().put("ELEMENT_"+element.getCode(),element);
			}
			Editor editor =	prefs.edit();
			editor.putString("ELEMENT_"+element.getCode(), serializer.toJson(element));
			editor.commit();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
	public static Element restoreElement(String elementId) {
		try{
			SharedPreferences prefs =  context.getSharedPreferences("CONTROL_APP", Context.MODE_PRIVATE);
			Gson gson = new Gson();
			String jsonElement = prefs.getString("ELEMENT_"+elementId, "");
			Element element = gson.fromJson(jsonElement, Element.class);
			return element;
		} catch (Exception ex) {
			return null;
		}
	}	
	
	public static void clearAllElements(){
		SharedPreferences prefs =  context.getSharedPreferences("CONTROL_APP", Context.MODE_PRIVATE);
		Editor editor =	prefs.edit();
		editor.clear();
		editor.commit();
	}

	public static void checkInputDate(final Date inputDate) {
		try{
			final SharedPreferences prefs =  context.getSharedPreferences("CONTROL_APP", Context.MODE_PRIVATE);
			Gson gson = new Gson();
			String jsonInputDate = prefs.getString("INPUT_DATE", "");
			if (jsonInputDate != "") {
				Date storedDate = gson.fromJson(jsonInputDate, Date.class);
				if (!inputDate.equals(storedDate)){
					 new AlertDialog.Builder(context)
			           .setMessage("¿Se detecto un nuevo archivo de ruta, esta seguro que desea eliminar las mediciones almacenadas en la memoria?")
			           .setCancelable(false)
			           .setPositiveButton("Si", new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
								Store.clearAllElements();
								Editor editor =	prefs.edit();
								Gson serializer = new Gson();
								editor.putString("INPUT_DATE", serializer.toJson(inputDate));
								editor.putInt("SAVE_ORDER_COUNTER", 0);
								editor.commit();
			               }
			           })
			           .setNegativeButton("No", new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
			                   dialog.cancel();
			               }
			               })
			           .show();
				}
			} else {
				Editor editor =	prefs.edit();
				Gson serializer = new Gson();
				editor.putString("INPUT_DATE", serializer.toJson(inputDate));
				editor.commit();
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}	
	}
	
	private static int getSaveOrderCounter() {
		SharedPreferences prefs =  context.getSharedPreferences("CONTROL_APP", Context.MODE_PRIVATE);
		return prefs.getInt("SAVE_ORDER_COUNTER",0);
	}
	
	private static void incrementSaveOrderNumber(){
		SharedPreferences prefs =  context.getSharedPreferences("CONTROL_APP", Context.MODE_PRIVATE);
		Editor editor =	prefs.edit();
		editor.putInt("SAVE_ORDER_COUNTER", prefs.getInt("SAVE_ORDER_COUNTER",0)+1);
		editor.commit();
	}

}
