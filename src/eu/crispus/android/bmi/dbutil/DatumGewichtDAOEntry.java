package eu.crispus.android.bmi.dbutil;

import java.util.Calendar;
import java.util.Date;

/**
 * Kleine Hilfsklasse (eine kleines DAO-Objekt) um einfacher auf die Datenbank
 * zugreifen zu können.
 * 
 * @author Johannes Kraus
 * @version 1.0
 */
public class DatumGewichtDAOEntry implements Comparable<DatumGewichtDAOEntry> {
	/**
	 * ID des Eintrags.
	 */
	private final String id;

	/**
	 * Gewicht des Tages.
	 */
	private final String gewicht;
	
	/**
	 * Fettanteil des Tages.
	 */
	private final String fettanteil;
	
	/**
	 * Wasseranteil des Tages.
	 */
	private final String wasseranteil;
	
	/**
	 * Muskelanteil des Tages.
	 */
	private final String muskelanteil;

	/**
	 * Datum des Eintrags.
	 */
	private final Calendar datum;

	/**
	 * Datum des Eintrags als String.
	 */
	private final String datumString;

	public DatumGewichtDAOEntry(String id, Date dateDatum, String gewicht, String datumString, String fettanteil, String wasseranteil, String muskelanteil) {
		this.id = id;
		datum = Calendar.getInstance();
		datum.setTime(dateDatum);
		this.gewicht = gewicht;
		this.fettanteil = fettanteil;
		this.wasseranteil = wasseranteil;
		this.muskelanteil = muskelanteil;
		this.datumString =  datumString.substring(6) + datumString.substring(3, 5) + datumString.substring(0, 2);
	}

	public String getGewicht() {
		return gewicht;
	}

	public String getDatumString() {
		return datumString;
	}

	public Calendar getDatum() {
		return datum;
	}

	public String getId() {
		return id;
	}

	public String getFettanteil() {
		return fettanteil;
	}

	public String getWasseranteil() {
		return wasseranteil;
	}

	public String getMuskelanteil() {
		return muskelanteil;
	}

	/**
	 * Nach Datum sortieren.
	 */
	@Override
	public int compareTo(DatumGewichtDAOEntry datumGewichtDAOEntry) {
		return datumString.compareTo(datumGewichtDAOEntry.datumString);
	}
}
