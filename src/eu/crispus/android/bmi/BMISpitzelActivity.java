package eu.crispus.android.bmi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Programm um sein Gewicht zu Dokumentieren. Es kann pro Tag ein Gewicht in Kg eingegeben werden. Mit Hilfe des Gewichts und 
 * der Angaben des Geburtstags, der Größe und des Geschlechts wird dann der Body-Maß-Index (BMI) berechnet.
 * Der BMI kann für die letzten 7 Tage in einem Graphen angezeigt werden. Das Gewicht der letzten 7 Tage wird ebenfalls im
 * Graphen angezeigt.
 * 
 * @author Johannes Kraus
 * @version 1.0
 */
public class BMISpitzelActivity extends Activity {

	/**
	 * String Konstante, um beim Loggen einen Hinweis auf diese Klasse zu haben.
	 */
	private static final String TAG = "BMISpitzelActivity";

	/**
	 * Id für den Informations-Dialog.
	 */
	private static final int DIALOG_INFO_ID = 0;

	/**
	 * Angabe der Programmversion. Wird eigentlich mit PackageInfo aus dem AndroidManifest gelesen, wird nur im Fehlerfall benötigt.
	 */
	public static final String VERSION = "1.11.0.0";

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		Einstellungen.loadLocalInformation(getBaseContext());
		
