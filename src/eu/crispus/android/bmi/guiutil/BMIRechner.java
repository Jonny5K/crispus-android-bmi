package eu.crispus.android.bmi.guiutil;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.util.Log;
import eu.crispus.android.bmi.Einstellungen;
import eu.crispus.android.bmi.R;
import eu.crispus.android.bmi.util.BMIBereich;
import eu.crispus.android.bmi.util.Bereich;

/**
 * Diese Klasse ist zum Berechnen des BMI zuständig. Beim erzeugen dieser Klasse muss ein Context übergeben werden. Mit dem Context lädt sich der BMIRechner die
 * Daten aus den Einstellungen (Größe, Alter etc).
 * 
 * @author Johannes Kraus
 * @version 1.0
 */
public class BMIRechner {

	/**
	 * String Konstante, um beim Loggen einen Hinweis auf diese Klasse zu haben.
	 */
	private static final String TAG = "BMIRechner";

	/**
	 * BMI-Bereiche für Frauen (nach Alter).
	 */
	public final static BMIBereich[] frauenBMIBereiche = {
			new BMIBereich(new Bereich(-150, 24), new Bereich(0, 19), new Bereich(19, 24), new Bereich(24, 29), new Bereich(29, 39), new Bereich(39, 100)),
			new BMIBereich(new Bereich(24, 35), new Bereich(0, 20), new Bereich(20, 25), new Bereich(25, 30), new Bereich(30, 40), new Bereich(40, 100)),
			new BMIBereich(new Bereich(35, 45), new Bereich(0, 21), new Bereich(21, 26), new Bereich(26, 31), new Bereich(31, 41), new Bereich(41, 100)),
			new BMIBereich(new Bereich(45, 55), new Bereich(0, 22), new Bereich(22, 27), new Bereich(27, 32), new Bereich(32, 42), new Bereich(42, 100)),
			new BMIBereich(new Bereich(55, 65), new Bereich(0, 23), new Bereich(23, 28), new Bereich(28, 33), new Bereich(33, 43), new Bereich(43, 100)),
			new BMIBereich(new Bereich(65, 150), new Bereich(0, 24), new Bereich(24, 29), new Bereich(29, 34), new Bereich(34, 44), new Bereich(44, 100)), };

	/**
	 * BMI-Bereiche für Männer (nach Alter).
	 */
	public final static BMIBereich[] maennerBMIBereiche = {
			new BMIBereich(new Bereich(-150, 24), new Bereich(0, 20), new Bereich(20, 25), new Bereich(25, 30), new Bereich(30, 40), new Bereich(40, 100)),
			new BMIBereich(new Bereich(24, 35), new Bereich(0, 21), new Bereich(21, 26), new Bereich(26, 31), new Bereich(31, 41), new Bereich(41, 100)),
			new BMIBereich(new Bereich(35, 45), new Bereich(0, 22), new Bereich(22, 27), new Bereich(27, 32), new Bereich(32, 42), new Bereich(42, 100)),
			new BMIBereich(new Bereich(45, 55), new Bereich(0, 23), new Bereich(23, 28), new Bereich(28, 33), new Bereich(33, 43), new Bereich(43, 100)),
			new BMIBereich(new Bereich(55, 65), new Bereich(0, 24), new Bereich(24, 29), new Bereich(29, 34), new Bereich(34, 44), new Bereich(44, 100)),
			new BMIBereich(new Bereich(65, 150), new Bereich(0, 25), new Bereich(25, 30), new Bereich(30, 35), new Bereich(35, 45), new Bereich(45, 100)), };

	/**
	 * Aktueller Context der Anwendung, damit auf die Preferences zugegriffen werden kann.
	 */
	private final Context context;

	/**
	 * Decimal-Formater um Zahlen im ganzen Projekt ordentlich ausgeben zu können.
	 */
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");

	/**
	 * Zahlenkonstante um einen KG-Wert in Pfund umzurechnen.
	 */
	public static final float KG_TO_POUND = 2.2046f;

	/**
	 * Zahlenkonstante um einen Pfund-Wert in Kilogramm umzurechnen.
	 */
	public static final float POUND_TO_KG = 0.4536f;

	/**
	 * Körpergröße aus den Einstellungen der Anwendung.
	 */
	private int koerpergroesse;

	/**
	 * Geschlechtsangabe aus den Einstellungen der Anwendung. "maenndliche" oder "weiblich".
	 */
	private String geschlecht;

	/**
	 * Alter in Jahren (wird aus den Einstellungen der Anwendung berechnet).
	 */
	private int alter;

	/**
	 * Einheitensystem, dass in den Einstellungen eingestellt ist.
	 */
	private String einheitensystem;

