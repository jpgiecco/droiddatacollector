package ar.com.eurekaconsulting.elementControl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.util.ArrayList;
import java.util.List;

import ar.com.eurekaconsulting.elementControl.model.Element;
import ar.com.eurekaconsulting.elementControl.util.ElementListLoader;
import ar.com.eurekaconsulting.elementControl.util.NewsListLoader;
import ar.com.eurekaconsulting.elementControl.util.Store;

public class TakenValuesActivity extends Activity implements OnGestureListener {

	private GestureDetector gesturedetector = null;

	private Dialog dialog;

	private static final int SWIPE_THRESHOLD = 100;
	private static final int SWIPE_VELOCITY_THRESHOLD = 100;
	private static final int REQUEST_CODE = 1;
	private static final int REQUEST_CODE_TWICE = 2;
	private static final int REQUEST_CODE_NEW = 3;
	private static final Long READ_ELEMENT_CERO_VALUE = -1L;
	private long delayForReadingInMillis;
	private long delayForNotificationsInMillis;
	private int elementIndex = -1;
	private Element currentElement;
	private Handler postDelayHandler;
	private Runnable postDelayRunnable;
	private EditText elementActualValueEditText;
	private boolean inputKeyFirstRecognizement = true;
	private boolean searchElementByCodeWindowsOpened = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.get_values);

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		this.delayForReadingInMillis = Long.parseLong(sharedPref.getString(
				"delay_for_reading", "7000"));
		this.delayForNotificationsInMillis = Long.parseLong(sharedPref
				.getString("delay_for_notifications", "4000"));

		/*this.autoNav = (ToggleButton) findViewById(R.id.toggleButtonAutoNav);
		this.autoNav.setChecked(this.getIntent().getBooleanExtra("autoNav",
				false));		
		this.autoNav.setEnabled(false);
		this.autoNav.setVisibility(View.GONE);*/

		/* Manual input button */
		Button buttonTakeAgain = (Button) findViewById(R.id.buttonTakeAgain);
		buttonTakeAgain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				TakenValuesActivity.this.removeCallBacks();
				TakenValuesActivity.this.startVoiceRecognitionActivity(false,
						true);
			}
		});
		
		/* Menu Busqueda button */
		Button buttonSearch = (Button) findViewById(R.id.buttonSearch);
		buttonSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				openOptionsMenu();
			}
		});

		/* Select New Button */
		Button btnNovedad = (Button) findViewById(R.id.ButtonIngresarNovedad);
		btnNovedad.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				TakenValuesActivity.this.removeCallBacks();
				TakenValuesActivity.this.selectNew();
			}
		});

		/* Next Button */
		Button btnSiguiente = (Button) findViewById(R.id.ButtonSiguiente);
		btnSiguiente.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				TakenValuesActivity.this.removeCallBacks();
				TakenValuesActivity.this.loadNextElement();
			}
		});

		/* Go Previous Activity Button */
		Button btnHome = (Button) findViewById(R.id.ButtonHome);
		btnHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				TakenValuesActivity.this.onBackPressed();
			}
		});

		this.elementActualValueEditText = (EditText) findViewById(R.id.editTextRealStock);

		this.elementActualValueEditText
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						TakenValuesActivity.this.removeCallBacks();
						//TakenValuesActivity.this.autoNav.setChecked(false);
					}
				});

		this.elementActualValueEditText
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView tv, int keyCode,
							KeyEvent event) {
						if (keyCode == EditorInfo.IME_ACTION_DONE
								|| keyCode == EditorInfo.IME_ACTION_SEND
								|| keyCode == EditorInfo.IME_ACTION_NEXT) {
							try {
								InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								Long value = Long.parseLong(tv.getText()
										.toString());
								if (tv.getText().toString().equals("0")) {
									TakenValuesActivity.this.getCurrentElement()
											.setActualValue(-1L);
									imm.hideSoftInputFromWindow(
											TakenValuesActivity.this.elementActualValueEditText
													.getWindowToken(), 0);
									Store.saveElement(TakenValuesActivity.this
											.getCurrentElement());
									//TakenValuesActivity.this.autoNav.setChecked(true);
									TakenValuesActivity.this.loadNextElement();
								} else {
									//TakenValuesActivity.this.autoNav.setChecked(true);
									imm.hideSoftInputFromWindow(
											TakenValuesActivity.this.elementActualValueEditText
													.getWindowToken(), 0);
									if (!TakenValuesActivity.this.inputKeyFirstRecognizement) {
										checkSecondInput(value);
									} else {
										checkFirstInput(value);
									}
									/*
									 * GetValuesActivity.this
									 * .getCurrentElement() .setActualValue(
									 * Long.parseLong(tv.getText().toString()));
									 */
								}
								/*
								 * Store.saveElement(GetValuesActivity.this
								 * .getCurrentElement());
								 */

								return true;
							} catch (Exception e) {
								//GetValuesActivity.this.autoNav.setChecked(true);
								return false;
								// GetValuesActivity.this.getCurrentElement().setActualValue(0L);
								// Store.saveElement(GetValuesActivity.this.getCurrentElement());
							}
						} else {
							return false;
						}
					}

				});

		/*
		 * this.elementActualValueEditText .addTextChangedListener(new
		 * TextWatcher() {
		 * 
		 * @Override public void afterTextChanged(Editable editable) { /*try {
		 * GetValuesActivity.this .getCurrentElement() .setActualValue(
		 * Long.parseLong(editable.toString()));
		 * Store.saveElement(GetValuesActivity.this .getCurrentElement());
		 * GetValuesActivity.this.loadNextElementDelayed(); } catch (Exception
		 * e) { //
		 * GetValuesActivity.this.getCurrentElement().setActualValue(0L); //
		 * Store.saveElement(GetValuesActivity.this.getCurrentElement()); } }
		 * 
		 * @Override public void beforeTextChanged(CharSequence arg0, int arg1,
		 * int arg2, int arg3) {
		 * 
		 * }
		 * 
		 * @Override public void onTextChanged(CharSequence arg0, int arg1, int
		 * arg2, int arg3) { GetValuesActivity.this.autoNav.setChecked(false);
		 * GetValuesActivity.this.removeCallBacks(); }
		 * 
		 * });
		 */

		// Gesture for navigation
		gesturedetector = new GestureDetector(this, this);

		/*
		 * Integer index = this.getIntent().getIntExtra("index", -1); if (index
		 * != -1) { Integer codigoNovedad =
		 * this.getIntent().getIntExtra("novedad", -1); if (codigoNovedad != -1)
		 * { this.elementIndex = index; Element elementAtIndex =
		 * ElementListLoader.getElements().get( this.elementIndex);
		 * elementAtIndex.setNovedad(NewsListLoader
		 * .getNewByCode(codigoNovedad)); Store.saveElement(elementAtIndex);
		 * this.updateElement(this.elementIndex); }
		 * this.loadNextElementDelayed(); } else { // Get element to process
		 * this.loadNextElement(); }
		 */

		this.loadNextElement();

	}

	private void selectNew() {
		Intent i = new Intent(this, SelectNewActivity.class);
		i.putExtra("index", elementIndex);
		//i.putExtra("autoNav", this.autoNav.isChecked());
		this.startActivityForResult(i, REQUEST_CODE_NEW);
		// this.finish();
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.activity_main, menu); return true; }
	 */

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		boolean result = false;
		try {
			float diffY = e2.getY() - e1.getY();
			float diffX = e2.getX() - e1.getX();
			if (Math.abs(diffX) > Math.abs(diffY)) {
				if (Math.abs(diffX) > SWIPE_THRESHOLD
						&& Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
					if (diffX > 0) {
						onSwipeRight();
					} else {
						onSwipeLeft();
					}
				}
			} else {
				if (Math.abs(diffY) > SWIPE_THRESHOLD
						&& Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
					if (diffY > 0) {
						onSwipeBottom();
					} else {
						onSwipeTop();
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return result;
	}

	private void onSwipeLeft() {
		//if (!this.autoNav.isChecked()) {
		this.loadNextElement();
		//}
	}

	private void onSwipeTop() {
		// TODO Auto-generated method stub

	}

	private void onSwipeBottom() {
		// TODO Auto-generated method stub

	}

	private void onSwipeRight() {
		//if (!this.autoNav.isChecked()) {
		this.loadPreviousElement();
		//}
	}

	public void loadNextElementRec() {
		if (elementIndex < ElementListLoader.getTakenElements().size() - 1) {
			elementIndex++;
		} else {
			elementIndex = 0;
		}
		/*if (this.autoNav.isChecked()) {
			if (!ElementListLoader.isFinRelevamiento()) {
				if (ElementListLoader.getTakenElements().get(elementIndex)
						.isRelevado()) {
					this.loadNextElement();
				} else {
					this.updateElement(elementIndex);
				}
			} else {
				this.removeCallBacks();
				this.autoNav.setChecked(false);
				this.updateElement(elementIndex);
			}
		} else {*/
		this.updateElement(elementIndex);
		//}
	}

	public void loadNextElement() {
		Store.saveElement(this.currentElement);
		if (elementIndex < ElementListLoader.getTakenElements().size() - 1) {
			elementIndex++;
		} else {
			elementIndex = 0;
		}
		this.updateElement(elementIndex);
	}

	private void updateElement(int elementIndex) {
		this.currentElement = new ArrayList<Element>(ElementListLoader.getTakenElements().values()).get(elementIndex);

		TextView elementCodeTextView = (TextView) findViewById(R.id.textViewProductCode);
		elementCodeTextView.setText(this.currentElement.getCode());

		TextView elementServiceTextView = (TextView) findViewById(R.id.ServiceType);
		elementServiceTextView.setText(this.currentElement.getService());

		TextView elementDescriptionEditText = (TextView) findViewById(R.id.textViewProductDescription);
		elementDescriptionEditText
				.setText(this.currentElement.getDescription());

		TextView novedadesTextView = (TextView) findViewById(R.id.TextViewNovedad);
		if (this.getCurrentElement().getNovedad() != null) {
			novedadesTextView.setText(this.getCurrentElement().getNovedad()
					.toString());
		} else {
			novedadesTextView.setText("Ninguna");
		}

		/*
		 * EditText elementCurrentValue = (EditText)
		 * findViewById(R.id.editTextStockActual);
		 * elementCurrentValue.setText(this
		 * .print(this.currentElement.getPreviousValue()));
		 */
		
		EditText elementPreviousValue = (EditText) findViewById(R.id.EditTextEstadoAnterior);
		elementPreviousValue.setText(this.print(this.currentElement.getPreviousValue()));

		EditText elementActualValue = (EditText) findViewById(R.id.editTextRealStock);
		elementActualValue.setText(this.print(this.currentElement
				.getActualValue()));
		
		EditText elementConsumption = (EditText) findViewById(R.id.EditTextConsumo);
		if (this.currentElement.getActualValue() != 0) {
			elementConsumption.setText(this.print(this.currentElement.getActualValue() - this.currentElement.getPreviousValue()));
		}
		
		TextView elementUsuario = (TextView) findViewById(R.id.TextViewUsuario);
		elementUsuario.setText(this.getCurrentElement().getUsuario());
		
		/*if (this.autoNav.isChecked()) {
			this.startVoiceRecognitionDelayed(false, false);
		}*/
	}

	public CharSequence print(Long realStock) {
		if (realStock == 0L) {
			return "";
		} else {
			if (realStock == -1L) {
				return "";
			} else {
				return realStock.toString();
			}
		}
	}

	private void loadPreviousElement() {
		if (elementIndex > 0) {
			elementIndex--;
		} else {
			elementIndex = ElementListLoader.getTakenElements().size();
		}
		this.updateElement(elementIndex);
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gesturedetector.onTouchEvent(event);
	}

	/**
	 * Fire an intent to start the voice recognition activity.
	 * 
	 * @param force
	 * 
	 * @param b
	 */
	public void startVoiceRecognitionActivity(boolean secondRecognizement,
			boolean force) {
		if (force || secondRecognizement
				|| (this.getCurrentElement().getActualValue() == 0L)) {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			if (secondRecognizement) {
				intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
						"Ingrese nuevamente la medición para "
								+ this.getCurrentElement().getDescription()
										.toUpperCase());
				startActivityForResult(intent, REQUEST_CODE_TWICE);
			} else {
				intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
						"Ingrese la medición para "
								+ this.getCurrentElement().getDescription()
										.toUpperCase());
				startActivityForResult(intent, REQUEST_CODE);
			}
		}
	}

	/**
	 * Handle the results from the voice recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE:
				this.processFirstRecognizement(data);
				break;
			case REQUEST_CODE_TWICE:
				this.processSecondRecognizement(data);
				break;
			case REQUEST_CODE_NEW:
				this.processNewSelection(data);
				break;

			default:
				break;
			}
		} else if (resultCode == RESULT_CANCELED) {
			/*if (this.autoNav.isChecked()) {
				this.startVoiceRecognitionDelayed(false, false);
			}*/
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void processNewSelection(Intent data) {
		Integer codigoNovedad = data.getIntExtra("novedad", -1);
		if (codigoNovedad != -1) {
			this.getCurrentElement().setNovedad(
					NewsListLoader.getNewByCode(codigoNovedad));
			//Store.saveElement(this.getCurrentElement());
			this.updateElement(this.elementIndex);
		}
		// this.loadNextElementDelayed();
		this.startVoiceRecognitionDelayed(false, false);
		Store.saveElement(this.getCurrentElement());
	}

	private void processSecondRecognizement(Intent data) {
		// Populate the wordsList with the String values the recognition
		// engine thought it heard
		ArrayList<String> matches = data
				.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
		try {
			Long recognizedNumber = Long.parseLong(StringMatchesAnalizer
					.getInstance().getBestMatch(matches));
			/*
			 * this.currentElement.setActualValue(recognizedNumber); EditText
			 * elementActualValueEditText = (EditText)
			 * findViewById(R.id.editTextRealStock);
			 * elementActualValueEditText.setText(this.print(this.currentElement
			 * .getActualValue())); Store.saveElement(this.getCurrentElement());
			 * if (this.autoNav.isChecked()) { this.loadNextElementDelayed(); }
			 */
			this.checkSecondInput(recognizedNumber);
			/*
			 * if (recognizedNumber.equals(0L)) { this.loadNextElement(); } else
			 * if (this.valorIgualAlAnterior()) {
			 * Store.saveElement(this.getCurrentElement()); if
			 * (this.autoNav.isChecked()) { this.loadNextElementDelayed(); } }
			 * else { if (this.diferenciaNegativa()) {
			 * this.initiatePopupWindow("NEGATIVO", false); } else {
			 * this.previousValue = this.currentElement.getActualValue();
			 * this.initiatePopupWindow("CONTROLAR", true); } }
			 */

		} catch (NumberFormatException nfe) {
			this.startVoiceRecognitionActivity(true, false);
		}
	}

	private void processFirstRecognizement(Intent data) {
		// Populate the wordsList with the String values the recognition
		// engine thought it heard
		ArrayList<String> matches = data
				.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
		try {
			Long recognizedNumber = Long.parseLong(StringMatchesAnalizer
					.getInstance().getBestMatch(matches));
			/*
			 * this.currentElement.setActualValue(recognizedNumber); EditText
			 * elementActualValueEditText = (EditText)
			 * findViewById(R.id.editTextRealStock);
			 * elementActualValueEditText.setText(this.print(this.currentElement
			 * .getActualValue())); this.previousValue =
			 * this.currentElement.getActualValue(); if
			 * (recognizedNumber.equals(0L)) {
			 * this.getCurrentElement().setActualValue
			 * (GetValuesActivity.READ_ELEMENT_CERO_VALUE);
			 * Store.saveElement(this.getCurrentElement());
			 * this.loadNextElement(); } else if (this.diferenciaNegativa()) {
			 * this.initiatePopupWindow("NEGATIVO", true); } else if
			 * (!this.diferenciaCorrecta()) {
			 * this.initiatePopupWindow("CONTROLAR", true); } else {
			 * Store.saveElement(this.getCurrentElement());
			 * this.loadNextElementDelayed(); }
			 */
			this.checkFirstInput(recognizedNumber);

		} catch (NumberFormatException nfe) {
			this.startVoiceRecognitionActivity(false, false);
		}
	}

	/* For testing purposes */
	/*private void saveNext99ElementsWithSameValue(Long actualValue) {
		for (int i = 0; i < 100; i++) {
			GetValuesActivity.this.loadNextElement();
			GetValuesActivity.this.getCurrentElement().setActualValue(
					actualValue);
			Store.saveElement(GetValuesActivity.this.getCurrentElement());
		}
	}*/

	private void checkFirstInput(Long value) {
		this.currentElement.setActualValue(value);
		EditText elementActualValueEditText = (EditText) findViewById(R.id.editTextRealStock);
		elementActualValueEditText.setText(this.print(this.currentElement
				.getActualValue()));
		if (value.equals(0L)) {
			this.getCurrentElement().setActualValue(
					TakenValuesActivity.READ_ELEMENT_CERO_VALUE);
			Store.saveElement(this.getCurrentElement());
			this.loadNextElement();
		} else if (this.diferenciaNegativa()) {
			this.initiatePopupWindow("NEGATIVO", true);
		} else if (!this.diferenciaCorrecta()) {
			this.initiatePopupWindow("CONTROLAR", true);
		} else {
			Store.saveElement(this.getCurrentElement());
		}
		TakenValuesActivity.this.inputKeyFirstRecognizement = false;
	}

	private void checkSecondInput(Long value) {
		this.currentElement.setActualValue(value);
		EditText elementActualValueEditText = (EditText) findViewById(R.id.editTextRealStock);
		elementActualValueEditText.setText(this.print(this.currentElement
				.getActualValue()));
		Store.saveElement(this.getCurrentElement());
		// this.saveNext99ElementsWithSameValue(value);
		//if (this.autoNav.isChecked()) {
		//this.loadNextElementDelayed();
		//}
		TakenValuesActivity.this.inputKeyFirstRecognizement = true;
	}

	private void startVoiceRecognitionDelayed(
			final boolean secondrecognizement, final boolean force) {
		this.removeCallBacks();
		this.postDelayHandler = new Handler();
		this.postDelayRunnable = new Runnable() {
			public void run() {
				TakenValuesActivity.this.startVoiceRecognitionActivity(
						secondrecognizement, force);
			}
		};
		this.postDelayHandler.postDelayed(this.postDelayRunnable,
				this.delayForReadingInMillis);
	}

	private void initiatePopupWindow(String text,
			final boolean secondRecognizement) {
		try {
			this.dialog = new Dialog(this);
			this.dialog.setContentView(R.layout.popup);
			this.dialog.setTitle(text);
			this.dialog.setCancelable(true);

			// set up text
			TextView textViewPopup = (TextView) dialog
					.findViewById(R.id.textViewPopup);
			textViewPopup.setText("INGRESE NUEVAMENTE LA MEDICION");

			// now that the dialog is set up, it's time to show it
			this.dialog.show();

			// new Thread(PopupWindowThread.getInstance(this)).start();
			new Handler().postDelayed(new Runnable() {
				public void run() {
					TakenValuesActivity.this.getDialog().dismiss();
					TakenValuesActivity.this.postDelayHandler = new Handler();
					TakenValuesActivity.this.startVoiceRecognitionDelayed(
							secondRecognizement, false);
				}
			}, this.delayForNotificationsInMillis);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Dialog getDialog() {

		return dialog;
	}

	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}

	public GestureDetector getGesturedetector() {
		return gesturedetector;
	}

	public void setGesturedetector(GestureDetector gesturedetector) {
		this.gesturedetector = gesturedetector;
	}

	public Element getCurrentElement() {
		return currentElement;
	}

	public void setCurrentElement(Element currentElement) {
		this.currentElement = currentElement;
	}

	public int getElementIndex() {
		return elementIndex;
	}

	public void setProductIndex(int productIndex) {
		this.elementIndex = productIndex;
	}

	private boolean diferenciaNegativa() {
		return this.getCurrentElement().getActualValue()
				- this.currentElement.getPreviousValue() < 0;
	}

	private boolean diferenciaCorrecta() {
		return this.getCurrentElement().getActualValue()
				- this.currentElement.getPreviousValue() <= this.currentElement
					.getMaxDifference();
	}

	/*private boolean valorIgualAlAnterior() {
		return this.getCurrentElement().getActualValue()
				.equals(this.previousValue);
	}*/

	@Override
	protected void onDestroy() {
		this.removeCallBacks();
		super.onDestroy();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.layout.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.search_element:
			searchByElementCode();
			return true;

		case R.id.first_element:
			loadFirstElement();
			return true;

		case R.id.last_element:
			loadLastElement();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void loadFirstElement() {
		elementIndex = -1;
		loadNextElement();
		//TakenValuesActivity.this.autoNav.setChecked(true);
	}

	private void loadLastElement() {
		elementIndex = ElementListLoader.getTakenElements().size();
		loadPreviousElement();
		//TakenValuesActivity.this.autoNav.setChecked(true);
	}

	private void searchByElementCode() {
		
		this.searchElementByCodeWindowsOpened = true;

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Buscar Medidor");
		alert.setMessage("Ingrese el número de medidor");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				int index = searchByElementCode(input.getText().toString());
				if (index >= 0) {
					TakenValuesActivity.this.elementIndex = searchByElementCode(input
							.getText().toString());
				}
				TakenValuesActivity.this
						.updateElement(TakenValuesActivity.this.elementIndex);
				TakenValuesActivity.this.searchElementByCodeWindowsOpened = false;
				//TakenValuesActivity.this.autoNav.setChecked(true);
			}

		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						TakenValuesActivity.this.searchElementByCodeWindowsOpened = false;
						//TakenValuesActivity.this.autoNav.setChecked(true);
					}
				});

		alert.show();
	}

	private int searchByElementCode(String code) {
		int index = 0;
		int result = -1;
		for (Element element : ElementListLoader.getTakenElements().values()) {
			if (element.getCode().equals(code)) {
				result = index;
			}
			index++;
		}
		return result;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			//TakenValuesActivity.this.autoNav.setChecked(false);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		/*if (!this.searchElementByCodeWindowsOpened) {
			TakenValuesActivity.this.autoNav.setChecked(true);	
		}*/
		super.onOptionsMenuClosed(menu);
	}

}
