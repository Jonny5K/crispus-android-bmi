package eu.crispus.android.bmi.guiutil;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import eu.crispus.android.bmi.Einstellungen;
import eu.crispus.android.bmi.R;
import eu.crispus.android.bmi.dbutil.DatumGewichtTable;
import eu.crispus.android.bmi.util.BMIBereich;

/**
 * Adapter um Datum und Gewichts Einträge in einer Liste auszugeben. Auch der Fett-, Wasser- und Museklanteil werden auch über diesen Adapter ausgegeben, werden
 * aber nur optional angegeben.
 * 
 * @author Johannes Kraus
 * @version 1.0
 */
public class DatumGewichtsAdapter extends SimpleCursorAdapter {

	/**
	 * Tag um die Log's unterscheiden zu können.
	 * 
	 * @see http://developer.android.com/reference/android/util/Log.html
	 */
	private static final String TAG = "DatumGewichtsAdapter";

	/**
	 * Datenbank-Cursor um die Einträge in der Liste ausgeben zu können.
	 */
	private final Cursor cursor;

	/**
	 * Namen der Daten-Spalten, die angezeigt werden sollen.
	 */
	private static final String[] ANZEIGE_DATEN = new String[] { DatumGewichtTable.COLUMN_DATUM, DatumGewichtTable.COLUMN_GEWICHT,
			DatumGewichtTable.COLUMN_FETT, DatumGewichtTable.COLUMN_WASSER, DatumGewichtTable.COLUMN_MUSKEL };

	/**
	 * View IDs, in welche die Daten geschrieben werden. Werden nicht wirklich genutzt, da die View in der Methode getView selbst zusammengebaut wird.
	 */
	private static final int[] SIMPLE_LIST_VIEW_IDS = new int[] { android.R.id.text1, android.R.id.text2 };

	public DatumGewichtsAdapter(Context context, int textViewResourceId, Cursor cursor) {
		super(context, textViewResourceId, cursor, ANZEIGE_DATEN, SIMPLE_LIST_VIEW_IDS);
		this.cursor = cursor;
	}

