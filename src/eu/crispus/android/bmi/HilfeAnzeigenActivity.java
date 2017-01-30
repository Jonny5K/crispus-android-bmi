package eu.crispus.android.bmi;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;

/**
 * Activity um die Hilfeseite der App anzuzeigen.
 *
 * @author Johannes Kraus
 * @version 1.0
 */
public class HilfeAnzeigenActivity extends Activity {

    /**
     * String Konstante, um beim Loggen einen Hinweis auf diese Klasse zu haben.
     */
    private static final String TAG = "HilfeAnzeigenActivity";

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.hilfe);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.crispus_custom_titlebar);
        getWindow().setFormat(PixelFormat.RGBA_8888);

        final WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        initialisiereWebKit(webview);
        webview.setBackgroundColor(0);//Hintergrund auf Transparent setzen
        webview.setBackgroundResource(R.drawable.background); //Gradient einfügen
        webview.bringToFront();
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

    private void initialisiereWebKit(final WebView webView) {
        InputStream inputStream = this.getResources().openRawResource(R.raw.hilfe);
        try {
            if ((inputStream != null) && (inputStream.available() > 0)) {
                final byte[] byteArray = new byte[inputStream.available()];
                inputStream.read(byteArray);

                webView.loadDataWithBaseURL(null, new String(byteArray), "text/html", "utf-8", null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Es ist ein Fehler beim initialisieren der Hilfe aufgetreten.", e);
        }
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
}
