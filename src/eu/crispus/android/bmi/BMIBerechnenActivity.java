package eu.crispus.android.bmi;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import eu.crispus.android.bmi.dbutil.BMISpitzelDatenbank;
import eu.crispus.android.bmi.dbutil.DatumGewichtTable;
import eu.crispus.android.bmi.guiutil.BMIRechner;
import eu.crispus.android.bmi.guiutil.EditKeyListenerCloseOnEnter;
import eu.crispus.android.bmi.util.BMIBereich;
import eu.crispus.android.bmi.util.Bereich;

/**
 * Mit dieser Activity soll der BMI direkt berechnet werden können. Auf der Seite kann das Gewicht, das Alter und die Größe direkt eingegeben werden. Falls
 * Alter und Größe bereits in den Einstellungen hinterlegt sind, werden diese Daten als Vorgabe geladen. Es soll ein Hinweis zu dem BMI erscheinen, und auch die
 * Angabe wie viel man event. noch abnehmen oder zunehmen muss, um auf einen "guten" BMI-Wert zu kommen.
 * 
 * @author Johannes Kraus
 * @version 1.0
 */
public class BMIBerechnenActivity extends Activity {

	/**
	 * String Konstante, um beim Loggen einen Hinweis auf diese Klasse zu haben.
	 */
	private static final String TAG = "BMIBerechnenActivity";

	/**
	 * Hier wird eine Referenz auf das Eingabefeld gespeichert, damit dieses nachträglich verbessert werden kann.
	 */
	private EditText editTextAlter;

	/**
	 * Hier wird eine Referenz auf das Eingabefeld gespeichert, damit dieses nachträglich verbessert werden kann.
	 */
	private EditText editTextGewicht;

	/**
	 * Hier wird eine Referenz auf das Eingabefeld gespeichert, damit dieses nachträglich verbessert werden kann.
	 */
	private EditText editTextGroesse;

	/**
	 * Hier wird eine Referenz auf das Eingabefeld gespeichert, damit dieses nachträglich ausgelesen werden kann.
	 */
	private RadioButton radioButtonMaennlich;

	/**
	 * Hier wird eine Referenz auf das Eingabefeld gespeichert, damit dieses nachträglich ausgelesen werden kann.
	 */
	private RadioButton radioButtonWeiblich;

	/**
	 * Hier wird eine Referenz auf das Ausgabefeld für den BMI-Wert gespeichert.
	 */
	private TextView textViewBmiBerechnenAusgabeBmi;

