package eu.crispus.android.bmi;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import eu.crispus.android.bmi.guiutil.CharacterManager;
import eu.crispus.android.bmi.guiutil.Graph;
import eu.crispus.android.bmi.guiutil.GraphAuswahlEnum;
import eu.crispus.android.bmi.guiutil.Hintergrund;

/**
 * Activity zum ausgeben eines Graphen mit verschiedenen Kurven.
 * 
 * @author Johannes Kraus
 * @version 1.0
 */
public class GraphActivity extends Activity {

	/**
	 * String Konstante, um beim Loggen einen Hinweis auf diese Klasse zu haben.
	 */
	private static final String TAG = "GraphActivity";

	/**
	 * Gibt an, ob es sich um ein kleines Display handelt.
	 */
	static boolean smallScreenResolution; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Einstellungen.loadLocalInformation(getBaseContext());
		// Graph im Full-Screen anzeigen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Graph im Landscape Modus anzeigen.
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		smallScreenResolution = getWindowManager().getDefaultDisplay().getWidth() < 400;
		
		erzeugeGrap();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Einstellungen.loadLocalInformation(getBaseContext());
		erzeugeGrap();
	}

	/**
	 * In dieser Methode wird der Graph zum Anzeigen der Gewichtskurve erzeugt. Wird in der Methode erledigt, da der Code doppelt benötigt wird.
	 */
	private void erzeugeGrap() {
		GraphRenderer graphRenderer = new GraphRenderer();
		GLSurfaceView glSurfaceView = new GLSurfaceView(this);
		try {
			glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
			glSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim setFormat ", e);
		}
		glSurfaceView.setRenderer(graphRenderer);

		setContentView(glSurfaceView);

		// Erzeugen eines CharacterManager
		CharacterManager characterManager = new CharacterManager(1, 1);

		// Laden der Texturen.
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.opengl_zahlen);
		characterManager.loadBitmapDigits(bitmap);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.opengl_buchstaben);
		characterManager.loadBitmapLetters(bitmap);

		// Graph zum Rendern setzen
		graph = new Graph(this);
		graph.setCharacterManager(characterManager);
		graphRenderer.setGraph(graph);
	}

	/**
	 * In der folgenden Methode wird das Menü (menue_gewicht_bearbeiten.xml) in die Gewicht-Bearbeiten-Seite eingebunden.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menue_graph, menu);
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
			case R.id.menueZurueck:
				finish();
				return true;
			case R.id.menueNaechsterGraph:
				onTouchEvent(null);
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
	 * GraphRenderer für diese Klasse.
	 */
	private Graph graph = null;

	/**
	 * Diese Variable wird benötigt, um zu unterscheiden ob ein neues onTouchEvent aufgetreten ist.
	 */
	private long lastDownTime = 0;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if ((event != null) && (lastDownTime == event.getDownTime())) {
			return false;
		} else {
			if (event != null) {
				lastDownTime = event.getDownTime();
			}
		}
		
		boolean fettanteilAnzeigen = Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getBoolean(Einstellungen.KEY_FETTANTEIL, false);
		boolean wasseranteilAnzeigen = Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getBoolean(Einstellungen.KEY_WASSERANTEIL, false);
		boolean muskelanteilAnzeigen = Einstellungen.getAnwendungsEinstellungen(getBaseContext()).getBoolean(Einstellungen.KEY_MUSKELANTEIL, false);

		if (graph.getGraphAuswahlEnum() == GraphAuswahlEnum.GEWICHT) {
			if (fettanteilAnzeigen) {
				graph.setGraphAuswahlEnum(GraphAuswahlEnum.FETTANTEIL);
			} else if (wasseranteilAnzeigen) {
				graph.setGraphAuswahlEnum(GraphAuswahlEnum.WASSERANTEIL);
			} else if (muskelanteilAnzeigen) {
				graph.setGraphAuswahlEnum(GraphAuswahlEnum.MUSKELANTEIL);
			}
		} else if (graph.getGraphAuswahlEnum() == GraphAuswahlEnum.FETTANTEIL) {
			if (wasseranteilAnzeigen) {
				graph.setGraphAuswahlEnum(GraphAuswahlEnum.WASSERANTEIL);
			} else if (muskelanteilAnzeigen) {
				graph.setGraphAuswahlEnum(GraphAuswahlEnum.MUSKELANTEIL);
			} else {
				graph.setGraphAuswahlEnum(GraphAuswahlEnum.GEWICHT);
			}
		} else if (graph.getGraphAuswahlEnum() == GraphAuswahlEnum.WASSERANTEIL) {
			if (muskelanteilAnzeigen) {
				graph.setGraphAuswahlEnum(GraphAuswahlEnum.MUSKELANTEIL);
			} else {
				graph.setGraphAuswahlEnum(GraphAuswahlEnum.GEWICHT);
			}
		} else {
			graph.setGraphAuswahlEnum(GraphAuswahlEnum.GEWICHT);
		}
		return super.onTouchEvent(event);
	}

	/**
	 * Diese Methode gibt an, ob eine kleine Auflösung vorhanden ist. Wenn dies der Fall ist,
	 * dann wird der Graph anders (näher) gerendert.
	 * 
	 * @return true wenn die Width-Auflösung kleiner als 400 px ist.
	 */
	public static boolean isSmallScreenResolution() {
		return smallScreenResolution;
	}
}

class GraphRenderer implements GLSurfaceView.Renderer {

	/**
	 * Dieses Objekt zeichnet den eigentlichen Graphen und dessen Inhalt.
	 */
	private Graph graph;

	/**
	 * Rechteck für den Hintergrund.
	 */
	private final Hintergrund hintergrund = new Hintergrund();;

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Hintergrundfarbe ( rgba ). Im Prinzip egal, da im Hintergrund ein Objekt "Hintergrund" liegt, dass alles andere verbirgt.
		gl.glClearColor(0f, 0.38f, 0.62f, 1.0f);// Gleiche Farbe wie in CharacterManager!
		// Enable Smooth Shading.
		gl.glShadeModel(GL10.GL_SMOOTH);
		// Depth buffer setup.
		gl.glClearDepthf(1.0f);
		// Enable depth testing.
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// The type of depth testing to do.
		gl.glDepthFunc(GL10.GL_LEQUAL);
		// Really nice perspective calculations.
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// Reset the projection matrix
		gl.glLoadIdentity();
		// Calculate the aspect ratio of the window
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 1000.0f);
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset the modelview matrix
		gl.glLoadIdentity();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Replace the current matrix with the identity matrix
		gl.glLoadIdentity();
		// Zeichnen des Hintergrundes
		hintergrund.draw(gl);
		// Nach hintenfahren (die Kamera verschieben)
		if (GraphActivity.isSmallScreenResolution()) {
			gl.glTranslatef(0, 0, -4.7f);
		} else {
			gl.glTranslatef(-0.07f, 0, -4);
		}
		// Zeichnen des Graphen
		graph.draw(gl);
	}

	/**
	 * Setzt den Graphen, der gezeichnet werden soll
	 * 
	 * @param graph
	 *            Graph der gezeichnet werden soll.
	 */
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
}