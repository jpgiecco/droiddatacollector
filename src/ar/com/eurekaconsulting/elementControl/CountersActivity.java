package ar.com.eurekaconsulting.elementControl;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import ar.com.eurekaconsulting.elementControl.util.ElementListLoader;

public class CountersActivity extends Activity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.counters);	
		
		/* Go home Button */
		Button btnHome = (Button)findViewById(R.id.ButtonHome);
		btnHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CountersActivity.this.onBackPressed();
			}
		});
		
		TextView elementTotalMediciones = (TextView) findViewById(R.id.textViewMedicionesARealizar);
    	elementTotalMediciones.setText(Integer.valueOf(ElementListLoader.getTotalMediciones()).toString());
    	
		TextView elementMedicionesRealizadas = (TextView) findViewById(R.id.textViewMedicionesRealizadas);
		elementMedicionesRealizadas.setText(Integer.valueOf(ElementListLoader.getMedicionesRealizadas()).toString());
    	
		TextView elementMedicionesFaltantes = (TextView) findViewById(R.id.TextViewMedicionesFaltantes);
		elementMedicionesFaltantes.setText(Integer.valueOf(ElementListLoader.getMedicionesFaltantes()).toString());
	}


}
