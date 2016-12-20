package eu.crispus.android.bmi.dbutil;

/**
 * Schema-Klasse um auf die Datenbankfelder der Tabelle datum_gewicht zugreifen zu können. Hier werden alle Spalten der oben genannten Tabelle festgehalten.
 * Zudem die Standard SQL-Befehle, die häufig genutzt werden.
 * 
 * @author Johannes Kraus
 * @version 1.0
 */
public class DatumGewichtTable {

	/**
	 * Primary-Key der Tabelle.
	 */
	public static final String COLUMN_ID = "_id";

	/**
	 * Name der Spalte Datum.
	 */
	public static final String COLUMN_DATUM = "datum";

	/**
	 * Name der Spalte Gewicht.
	 */
	public static final String COLUMN_GEWICHT = "gewicht";

	/**
	 * Name der Spalte für Körperfettanteil.
	 */
	public static final String COLUMN_FETT = "fettanteil";

	/**
	 * Name der Spalte für Wasseranteil.
	 */
	public static final String COLUMN_WASSER = "wasseranteil";

	/**
	 * Name der Spalte für Muskelanteil.
	 */
	public static final String COLUMN_MUSKEL = "muskelanteil";

	/**
	 * Tabellennamen.
	 */
	public static final String TABLE_NAME = "datum_gewicht";

	/**
	 * SQL-Befehl zum erstellen der Tabelle.
	 */
	public static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_DATUM
			+ " DATE NOT NULL, " + COLUMN_GEWICHT + " REAL NOT NULL);";

	/**
	 * SQL-Befhel um neue Spalte 'fettanteil' an die Tabelle anzuhängen (erstes Update der Datenbank).
	 */
	public static final String SQL_ALTER_TABLE_UPDATE_FETTANTEIL = "ALTER TABLE " + TABLE_NAME + " ADD " + COLUMN_FETT + " REAL NOT NULL DEFAULT(-1);";

	/**
	 * SQL-Befhel um neue Spalte 'wasseranteil' an die Tabelle anzuhängen (erstes Update der Datenbank).
	 */
	public static final String SQL_ALTER_TABLE_UPDATE_WASSERANTEIL = "ALTER TABLE " + TABLE_NAME + " ADD " + COLUMN_WASSER + " REAL NOT NULL DEFAULT(-1);";

	/**
	 * SQL-Befhel um neue Spalte 'muskelanteil' an die Tabelle anzuhängen (erstes Update der Datenbank).
	 */
	public static final String SQL_ALTER_TABLE_UPDATE_MUSKELANTEIL = "ALTER TABLE " + TABLE_NAME + " ADD " + COLUMN_MUSKEL + " REAL NOT NULL DEFAULT(-1);";

	/**
	 * SQL-Befehl zum löschen der Tabelle.
	 */
	public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

	/**
	 * SQL-Befehl zum einfügen neuer Datensätze.
	 */
	public static final String SQL_INSERT = "INSERT INTO " + TABLE_NAME + " (" + COLUMN_DATUM + ", " + COLUMN_GEWICHT + ", " + COLUMN_FETT + ", "
			+ COLUMN_WASSER + ", " + COLUMN_MUSKEL + ") VALUES (?, ?, ?, ?, ?)";

	/**
	 * SQL-Befehl zum einfügen neuer Datensätze.
	 */
	public static final String SQL_UPDATE = "UPDATE " + TABLE_NAME + " SET " + COLUMN_GEWICHT + "=?, " + COLUMN_FETT + "=?, " + COLUMN_WASSER + "=?, " + COLUMN_MUSKEL + "=? WHERE " + COLUMN_DATUM + "=?";

	/**
	 * SQL-Befehl zum löschen aller Datensätze. bmiSpitzelDatenbank.getWritableDatabase().execSQL(DatumGewichtTable.SQL_DELETE, new String[]{});
	 */
	public static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME;

	/**
	 * SQL-Befehl auswählen aller Datensätze. Sortiert nach Datum!
	 * 
	 * try { BMISpitzelDatenbank bmiSpitzelDatenbank = new BMISpitzelDatenbank(this); Cursor cursor =
	 * bmiSpitzelDatenbank.getReadableDatabase().rawQuery(DatumGewichtTable.SQL_SELECT_ALL, null); cursor.moveToFirst(); int postion = 0; while
	 * (!cursor.isAfterLast()) { Log.i(TAG, "Aktueller Eintrag (" + postion + ": " + cursor.getString(1) + " (" + cursor.getString(3) + ", " +
	 * cursor.getString(4) + ", " + cursor.getString(5) +")"); cursor.moveToNext(); postion++; } } catch (Exception e) { Log.e(TAG, "Fehler beim testen...", e);
	 * }
	 */
	public static final String SQL_SELECT_ALL = "SELECT " + COLUMN_ID + ", " + COLUMN_DATUM + ", " + COLUMN_GEWICHT + ", " + COLUMN_FETT + ", " + COLUMN_WASSER
			+ ", " + COLUMN_MUSKEL + " FROM " + TABLE_NAME + " ORDER BY substr(" + COLUMN_DATUM + ",7)||substr(" + COLUMN_DATUM + ",4 ,2)||substr("
			+ COLUMN_DATUM + ", 0, 3) ASC";
	public static final String SQL_SELECT_ALL_DESC = "SELECT " + COLUMN_ID + ", " + COLUMN_DATUM + ", " + COLUMN_GEWICHT + ", " + COLUMN_FETT + ", "
			+ COLUMN_WASSER + ", " + COLUMN_MUSKEL + " FROM " + TABLE_NAME + " ORDER BY (substr(" + COLUMN_DATUM + ",7)||substr(" + COLUMN_DATUM
			+ ",4 ,2)||substr(" + COLUMN_DATUM + ", 0, 3)) DESC";

	/**
	 * SQL-Befehl auswählen eines Datensatzes (gefiltert nach Datum).
	 */
	public static final String SQL_SELECT_BY_DATE = "SELECT " + COLUMN_DATUM + ", " + COLUMN_GEWICHT + " FROM " + TABLE_NAME + " WHERE " + COLUMN_DATUM + "=?";
}
