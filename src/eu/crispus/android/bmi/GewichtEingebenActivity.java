package eu.crispus.android.bmi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import eu.crispus.android.bmi.dbutil.BMISpitzelDatenbank;
import eu.crispus.android.bmi.dbutil.DatumGewichtTable;
import eu.crispus.android.bmi.guiutil.BMIRechner;
import eu.crispus.android.bmi.guiutil.EditKeyListenerCloseOnEnter;
import eu.crispus.android.bmi.guiutil.TextWatcherNurEinKommaOderPunkt;
import eu.crispus.android.bmi.util.BMIBereich;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Klasse um ein neues Gewicht (am aktuellen Tag) eingegeben zu können.
 *
 * @author Johannes Kraus
 * @version 1.0
 */
public class GewichtEingebenActivity extends Activity {

    /**
     * String Konstante, um beim Loggen einen Hinweis auf diese Klasse zu haben.
     */
    private static final String TAG = "GewichtEingebenActivity";

    /**
     * ID um den Eingabedialog für das Datum zu identifizieren.
     */
    private static final int DATE_DIALOG_ID = 0;

    /**
     * ID um den Hinweisdialog für das fehlende Eingaben zu identifizieren.
     */
    private static final int DATA_DIALOG_ID = 1;

    /**
     * ID um den Nachfragedialog zum Überschreiben von Datensätzen zu identifizieren.
     */
    private static final int OVERWRITE_DIALOG_ID = 2;

    /**
     * ID um den Hinweisdialog für Fehlerhafte Eingabe anzuzeigen.
     */
    private static final int DATA_DIALOG_ID_ERROR = 3;

    /**
     * ID um den Hinweisdialog für Fehlerhafte Eingabe in den Einstellungen anzuzeigen.
     */
    private static final int ERROR_READING_PREFERENCES = 4;

