package eu.crispus.android.bmi.guiutil;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Einfacher EditKexListener der die Tastertur eines Elements schließt, wenn die Entertaste gedrückt wurde.
 * 
 * @author Johannes Kraus
 * @version 1.0
 */
public class EditKeyListenerCloseOnEnter implements View.OnKeyListener {
	
	/**
	 * Schließt die Tastatur, wenn Enter gedrückt wurde.
	 * 
	 * @param view View die verarbeitet wird.
	 * @param keyCode Key-Code der gedrückten Taste.
	 * @param event Ausgelöstes Event-Objekt.
	 * @return true, wenn das Ereigniss bearbeitet wurde, ansonsten false.
	 */
	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {
		// Enter Taste wurde geklickt
		if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
			InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

			return true;
		}

		return false;
	}
}