	@Override
	public View getView(int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		try {
			if (view == null) {
				LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = layoutInflater.inflate(R.layout.row_gewicht_bearbeiten, null);
			}

			cursor.moveToPosition(position);

			TextView textViewDatum = (TextView) view.findViewById(R.id.toptext);
			TextView textViewGewicht = (TextView) view.findViewById(R.id.gewichttext);
			TextView textViewBMI = (TextView) view.findViewById(R.id.bmitext);

			// Textviews für die unterschiedlichen optionalen Angaben (Fett-, Wasser- und Muskelanteil).
			TextView textViewFettanteil = (TextView) view.findViewById(R.id.textViewFettanteil);
			TextView textViewWasseranteil = (TextView) view.findViewById(R.id.textViewWasseranteil);
			TextView textViewMuskelanteil = (TextView) view.findViewById(R.id.textViewMuskelanteil);
			TextView textViewFettanteilLabel = (TextView) view.findViewById(R.id.textViewFettanteilLabel);
			TextView textViewWasseranteilLabel = (TextView) view.findViewById(R.id.textViewWasseranteilLabel);
			TextView textViewMuskelanteilLabel = (TextView) view.findViewById(R.id.textViewMuskelanteilLabel);

			BMIRechner bmiRechner = new BMIRechner(parent.getContext());
			if (bmiRechner.isErrorReadingPreferences()) {
				//showDialog(ERROR_READING_PREFERENCES);
				Log.e(TAG, "");
			} else {
				
			
			

				textViewDatum.setText(parent.getContext().getString(R.string.datumGewichtsAdapterDate) + ": "
						+ cursor.getString(cursor.getColumnIndex(DatumGewichtTable.COLUMN_DATUM)));
				// In der Datenbank werden nur metrische Werte gespeichert, deshalbt müssen diese hier gegebenfalls umgerechnet werden.
				String einheitensystem = Einstellungen.getAnwendungsEinstellungen(view.getContext()).getString(Einstellungen.KEY_EINHEITENSYSTEM, "metrisch");
				if ("metrisch".equals(einheitensystem)) {
					String tmpGewicht = cursor.getString(cursor.getColumnIndex(DatumGewichtTable.COLUMN_GEWICHT));
					if (tmpGewicht.contains(",")) {
						tmpGewicht = tmpGewicht.replace(",", ".");
					}
					float berechneterBMI = bmiRechner.berechneBMIAsFlaot(Float.parseFloat(tmpGewicht), bmiRechner.getKoerpergroesse());
//					textViewGewicht.setText(view.getContext().getString(R.string.ihrBMI) + ": " + BMIRechner.DECIMAL_FORMAT.format(berechneterBMI));
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
						//textViewGewicht.setTextColor(Color.YELLOW);
						textViewBMI.setTextColor(Color.YELLOW);
						textViewBMI.setText(R.string.bmiBerechnenInfoUntergewicht);
					} else if (bmiBereich.getBereich2().isWertInBereich(berechneterBMI)) {
						//textViewGewicht.setTextColor(Color.GREEN);
						textViewBMI.setTextColor(Color.GREEN);
						textViewBMI.setText(R.string.bmiBerechnenInfoNormal);
					} else if (bmiBereich.getBereich3().isWertInBereich(berechneterBMI)) {
						//textViewGewicht.setTextColor(Color.YELLOW);
						textViewBMI.setTextColor(Color.YELLOW);
						textViewBMI.setText(R.string.bmiBerechnenInfoUebergewicht);
					} else if (bmiBereich.getBereich4().isWertInBereich(berechneterBMI)) {
						//textViewGewicht.setTextColor(Color.rgb(251, 166, 1));
						textViewBMI.setTextColor(Color.rgb(251, 166, 1));
						textViewBMI.setText(R.string.bmiBerechnenInfoAdipositas);
					} else if (bmiBereich.getBereich5().isWertInBereich(berechneterBMI)) {
						//textViewGewicht.setTextColor(Color.RED);
						textViewBMI.setTextColor(Color.RED);
						textViewBMI.setText(R.string.bmiBerechnenInfoStarkAdipositas);
					} 
					
					textViewBMI.setText(view.getContext().getString(R.string.ihrBMI) + ": " + BMIRechner.DECIMAL_FORMAT.format(berechneterBMI) + " (" + textViewBMI.getText().toString() + ")");
//					float gewicht = Float.parseFloat(tmpGewicht);
					textViewGewicht.setText(parent.getContext().getString(R.string.datumGewichtsAdapterGewicht) + ": " + BMIRechner.DECIMAL_FORMAT.format(Float.parseFloat(tmpGewicht))
							+ " " + parent.getContext().getString(R.string.kilogrammKurztext));
//					textViewBMI.setText(parent.getContext().getString(R.string.datumGewichtsAdapterIhrBMI) + ": "
//							+ bmiRechner.berechneBMIWithRating(gewicht, parent.getContext()));
				} else {
					String tmpGewicht = cursor.getString(cursor.getColumnIndex(DatumGewichtTable.COLUMN_GEWICHT));
					if (tmpGewicht.contains(",")) {
						tmpGewicht = tmpGewicht.replace(",", ".");
					}
					float berechneterBMI = bmiRechner.berechneBMIAsFlaot(Float.parseFloat(tmpGewicht) * BMIRechner.KG_TO_POUND, bmiRechner.getKoerpergroesse());
					textViewGewicht.setText(view.getContext().getString(R.string.ihrBMI) + ": " + BMIRechner.DECIMAL_FORMAT.format(berechneterBMI));
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
						//textViewGewicht.setTextColor(Color.YELLOW);
						textViewBMI.setTextColor(Color.YELLOW);
						textViewBMI.setText(R.string.bmiBerechnenInfoUntergewicht);
					} else if (bmiBereich.getBereich2().isWertInBereich(berechneterBMI)) {
						//textViewGewicht.setTextColor(Color.GREEN);
						textViewBMI.setTextColor(Color.GREEN);
						textViewBMI.setText(R.string.bmiBerechnenInfoNormal);
					} else if (bmiBereich.getBereich3().isWertInBereich(berechneterBMI)) {
						//textViewGewicht.setTextColor(Color.YELLOW);
						textViewBMI.setTextColor(Color.YELLOW);
						textViewBMI.setText(R.string.bmiBerechnenInfoUebergewicht);
					} else if (bmiBereich.getBereich4().isWertInBereich(berechneterBMI)) {
						//textViewGewicht.setTextColor(Color.rgb(251, 166, 1));
						textViewBMI.setTextColor(Color.rgb(251, 166, 1));
						textViewBMI.setText(R.string.bmiBerechnenInfoAdipositas);
					} else if (bmiBereich.getBereich5().isWertInBereich(berechneterBMI)) {
						//textViewGewicht.setTextColor(Color.RED);
						textViewBMI.setTextColor(Color.RED);
						textViewBMI.setText(R.string.bmiBerechnenInfoStarkAdipositas);
					} 
					
					textViewBMI.setText(view.getContext().getString(R.string.ihrBMI) + ": " + BMIRechner.DECIMAL_FORMAT.format(berechneterBMI) + " (" + textViewBMI.getText().toString() + ")");
//					float gewicht = Float.parseFloat(tmpGewicht) * BMIRechner.KG_TO_POUND;
//					textViewGewicht.setText(parent.getContext().getString(R.string.datumGewichtsAdapterGewicht) + ": " + BMIRechner.DECIMAL_FORMAT.format(gewicht)
//							+ " " + parent.getContext().getString(R.string.pfundText));
					textViewGewicht.setText(parent.getContext().getString(R.string.datumGewichtsAdapterGewicht) + ": " + BMIRechner.DECIMAL_FORMAT.format(Float.parseFloat(tmpGewicht) * BMIRechner.KG_TO_POUND)
							+ " " + parent.getContext().getString(R.string.kilogrammKurztext));
//					textViewBMI.setText(parent.getContext().getString(R.string.datumGewichtsAdapterIhrBMI) + ": "
//							+ bmiRechner.berechneBMIWithRating(gewicht, parent.getContext()));
				}
			}
			
