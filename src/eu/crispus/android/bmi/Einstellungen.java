package eu.crispus.android.bmi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

/**
 * Klasse zum bearbeiten der App-Einstellungen
 * 
 * @author Johannes Kraus
 * @version 1.0
 */
public class Einstellungen extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	/**
	 * String Konstante, um beim Loggen einen Hinweis auf diese Klasse zu haben.
	 */
	private static final String TAG = "Einstellungen";

	/**
	 * Einfache Stringkonstante um beim auslesen der Preferences einen Default-Wert zu haben. Wenn nichts in den Preferences steht, sollte dieser Wert
	 * zurückgegeben werden.
	 */
	public static final String NONE = "NONE";

	/**
	 * Key um auf die Körpergröße in den Einstellungen zugreifen zu können.
	 */
	public static final String KEY_KOERPERGROESSE = "groesse";

	/**
	 * Key um auf das Geschlecht in den Einstellungen zugreifen zu können.
	 */
	public static final String KEY_GESCHLECHT = "geschlecht";

	/**
	 * Key um auf den Geburtstag in den Einstellungen zugreifen zu können.
	 */
	public static final String KEY_GEBURTSTAG = "geburtstag";

	/**
	 * Key um auf die Sprache in den Einstellungen zugreifen zu können.
	 */
	public static final String KEY_SPRACHE = "sprache";

	/**
	 * Key um auf das Einheiten-System (CM und KG oder Zoll und Pfund) in den Einstellungen zugreifen zu können.
	 */
	public static final String KEY_EINHEITENSYSTEM = "einheitensystem";

	/**
	 * Key um die Auswahl des Zeitraum anzugeben, die im Graphen angezeigt werden sollen.
	 */
	public static final String KEY_ZEITAUSWAHL = "zeitauswahl";
	
	/**
	 * Key um die Eingabe des Fettanteil zu aktivieren und (gibt auch an, ob der Fettanteil im Graphen angezeigt werden soll).
	 */
	public static final String KEY_FETTANTEIL = "fettanteil";
	
	/**
	 * Key um die Eingabe des Wasseranteils zu aktivieren und (gibt auch an, ob der Wasseranteil im Graphen angezeigt werden soll).
	 */
	public static final String KEY_WASSERANTEIL = "wasseranteil";
	
	/**
	 * Key um die Eingabe des Muskelanteil zu aktivieren und (gibt auch an, ob der Muskelanteil im Graphen angezeigt werden soll).
	 */
	public static final String KEY_MUSKELANTEIL = "muskelanteil";
	
	/**
	 * Key um auf die Anzahl der anzuzeigenden Einträge in der GewichtBearbeitenActivity zu speichern.
	 * Dieser Wert wird nur in der entsprechenden Activity gespeichert und verwaltet, hier wird nur der
	 * Preferences-Key gespeichert.
	 */
	public static final String KEY_GEWICHTBEARBEITEN_ANZAHL = "gewichtbearbeitenAnzahl";
	
	/**
	 * Key um die Eingabe des Wasseranteils zu aktivieren und (gibt auch an, ob der Wasseranteil im Graphen angezeigt werden soll).
	 */
	public static final String KEY_AMPUTATION = "amputation";
	
	/**
	 * Key um die Eingabe des Muskelanteil zu aktivieren und (gibt auch an, ob der Muskelanteil im Graphen angezeigt werden soll).
	 */
	public static final String KEY_WEITERE_AMPUTATION = "weitere_amputation";
	
	/**
	 * SimpelDateFormater um das Datum zu formatieren.
	 */
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyy.MM.dd");

	/**
	 * Liste von List-Preference, wo der ausgewählte Wert anstatt eines Standardsatzes angezeigt werden soll.
	 */
	private final String[] listPreferencesKeys = new String[] { KEY_GESCHLECHT, KEY_ZEITAUSWAHL, KEY_SPRACHE, KEY_EINHEITENSYSTEM, KEY_AMPUTATION, KEY_WEITERE_AMPUTATION };

	/**
	 * Liste der Widgets zu listPreferencesKeys
	 */
	private ArrayList<ListPreference> listPreferences;

	/**
	 * Liste von EditText-Preferences, wo der ausgewählte Wert anstatt eines Standardsatzes angezeigt werden soll.
	 */
	private final String[] editTextPreferencesKeys = new String[] { KEY_KOERPERGROESSE };

	/**
	 * Liste der Widgets zu editTextPreferencesKeys
	 */
	private ArrayList<EditTextPreference> editTextPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			super.onCreate(savedInstanceState);
			Einstellungen.loadLocalInformation(getBaseContext());
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.crispus_custom_titlebar);
			addPreferencesFromResource(R.layout.einstellungen);

			getListView().setCacheColorHint(0);
			
			listPreferences = new ArrayList<ListPreference>();
			SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();
			sharedPrefs.registerOnSharedPreferenceChangeListener(this);
			for (String prefKey : listPreferencesKeys) {
				ListPreference tmpPreference = (ListPreference) getPreferenceManager().findPreference(prefKey);
				listPreferences.add(tmpPreference);
				onSharedPreferenceChanged(sharedPrefs, prefKey);
			}

			editTextPreferences = new ArrayList<EditTextPreference>();
			for (String prefKey : editTextPreferencesKeys) {
				EditTextPreference tmpPreference = (EditTextPreference) getPreferenceManager().findPreference(prefKey);
				editTextPreferences.add(tmpPreference);
				onSharedPreferenceChanged(sharedPrefs, prefKey);
			}

			// Change Listener für Sprache und Einheitensystem
			ListPreference tmpListPreferencePreference = (ListPreference) findPreference(KEY_EINHEITENSYSTEM);
			tmpListPreferencePreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					if ("imperial".equals(newValue.toString())) {
						// Hier werden die Texte für die Oberfläche vom metrischen Einheitensystem auf das Imperiale Einheitensystem gesetzt.
						EditTextPreference tmpEditTextPreference = (EditTextPreference) findPreference(KEY_KOERPERGROESSE);// (R.id.preferenceGroesse);
						tmpEditTextPreference.setSummary(tmpEditTextPreference.getSummary().toString().replaceAll("cm", getString(R.string.inchText)));
					} else {
						EditTextPreference tmpEditTextPreference = (EditTextPreference) findPreference(KEY_KOERPERGROESSE);// (R.id.preferenceGroesse);
						tmpEditTextPreference.setSummary(getString(R.string.preferenceGroesseSummary));
					}
					
					//Wenn newValue von den Preferences abweicht, muss umgerechnet werden.
					String aktuellesEinheitensystem = preference.getSharedPreferences().getString(Einstellungen.KEY_EINHEITENSYSTEM, Einstellungen.NONE);
					if (!aktuellesEinheitensystem.equals(newValue)) {
						if ("metrisch".equals(newValue)) {
							//Von zoll nach cm umrechnen
							String tmpString = preference.getSharedPreferences().getString(Einstellungen.KEY_KOERPERGROESSE, Einstellungen.NONE);
							if (!Einstellungen.NONE.equals(tmpString)) {
								try {
									SharedPreferences.Editor editor = preference.getSharedPreferences().edit();
									editor.putString(Einstellungen.KEY_KOERPERGROESSE, "" + (int) (Float.parseFloat(tmpString) * 2.54));
									editor.commit();
								} catch (Exception e) {
									Log.e(TAG, "Fehler beim umrechnen von CM nach Zoll.");
								}
							}
						} else {
							//Von cm nach zoll umrechnen.
							String tmpString = preference.getSharedPreferences().getString(Einstellungen.KEY_KOERPERGROESSE, Einstellungen.NONE);
							if (!Einstellungen.NONE.equals(tmpString)) {
								try {
									SharedPreferences.Editor editor = preference.getSharedPreferences().edit();
									editor.putString(Einstellungen.KEY_KOERPERGROESSE, "" + (int) (Float.parseFloat(tmpString) * 0.393700787));
									editor.commit();
								} catch (Exception e) {
									Log.e(TAG, "Fehler beim umrechnen von CM nach Zoll.");
								}
							}
						}
						//Neustarten, damit das neue Gewicht angezeigt wird.
						startActivity(getIntent());
						finish();
					}
					return true;
				}
			});

			tmpListPreferencePreference = (ListPreference) findPreference(KEY_SPRACHE);
			tmpListPreferencePreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					if ("german".equals(newValue.toString())) {
						Locale locale = new Locale("de");
						Locale.setDefault(locale);
						Configuration config = new Configuration();
						config.locale = locale;
						getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
					} else {
						Locale locale = new Locale("en");
						Locale.setDefault(locale);
						Configuration config = new Configuration();
						config.locale = locale;
						getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
					}
					//Bei den Preferences funktioniert der Trick mit dem drehen des Bildschirms leider nicht.
					startActivity(getIntent());
					finish();

					return true;
				}
			});
		} catch (Exception e) {
			Log.e(TAG, "Fehler im onCreate.", e);
		}
	}
	
	/**
	 * Kleine Hilfsmethode, damit die Preferences aus jeder Activity der APP einfach geladen werden können.
	 * 
	 * @param context
	 *            Wird verwendet, um den Packagenamen zu erhalten.
	 * @return Einstellungen der Anwendung.
	 */
	public static final SharedPreferences getAnwendungsEinstellungen(final Context context) {
		return context.getSharedPreferences(context.getPackageName() + "_preferences", MODE_PRIVATE);
	}

	/**
	 * Implementierung von SharedPreferences.OnSharedPreferenceChangeListener.
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String prefKey) {
		if (listPreferences != null) {
			for (ListPreference tmpListPreference : listPreferences) {
				if (tmpListPreference.getKey().equals(prefKey)) {
					tmpListPreference.setSummary(tmpListPreference.getEntry());
				}
			}
		}

		if (editTextPreferences != null) {
			for (EditTextPreference tmpPreference : editTextPreferences) {
				if (tmpPreference.getKey().equals(prefKey) && (!tmpPreference.getText().trim().equals(""))) {
					if (prefKey.equals(KEY_KOERPERGROESSE)) {
						if ("imperial".equals(pref.getString(KEY_EINHEITENSYSTEM, Einstellungen.NONE))) {
							tmpPreference.setSummary(tmpPreference.getText() + " " + getString(R.string.inchText));
						} else {
							tmpPreference.setSummary(tmpPreference.getText() + " " + getString(R.string.cmText));
						}
					} else {
						tmpPreference.setSummary(tmpPreference.getText());
					}
				} else if (tmpPreference.getKey().equals(prefKey)) {
					// Standard Summary Text setzen. Wir nur bei EditTextPreferences benötigt, da diese gelöscht werden können.
					if (prefKey.equals(KEY_KOERPERGROESSE)) {
						tmpPreference.setSummary(R.string.preferenceGroesseSummary);
					} else {
						tmpPreference.setSummary(R.string.preferenceDefaultSummary);
					}
				}
			}
		}
	}

	/**
	 * @see http://code.google.com/p/android/issues/detail?id=3981
	 */
	@Override
	protected void onRestoreInstanceState(Bundle state) {
		try {
			super.onRestoreInstanceState(state);
		} catch (Exception e) {
			// Fehler in der Android API
			// Log.e(TAG, "Fehler im onRestoreInstanceState.", e);
		}
	}
	
	/**
	 * Diese Methode überprüft welche Einstellung in den Preferences vorhanden sind und welche Sprache auf einem Handy eingestellt sind. Abhängig davon wird
	 * dann die aktuelle Sprache für den übergebenen Context gesetz.
	 *
	 * @param context Kontext für den die Sprache geladen werden soll.
	 */
	public static void loadLocalInformation(Context context) {
		SharedPreferences sharedPreferences = Einstellungen.getAnwendungsEinstellungen(context);
		String tmpStringSprache = sharedPreferences.getString(Einstellungen.KEY_SPRACHE, Einstellungen.NONE);
		if ((Einstellungen.NONE.equals(tmpStringSprache) && Locale.getDefault().getDisplayLanguage().equals("Deutsch")) || "german".equals(tmpStringSprache)) {
			Locale locale = new Locale("de"); 
	        Locale.setDefault(locale);
	        Configuration config = new Configuration();
	        config.locale = locale;
	        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
	        
			if (Einstellungen.NONE.equals(tmpStringSprache)) {
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString(Einstellungen.KEY_SPRACHE, "german");
				editor.commit();
			}
		} else {
			Locale locale = new Locale("en"); 
	        Locale.setDefault(locale);
	        Configuration config = new Configuration();
	        config.locale = locale;
	        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
	        
			if (Einstellungen.NONE.equals(tmpStringSprache)) {
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString(Einstellungen.KEY_SPRACHE, "englisch");
				editor.commit();
			}
		}
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
			Log.e(TAG, "Fehler beim schließen der App.", e);
		}
	}
}
