package ar.com.eurekaconsulting.elementControl;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import ar.com.eurekaconsulting.elementControl.R;
import ar.com.eurekaconsulting.elementControl.model.New;
import ar.com.eurekaconsulting.elementControl.util.NewsListLoader;

public class SelectNewActivity extends Activity {

	private static final int REQUEST_CODE = 1;
	private ListView listNovedades;
	private Handler postDelayHandler;
	private Runnable postDelayRunnable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_new);
		
		/* Go Previous Activity Button */
		Button btnHome = (Button) findViewById(R.id.ButtonHome);
		btnHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SelectNewActivity.this.onBackPressed();
			}
		});


		/* Novedades ComboBox */
		listNovedades = (ListView) findViewById(R.id.listViewNovedades);
		ArrayAdapter<New> adp1 = new ArrayAdapter<New>(this,
				android.R.layout.simple_list_item_1, NewsListLoader.getNews());
		adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		listNovedades.setAdapter(adp1);
		listNovedades.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int itemPos,
					long arg3) {
				SelectNewActivity.this.backToGetValuesActivity((New) SelectNewActivity.this.listNovedades.getItemAtPosition(itemPos), SelectNewActivity.this.getIntent().getIntExtra("index", -1));
				SelectNewActivity.this.onBackPressed();
				
			}
        });
		
		this.postDelayHandler = new Handler();
		this.postDelayRunnable = new Runnable() {
			public void run() {
				SelectNewActivity.this
						.startVoiceRecognitionActivity();
			}
		};
		this.postDelayHandler.postDelayed(this.postDelayRunnable, 5000);
		/*
		 * listNovedades.setOnItemSelectedListener(new OnItemSelectedListener()
		 * {
		 * 
		 * @Override public void onItemSelected(AdapterView<?> arg0, View arg1,
		 * int position, long id) {
		 * GetValuesActivity.this.getCurrentElement().setNovedad( (New)
		 * arg0.getSelectedItem()); }
		 * 
		 * @Override public void onNothingSelected(AdapterView<?> arg0) { //
		 * TODO Auto-generated method stub
		 * 
		 * }
		 * 
		 * });
		 */

	}

	public void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"Ingrese el código de novedad ");
		startActivityForResult(intent, REQUEST_CODE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			try{
				ArrayList<String> matches = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				Integer numberRecognized = Integer.parseInt(StringMatchesAnalizer.getInstance().getBestMatch(matches));
				this.backToGetValuesActivity((New) this.listNovedades.getItemAtPosition(numberRecognized-1), this.getIntent().getIntExtra("index", -1));
				this.onBackPressed();
			} catch (Exception e) {
				this.startVoiceRecognitionActivity();
			}
		}
	}
	
	private void backToGetValuesActivity(New novedad, Integer index) {
		/*Intent intent = new Intent(this, GetValuesActivity.class);
		Intent intent = this.getIntent();
		intent.putExtra("novedad", novedad.getCode());
		intent.putExtra("index", index);
		intent.putExtra("autoNav", this.getIntent().getBooleanExtra("autoNav", true));
		//this.startActivity(intent);
		this.finish();*/
		
		Intent resultIntent = new Intent();
		resultIntent.putExtra("novedad", novedad.getCode());
		resultIntent.putExtra("index", index);
		resultIntent.putExtra("autoNav", this.getIntent().getBooleanExtra("autoNav", true));
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}
	
	public void removeCallBacks() {
		if (this.postDelayHandler != null) {
			this.postDelayHandler.removeCallbacks(this.postDelayRunnable);
		}
	}

	@Override
	public void onBackPressed() {
		this.removeCallBacks();
		super.onBackPressed();
	}

}
