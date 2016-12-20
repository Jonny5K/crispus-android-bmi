package eu.crispus.android.bmi.util;

/**
 * Einfache Klasse um in einem Spinner Daten anzuzeigen, und zu diesen Daten einen entsprechenden Value zuzuweisen.
 * 
 * @author Johannes Kraus
 * @version 1.0
 */
public class SpinnerItem {

	/**
	 * Wert der zu einem Eintrag gespeichert werden soll.
	 */
	private final String value;
	
	/**
	 * Label der für einen Eintrag angezeigt werden soll.
	 */
	private final String label;

	public SpinnerItem(String value, String label) {
		super();
		this.value = value;
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return label;
	}
}