		//Wenn noch kein Einheitensystem gewählt wurde, dann wird das metrische-System als Standard gesetzt.
		SharedPreferences sharedPreferences = Einstellungen.getAnwendungsEinstellungen(getBaseContext());
		String tmpStringEinheitensystem = Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getString(Einstellungen.KEY_EINHEITENSYSTEM, Einstellungen.NONE);
		if (Einstellungen.NONE.equals(tmpStringEinheitensystem)) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(Einstellungen.KEY_EINHEITENSYSTEM, "metrisch");
			editor.commit();
		}
		//Wenn noch keine Amputationen gewählt wurden, dann wird "- keine Amputation -" gespeichert.
		String tmpStringAmputation = Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getString(Einstellungen.KEY_AMPUTATION, Einstellungen.NONE);
		if (Einstellungen.NONE.equals(tmpStringAmputation)) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(Einstellungen.KEY_AMPUTATION, "0");
			editor.commit();
		}
		tmpStringAmputation = Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getString(Einstellungen.KEY_WEITERE_AMPUTATION, Einstellungen.NONE);
		if (Einstellungen.NONE.equals(tmpStringAmputation)) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(Einstellungen.KEY_WEITERE_AMPUTATION, "0");
			editor.commit();
		}
		
		getWindow().setFormat(PixelFormat.RGBA_8888);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.crispus_custom_titlebar);
		
		((Button) findViewById(R.id.buttonBackTitlebar)).setText(getText(R.string.menueBeenden));
	}
	
	/**
	 * Einfacher Trick um die Sprache der App anzupassen. Wenn in den Einstellungen die Schriftart geändert wurde, wird der Bildschirm einmal gedreht, dadurch
	 * wird die App neu gezeichnet.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		if (!((TextView) findViewById(R.id.textViewUeberschrift)).getText().equals(getString(R.string.startseiteIntro))) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}
	}

	/**
	 * OnClick Ereignis der Schaltfläche zur Gewichtseingabe eines bestimmten
	 * Datums.
	 * 
	 * @param view
	 *            Schaltfläche die aktiviert (gedrückt) wurde.
	 */
	public void onClickButtonGewichtEingeben(final View view) {
		try {
			Intent intent = new Intent(this, GewichtEingebenActivity.class);
			startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "Es ist ein Fehler in der onClickButtonGewichtEingeben()-Methode aufgetreten.", e);
		}
	}

	/**
	 * OnClick Ereignis der Schaltfläche zur Gewichtseingabe eines bestimmten
	 * Datums.
	 * 
	 * @param view
	 *            Schaltfläche die aktiviert (gedrückt) wurde.
	 */
	public void onClickButtonGewichtBearbeiten(final View view) {
		try {
			Intent intent = new Intent(this, GewichtBearbeitenActivity.class);
			startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "Es ist ein Fehler in der onClickButtonGewichtBearbeiten()-Methode aufgetreten.", e);
		}
	}

	/**
	 * OnClick Ereignis der Schaltfläche um den Verlaufsgraphen anzuzeigen.
	 * 
	 * @param view
	 *            Schaltfläche die aktiviert (gedrückt) wurde.
	 */
	public void onClickButtonGraphAnzeigen(final View view) {
		try {
			Intent intent = new Intent(this, GraphActivity.class);
			startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "Es ist ein Fehler in der onClickButtonGraphAnzeigen()-Methode aufgetreten.", e);
		}
	}
	
	/**
	 * OnClick Ereignis der Schaltfläche um den BMI direkt zu berechnen.
	 * 
	 * @param view
	 *            Schaltfläche die aktiviert (gedrückt) wurde.
	 */
	public void onClickButtonBmiBerechnen(final View view) {
		try {
			Intent intent = new Intent(this, BMIBerechnenActivity.class);
			startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "Es ist ein Fehler in der onClickButtonBmiBerechnen()-Methode aufgetreten.", e);
		}
	}
	
	/**
	 * OnClick Ereignis der Schaltfläche zur Einstellungen.
	 * 
	 * @param view
	 *            Schaltfläche die aktiviert (gedrückt) wurde.
	 */
	public void onClickButtonEinstellungen(final View view) {
		try {
			Intent intent = new Intent(this, Einstellungen.class);
			startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "Es ist ein Fehler in der onClickButtonEinstellungen()-Methode aufgetreten.", e);
		}
	}
	
	/**
	 * OnClick Ereignis der Schaltfläche zur eMail an den Entwickler.
	 * 
	 * @param view
	 *            Schaltfläche die aktiviert (gedrückt) wurde.
	 */
	public void onClickButtonEMailAnEntwickler(final View view) {
		try {
			final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("text/plain");
			emailIntent.putExtra( android.content.Intent.EXTRA_EMAIL, new String[]{ "android@crispus.eu" } );
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, view.getContext().getString(R.string.emailSubject));
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, view.getContext().getString(R.string.emailText) + "\n\n");
			startActivity(Intent.createChooser(emailIntent, "E-Mail senden..."));
		} catch (Exception e) {
			Log.e(TAG, "Es ist ein Fehler in der onClickButtonEMailAnEntwickler()-Methode aufgetreten.", e);
		}
	}

	/**
	 * In der folgenden Methode wird das Menü (hauptmenu.xml) in die Startseite
	 * eingebunden.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.hauptmenue, menu);
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
				break;
			case R.id.menueHilfe:
				intent = new Intent(this, HilfeAnzeigenActivity.class);
				startActivity(intent);
				break;
			case R.id.menueInfo:
				showDialog(DIALOG_INFO_ID);
				break;
			case R.id.menueBeenden:
				finish();
				return true;
			case R.id.menueEMailAnEntwickler:
				try {
					final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
					emailIntent.setType("text/plain");
					emailIntent.putExtra( android.content.Intent.EXTRA_EMAIL, new String[]{ "android@crispus.eu" } );
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getBaseContext().getString(R.string.emailSubject));
					emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getBaseContext().getString(R.string.emailText) + "\n\n");
					startActivity(Intent.createChooser(emailIntent, "E-Mail senden..."));
				} catch (Exception e) {
					Log.e(TAG, "Es ist ein Fehler in der onOptionsItemSelected()-Methode aufgetreten.", e);
				}
				break;
			default:
				return super.onOptionsItemSelected(item);
			}
		} catch (Exception e) {
			Log.e(TAG, "Es ist ein Fehler in der onOptionsItemSelected()-Methode aufgetreten.", e);
		}

		return false;
	}

	/**
	 * Diese Methode erzeugt den Info-Über Dialog der App.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		if (id == DIALOG_INFO_ID) {
			dialog = new Dialog(this);
			dialog.setContentView(R.layout.infoueber);
			dialog.setTitle(getString(R.string.bmiSpitzelActivityInfoUeberDialog));//"Info zu Crispus\u00ae BMI-Spitzel");
			TextView text = (TextView) dialog.findViewById(R.id.name);
			text.setText(getString(R.string.bmiSpitzelActivityInfoUeberDialogName));//"Body Maß Index (BMI)-Spitzel");
			text = (TextView) dialog.findViewById(R.id.version);
			PackageInfo packageInfo;
			try {
				packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				text.setText(getString(R.string.bmiSpitzelActivityInfoUeberDialogVersion) + ": " + packageInfo.versionName);
			} catch (NameNotFoundException e) {
				Log.e(TAG, "Fehler beim ermitteln der Version.", e);
				text.setText(getString(R.string.bmiSpitzelActivityInfoUeberDialogVersion) + ": " + VERSION);
			}
			text = (TextView) dialog.findViewById(R.id.copyright);
			text.setText(getString(R.string.bmiSpitzelActivityInfoUeberDialogCopyright));//"Copyright\u00a9 2011 Johannes Kraus. Alle Rechte vorbehalten.");
			text = (TextView) dialog.findViewById(R.id.author);
			text.setText("Crispus (Johannes Kraus)");
			dialog.setCanceledOnTouchOutside(true);
			ImageView image = (ImageView) dialog.findViewById(R.id.image);
			image.setImageResource(R.drawable.info);
		}

		return dialog;
	}
	
	/**
	 * Methode um bei einem Click auf den Zurück-Button in der Titlebar.
	 * 
	 * @param view
	 *            Button der gedrückt wurde.
	 */
	public void onClickButtonBackTitlebar(View view) {
		try {
			finish();// Beenden der App
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim schließen der App.", e);
		}
	}
}