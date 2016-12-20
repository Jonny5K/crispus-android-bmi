package eu.crispus.android.bmi.guiutil;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * TextWatcher der die Aufgabe hat, zu verhindern dass mehr als ein Punkt oder ein Komma eingegeben werden kann.
 * 
 * @author Johannes Kraus
 * @version 1.0
 */
public class TextWatcherNurEinKommaOderPunkt implements TextWatcher {

	/**
	 * Wegen Fehler in Android: http://code.google.com/p/android/issues/detail?id=2626
	 */
	private boolean kommaOderPunktBereitsEnthalten = false;

	/**
	 * Alter String des Eingabefeldes.
	 */
	private String oldString;

	/**
	 * Edittext, an dem dieser TextWatche hängt.
	 */
	private final EditText editTextObject;

	/**
	 * Konstruktor dem das EditText Widget übergeben werden muss, an welchem der Textwatcher hängt.
	 * 
	 * @param editTextObject
	 *            EditText, an welchem der Textwatcher hängt.
	 */
	public TextWatcherNurEinKommaOderPunkt(EditText editTextObject) {
		this.editTextObject = editTextObject;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (kommaOderPunktBereitsEnthalten && (before == 0) && ((s.charAt(start) == '.') || (s.charAt(start) == ','))) {
			editTextObject.setText(oldString);
			editTextObject.setSelection(oldString.length() - 1);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		kommaOderPunktBereitsEnthalten = s.toString().contains(".") || s.toString().contains(",");
		oldString = s.toString();
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
