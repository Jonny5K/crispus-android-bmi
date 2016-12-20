package eu.crispus.android.bmi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import eu.crispus.android.bmi.dbutil.BMISpitzelDatenbank;
import eu.crispus.android.bmi.dbutil.DatumGewichtTable;
import eu.crispus.android.bmi.guiutil.DatumGewichtsAdapter;
import eu.crispus.android.bmi.util.SpinnerItem;

/**
 * Klasse um ein bestehendes Gewicht zu bearbeiten bzw. zu löschen.
 * 
 * @author Johannes Kraus
 * @version 1.0
 */
public class GewichtBearbeitenActivity extends ListActivity {

	/**
	 * String Konstante, um beim Loggen einen Hinweis auf diese Klasse zu haben.
	 */
	private static final String TAG = "GewichtBearbeitenActivity";

	/**
	 * Name der Datei, die für den Datenexport abgelegt wird.
	 */
	private static final String FILENAME = "BMI_SPITZEL.csv";

	/**
	 * Trennzeichen für die Einträge der CSV-Datei.
	 */
	private static final String CSV_SEPARATOR = ";";
	
	/**
	 * Platzhalter falls ein Anteil nicht angegeben wurde.
	 */
	private static final String PLATZHALTER = "X";

	/**
	 * Verbindung zur Datenbank.
	 */
	private BMISpitzelDatenbank bmiSpitzelDatenbank;
	
	/**
	 * ID um den Nachfragedialog zum überschreiben einer existierenden Datei zu
	 * identifizieren.
	 */
	private static final int OVERWRITE_EXISTING_FILE_DIALOG_ID = 1;
	
	/**
	 * ID um den Nachfragedialog zum Löschen aller Datensätzen beim importieren zu
	 * identifizieren.
	 */
	private static final int OVERWRITE_DELETE_DIALOG_ID = 2;

	/**
	 * Cursor auf die Datenbank. Wird als Datenelement verwaltet, damit mit cursor.requery(); Änderungen an der Datenmenge beachtet werden können.
	 */
	private Cursor cursorTop;

