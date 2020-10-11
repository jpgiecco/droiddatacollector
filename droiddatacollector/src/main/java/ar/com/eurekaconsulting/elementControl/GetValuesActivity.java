package ar.com.eurekaconsulting.elementControl;

import android.annotation.SuppressLint;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ar.com.eurekaconsulting.elementControl.model.Element;
import ar.com.eurekaconsulting.elementControl.util.ElementListLoader;
import ar.com.eurekaconsulting.elementControl.util.NewsListLoader;
import ar.com.eurekaconsulting.elementControl.util.Store;

public class GetValuesActivity extends Activity implements OnGestureListener {

	private GestureDetector gesturedetector = null;

	private Dialog dialog;

	private static final int SWIPE_THRESHOLD = 100;
	private static final int SWIPE_VELOCITY_THRESHOLD = 100;
	//private static final int REQUEST_CODE = 1;
	//private static final int REQUEST_CODE_TWICE = 2;
	private static final int REQUEST_CODE_NEW = 3;
	//private static final Long READ_ELEMENT_CERO_VALUE = -1L;
	private long delayForReadingInMillis;
	private long delayForNotificationsInMillis;
	private boolean voiceRecognition;
	private int elementIndex = -1;
	private Element currentElement;
	//private Handler postDelayHandler;
	//private Runnable postDelayRunnable;
	private EditText elementActualValueEditText;
	private boolean inputKeyFirstRecognizement = true;
	//private boolean searchElementByCodeWindowsOpened = false;
	private boolean isGetValues;
	private List<Element> elements;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.get_values);

		Intent intent = this.getIntent();
		this.isGetValues = intent.getBooleanExtra(HomeActivity.ISGETVALUES, true);

		if (isGetValues) {
			elements = ElementListLoader.getPendingElements();
		} else {
			elements = new ArrayList<Element>(ElementListLoader.getTakenElements().values());
		}

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		this.delayForReadingInMillis = Long.parseLong(sharedPref.getString(
				"delay_for_reading", "7000"));
		this.delayForNotificationsInMillis = Long.parseLong(sharedPref
				.getString("delay_for_notifications", "4000"));
		this.voiceRecognition = sharedPref.getBoolean("voice_recognition",
				false);
		
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
				GetValuesActivity.this.selectNew();
			}
		});
		
		/* Next Button */
		Button btnSiguiente = (Button) findViewById(R.id.ButtonSiguiente);
		btnSiguiente.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				GetValuesActivity.this.saveElement();
				GetValuesActivity.this.loadNextElement();
			}
		});

		/* Go Previous Activity Button */
		Button btnHome = (Button) findViewById(R.id.ButtonHome);
		btnHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				GetValuesActivity.this.onBackPressed();
			}
		});

		this.elementActualValueEditText = (EditText) findViewById(R.id.editTextRealStock);

		this.elementActualValueEditText.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				GetValuesActivity.this.inactivateButtons();
				return false;
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
									imm.hideSoftInputFromWindow(
											GetValuesActivity.this.elementActualValueEditText
													.getWindowToken(), 0);
									Store.saveElement(GetValuesActivity.this
											.getCurrentElement());
									// GetValuesActivity.this.autoNav.setChecked(true);
									/*if (GetValuesActivity.this.autoNav
											.isChecked()) {
										GetValuesActivity.this
												.loadNextElement();
									}*/
								} else {
									// GetValuesActivity.this.autoNav.setChecked(true);
									imm.hideSoftInputFromWindow(
											GetValuesActivity.this.elementActualValueEditText
													.getWindowToken(), 0);
									if (!GetValuesActivity.this.inputKeyFirstRecognizement) {
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
								GetValuesActivity.this.activateButtons();
								return true;
							} catch (Exception e) {
								GetValuesActivity.this.activateButtons();
								return false;
							}
						} else {
							GetValuesActivity.this.activateButtons();
							return false;
						}
					}

				});

		// Gesture for navigation
		gesturedetector = new GestureDetector(this, this);

		this.loadNextElement();

	}

	private void inactivateButtons() {
		/* Menu Busqueda button */
		Button buttonSearch = (Button) findViewById(R.id.buttonSearch);
		buttonSearch.setEnabled(false);

		/* Select New Button */
		Button btnNovedad = (Button) findViewById(R.id.ButtonIngresarNovedad);
		btnNovedad.setEnabled(false);

		/* Next Button */
		Button btnSiguiente = (Button) findViewById(R.id.ButtonSiguiente);
		btnSiguiente.setEnabled(false);
	}

	private void activateButtons() {
		/* Menu Busqueda button */
		Button buttonSearch = (Button) findViewById(R.id.buttonSearch);
		buttonSearch.setEnabled(true);

		/* Select New Button */
		Button btnNovedad = (Button) findViewById(R.id.ButtonIngresarNovedad);
		btnNovedad.setEnabled(true);

		/* Next Button */
		Button btnSiguiente = (Button) findViewById(R.id.ButtonSiguiente);
		btnSiguiente.setEnabled(true);
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
		//No hay funciones asociadas

	}

	private void onSwipeBottom() {
		//No hay funciones asociadas
	}

	private void onSwipeRight() {
		//if (!this.autoNav.isChecked()) {
		this.loadPreviousElement();
		//}
	}

	public void saveElement(){
		Store.saveElement(this.getCurrentElement());
	}

	public void loadNextElement() {
		if (elementIndex < this.elements.size() - 1) {
			elementIndex++;
		} else {
			elementIndex = 0;
		}
		this.updateElement(elementIndex);
	}

	private void updateElement(int elementIndex) {
		this.currentElement = this.elements.get(elementIndex);

		if (this.currentElement.isDebt()) {
			this.showDebtAlert();
		}

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
		elementPreviousValue.setText(this.print(this.currentElement
				.getPreviousValue()));

		EditText elementActualValue = (EditText) findViewById(R.id.editTextRealStock);
		elementActualValue.setText(this.print(this.currentElement
				.getActualValue()));

		TextView elementConsumptionTV = (TextView) findViewById(R.id.TextViewConsumo);
		elementConsumptionTV.setVisibility(View.GONE);
		EditText elementConsumption = (EditText) findViewById(R.id.EditTextConsumo);
		elementConsumption.setVisibility(View.GONE);
		
		TextView elementUsuario = (TextView) findViewById(R.id.TextViewUsuario);
		elementUsuario.setText(this.getCurrentElement().getUsuario());

	}

	private void showDebtAlert() {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.debt_alert,
				(ViewGroup) findViewById(R.id.toast_layout_root));

		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText("Cliente con deuda");

		final Dialog alert = new Dialog(this);
		alert.setContentView(layout);
		alert.setTitle("Alerta!");
		alert.show();
		layout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//if (v.equals(alert)){
					alert.cancel();
				//}
				return true;
			}
		});
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
			elementIndex = this.elements.size();
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
	 * Handle the results from the voice recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_NEW:
				this.processNewSelection(data);
				break;
			default:
				break;
			}
		} else if (resultCode == RESULT_CANCELED) {
			//Nothing here
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void processNewSelection(Intent data) {
		Integer codigoNovedad = data.getIntExtra("novedad", -1);
		if (codigoNovedad != -1) {
			this.getCurrentElement().setNovedad(
					NewsListLoader.getNewByCode(codigoNovedad));
			Store.saveElement(this.getCurrentElement());
			this.updateElement(this.elementIndex);
		}
	}

	private void checkFirstInput(Long value) {
		this.currentElement.setActualValue(value);
		EditText elementActualValueEditText = (EditText) findViewById(R.id.editTextRealStock);
		elementActualValueEditText.setText(this.print(this.currentElement
				.getActualValue()));
		if (value.equals(0L)) {
			Store.saveElement(this.getCurrentElement());
			this.loadNextElement();
		} else if (this.diferenciaNegativa()) {
			this.initiatePopupWindow("NEGATIVO", true);
		} else if (!this.diferenciaCorrecta()) {
			this.initiatePopupWindow("CONTROLAR", true);
		} else {
			Store.saveElement(this.getCurrentElement());
			/*if (this.autoNav.isChecked()) {
				this.loadNextElementDelayed();
			}*/
		}
		GetValuesActivity.this.inputKeyFirstRecognizement = false;
	}

	private void checkSecondInput(Long value) {
		this.currentElement.setActualValue(value);
		EditText elementActualValueEditText = (EditText) findViewById(R.id.editTextRealStock);
		elementActualValueEditText.setText(this.print(this.currentElement
				.getActualValue()));
		Store.saveElement(this.getCurrentElement());
		GetValuesActivity.this.inputKeyFirstRecognizement = true;
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

	@SuppressLint("ResourceType")
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
	}

	private void loadLastElement() {
		elementIndex = this.elements.size();
		loadPreviousElement();
		// GetValuesActivity.this.autoNav.setChecked(true);
	}

	private void searchByElementCode() {

		//this.searchElementByCodeWindowsOpened = true;

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
					GetValuesActivity.this.elementIndex = searchByElementCode(input
							.getText().toString());
				}
				GetValuesActivity.this
						.updateElement(GetValuesActivity.this.elementIndex);
				//GetValuesActivity.this.searchElementByCodeWindowsOpened = false;
			}

		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//GetValuesActivity.this.searchElementByCodeWindowsOpened = false;
					}
				});

		alert.show();
	}

	private int searchByElementCode(String code) {
		int index = 0;
		int result = -1;
		for (Element element : this.elements) {
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
			//GetValuesActivity.this.autoNav.setChecked(false);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
	}

}