	/**
	 * Gibt an, ob ein Fehler beim Auslesen der Einstellungen aufgetreten ist (z.B. wenn die Angaben nicht vollständig sind).
	 */
	private boolean errorReadingPreferences;

	/**
	 * Faktor für die Korrektur bei einer (oder mehreren) Amputation(en).
	 * 
	 */
	private float amputationKorrektur = 0;

	public BMIRechner(Context context) {
		this.context = context;
		readPreferences();
	}

	/**
	 * Diese Methode liest die Preferences immer wieder beim erzeugen der Klasse aus.
	 */
	private void readPreferences() {
		errorReadingPreferences = false;
		try {
			try {
				amputationKorrektur = Float.parseFloat(Einstellungen.getAnwendungsEinstellungen(context).getString(Einstellungen.KEY_AMPUTATION, "0"))
					+ Float.parseFloat(Einstellungen.getAnwendungsEinstellungen(context).getString(Einstellungen.KEY_WEITERE_AMPUTATION, "0"));
			} catch (Exception e) {
				Log.e(TAG,  "Fehler beim auslesen der Amputationen aus den Einstellungen. Es wird von keiner Amputation ausgegangen.", e);
				amputationKorrektur = 0;
			}

			geschlecht = Einstellungen.getAnwendungsEinstellungen(context).getString(Einstellungen.KEY_GESCHLECHT, "meannlich");
			String tmpGroesse = Einstellungen.getAnwendungsEinstellungen(context).getString(Einstellungen.KEY_KOERPERGROESSE, "0");
			if ((tmpGroesse == null) || tmpGroesse.trim().equals("")) {
				tmpGroesse = "0";
				errorReadingPreferences = true;
			}
			koerpergroesse = Integer.parseInt(tmpGroesse);
			einheitensystem = Einstellungen.getAnwendungsEinstellungen(context).getString(Einstellungen.KEY_EINHEITENSYSTEM, "metrisch");

			// Alter wird als letztes ausgelesen.
			String stringGeburtstag = Einstellungen.getAnwendungsEinstellungen(context).getString(Einstellungen.KEY_GEBURTSTAG, "");
			if ((stringGeburtstag != null) && (!stringGeburtstag.trim().equals(""))) {
				// Berechnung des Alters aus dem Geburtstag.
				GregorianCalendar geburtstagCalendar = new GregorianCalendar();
				// geburtstagCalendar.setTime(GewichtEingebenActivity.simpleDateFormat.parse(stringGeburtstag));
				geburtstagCalendar.setTime(Einstellungen.SIMPLE_DATE_FORMAT.parse(stringGeburtstag));
				GregorianCalendar heuteCalendar = new GregorianCalendar();
				alter = heuteCalendar.get(Calendar.YEAR) - geburtstagCalendar.get(Calendar.YEAR);
				if (heuteCalendar.get(Calendar.MONTH) <= geburtstagCalendar.get(Calendar.MONTH)) {
					if (heuteCalendar.get(Calendar.DATE) < geburtstagCalendar.get(Calendar.DATE)) {
						alter -= 1;
					}
				}
				if (alter < 0) {
					throw new IllegalArgumentException("Ungültiges Alter: " + alter);
				}
			} else {
				errorReadingPreferences = true;
			}
			return;
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim auslesen der Preferences.", e);
		}

		errorReadingPreferences = true;
	}

	/**
	 * Berechnet den BMI und gibt diesen formatiert mit zwei Nachkommastellen zurück.
	 * 
	 * @param gewicht
	 *            Gewicht von dem der BMI berechnet werden soll.
	 * @return Liefert den BMI als String.
	 */
	public String berechneBMI(float gewicht) {
		try {
			gewicht = (gewicht * 100) / (100 - amputationKorrektur);

			if ("metrisch".equals(einheitensystem)) {
				return DECIMAL_FORMAT.format((gewicht / Math.pow(koerpergroesse / 100.0, 2)));
			} else {
				return DECIMAL_FORMAT.format(gewicht / (koerpergroesse * koerpergroesse) * 703);
			}
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim berechnen des BMI.", e);
		}

		return DECIMAL_FORMAT.format(0.0);
	}

	/**
	 * Berechnet den BMI und gibt diesen als Float zurück.
	 * 
	 * @param gewicht
	 *            Gewicht von dem der BMI berechnet werden soll.
	 * @return Liefert den BMI als Float.
	 */
	public float berechneBMIAsFlaot(float gewicht) {
		try {
			gewicht = (gewicht * 100) / (100 - amputationKorrektur);

			if ("metrisch".equals(einheitensystem)) {
				return (float) (gewicht / Math.pow(koerpergroesse / 100.0f, 2));
			} else {
				return gewicht / (koerpergroesse * koerpergroesse) * 703;
			}
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim berechnen des BMI.", e);
		}

		return 0.0f;
	}

