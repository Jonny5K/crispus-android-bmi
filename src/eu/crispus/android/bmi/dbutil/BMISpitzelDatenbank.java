package eu.crispus.android.bmi.dbutil;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import eu.crispus.android.bmi.GewichtEingebenActivity;
import eu.crispus.android.bmi.guiutil.ZeitAuswahlEnum;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Datenbank-Manager um die SQLite-Datenbank greifbar zu machen.
 *
 * @author Johannes Kraus
 * @version 1.0
 */
public class BMISpitzelDatenbank extends SQLiteOpenHelper {

    /**
     * String Konstante, um beim Loggen einen Hinweis auf diese Klasse zu haben.
     */
    private static final String TAG = "BMISpitzelDatenbank";

    /**
     * Name der Datenbank für den BMI-Spitzel.
     */
    private static final String DATENBANK_NAME = "bmi_spitzel.db";

    /**
     * Version der Datenbank für den BMI-Spitzel.
     */
    private static final int DATENBANK_VERSION = 2;

    /**
     * Überschriebener C'tor, in dem die Datenbank-Angaben übergeben werden.
     */
    public BMISpitzelDatenbank(Context context) {
        super(context, DATENBANK_NAME, null, DATENBANK_VERSION);
    }

    /**
     * Diese Methode wird aufgerufen, um die Datenbank zu erzeugen.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Datenbank-Tabelle für Gewicht und Datum anlegen.
            db.execSQL(DatumGewichtTable.SQL_CREATE);
            // Erstes DB-Update für Fett-, Wasser- und Muskelanteil einspielen.
            db.execSQL(DatumGewichtTable.SQL_ALTER_TABLE_UPDATE_FETTANTEIL);
            db.execSQL(DatumGewichtTable.SQL_ALTER_TABLE_UPDATE_WASSERANTEIL);
            db.execSQL(DatumGewichtTable.SQL_ALTER_TABLE_UPDATE_MUSKELANTEIL);
        } catch (Exception e) {
            Log.e(TAG, "Fehler beim erstellen der Datenbank.", e);
        }

    }

    /**
     * Diese Methode wird aufgerufen, um die Datenbank zu aktualisieren.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            if ((oldVersion == 1) && (newVersion == 2)) {
                db.execSQL(DatumGewichtTable.SQL_ALTER_TABLE_UPDATE_FETTANTEIL);
                db.execSQL(DatumGewichtTable.SQL_ALTER_TABLE_UPDATE_WASSERANTEIL);
                db.execSQL(DatumGewichtTable.SQL_ALTER_TABLE_UPDATE_MUSKELANTEIL);
            }
        } catch (Exception e) {
            Log.e(TAG, "Fehler beim aktualisieren der Datenbank.", e);
        }
    }

    public ArrayList<DatumGewichtDAOEntry> getEntriesByZeitAuswahl(ZeitAuswahlEnum zeitAuswahlEnum) {
        ArrayList<DatumGewichtDAOEntry> returnArrayList = new ArrayList<>();
        Cursor cursor = null;
        try {
            DatumGewichtDAOEntry tmpDatumGewichtDAOEntry;

            cursor = getReadableDatabase().query(
                    DatumGewichtTable.TABLE_NAME,
                    new String[]{DatumGewichtTable.COLUMN_DATUM, DatumGewichtTable.COLUMN_GEWICHT, DatumGewichtTable.COLUMN_FETT, DatumGewichtTable.COLUMN_WASSER, DatumGewichtTable.COLUMN_MUSKEL},
                    null,
                    null,
                    null,
                    null,
                    "substr(" + DatumGewichtTable.COLUMN_DATUM + ",7)||substr(" + DatumGewichtTable.COLUMN_DATUM + ",4 ,2)||substr("
                            + DatumGewichtTable.COLUMN_DATUM + ", 0, 2) ASC");

            Calendar todayCalendar = Calendar.getInstance();
            Calendar maxCalendar = Calendar.getInstance();
            maxCalendar.add(Calendar.DAY_OF_YEAR, -zeitAuswahlEnum.getDaysOfYearValue());
            while (cursor.moveToNext()) {
                tmpDatumGewichtDAOEntry = new DatumGewichtDAOEntry(null, GewichtEingebenActivity.simpleDateFormat.parse(cursor.getString(0)),
                        cursor.getString(1), cursor.getString(0), cursor.getString(2), cursor.getString(3), cursor.getString(4));

                if (maxCalendar.before(tmpDatumGewichtDAOEntry.getDatum()) && todayCalendar.after(tmpDatumGewichtDAOEntry.getDatum())) {
                    returnArrayList.add(tmpDatumGewichtDAOEntry);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Es ist ein Fehler beim ermitteln der Einträge aufgetreten. Der angegebene Zeitraum war: " + zeitAuswahlEnum, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // ArrayListe sortieren (DatumGewichtDAOEntry wird nach Datum sortiert)
        Collections.sort(returnArrayList);
        return returnArrayList;
    }

}
