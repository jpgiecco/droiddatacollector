package ar.com.eurekaconsulting.elementControl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import ar.com.eurekaconsulting.elementControl.util.ElementListLoader;
import ar.com.eurekaconsulting.elementControl.util.ElementsReadExporter;
import ar.com.eurekaconsulting.elementControl.util.ExceptionHandler;
import ar.com.eurekaconsulting.elementControl.util.NewsListLoader;
import ar.com.eurekaconsulting.elementControl.util.Store;

public class HomeActivity extends Activity implements OnClickListener {
	
	//private static String[] validDevices = {"e9d5a605d303d475", "88f8f33e6f4f4024", "45d1ab536aa49fe6", "517e017e8469efe0", "30d188c5cc99792c"};
	private static String[] validDevices = {"b778fba8e2ac34eb", "fe4b2ab7a6c3d0dd", "61039c4b9995ddda", "e1b812772e8b663c", "4b9e3f8c57fd51eb", "ed1e742669486af5", "1f5351a6ce25dd09", "66a3d3e86dc5c8cd"};

	private static final int REQUEST_CODE_EXPORT_MAIL = 1;
	public static final String ISGETVALUES = "ISGETVALUES";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
    	String deviceId = Settings.System.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
    	
    	//if (Arrays.asList(validDevices).contains(deviceId)){
    		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
    		
    		setContentView(R.layout.home);
    		
    		// Init Store
    		Store.init(this);
    		
    		//Loading news
    		NewsListLoader.loadNews();
    		
    		//Loading elements
    		ElementListLoader.loadElements();
    		
    		Button btnGetValues = (Button) findViewById(R.id.ButtonTomarMediciones);
    		btnGetValues.setOnClickListener(this);
    		
    		Button btnTalenValues = (Button) findViewById(R.id.ButtonMedicionesTomadas);
    		btnTalenValues.setOnClickListener(this);
    		
    		Button btnCounters = (Button) findViewById(R.id.ButtonContadores);
    		btnCounters.setOnClickListener(this);
    		
    		Button btnExportar = (Button) findViewById(R.id.ButtonExportar);
    		btnExportar.setOnClickListener(this);
    		
    		Button btnSettings = (Button) findViewById(R.id.ButtonSettings);
    		btnSettings.setOnClickListener(this);
    		
    		Button btnSalir = (Button) findViewById(R.id.ButtonSalir);
    		btnSalir.setOnClickListener(this);
    	/*} else {
        	Context context = getApplicationContext();
        	String msg = "La aplicación no tiene una licencia válida para este dispositivo";
        	Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        	toast.show();
        	this.onBackPressed();
    	}*/
	}
	
	private void getValues() {
		Intent i = new Intent(this, GetValuesActivity.class);
		i.putExtra(HomeActivity.ISGETVALUES, true);
		this.startActivity(i);
	}
	
	private void takenValues() {
		Intent i = new Intent(this, GetValuesActivity.class);
		i.putExtra(HomeActivity.ISGETVALUES, false);
		this.startActivity(i);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ButtonTomarMediciones:
			ElementListLoader.removeStoredElements();
			if (ElementListLoader.getPendingElements().size() > 0) {
				if (NewsListLoader.getNews().size() > 0) {
					this.getValues();
				} else {
		        	Context context = getApplicationContext();
		        	String msg = "No se encontró el archivo de novedades, tiene errores en su estructura o está vacío.";
		        	Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		        	toast.show();
				}
			} else {
				if (ElementListLoader.getTakenElements().size() > 0) {
		        	Context context = getApplicationContext();
		        	String msg = "Se completó la toma de mediciones, no hay elementos pendientes";
		        	Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		        	toast.show();
				} else {
		        	Context context = getApplicationContext();
		        	String msg = "No se encontró el archivo de ruta "+ ElementListLoader.pathRuta+", tiene errores en su estructura o está vacío.";
		        	Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		        	toast.show();
				}
			}
			break;
		case R.id.ButtonMedicionesTomadas:
			if (ElementListLoader.getTakenElements().size() > 0) {
				if (NewsListLoader.getNews().size() > 0) {
					this.takenValues();
				} else {
		        	Context context = getApplicationContext();
		        	String msg = "No se encontró el archivo de novedades, tiene errores en su estructura o está vacío.";
		        	Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		        	toast.show();
				}
			} else {
	        	Context context = getApplicationContext();
	        	String msg = "No se encontraron mediciones tomadas.";
	        	Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
	        	toast.show();
			}
			break;
		case R.id.ButtonContadores:
			this.showCounters();
			break;
		case R.id.ButtonSettings:
			this.showSettings();
			break;
		case R.id.ButtonExportar:
			this.showEmailExport();
			break;
		case R.id.ButtonSalir:
			this.onBackPressed();
            break;
		default:
			break;
		}
	}

	private void showSettings() {
		Intent i = new Intent(this, SettingsActivity.class);
		this.startActivity(i);
	}

	private void showCounters() {
		Intent i = new Intent(this, CountersActivity.class);
		this.startActivity(i);
	}
	
/*	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == DirectoryPicker.PICK_DIRECTORY && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			String path = (String) extras.get(DirectoryPicker.CHOSEN_DIRECTORY);
			ElementsReadExporter.exportReadElements(path, this);
		}
	}*/

	private void showEmailExport() {
		File storageDirTemp = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS
						+ "/.DDC/");
		if (!storageDirTemp.exists()) {
			storageDirTemp.mkdir();
		}
		String msg = "Se ha creado directorio temporal de almacenamiento en "+ storageDirTemp.getAbsolutePath();
		Context context = getApplicationContext();
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		toast.show();
		File exportReads = ElementsReadExporter.exportReadElements(storageDirTemp.getAbsolutePath(), this);
		Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		intent.putExtra(Intent.EXTRA_EMAIL,
				new String[] { "jpgiecco@gmail.com" });
		intent.putExtra(Intent.EXTRA_SUBJECT, "[Toma Estado Gupo Servicios Junín] Resultados");
		if (ElementListLoader.isFinRelevamiento()) {
			intent.putExtra(Intent.EXTRA_SUBJECT,
					"[Toma Estado Grupo Servicios Junín] Resultados - Versión Final");
		}
		intent.putExtra(Intent.EXTRA_TEXT,
				"Envío de Resultados de toma estado Grupo Servicios Junín");
		intent.setType("text/plain");
		intent.putExtra(
				Intent.EXTRA_STREAM,
				new ArrayList<Uri>(Arrays.asList(
						Uri.fromFile(exportReads))));
		startActivityForResult(Intent.createChooser(intent, "Send Email"),
				REQUEST_CODE_EXPORT_MAIL);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_EXPORT_MAIL) {
			Context context = getApplicationContext();
			Toast.makeText(context, "Mail enviado con éxtito!", Toast.LENGTH_SHORT).show();
		}
	}

}