	/**
	 * Berechnet den BMI und gibt diesen formatiert mit zwei Nachkommastellen zurück. Zudem wird ein Hinweis angefügt, ob der BMI in Ordnung ist.
	 * 
	 * @param gewicht
	 *            Gewicht von dem der BMI berechnet werden soll.
	 * @param context
	 *            Context der Aufrufenden View, wird benötigt um auf die Strings aus der Datei string.xml zuzugreifen.
	 * @return Liefert den BMI als String mit einer Wertung.
	 */
	public String berechneBMIWithRating(float gewicht, Context context) {
		try {
			gewicht = (gewicht * 100) / (100 - amputationKorrektur);

			if ("metrisch".equals(einheitensystem)) {
				double bmi = (gewicht / Math.pow(koerpergroesse / 100.0, 2));
				return DECIMAL_FORMAT.format(bmi) + " " + getRatingFromBmi(bmi, context);
			} else {
				double bmi = gewicht / (koerpergroesse * koerpergroesse) * 703;
				return DECIMAL_FORMAT.format(bmi) + " " + getRatingFromBmi(bmi, context);
			}
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim berechnen des BMI.", e);
		}

		return "BMI konnte nicht berechnet werden.";
	}

	/**
	 * Diese Methode liefert ein Ranking (Bewertung) ob der berechnete BMI OK ist. Dazu wird das Geschlecht und alter aus den Preferences verwendet.
	 * 
	 * @param currentBMI
	 *            aktuell berechneter BMI.
	 * @param context
	 *            Context der Aufrufenden View, wird benötigt um auf die Strings aus der Datei string.xml zuzugreifen.
	 * @return String der angibt, ob der BMI gut oder eher schlecht ist.
	 */
	private final String getRatingFromBmi(double currentBMI, Context context) {
		try {
			if ((geschlecht == null) || geschlecht.trim().equals("")) {
				return context.getString(R.string.bMIRechnerAngabenUnvollstaendig);// "Angaben sind unvollständig (siehe Einstellungen).";
			}

			if (geschlecht.equals("maennlich")) {
				if (currentBMI < 20) {
					return context.getString(R.string.bmiBerechnenInfoUntergewicht);
				} else if (currentBMI < 26) {
					return context.getString(R.string.bmiBerechnenInfoNormal);
				} else if (currentBMI < 31) {
					return context.getString(R.string.bmiBerechnenInfoUebergewicht);
				} else if (currentBMI < 41) {
					return context.getString(R.string.bmiBerechnenInfoAdipositas);
				} else {
					return context.getString(R.string.bmiBerechnenInfoStarkAdipositas);
				}
			} else {
				// Dann wird weiblich angenommen.
				if (currentBMI < 19) {
					return context.getString(R.string.bmiBerechnenInfoUntergewicht);
				} else if (currentBMI < 25) {
					return context.getString(R.string.bmiBerechnenInfoNormal);
				} else if (currentBMI < 31) {
					return context.getString(R.string.bmiBerechnenInfoUebergewicht);
				} else if (currentBMI < 41) {
					return context.getString(R.string.bmiBerechnenInfoAdipositas);
				} else {
					return context.getString(R.string.bmiBerechnenInfoStarkAdipositas);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim erstellen des Ranking zum BMI: " + currentBMI, e);
		}

		return "";
	}

	public boolean isErrorReadingPreferences() {
		return errorReadingPreferences;
	}

	public void setErrorReadingPreferences(boolean errorReadingPreferences) {
		this.errorReadingPreferences = errorReadingPreferences;
	}

	/**
	 * Berechnet den BMI und gibt diesen als Float zurück. Der BMI wird mit den im Formular eingegeben Daten berechnet, es werden nicht die Daten aus den
	 * Einstellungen genommen (ausser das Einheitensystem).
	 * 
	 * @param gewicht
	 *            Gewicht von dem der BMI berechnet werden soll.
	 * @param koerpergroesse
	 *            die für die Berechnung genommen wird.
	 * @return Liefert den BMI als Float.
	 */
	public float berechneBMIAsFlaot(float gewicht, int koerpergroesse) {
		try {
			gewicht = (gewicht * 100) / (100 - amputationKorrektur);

			if ("metrisch".equals(einheitensystem)) {
				return (float) (gewicht / Math.pow(koerpergroesse / 100.0f, 2));
			} else {
				return gewicht / (koerpergroesse * koerpergroesse) * 703;
			}
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim berechnen des BMI.", e);
		}

		return 0.0f;
	}

	public int getKoerpergroesse() {
		return koerpergroesse;
	}

	public String getGeschlecht() {
		return geschlecht;
	}

	public int getAlter() {
		return alter;
	}
}