			// Prüfen ob zu den unterschiedlichen Anteilen Angaben gemacht wurden und dann entsprechend anzeigen (oder auch nicht)
			try {
				boolean showFettanteil = false;
				boolean showWasseranteil = false;
				boolean showMuskelanteil = false;
				
				Double tmpValue = cursor.getDouble(cursor.getColumnIndex(DatumGewichtTable.COLUMN_FETT));
				if (tmpValue.doubleValue() <= 0) {
					textViewFettanteil.setText("X");
				} else {
					showFettanteil = true;
					textViewFettanteil.setText(BMIRechner.DECIMAL_FORMAT.format(tmpValue) + " %");
				}

				tmpValue = cursor.getDouble(cursor.getColumnIndex(DatumGewichtTable.COLUMN_WASSER));
				if (tmpValue.doubleValue() <= 0) {
					textViewWasseranteil.setText("X");
				} else {
					showWasseranteil = true;
					textViewWasseranteil.setText(BMIRechner.DECIMAL_FORMAT.format(tmpValue) + " %");
				}

				tmpValue = cursor.getDouble(cursor.getColumnIndex(DatumGewichtTable.COLUMN_MUSKEL));
				if (tmpValue.doubleValue() <= 0) {
					textViewMuskelanteil.setText("X");
				} else {
					showMuskelanteil = true;
					textViewMuskelanteil.setText(BMIRechner.DECIMAL_FORMAT.format(tmpValue) + " %");
				}
				
				if (!showFettanteil && !showWasseranteil && !showMuskelanteil) {
					view.findViewById(R.id.imageViewAnteile).setVisibility(View.GONE);
					textViewFettanteil.setVisibility(View.GONE);
					textViewFettanteilLabel.setVisibility(View.GONE);
					textViewWasseranteil.setVisibility(View.GONE);
					textViewWasseranteilLabel.setVisibility(View.GONE);
					textViewMuskelanteil.setVisibility(View.GONE);
					textViewMuskelanteilLabel.setVisibility(View.GONE);
				} else {
					view.findViewById(R.id.imageViewAnteile).setVisibility(View.VISIBLE);
					textViewFettanteil.setVisibility(View.VISIBLE);
					textViewFettanteilLabel.setVisibility(View.VISIBLE);
					textViewWasseranteil.setVisibility(View.VISIBLE);
					textViewWasseranteilLabel.setVisibility(View.VISIBLE);
					textViewMuskelanteil.setVisibility(View.VISIBLE);
					textViewMuskelanteilLabel.setVisibility(View.VISIBLE);
				}
			} catch (Exception e) {
				Log.e(TAG, "Fehler beim erstellen der zusätzlichen Angaben bei GewichtBearbeiten-View. Position: " + position, e);
			}
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim erstellen der View. Position: " + position, e);
		}

		return view;
	}
}