	/**
	 * Wird aufgerufen, wenn diese Aktivity erzeugt wird. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		Einstellungen.loadLocalInformation(getBaseContext());
		setContentView(R.layout.gewicht_bearbeiten);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.crispus_custom_titlebar);
		getWindow().setFormat(PixelFormat.RGBA_8888);

		final ListView listView = getListView();
		 
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				AlertDialog.Builder adb = new AlertDialog.Builder(GewichtBearbeitenActivity.this);
				adb.setTitle(getString(R.string.gewichtBearbeitenActivityTitel));
				adb.setMessage(getString(R.string.gewichtBearbeitenActivityBitte));
																					
				adb.setPositiveButton("Ok", null);
				adb.show();
			}
		});

		fillFilerSpinnerWithData();
		
		registerForContextMenu(findViewById(android.R.id.list));
		try {
			bmiSpitzelDatenbank = new BMISpitzelDatenbank(this);
			cursorTop = bmiSpitzelDatenbank.getReadableDatabase().rawQuery(DatumGewichtTable.SQL_SELECT_ALL_DESC + " limit 5", null);

			startManagingCursor(cursorTop);
			

			DatumGewichtsAdapter datumGewichtsAdapter = new DatumGewichtsAdapter(this, R.layout.row_gewicht_bearbeiten, cursorTop);
			setListAdapter(datumGewichtsAdapter);
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim Abfragen der Datenbank.", e);
		}
	}
	
	/**
	 * Die onResume-Methode wird aufgerufen, wenn die View "wieder" angezeigt wird, ohne dass Sie vorher beendet wurde. Falls aus dem Dialog in die Prefenreces
	 * gesprungen wird müssen die Texte gegebenfalls wieder angepasst werden.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		((Button) findViewById(R.id.buttonBackTitlebar)).setText(R.string.buttonZurueck);
		((TextView) findViewById(R.id.textViewTitlebar)).setText(R.string.app_name);
	}

	/**
	 * Diese Methode befüllt die Auswahl-Box spinnerFilterAnzahl mit Einträgen zum Filtern von den Daten. Dabei sind die
	 * Grenzen 10, 25, 50 und alle Einträge vorhanden. Wenn ein Einträg aus dem Spinner ausgewählt wird, wird der aktuelle
	 * Cursor geschlossen und ein neuer Query erzeugt und entsprechend aktiv gesetzt. 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void fillFilerSpinnerWithData() {
		try {
		final Spinner spinnerFilterAnzahl = (Spinner) findViewById(R.id.spinnerFilterAnzahl);
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, new SpinnerItem[] {   
				new SpinnerItem("10", getString(R.string.gewichtBearbeitenFilter10)), 
				new SpinnerItem("25", getString(R.string.gewichtBearbeitenFilter25)), 
				new SpinnerItem("50", getString(R.string.gewichtBearbeitenFilter50)), 
				new SpinnerItem("" + Integer.MAX_VALUE, getString(R.string.gewichtBearbeitenFilterAlle))
         });
		spinnerFilterAnzahl.setAdapter(adapter);
		spinnerFilterAnzahl.setOnItemSelectedListener(
	            new OnItemSelectedListener() {
	                @Override
					public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
	                	SpinnerItem spinnerItem = (SpinnerItem) spinnerFilterAnzahl.getSelectedItem();
	                	//Speichern der Auswahl in den Einstellungen
	                	SharedPreferences sharedPreferences = Einstellungen.getAnwendungsEinstellungen(getBaseContext());
	            		SharedPreferences.Editor editor = sharedPreferences.edit();
	    				editor.putString(Einstellungen.KEY_GEWICHTBEARBEITEN_ANZAHL, spinnerItem.getValue());
	    				editor.commit();
	                	
	    				stopManagingCursor(cursorTop);
	                	cursorTop.deactivate();
	                	cursorTop = bmiSpitzelDatenbank.getReadableDatabase().rawQuery(DatumGewichtTable.SQL_SELECT_ALL_DESC + " limit " + spinnerItem.getValue(), null);
	                	startManagingCursor(cursorTop);
	                	DatumGewichtsAdapter datumGewichtsAdapter = new DatumGewichtsAdapter(GewichtBearbeitenActivity.this, R.layout.row_gewicht_bearbeiten, cursorTop);
	        			setListAdapter(datumGewichtsAdapter);
	                }

	                @Override
					public void onNothingSelected(AdapterView<?> arg0) {
	                }
	    });
		
		SharedPreferences sharedPreferences = Einstellungen.getAnwendungsEinstellungen(getBaseContext());
		String tmpAnzahlEintraege = sharedPreferences.getString(Einstellungen.KEY_GEWICHTBEARBEITEN_ANZAHL, Einstellungen.NONE);
		if (tmpAnzahlEintraege.equals(Einstellungen.NONE)) {
			//Es wurde noch kein Eintrag ausgewählt, standardmäßig werden nur 25 Einträge angezeigt.
			spinnerFilterAnzahl.setSelection(1);
		} else {
			if (tmpAnzahlEintraege.equals("10")) {
				spinnerFilterAnzahl.setSelection(0);
			} else if (tmpAnzahlEintraege.equals("25")) {
				spinnerFilterAnzahl.setSelection(1);
			} else if (tmpAnzahlEintraege.equals("50")) {
				spinnerFilterAnzahl.setSelection(2);
			} else if (tmpAnzahlEintraege.equals("" + Integer.MAX_VALUE)) {
				spinnerFilterAnzahl.setSelection(3);
			} else {
				//Der gespeicherte Eintrag existiert nicht, es wird der Standard-Eintrag (25) ausgewählt.
				spinnerFilterAnzahl.setSelection(1);
			}
		}
		
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim erstellen der Auswahl-Box (spinnerFilterAnzahl).", e);
		}
	}
	
	/**
	 * In der folgenden Methode wird das Menü (menue_gewicht_bearbeiten.xml) in die Gewicht-Bearbeiten-Seite eingebunden.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menue_gewicht_bearbeiten, menu);
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
			case R.id.menueNeu:
				intent = new Intent(this, GewichtEingebenActivity.class);
				startActivity(intent);
				return true;
			case R.id.menueZurueck:
				finish();
				return true;
			case R.id.menueExport:
				exportDataToSDCard(false);
				return true;
			case R.id.menueImport:
				importDataFromSDCard();
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
	 * Diese Methode erledigt den Export aller Daten aus der Datenbank in eine einfache CSV-Datei. Die Einträge in der CSV-Datei sind wie folgt aufgebaut: ID;
	 * Datum; Gewicht
	 * 
	 * @param ignoreExistingFile Gibt an, ob eine vorhandene Datei einfach überschrieben werden soll.
	 */
	private void exportDataToSDCard(boolean ignoreExistingFile) {
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File root = Environment.getExternalStorageDirectory();
				if (root.canWrite()) {
					// Nur wenn eine SD-Karte gemountet ist, wird auch etwas geschrieben.
					File file = new File(root, FILENAME);
					if (file.exists() && !ignoreExistingFile) {
						showDialog(OVERWRITE_EXISTING_FILE_DIALOG_ID);
						return;
					}
					
					FileWriter fileWriter = new FileWriter(file);
					BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

					Cursor cursorExport = bmiSpitzelDatenbank.getReadableDatabase().rawQuery(DatumGewichtTable.SQL_SELECT_ALL, null);
					cursorExport.moveToFirst();
					while (!cursorExport.isAfterLast()) {
						bufferedWriter.write(cursorExport.getString(0) + CSV_SEPARATOR + cursorExport.getString(1) + CSV_SEPARATOR + cursorExport.getString(2)
								+ CSV_SEPARATOR + cursorExport.getString(3) + CSV_SEPARATOR + cursorExport.getString(4) + CSV_SEPARATOR 
								+ cursorExport.getString(5) + CSV_SEPARATOR + System.getProperty("line.separator"));

						cursorExport.moveToNext();
					}

					bufferedWriter.close();
					Toast.makeText(this.getBaseContext(),
							getString(R.string.gewichtBearbeitenHinweisDatenErfolgreichExportiertSDCard) + file.getCanonicalPath(), Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(this.getBaseContext(),
							getString(R.string.gewichtBearbeitenHinweisKeineSchreibrechteSDCard), Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(this.getBaseContext(), getString(R.string.gewichtBearbeitenHinweisNichtGemountetSDCard), Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim exportieren der Datenbank.", e);
		}
	}

	/**
	 * Diese Methode erledigt den Import aller Daten aus einer einfachen CSV-Datei in die Datenbank. Es werden alle bestehenden Einträge gelöscht (Der Benutzer
	 * wird vorab gefragt, ob er sicher ist). Die Einträge in der CSV-Datei sind wie folgt aufgebaut: ID; Datum; Gewicht
	 */
	private void importDataFromSDCard() {
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File root = Environment.getExternalStorageDirectory();
				if (root.canRead()) {
					// Nur wenn eine SD-Karte gemountet ist, wird auch etwas gelesen.
					File file = new File(root, FILENAME);
					if (!file.exists()) {
						Toast.makeText(this.getBaseContext(),
								getString(R.string.gewichtBearbeitenHinweisImportiertKeineDatei) + ": " + file.getCanonicalPath(), Toast.LENGTH_LONG)
								.show();
						return;
					}
					
					FileReader fileReader = new FileReader(file);
					BufferedReader bufferedReader = new BufferedReader(fileReader);

					Cursor cursorExport = bmiSpitzelDatenbank.getReadableDatabase().rawQuery(DatumGewichtTable.SQL_SELECT_ALL, null);
					if (cursorExport.getCount() > 0) {
						showDialog(OVERWRITE_DELETE_DIALOG_ID);
						return;
					}
					
					String currentLine = bufferedReader.readLine();
					StringTokenizer stringTokenizer = null;
					String datum = null;
					String gewicht = null;
					String fettAnteil = null;
					String wasserAnteil = null;
					String muskelAnteil = null;
					while (currentLine != null) {
						stringTokenizer = new StringTokenizer(currentLine, CSV_SEPARATOR);
						try {
							stringTokenizer.nextToken(); //ID wird nicht benötigt
							datum = stringTokenizer.nextToken();
							gewicht = stringTokenizer.nextToken().replace(',', '.');
							fettAnteil = stringTokenizer.nextToken().replace(',', '.');
							wasserAnteil = stringTokenizer.nextToken().replace(',', '.');
							muskelAnteil = stringTokenizer.nextToken().replace(',', '.');
							bmiSpitzelDatenbank.getWritableDatabase().execSQL(DatumGewichtTable.SQL_INSERT,
									new String[] { datum, gewicht, fettAnteil, wasserAnteil, muskelAnteil });
							
						} catch (Exception e) {
							Log.e(TAG, "Fehler beim einem CSV-Eintrag wird ignoriert und mit den übrigen weiter gearbeitet.");
						}
						currentLine = bufferedReader.readLine();
					}
					cursorTop.requery();
					
					bufferedReader.close();
					Toast.makeText(this.getBaseContext(),
							getString(R.string.gewichtBearbeitenHinweisDatenErfolgreichImportiertSDCard) + file.getCanonicalPath(), Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(this.getBaseContext(),
							getString(R.string.gewichtBearbeitenHinweisKeineLeserechteSDCard), Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(this.getBaseContext(), getString(R.string.gewichtBearbeitenHinweisNichtGemountetSDCard), Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim importieren der Daten.", e);
		}
	}

	/**
	 * Hier wird ein ContextMenu an die einzelnen List-Einträge gehangen.
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		menu.setHeaderTitle(R.string.contextHeaderTitel);
		inflater.inflate(R.menu.gewicht_datum_kontext, menu);
	}

	/**
	 * Diese Methode wird aufgerufen, wenn eine Eintrag aus dem Kontextmenü aufgerufen wurde.
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		String selectedDatumString = ((TextView) info.targetView.findViewById(R.id.toptext)).getText().toString().substring(6).trim();
		
		String selectedGewichtString = ((TextView) info.targetView.findViewById(R.id.gewichttext)).getText().toString();
		selectedGewichtString = selectedGewichtString.substring(8, selectedGewichtString.length() - 2).trim();
		
		String selectedFettanteilString = ((TextView) info.targetView.findViewById(R.id.textViewFettanteil)).getText().toString().replace("%", "").trim();
		String selectedWasseranteilString = ((TextView) info.targetView.findViewById(R.id.textViewWasseranteil)).getText().toString().replace("%", "").trim();
		String selectedMuskelanteilString = ((TextView) info.targetView.findViewById(R.id.textViewMuskelanteil)).getText().toString().replace("%", "").trim();
		if (selectedFettanteilString.equals(PLATZHALTER)) {
			selectedFettanteilString = "";
		} 
		if (selectedWasseranteilString.equals(PLATZHALTER)) {
			selectedWasseranteilString = "";
		}
		if (selectedMuskelanteilString.equals(PLATZHALTER)) {
			selectedMuskelanteilString = "";
		}
		
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.contextNeu:
			intent = new Intent(this, GewichtEingebenActivity.class);
			startActivity(intent);
			return true;
		case R.id.contextBearbeiten:
			intent = new Intent(this, GewichtEingebenActivity.class);
			intent.putExtra(DatumGewichtTable.COLUMN_DATUM, selectedDatumString);
			intent.putExtra(DatumGewichtTable.COLUMN_GEWICHT, selectedGewichtString);
			intent.putExtra(DatumGewichtTable.COLUMN_FETT, selectedFettanteilString);
			intent.putExtra(DatumGewichtTable.COLUMN_WASSER, selectedWasseranteilString);
			intent.putExtra(DatumGewichtTable.COLUMN_MUSKEL, selectedMuskelanteilString);
			startActivity(intent);
			return true;
		case R.id.contextLoeschen:
			try {
				bmiSpitzelDatenbank.getWritableDatabase().delete(DatumGewichtTable.TABLE_NAME, DatumGewichtTable.COLUMN_DATUM + "=?",
						new String[] { selectedDatumString });
				cursorTop.requery();
			} catch (Exception e) {
				Log.e(TAG, "Fehler beim löschen aus der Datenbank.", e);
			}
			return true;
		}

		return super.onContextItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		stopManagingCursor(cursorTop);
		bmiSpitzelDatenbank.close();
		cursorTop.close();
		super.onDestroy();
	}
	
	/**
	 * Hier werden die Dialoge erzeugt, die event. in der Activity benötigt werden.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case OVERWRITE_EXISTING_FILE_DIALOG_ID:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.gewichtBearbeitenHinweisExportierenDateiVorhanden));
			builder.setCancelable(false);
			builder.setPositiveButton(getString(R.string.gewichtEingebenActivityPositiveButton), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					exportDataToSDCard(true);
				}
			});
			builder.setNegativeButton(getString(R.string.gewichtEingebenActivityNegativButton), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			return builder.create();
		case OVERWRITE_DELETE_DIALOG_ID:
			AlertDialog.Builder tmpBuilder = new AlertDialog.Builder(this);
			tmpBuilder.setMessage(getString(R.string.gewichtBearbeitenActivityOverride)).setCancelable(false)
					.setPositiveButton(getString(R.string.gewichtEingebenActivityPositiveButton), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							//Wenn OK gewählt wurde, dann werden alle Einträge in der DB gelöscht und die Methode "" erneut aufgerufen.
							bmiSpitzelDatenbank.getWritableDatabase().execSQL(DatumGewichtTable.SQL_DELETE, new String[]{});
							importDataFromSDCard();
						}
					}).setNegativeButton(getString(R.string.gewichtEingebenActivityNegativButton), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			return tmpBuilder.create();
		}

		return null;
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
}
