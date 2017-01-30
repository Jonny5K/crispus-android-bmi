package eu.crispus.android.bmi.util;

/**
 * Klasse um einen einfachen Bereich (z.B. von 5 bis 85) zu verwalten.
 *
 * @author Johannes Kraus
 * @version 1.0
 */
public class Bereich {
    /**
     * Gibt den Anfangswert für einen Bereich an.
     */
    private int startBereich;

    /**
     * Gibt den Endwert für einen Bereich an.
     */
    private int endeBereich;

    /**
     * Konstruktor um diese Klasse direkt mit Daten zu füllen.
     *
     * @param startBereich Gibt den Anfangswert für einen Bereich an.
     * @param endeBereich  Gibt den Endwert für einen Bereich an.
     */
    public Bereich(int startBereich, int endeBereich) {
        this.startBereich = startBereich;
        this.endeBereich = endeBereich;
    }

    public int getStartBereich() {
        return startBereich;
    }

    public void setStartBereich(int startBereich) {
        this.startBereich = startBereich;
    }

    public int getEndeBereich() {
        return endeBereich;
    }

    public void setEndeBereich(int endeBereich) {
        this.endeBereich = endeBereich;
    }

    /**
     * Diese Methode überprüft ob der übergebene Wert innerhalb des Bereiches liegt.
     *
     * @param value Wert der überprüft werden soll.
     * @return true wenn der übergebene Wert innerhalb des angegebenen Bereichs liegt, andernfalls false.
     */
    public boolean isWertInBereich(float value) {
        return (value > startBereich) && (value <= endeBereich);
    }

//	/**
//	 * Diese Methode überprüft ob der übergebene Wert innerhalb des Bereiches liegt. Der Nachkommaanteil wird einfach abgeschnitten.
//	 * 
//	 * @param value Wert der überprüft werden soll.
//	 * @return true wenn der übergebene Wert innerhalb des angegebenen Bereichs liegt, andernfalls false.
//	 */
//	public boolean isWertInBereich(float value) {
//		return isWertInBereich((int) value);
//	}
}