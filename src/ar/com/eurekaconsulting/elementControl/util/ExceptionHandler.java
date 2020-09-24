package ar.com.eurekaconsulting.elementControl.util;

import java.io.*;
import android.content.*;
import android.os.Process;
import android.widget.Toast;

public class ExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
    
	private final Context myContext;

    public ExceptionHandler(Context context) {
        myContext = context;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        System.err.println(stackTrace);

        /*Intent intent = new Intent(myContext, LogCollector.class);
        myContext.startActivity(intent);*/
        
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"jpgiecco@eureka-consulting.com.ar"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Reporte de Errores - Element Control");
        i.putExtra(Intent.EXTRA_TEXT   , stackTrace.toString());
        try {
        	myContext.startActivity(Intent.createChooser(i, "Error desconocido - Enviar el reporte del error"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(myContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

        Process.killProcess(Process.myPid());
        System.exit(10);
    }
}