	/**
	 * Verbindung zur Datenbank.
	 */
	private BMISpitzelDatenbank bmiSpitzelDatenbank;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		Einstellungen.loadLocalInformation(getBaseContext());
		getWindow().setFormat(PixelFormat.RGBA_8888);
		setContentView(R.layout.bmi_rechner);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.crispus_custom_titlebar);
		editTextAlter = (EditText) findViewById(R.id.editTextAlter);// Berechnet sich event. aus der Angabe des Alters in den Preferences
		editTextAlter.setOnKeyListener(new EditKeyListenerCloseOnEnter());
		editTextGewicht = (EditText) findViewById(R.id.editTextGewicht);// Kann hier nicht geladen werden.
		editTextGewicht.setOnKeyListener(new EditKeyListenerCloseOnEnter());
		editTextGroesse = (EditText) findViewById(R.id.editTextGroesse);// Wird in cm aus den Preferences geladen.
		editTextGroesse.setOnKeyListener(new EditKeyListenerCloseOnEnter());
		radioButtonMaennlich = (RadioButton) findViewById(R.id.optionBmiBerechnenMannlich);
		radioButtonWeiblich = (RadioButton) findViewById(R.id.optionBmiBerechnenWeiblich);
		textViewBmiBerechnenAusgabeBmi = (TextView) findViewById(R.id.bmiBerechnenAusgabeBmi);

		bmiSpitzelDatenbank = new BMISpitzelDatenbank(this);
		try {
			// Laden der Einstellungen aus den Preferences
			String geschlecht = Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getString(Einstellungen.KEY_GESCHLECHT, "meannlich");
			if ("maennlich".equals(geschlecht)) {
				radioButtonMaennlich.setChecked(true);
				radioButtonWeiblich.setChecked(false);
			} else {
				radioButtonMaennlich.setChecked(false);
				radioButtonWeiblich.setChecked(true);
			}

			editTextGroesse.setText(Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getString(Einstellungen.KEY_KOERPERGROESSE, ""));
			String stringGeburtstag = Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getString(Einstellungen.KEY_GEBURTSTAG, "");

			if ((stringGeburtstag != null) && !stringGeburtstag.trim().equals("")) {
				// Berechnung des Alters aus dem Geburtstag.
				GregorianCalendar geburtstagCalendar = new GregorianCalendar();
				geburtstagCalendar.setTime(Einstellungen.SIMPLE_DATE_FORMAT.parse(stringGeburtstag));
				GregorianCalendar heuteCalendar = new GregorianCalendar();
				Log.i(TAG, geburtstagCalendar.get(Calendar.YEAR) + "");
				Log.i(TAG, heuteCalendar.get(Calendar.YEAR) + "");
				int alter = heuteCalendar.get(Calendar.YEAR) - geburtstagCalendar.get(Calendar.YEAR);
				if (heuteCalendar.get(Calendar.MONTH) <= geburtstagCalendar.get(Calendar.MONTH)) {
					if (heuteCalendar.get(Calendar.DATE) < geburtstagCalendar.get(Calendar.DATE)) {
						alter -= 1;
					}
				}
				if ((alter > 17) && (alter < 150)) {
					editTextAlter.setText("" + alter);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Es ist ein Fehler in der onCreate()-Methode aufgetreten.", e);
		}
	}

	/**
	 * Die onResume-Methode wird aufgerufen, wenn die View "wieder" angezeigt wird, ohne dass Sie vorher beendet wurde. Falls aus dem Dialog in die Prefenreces
	 * gesprungen wird müssen die Texte gegebenfalls wieder angepasst werden. Einfacher Trick um die Sprache der App anzupassen. Wenn in den Einstellungen die
	 * Schriftart geändert wurde, wird der Bildschirm einmal gedreht, dadurch wird die App neu gezeichnet.
	 */
	@Override
	protected void onResume() {
		super.onResume();

		((TextView) findViewById(R.id.textViewAlter)).setText(R.string.bmiBerechnenAlter);
		radioButtonMaennlich.setText(R.string.bmiBerechnenMaennlich);
		radioButtonWeiblich.setText(R.string.bmiBerechnenWeiblich);
		((Button) findViewById(R.id.buttonBmiBerechnen)).setText(R.string.buttonBmiBerechnen);
		((Button) findViewById(R.id.buttonBackTitlebar)).setText(R.string.buttonZurueck);
		((TextView) findViewById(R.id.textViewTitlebar)).setText(R.string.app_name);
		((TextView) findViewById(R.id.textViewGewicht)).setText(R.string.bmiBerechnenGewicht);
		((TextView) findViewById(R.id.bmiBerechnenInfoUntergewicht)).setText(R.string.bmiBerechnenInfoUntergewicht);
		((TextView) findViewById(R.id.bmiBerechnenInfoNormal)).setText(R.string.bmiBerechnenInfoNormal);
		((TextView) findViewById(R.id.bmiBerechnenInfoUebergewicht)).setText(R.string.bmiBerechnenInfoUebergewicht);
		((TextView) findViewById(R.id.bmiBerechnenInfoAdipositas)).setText(R.string.bmiBerechnenInfoAdipositas);
		((TextView) findViewById(R.id.bmiBerechnenInfoStarkAdipositas)).setText(R.string.bmiBerechnenInfoStarkAdipositas);

		if ("imperial".equals(Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getString(Einstellungen.KEY_EINHEITENSYSTEM, Einstellungen.NONE))) {
			// Hier werden die Texte für die Oberfläche vom metrischen Einheitensystem auf das Imperiale Einheitensystem gesetzt.
			TextView tmpTextView = (TextView) findViewById(R.id.textViewGewicht);
			tmpTextView.setText(tmpTextView.getText().toString().replaceAll("kg", getString(R.string.pfundKurztext)));
			tmpTextView = (TextView) findViewById(R.id.textViewGroesse);
			tmpTextView.setText(tmpTextView.getText().toString().replaceAll("cm", getString(R.string.inchKurztext)));
		} else {
			TextView tmpTextView = (TextView) findViewById(R.id.textViewGewicht);
			tmpTextView.setText(getString(R.string.bmiBerechnenGewicht));
			tmpTextView = (TextView) findViewById(R.id.textViewGroesse);
			tmpTextView.setText(getString(R.string.bmiBerechnenGroesse));
		}
	}

	/**
	 * Methode um bei einem Click auf einen RadioButton alle Tastaturfelder zu löschen (verbergen).
	 * 
	 * @param view
	 *            RadioButton der gedrückt wurde.
	 */
	public void onClickRadioButtonDeleteKeyboard(View view) {
		try {
			// Löschen der Tastatur Eingabefelder.
			if (editTextAlter.hasFocus()) {
				InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(editTextAlter.getWindowToken(), 0);
				editTextAlter.requestFocus();
			}
			if (editTextGroesse.hasFocus()) {
				InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(editTextGroesse.getWindowToken(), 0);
				editTextGroesse.requestFocus();
			}
			if (editTextGewicht.hasFocus()) {
				InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(editTextGewicht.getWindowToken(), 0);
				editTextGewicht.requestFocus();
			}
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim löschen der Tastatur.", e);
		}
	}

	/**
	 * Wenn diese Methode aufgerufen wird, dann wird der BMI anhand der Eingaben berechnet.
	 * 
	 * @param view
	 */
	public void onClickButtonBmiBerechnen2(final View view) {
		try {
			findViewById(R.id.bmiBerechnenInformationsLayout).setVisibility(View.GONE);
			if (editTextAlter.getText().toString().trim().equals("") || editTextGroesse.getText().toString().trim().equals("")
					|| editTextGewicht.getText().toString().trim().equals("") || (!radioButtonMaennlich.isChecked() && !radioButtonWeiblich.isChecked())) {
				// Erst prüfen ob überhaupt etwas eingegeben wurde
				Toast.makeText(this.getBaseContext(), getString(R.string.bmiBerechnenActivityHinweisEingabenFehlen),
				// "Sie habe noch nicht alle Einstellungen vorgenommen. Bitte beachten Sie, dass für die Berechnung des BMI das Alter, Geschlecht, Gewicht und die Körpergröße benötigt werden.",
						Toast.LENGTH_LONG).show();
				if (editTextGewicht.getText().toString().trim().equals("")) {
					editTextGewicht.requestFocus();
				}
				if (editTextAlter.getText().toString().trim().equals("")) {
					editTextAlter.requestFocus();
				}
				if (editTextGroesse.getText().toString().trim().equals("")) {
					editTextGroesse.requestFocus();
				}
				return;
			}
			int alter = Integer.parseInt(editTextAlter.getText().toString());
			if ((alter <= 17) || (alter > 150)) {
				// Prüfen des Alter auf einen gültigen Wert
				Toast.makeText(this.getBaseContext(), getString(R.string.bmiBerechnenActivityHinweisAlterUngueltig), // "Das Alter hat einen ungültigen Wert. Bitte geben Sie eine Zahl zwischen 18 und 150 Jahren ein.",
						Toast.LENGTH_LONG).show();
				editTextAlter.requestFocus();
				return;
			}
			int groesse = Integer.parseInt(editTextGroesse.getText().toString());
			if ((groesse <= 19) || (groesse > 300)) {
				// Prüfen des Alter auf einen gültigen Wert
				Toast.makeText(this.getBaseContext(), getString(R.string.bmiBerechnenActivityHinweisGroesseUngueltig), // "Die Größe hat einen ungültigen Wert. Bitte geben Sie eine Zahl zwischen 20cm und 300cm ein.",
						Toast.LENGTH_LONG).show();
				editTextGroesse.requestFocus();
				return;
			}
			float gewicht = 0;
			try {
				String tmpStringValue = editTextGewicht.getText().toString();
				if (tmpStringValue.contains(".") && tmpStringValue.contains(",")) {
					// Punkt und Komma geht nicht.
					gewicht = -1;// Falsches Gewicht
				} else if (tmpStringValue.contains(",")) {
					// Komma durch String ersetzen.
					tmpStringValue = tmpStringValue.replace(",", ".");
				}
				if (gewicht != -1) {
					// Wenn nun mehr als ein Punkt vorhanden ist, dann wird eine
					// Exception geworfen.
					gewicht = Float.parseFloat(tmpStringValue);
				}
			} catch (Exception e) {
				gewicht = -1;// Falsches Gewicht.
			}
			if ((gewicht <= 2) || (gewicht > 450)) {
				// Prüfen des Alter auf einen gültigen Wert
				Toast.makeText(this.getBaseContext(), getString(R.string.bmiBerechnenActivityHinweisGewichtUngueltig), // "Das Gewicht hat einen ungültigen Wert. Bitte geben Sie eine Zahl zwischen 3kg und 450kg ein.",
						Toast.LENGTH_LONG).show();
				editTextGewicht.requestFocus();
				return;
			}

			// Verbergen der Tastatur.
			onClickRadioButtonDeleteKeyboard(view);

			// Berechnung des BMI und
			BMIRechner bmiRechner = new BMIRechner(getBaseContext());
			float berechneterBMI = bmiRechner.berechneBMIAsFlaot(gewicht, groesse);
			textViewBmiBerechnenAusgabeBmi.setText(view.getContext().getString(R.string.ihrBMI) + ": " + BMIRechner.DECIMAL_FORMAT.format(berechneterBMI));

			// Den richtigen BMIBereich für das Alter und Geschlecht suchen
			BMIBereich bmiBereich = null;
			if (radioButtonMaennlich.isChecked()) {
				for (BMIBereich currentBMIBereich : BMIRechner.maennerBMIBereiche) {
					if (currentBMIBereich.getAlter().isWertInBereich(alter)) {
						bmiBereich = currentBMIBereich;
						break;
					}
				}
			} else {
				for (BMIBereich currentBMIBereich : BMIRechner.frauenBMIBereiche) {
					if (currentBMIBereich.getAlter().isWertInBereich(alter)) {
						bmiBereich = currentBMIBereich;
						break;
					}
				}
			}

			// Füllen der Informations-Tabelle und den aktuellen Bereich FETT und farblich machen
			if (setInformationstabelle(bmiBereich.getBereich1(), berechneterBMI, R.id.bmiBerechnenInfoUntergewicht, R.id.bmiBerechnenInfoUntergewichtValue,
					Color.YELLOW, "< " + bmiBereich.getBereich1().getEndeBereich())) {
				textViewBmiBerechnenAusgabeBmi.setTextColor(Color.YELLOW);
			}
			if (setInformationstabelle(bmiBereich.getBereich2(), berechneterBMI, R.id.bmiBerechnenInfoNormal, R.id.bmiBerechnenInfoNormalValue, Color.GREEN,
					bmiBereich.getBereich2().getStartBereich() + " - " + bmiBereich.getBereich2().getEndeBereich())) {
				textViewBmiBerechnenAusgabeBmi.setTextColor(Color.GREEN);
			}
			if (setInformationstabelle(bmiBereich.getBereich3(), berechneterBMI, R.id.bmiBerechnenInfoUebergewicht, R.id.bmiBerechnenInfoUebergewichtValue,
					Color.YELLOW, bmiBereich.getBereich3().getStartBereich() + " - " + bmiBereich.getBereich3().getEndeBereich())) {
				textViewBmiBerechnenAusgabeBmi.setTextColor(Color.YELLOW);
			}
			if (setInformationstabelle(bmiBereich.getBereich4(), berechneterBMI, R.id.bmiBerechnenInfoAdipositas, R.id.bmiBerechnenInfoAdipositasValue,
					Color.rgb(251, 166, 1), bmiBereich.getBereich4().getStartBereich() + " - " + bmiBereich.getBereich4().getEndeBereich())) {
				textViewBmiBerechnenAusgabeBmi.setTextColor(Color.rgb(251, 166, 1));
			}
			if (setInformationstabelle(bmiBereich.getBereich5(), berechneterBMI, R.id.bmiBerechnenInfoStarkAdipositas,
					R.id.bmiBerechnenInfoStarkAdipositasValue, Color.RED, bmiBereich.getBereich5().getStartBereich() + " >")) {
				textViewBmiBerechnenAusgabeBmi.setTextColor(Color.RED);
			}

			// Wenn noch keine Einstellungen vorhanden sind, wird nun noch die Größe und das Geschlecht in die Preferences geschrieben.
			SharedPreferences sharedPreferences = Einstellungen.getAnwendungsEinstellungen(getBaseContext());
			String geschlecht = sharedPreferences.getString(Einstellungen.KEY_GESCHLECHT, Einstellungen.NONE);
			if (Einstellungen.NONE.equals(geschlecht)) {
				SharedPreferences.Editor editor = sharedPreferences.edit();
				if (radioButtonMaennlich.isChecked()) {
					editor.putString(Einstellungen.KEY_GESCHLECHT, "maennlich");
				} else {
					editor.putString(Einstellungen.KEY_GESCHLECHT, "weiblich");
				}
				editor.commit();
			}
			String groesseEinstellungen = sharedPreferences.getString(Einstellungen.KEY_KOERPERGROESSE, Einstellungen.NONE);
			if (Einstellungen.NONE.equals(groesseEinstellungen) || groesseEinstellungen.trim().equals("")) {
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString(Einstellungen.KEY_KOERPERGROESSE, groesse + "");
				editor.commit();
			}

			findViewById(R.id.bmiBerechnenInformationsLayout).setVisibility(View.VISIBLE);

			// Wenn an dem aktuellen Datum (heute) noch kein Gewichtswert gespeichert wurde und die Werte aus den Eingabefeldern den Preferences-Werten
			// entsprechen, dann wird der Gewichtswert automatisch gespeichert.
			Cursor cursor = null;
			try {
				String currentDateString = GewichtEingebenActivity.simpleDateFormat.format(new Date());// Aktuelles Datum
				cursor = bmiSpitzelDatenbank.getReadableDatabase().rawQuery(DatumGewichtTable.SQL_SELECT_BY_DATE, new String[] { currentDateString });
				if (cursor.getCount() <= 0) {
					// Prüfen ob die Einstellungen gleich den aktuellen Werten der Eingabefelder sind.
					// Wenn noch keine Einstellungen vorhanden sind, wird nun noch die Größe und das Geschlecht in die Preferences geschrieben.
					if ((Einstellungen.MAENNLICH.equals(geschlecht) && radioButtonMaennlich.isChecked())
							|| (Einstellungen.WEIBLICH.equals(geschlecht) && radioButtonWeiblich.isChecked())) {
						if ((groesse + "").equals(groesseEinstellungen)) {
							String stringGeburtstag = Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getString(Einstellungen.KEY_GEBURTSTAG, "");

							if ((stringGeburtstag != null) && !stringGeburtstag.trim().equals("")) {
								// Berechnung des Alters aus dem Geburtstag.
								GregorianCalendar geburtstagCalendar = new GregorianCalendar();
								geburtstagCalendar.setTime(Einstellungen.SIMPLE_DATE_FORMAT.parse(stringGeburtstag));
								GregorianCalendar heuteCalendar = new GregorianCalendar();
								Log.i(TAG, geburtstagCalendar.get(Calendar.YEAR) + "");
								Log.i(TAG, heuteCalendar.get(Calendar.YEAR) + "");
								int alterAusEinstellungen = heuteCalendar.get(Calendar.YEAR) - geburtstagCalendar.get(Calendar.YEAR);
								if (heuteCalendar.get(Calendar.MONTH) <= geburtstagCalendar.get(Calendar.MONTH)) {
									if (heuteCalendar.get(Calendar.DATE) < geburtstagCalendar.get(Calendar.DATE)) {
										alterAusEinstellungen -= 1;
									}
								}
								
								if (alter == alterAusEinstellungen) {
									// In der Datenbank werden immer Metrische-Werte gespeichert.
									String einheitensystem = Einstellungen.getAnwendungsEinstellungen(view.getContext()).getString(Einstellungen.KEY_EINHEITENSYSTEM,
											"metrisch");
									if ("metrisch".equals(einheitensystem)) {
										bmiSpitzelDatenbank.getWritableDatabase().execSQL(DatumGewichtTable.SQL_INSERT,
												new String[] { currentDateString, "" + gewicht, "-1", "-1", "-1" });
									} else {
										bmiSpitzelDatenbank.getWritableDatabase()
												.execSQL(
														DatumGewichtTable.SQL_INSERT,
														new String[] { currentDateString, "" + (gewicht * BMIRechner.POUND_TO_KG), "-1",
																"-1", "-1" });
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				Log.e(TAG, "Fehler beim Einfügen in die Datenbank.", e);
			} finally {
				cursor.close();
				bmiSpitzelDatenbank.close();
			}
		} catch (Exception e) {
			Log.e(TAG, "Es ist ein Fehler in der onClickButtonGewichtEingeben()-Methode aufgetreten.", e);
		}
	}

	/**
	 * Füllen der Informations-Tabelle und den aktuellen Bereich FETT und Farblich machen
	 * 
	 * @param bmiBereich
	 *            Aktueller BMI-Bereich (z.B. Untergewicht), für das angegebene Alter.
	 * @param berechneterBMI
	 *            Berechneter BMI-Wert, wird benötigt um zu überprüfen ob der Wert im aktuellen BMI-Bereich liegt.
	 * @param bmiBerechnenInfoLabel
	 *            ID der TextView für den aktuellen BMI-Bereich Label.
	 * @param bmiBerechnenInfoValue
	 *            ID der TextView für den aktuellen BMI-Bereich Wert.
	 * @param colorCurrent
	 *            Farbe für die TextViews, falls sie in dem aktuellen BMI-Bereich liegen.
	 * @return true wenn der berechnete BMI innerhalb des aktuellen bmiBereich liegt.
	 */
	private boolean setInformationstabelle(Bereich bereich, float berechneterBMI, int bmiBerechnenInfoLabel, int bmiBerechnenInfoValue, int colorCurrent,
			String valueText) {
		TextView tmpTextView = null;
		if (bereich.isWertInBereich(berechneterBMI)) {
			tmpTextView = (TextView) findViewById(bmiBerechnenInfoLabel);
			tmpTextView.setTextColor(colorCurrent);
			tmpTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			tmpTextView = (TextView) findViewById(bmiBerechnenInfoValue);
			tmpTextView.setTextColor(colorCurrent);
			tmpTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			tmpTextView.setText(valueText);
			return true;
		} else {
			tmpTextView = (TextView) findViewById(bmiBerechnenInfoLabel);
			tmpTextView.setTextColor(getResources().getColor(R.color.textfarbe));
			tmpTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
			tmpTextView = (TextView) findViewById(bmiBerechnenInfoValue);
			tmpTextView.setTextColor(getResources().getColor(R.color.textfarbe));
			tmpTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
			tmpTextView.setText(valueText);
			return false;
		}
	}

	/**
	 * In der folgenden Methode wird das Menü (menue_bmi_berechnen.xml) in die BMI-Berechnung-Seite eingebunden.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menue_bmi_berechnen, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * In der folgenden Methode werden die Menü-Einträge abgearbeitet.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			Intent intent = null;
			switch (item.getItemId()) {
			case R.id.menueEinstellungen:
				intent = new Intent(this, Einstellungen.class);
				startActivity(intent);
				return true;
			case R.id.menueHilfe:
				intent = new Intent(this, HilfeAnzeigenActivity.class);
				startActivity(intent);
				return true;
			case R.id.menueZurueck:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		} catch (Exception e) {
			Log.e(TAG, "Es ist ein Fehler in der onOptionsItemSelected()-Methode aufgetreten.", e);
		}

		return false;
	}

	/**
	 * Methode um bei einem Click auf den Zurück-Button in der Titlebar.
	 * 
	 * @param view
	 *            Button der gedrückt wurde.
	 */
	public void onClickButtonBackTitlebar(View view) {
		try {
			finish();
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim Zurück-Button in der Title Bar.", e);
		}
	}

	/**
	 * Methode um bei einem Click auf den Einstellungs-Button in der Titlebar.
	 * 
	 * @param view
	 *            Button der gedrückt wurde.
	 */
	public void onClickButtonPreferencesTitlebar(View view) {
		try {
			Intent intent = new Intent(this, Einstellungen.class);
			startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "Es ist ein Fehler in der onClickButtonPreferencesTitlebar()-Methode aufgetreten.", e);
		}
	}
}