    /**
     * SimpelDateFormater um das Datum zu formatieren.
     */
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);

    /**
     * Hier wird eine Referenz auf das Eingabefeld gespeichert, damit dieses nachträglich verbessert werden kann.
     */
    private EditText editTextDatum;

    /**
     * Hier wird eine Referenz auf das Eingabefeld gespeichert, damit dieses nachträglich verbessert werden kann.
     */
    private EditText editTextGewicht;

    /**
     * Hier wird eine Referenz auf das Ausgabefeld gespeichert, damit dieses nachträglich gefüllt werden kann.
     */
    private TextView textViewBmi;

    /**
     * Hier wird eine Referenz auf das Ausgabefeld-Label gespeichert, damit dieses nachträglich angezeigt werden kann.
     */
    private TextView textViewBMILabel;

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
        setContentView(R.layout.gewicht_eingeben);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.crispus_custom_titlebar);

        bmiSpitzelDatenbank = new BMISpitzelDatenbank(this);

        editTextGewicht = (EditText) findViewById(R.id.editTextGewicht);
        editTextGewicht.setOnKeyListener(new EditKeyListenerCloseOnEnter());
        editTextGewicht.addTextChangedListener(new TextWatcherNurEinKommaOderPunkt(editTextGewicht));
        textViewBmi = (TextView) findViewById(R.id.textViewBmi);
        textViewBMILabel = (TextView) findViewById(R.id.textViewBmiLabel);

        // Wenn ein Datum als Extra übergeben wurde, wird dieses fest verankert.
        final Bundle intentExtras = getIntent().getExtras();
        if (intentExtras != null) {
            editTextDatum = (EditText) findViewById(R.id.editTextDatum);
            editTextDatum.setText(intentExtras.getString(DatumGewichtTable.COLUMN_DATUM));
            editTextDatum.setEnabled(false);

            editTextGewicht.setText(intentExtras.getString(DatumGewichtTable.COLUMN_GEWICHT));
            // Es wird kein OnClickListern an das Feld gehangen, da dieses nicht geändert werden soll.
            // Auslesen der Anteile (Fett, Wasser und Muskeln)
            ((EditText) findViewById(R.id.editTextFettanteil)).setText(intentExtras.getString(DatumGewichtTable.COLUMN_FETT));
            ((EditText) findViewById(R.id.editTextWasseranteil)).setText(intentExtras.getString(DatumGewichtTable.COLUMN_WASSER));
            ((EditText) findViewById(R.id.editTextMuskelanteil)).setText(intentExtras.getString(DatumGewichtTable.COLUMN_MUSKEL));
        } else {
            // Eingabefeld für das Datum mit "heute" füllen.
            Date currentDate = new Date();
            editTextDatum = (EditText) findViewById(R.id.editTextDatum);
            editTextDatum.setText(simpleDateFormat.format(currentDate));
            editTextDatum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(DATE_DIALOG_ID);
                }
            });
        }

        EditText editText = (EditText) findViewById(R.id.editTextFettanteil);
        editText.setVisibility(View.VISIBLE);
        editText.setOnKeyListener(new EditKeyListenerCloseOnEnter());
        editText.addTextChangedListener(new TextWatcherNurEinKommaOderPunkt(editText));
        editText = (EditText) findViewById(R.id.editTextWasseranteil);
        editText.setVisibility(View.VISIBLE);
        editText.setOnKeyListener(new EditKeyListenerCloseOnEnter());
        editText.addTextChangedListener(new TextWatcherNurEinKommaOderPunkt(editText));
        editText = (EditText) findViewById(R.id.editTextMuskelanteil);
        editText.setVisibility(View.VISIBLE);
        editText.setOnKeyListener(new EditKeyListenerCloseOnEnter());
        editText.addTextChangedListener(new TextWatcherNurEinKommaOderPunkt(editText));
    }

    /**
     * Die onResume-Methode wird aufgerufen, wenn die View "wieder" angezeigt wird, ohne dass Sie vorher beendet wurde. Falls aus dem Dialog in die Prefenreces
     * gesprungen wird müssen die Texte gegebenfalls wieder angepasst werden.
     */
    @Override
    protected void onResume() {
        super.onResume();
        ((TextView) findViewById(R.id.gewichtEingeben)).setText(R.string.gewichtEingeben);
        ((TextView) findViewById(R.id.gewichtEingebenInfo)).setText(R.string.gewichtEingebenInfo);
        ((TextView) findViewById(R.id.gewichtEingabeDatum)).setText(R.string.gewichtEingabeDatum);
        ((TextView) findViewById(R.id.textViewGewicht)).setText(R.string.gewichtEingabeGewicht);
        ((TextView) findViewById(R.id.textViewBmiLabel)).setText(R.string.gewichtEingabeBmi);

        ((Button) findViewById(R.id.buttonGewichtHinzufuegen)).setText(R.string.buttonGewichtHinzufuegen);
        ((Button) findViewById(R.id.buttonBackTitlebar)).setText(R.string.buttonZurueck);
        ((TextView) findViewById(R.id.textViewTitlebar)).setText(R.string.app_name);

        if ("imperial".equals(Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getString(Einstellungen.KEY_EINHEITENSYSTEM, Einstellungen.NONE))) {
            // Hier werden die Texte für die Oberfläche vom metrischen Einheitensystem auf das Imperiale Einheitensystem gesetzt.
            TextView tmpTextView = (TextView) findViewById(R.id.textViewGewicht);
            tmpTextView.setText(tmpTextView.getText().toString().replaceAll("kilogram", getString(R.string.pfundText)));
            tmpTextView.setText(tmpTextView.getText().toString().replaceAll("Kilogramm", getString(R.string.pfundText)));
        } else {
            TextView tmpTextView = (TextView) findViewById(R.id.textViewGewicht);
            tmpTextView.setText(getString(R.string.gewichtEingabeGewicht));
        }

        // Zusätzliche Eingabefelder für Körperanteile (Fett, Wasser und Muskel) sichtbar schalten
        if (Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getBoolean(Einstellungen.KEY_FETTANTEIL, false)) {
            findViewById(R.id.textViewFettanteil).setVisibility(View.VISIBLE);
            findViewById(R.id.editTextFettanteil).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.textViewFettanteil).setVisibility(View.GONE);
            findViewById(R.id.editTextFettanteil).setVisibility(View.GONE);
        }
        if (Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getBoolean(Einstellungen.KEY_WASSERANTEIL, false)) {
            findViewById(R.id.textViewWasseranteil).setVisibility(View.VISIBLE);
            findViewById(R.id.editTextWasseranteil).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.textViewWasseranteil).setVisibility(View.GONE);
            findViewById(R.id.editTextWasseranteil).setVisibility(View.GONE);
        }
        if (Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getBoolean(Einstellungen.KEY_MUSKELANTEIL, false)) {
            findViewById(R.id.textViewMuskelanteil).setVisibility(View.VISIBLE);
            findViewById(R.id.editTextMuskelanteil).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.textViewMuskelanteil).setVisibility(View.GONE);
            findViewById(R.id.editTextMuskelanteil).setVisibility(View.GONE);
        }
    }

    /**
     * In der folgenden Methode wird das Menü (menue_gewicht_bearbeiten.xml) in die Gewicht-Bearbeiten-Seite eingebunden.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menue_gewicht_eingeben, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * In der folgenden Methode werden die Menü-Einträge abgearbeitet.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            Intent intent;
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
     * Hier wird der Datums-Dialog erzeugt.
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Calendar c = Calendar.getInstance();
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, dateSetListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            case DATA_DIALOG_ID:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.gewichtEingebenActivityEingabefelderFehler));
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                return builder.create();
            case OVERWRITE_DIALOG_ID:
                AlertDialog.Builder tmpBuilder = new AlertDialog.Builder(this);
                tmpBuilder.setMessage(getString(R.string.gewichtEingebenActivityOverride)).setCancelable(false)
                        .setPositiveButton(getString(R.string.gewichtEingebenActivityPositiveButton), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // In der Datenbank werden immer Metrische-Werte gespeichert.
                                String einheitensystem = Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getString(Einstellungen.KEY_EINHEITENSYSTEM,
                                        "metrisch");
                                if ("metrisch".equals(einheitensystem)) {
                                    bmiSpitzelDatenbank.getWritableDatabase().execSQL(
                                            DatumGewichtTable.SQL_UPDATE,
                                            new String[]{editTextGewicht.getText().toString(), getFettanteil(), getWasseranteil(), getMuskelanteil(),
                                                    editTextDatum.getText().toString()});
                                } else {
                                    bmiSpitzelDatenbank.getWritableDatabase().execSQL(
                                            DatumGewichtTable.SQL_UPDATE,
                                            new String[]{"" + (Double.parseDouble(tmpStringValue) * BMIRechner.POUND_TO_KG), getFettanteil(), getWasseranteil(),
                                                    getMuskelanteil(), editTextDatum.getText().toString()});
                                }
                                bmiSpitzelDatenbank.close();
                            }
                        }).setNegativeButton(getString(R.string.gewichtEingebenActivityNegativButton), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                return tmpBuilder.create();
            case DATA_DIALOG_ID_ERROR:
                builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.gewichtEingebenActivityGewichtFehler));
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                return builder.create();
            case ERROR_READING_PREFERENCES:
                builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.gewichtEingebenActivityPreferencesFehler));
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Cursor cursor = null;
                        try {
                            cursor = bmiSpitzelDatenbank.getReadableDatabase().rawQuery(DatumGewichtTable.SQL_SELECT_BY_DATE,
                                    new String[]{editTextDatum.getText().toString()});
                            if (cursor.getCount() > 0) {
                                showDialog(OVERWRITE_DIALOG_ID);
                            } else {
                                bmiSpitzelDatenbank.getWritableDatabase().execSQL(DatumGewichtTable.SQL_INSERT,
                                        new String[]{editTextDatum.getText().toString(), tmpStringValue, getFettanteil(), getWasseranteil(), getMuskelanteil()});
                            }

                        } catch (Exception e) {
                            Log.e(TAG, "Fehler beim Einfügen in die Datenbank.", e);
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    }
                });
                return builder.create();
        }

        return null;
    }

    /**
     * Listener um das neue Datum zu verarbeiten.
     */
    private final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            try {
                Date selectedDate = simpleDateFormat.parse(String.valueOf(dayOfMonth) + "." + String.valueOf(monthOfYear + 1) + "." + String.valueOf(year));
                editTextDatum.setText(simpleDateFormat.format(selectedDate));
                Toast.makeText(GewichtEingebenActivity.this,
                        getString(R.string.gewichtEingebenActivitySelectedDate) + ": " + simpleDateFormat.format(selectedDate), Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                Log.e(TAG, "Fehler beim parsen des Datums.", e);
                Date currentDate = new Date();
                editTextDatum.setText(simpleDateFormat.format(currentDate));
            }
        }
    };

    private String tmpStringValue;

    /**
     * Methode um die Werte des Benutzer's in die Datenbank zu schreiben.
     *
     * @param view Schaltfläche des Dialogs.
     */
    public void onClickButtonGewichtHinzufuegen(View view) {
        // Als erstes wird geprüft, ob die beiden Eingabefelder gefüllt sind.
        if (editTextDatum.getText().toString().trim().equals("") || editTextGewicht.getText().toString().trim().equals("")) {
            showDialog(DATA_DIALOG_ID);
            return;
        }
        if (editTextGewicht.hasFocus()) {
            InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(editTextGewicht.getWindowToken(), 0);
        }
        try {
            tmpStringValue = editTextGewicht.getText().toString();
            if (tmpStringValue.contains(".") && tmpStringValue.contains(",")) {
                // Punkt und Komma geht nicht.
                showDialog(DATA_DIALOG_ID_ERROR);
                return;
            } else if (tmpStringValue.contains(",")) {
                // Komma durch String ersetzen.
                tmpStringValue = tmpStringValue.replace(",", ".");
            }
            // Wenn nun mehr als ein Punkt vorhanden ist, dann wird eine Exception geworfen.
            float tmpValue = Float.parseFloat(tmpStringValue);
            if ((tmpValue < 0) || (tmpValue > 400)) {
                showDialog(DATA_DIALOG_ID_ERROR);
                return;
            }
        } catch (Exception e) {
            showDialog(DATA_DIALOG_ID_ERROR);
            return;
        }

        // Berechnung des BMI und der Bewertung
        BMIRechner bmiRechner = new BMIRechner(getBaseContext());
        if (bmiRechner.isErrorReadingPreferences()) {
            showDialog(ERROR_READING_PREFERENCES);
        } else {
            float berechneterBMI = bmiRechner.berechneBMIAsFlaot(Float.parseFloat(tmpStringValue), bmiRechner.getKoerpergroesse());
            textViewBmi.setText(view.getContext().getString(R.string.ihrBMI) + ": " + BMIRechner.DECIMAL_FORMAT.format(berechneterBMI));
            // Den richtigen BMIBereich für das Alter und Geschlecht suchen
            BMIBereich bmiBereich = null;
            if (bmiRechner.getGeschlecht().equals("maennlich")) {
                for (BMIBereich currentBMIBereich : BMIRechner.maennerBMIBereiche) {
                    if (currentBMIBereich.getAlter().isWertInBereich(bmiRechner.getAlter())) {
                        bmiBereich = currentBMIBereich;
                        break;
                    }
                }
            } else {
                for (BMIBereich currentBMIBereich : BMIRechner.frauenBMIBereiche) {
                    if (currentBMIBereich.getAlter().isWertInBereich(bmiRechner.getAlter())) {
                        bmiBereich = currentBMIBereich;
                        break;
                    }
                }
            }
            // Den BMI-Label farblich machen
            if (bmiBereich.getBereich1().isWertInBereich(berechneterBMI)) {
                textViewBmi.setTextColor(Color.YELLOW);
                textViewBmi.setText(textViewBmi.getText() + " (" + getText(R.string.bmiBerechnenInfoUntergewicht) + ")");
            } else if (bmiBereich.getBereich2().isWertInBereich(berechneterBMI)) {
                textViewBmi.setTextColor(Color.GREEN);
                textViewBmi.setText(textViewBmi.getText() + " (" + getText(R.string.bmiBerechnenInfoNormal) + ")");
            } else if (bmiBereich.getBereich3().isWertInBereich(berechneterBMI)) {
                textViewBmi.setTextColor(Color.YELLOW);
                textViewBmi.setText(textViewBmi.getText() + " (" + getText(R.string.bmiBerechnenInfoUebergewicht) + ")");
            } else if (bmiBereich.getBereich4().isWertInBereich(berechneterBMI)) {
                textViewBmi.setTextColor(Color.rgb(251, 166, 1));
                textViewBmi.setText(textViewBmi.getText() + " (" + getText(R.string.bmiBerechnenInfoAdipositas) + ")");
            } else if (bmiBereich.getBereich5().isWertInBereich(berechneterBMI)) {
                textViewBmi.setTextColor(Color.RED);
                textViewBmi.setText(textViewBmi.getText() + " (" + getText(R.string.bmiBerechnenInfoStarkAdipositas) + ")");
            }

            // Alle Anzeigeelemente sichtbar schalten.
            textViewBmi.setVisibility(View.VISIBLE);
            textViewBMILabel.setVisibility(View.VISIBLE);

            Cursor cursor = null;
            try {
                cursor = bmiSpitzelDatenbank.getReadableDatabase().rawQuery(DatumGewichtTable.SQL_SELECT_BY_DATE,
                        new String[]{editTextDatum.getText().toString()});
                if (cursor.getCount() > 0) {
                    showDialog(OVERWRITE_DIALOG_ID);
                } else {
                    // In der Datenbank werden immer Metrische-Werte gespeichert.
                    String einheitensystem = Einstellungen.getAnwendungsEinstellungen(view.getContext()).getString(Einstellungen.KEY_EINHEITENSYSTEM,
                            "metrisch");
                    if ("metrisch".equals(einheitensystem)) {
                        bmiSpitzelDatenbank.getWritableDatabase().execSQL(DatumGewichtTable.SQL_INSERT,
                                new String[]{editTextDatum.getText().toString(), tmpStringValue, getFettanteil(), getWasseranteil(), getMuskelanteil()});
                    } else {
                        bmiSpitzelDatenbank.getWritableDatabase().execSQL(
                                DatumGewichtTable.SQL_INSERT,
                                new String[]{editTextDatum.getText().toString(), "" + (Double.parseDouble(tmpStringValue) * BMIRechner.POUND_TO_KG),
                                        getFettanteil(), getWasseranteil(), getMuskelanteil()});
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Fehler beim Einfügen in die Datenbank.", e);
            } finally {
                cursor.close();
                bmiSpitzelDatenbank.close();
            }
        }
    }

    /**
     * Diese Methode liefert (abhängig von der Einstellung in den Preferences), den eingegeben Fettanteil zurück. Wenn die Eingabe ungültig ist, wird -1
     * zurückgegeben.
     *
     * @return Den vom Benutzer eingegeben Fettanteil, oder -1 als Default-Wert-
     */
    private String getFettanteil() {
        String fettanteil = "-1";
        if (Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getBoolean(Einstellungen.KEY_FETTANTEIL, false)) {
            try {
                fettanteil = ((EditText) findViewById(R.id.editTextFettanteil)).getText().toString().replace(",", ".");
                if (fettanteil.trim().equals("") || (Double.parseDouble(fettanteil) <= 0)) {
                    fettanteil = "-1";
                } else if (Double.parseDouble(fettanteil) > 100) {
                    fettanteil = "100";
                }
            } catch (Exception e) {
                Log.e(TAG, "Fehler beim auslesen der Fettanteils, es wird -1 in der Datenbank gespeichert.", e);
                fettanteil = "-1";
            }
        }

        return fettanteil;
    }

    /**
     * Diese Methode liefert (abhängig von der Einstellung in den Preferences), den eingegeben Wasseranteil zurück. Wenn die Eingabe ungültig ist, wird -1
     * zurückgegeben.
     *
     * @return Den vom Benutzer eingegeben Wasseranteil, oder -1 als Default-Wert-
     */
    private String getWasseranteil() {
        String wasseranteil = "-1";

        if (Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getBoolean(Einstellungen.KEY_WASSERANTEIL, false)) {
            try {
                wasseranteil = ((EditText) findViewById(R.id.editTextWasseranteil)).getText().toString().replace(",", ".");
                if (wasseranteil.trim().equals("") || (Double.parseDouble(wasseranteil) <= 0)) {
                    wasseranteil = "-1";
                } else if (Double.parseDouble(wasseranteil) > 100) {
                    wasseranteil = "100";
                }
            } catch (Exception e) {
                Log.e(TAG, "Fehler beim auslesen der Wasseranteils, es wird -1 in der Datenbank gespeichert.", e);
                wasseranteil = "-1";
            }
        }

        return wasseranteil;
    }

    /**
     * Diese Methode liefert (abhängig von der Einstellung in den Preferences), den eingegeben Muskelanteil zurück. Wenn die Eingabe ungültig ist, wird -1
     * zurückgegeben.
     *
     * @return Den vom Benutzer eingegeben Muskelanteil, oder -1 als Default-Wert-
     */
    private String getMuskelanteil() {
        String muskelanteil = "-1";
        if (Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getBoolean(Einstellungen.KEY_MUSKELANTEIL, false)) {
            try {
                muskelanteil = ((EditText) findViewById(R.id.editTextMuskelanteil)).getText().toString().replace(",", ".");
                if (muskelanteil.trim().equals("") || (Double.parseDouble(muskelanteil) <= 0)) {
                    muskelanteil = "-1";
                } else if (Double.parseDouble(muskelanteil) > 100) {
                    muskelanteil = "100";
                }
            } catch (Exception e) {
                Log.e(TAG, "Fehler beim auslesen der Muskelanteils, es wird -1 in der Datenbank gespeichert.", e);
                muskelanteil = "-1";
            }
        }

        return muskelanteil;
    }

    /**
     * Methode um bei einem Click auf den Zurück-Button in der Titlebar.
     *
     * @param view Button der gedrückt wurde.
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
     * @param view Button der gedrückt wurde.
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
