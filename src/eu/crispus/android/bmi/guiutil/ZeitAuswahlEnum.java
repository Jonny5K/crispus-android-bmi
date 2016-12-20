package eu.crispus.android.bmi.guiutil;

import android.util.Log;

/**
 * Kleiner Enum um die Skalierung der Zeit besser Steuern zu können.
 * 
 * @author Johannes Kraus
 * @version 1.0
 */
public enum ZeitAuswahlEnum {
	WOCHE(7), MONAT(31), QUARTAL(93), JAHR(365);

	/**
	 * String Konstante, um beim Loggen einen Hinweis auf diese Klasse zu haben.
	 */
	private static final String TAG = "ZeitAuswahlEnum";
	
	/**
	 * Gibt den Zeitwert an, wieviele Tage an Einträgen angezeigt werden sollen.
	 */
	private final int daysOfYear;
	
	/**
	 * Constructor um zu jeder Konstante dieses Typs einen Zeitwert zu erzwingen.
	 * 
	 * @param value Wert in Tagen, der angezeigt werden soll.
	 */
	private ZeitAuswahlEnum(int value) {
		daysOfYear = value;
	}
	
	/**
	 * Liefert den Zeitwert zu einem Enum-Eintrag.
	 * 
	 * @return siehe oben.
	 */
	public int getDaysOfYearValue() {
		return daysOfYear;
	}
	
	/**
	 * Diese Methode gibt für einen Angegebenen Zeitraum den richtigen ZeitAuswahlEnum-Wert zurück.
	 * Wenn für die angegebene Anzahl an Tagen kein Wert vorhanden ist, wird WOCHE zurückgegeben.
	 * 
	 * @param dayCount Anzahl an Tagen, für die ein ZeitAuswahlEnum-Wert gesucht ist.
	 * @return Liefert einen ZeitAuswahlEnum-Wert zurück.
	 */
	public static ZeitAuswahlEnum getZeitAuswahlEnumByDayCount(int dayCount) {
		try {
			for (ZeitAuswahlEnum currentZeitauswahlItem : ZeitAuswahlEnum.values()) {
				if (currentZeitauswahlItem.getDaysOfYearValue() == dayCount) {
					return currentZeitauswahlItem;
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim ermitteln der Zeitauswahl. Übergebener Wert: " + dayCount, e);
		}
		
		return ZeitAuswahlEnum.WOCHE;
	}
}
