package eu.crispus.android.bmi.guiutil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Calendar;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.Log;
import eu.crispus.android.bmi.Einstellungen;
import eu.crispus.android.bmi.GewichtEingebenActivity;
import eu.crispus.android.bmi.GraphActivity;
import eu.crispus.android.bmi.R;
import eu.crispus.android.bmi.dbutil.BMISpitzelDatenbank;
import eu.crispus.android.bmi.dbutil.DatumGewichtDAOEntry;
import eu.crispus.android.bmi.guiutil.CharacterManager.Orientation;
import eu.crispus.android.bmi.util.BMIBereich;

/**
 * Diese Klasse ist zum Zeichnen des Graphen zuständig. Sie zeichnet den Graph und dessen Inhalt. Der Inhalt wird hier direkt aus der Datenbank geladen.
 * 
 * @author Johannes Kraus
 * @version 1.0
 */
public class Graph {

	/**
	 * String Konstante, um beim Loggen einen Hinweis auf diese Klasse zu haben.
	 */
	private static final String TAG = "Graph";

	/**
	 * Verbindung zur Datenbank.
	 */
	private final BMISpitzelDatenbank bmiSpitzelDatenbank;

	/**
	 * Character Manager zum beschriften der Achse.
	 */
	private CharacterManager characterManager;

	/**
	 * Buffer für die Farben des Gewichtsverlauf.
	 */
	private final FloatBuffer colorBuffer;
	
	/**
	 * Array mit den Koordinaten, um einen Graphen zu zeichnen.
	 */
	private final float[] vertices = { 0.0f, 2.0f, 0.0f, 0.0f, -0.1f, 0.0f, -0.1f, 0.0f, 0.0f, 4.0f, 0.0f, 0.0f,
			// Koordinaten für die Pfeil an der Y-Achse
			-0.05f, 2.0f, 0.0f, 0.05f, 2.0f, 0.0f, 0.0f, 2.15f, 0.0f,
			// Koordinaten für die Pfeil an der x-Achse
			4.0f, 0.05f, 0.0f, 4.0f, -0.05f, 0.0f, 4.15f, 0.0f, 0.0f };

	/**
	 * Reihenfolge der Punkte im vertices Array. Wird für die richtige Darstellung des Graphen (ohne Dreicke an den Enden) benötigt.
	 */
	private final short[] indicesGraph = { 0, 1, 2, 3 };

	/**
	 * Reihenfolge der Punkte im vertices Array. Wird für die richtige Darstellung des Pfeile an den Achsen des Graphen benötigt.
	 */
	private final short[] indicesGraphArrow = { 4, 5, 6, 7, 8, 9 };

	/**
	 * Vertices-Array in Form eines Vektor-Buffer.
	 */
	private final FloatBuffer vertexBuffer;

	/**
	 * Indices-Array in Form eines ShortBuffer.
	 */
	private final ShortBuffer indexGraphBuffer;

	/**
	 * indices Array in Form eines ShortBuffer.
	 */
	private final ShortBuffer indexGraphBufferArrow;

	/**
	 * Gibt das höchste Gewicht eines Benutzer's an.
	 */
	private float maxGewicht = -10000;

	/**
	 * Gibt den höchsten Fettanteil eines Benutzer's an.
	 */
	private float maxFettanteil = -10000;

	/**
	 * Gibt den höchsten Wasseranteil eines Benutzer's an.
	 */
	private float maxWasseranteil = -10000;

	/**
	 * Gibt den höchsten Muskelanteil eines Benutzer's an.
	 */
	private float maxMuskelanteil = -10000;

	/**
	 * Gibt den höchste Y Wert für ein Gewicht an.
	 */
	private final static float MAX_Y = 2.0f;

	/**
	 * Gibt das kleinste Gewicht eines Benutzer's an.
	 */
	private float minGewicht = 10000;

	/**
	 * Gibt den kleinsten Fettanteil eines Benutzer's an.
	 */
	private float minFettanteil = 10000;

	/**
	 * Gibt den kleinsten Wasseranteil eines Benutzer's an.
	 */
	private float minWasseranteil = 10000;

	/**
	 * Gibt den kleinsten Muskelanteil eines Benutzer's an.
	 */
	private float minMuskelanteil = 10000;

	/**
	 * Gibt den kleinstenY Wert für ein Gewicht an.
	 */
	private final static float MIN_Y = 0.0f;

	/**
	 * Gibt einen Wert an, der zu vom MIN_GEWICHT bzw. minBMI abgezogen wird und zum MAX_GEWICHT bzw maxBMI dazu addiert wird. Damit noch etwas Platz über und
	 * unter dem Graph bleibt.
	 */
	private final static float RANGE_GEWICHT = 1.0f;

	/**
	 * Gibt den Platz zwischen 2 Elementen auf der X-Achse an.
	 */
	private final float rangeDayX;

	/**
	 * Gibt den Wertebereich zwischen maxGewicht UND minGewicht.
	 */
	private final float valueRangeGewicht;

	/**
	 * Gibt den Wertebereich in der Y Achse an.
	 */
	private final float skalaRangeGewicht;

	/**
	 * Gibt den Wertebereich zwischen maxFettanteil UND minFettanteil.
	 */
	private final float valueRangeFettanteil;

	/**
	 * Gibt den Wertebereich in der Y Achse an.
	 */
	private final float skalaRangeFettanteil;

	/**
	 * Gibt den Wertebereich zwischen maxWasseranteil UND minWasseranteil.
	 */
	private final float valueRangeWasseranteil;

	/**
	 * Gibt den Wertebereich in der Y Achse an.
	 */
	private final float skalaRangeWasseranteil;

	/**
	 * Gibt den Wertebereich zwischen maxMuskelanteil UND minMuskelanteil.
	 */
	private final float valueRangeMuskelanteil;

	/**
	 * Gibt den Wertebereich in der Y Achse an.
	 */
	private final float skalaRangeMuskelanteil;

	/**
	 * Gibt die Skalierung der Zeit-Achse (X) an. Event. später auch in die Einstellungen aufnehmen.
	 */
	private final ZeitAuswahlEnum zeitAuswahlEnum;

	/**
	 * Liste mit allen Datums-Gewicht Werten, die für die Ausgabe beachtet werden sollen.
	 */
	private final ArrayList<DatumGewichtDAOEntry> datumGewichtArrayList;

	/**
	 * Array mit Vektoren für alle ausgewählten Einträge aus der Datenbank (für Gewicht-Kurve).
	 */
	private final float[] verticesDatumGewicht;

	/**
	 * Vertices-Array für die Punkte der Gewichts-Kurve in Form eines Vektor-Buffer.
	 */
	private final FloatBuffer vertexDatumGewichtBuffer;

	/**
	 * Vertices-Array für die Punkte der Fettanteil-Kurve in Form eines Vektor-Buffer.
	 */
	private final FloatBuffer vertexDatumFettanteilBuffer;

	/**
	 * Vertices-Array für die Punkte der Wasseranteil-Kurve in Form eines Vektor-Buffer.
	 */
	private final FloatBuffer vertexDatumWasseranteilBuffer;

	/**
	 * Vertices-Array für die Punkte der Muskelanteil-Kurve in Form eines Vektor-Buffer.
	 */
	private final FloatBuffer vertexDatumMuskelanteilBuffer;

	/**
	 * Reihenfolge der Punkte im vertices Array. Wird für die richtige Darstellung des Gewichts-Graphen (ohne Dreicke an den Enden) benötigt.
	 */
	private final short[] indicesDatumGewichtBmi;

	/**
	 * Array mit Vektoren für alle ausgewählten Fettanteil-Einträge aus der Datenbank (für Fettanteil-Kurve).
	 */
	private final float[] verticesDatumFettanteil;

	/**
	 * Array mit Vektoren für alle ausgewählten Wasseranteil-Einträge aus der Datenbank (für Wasseranteil-Kurve).
	 */
	private final float[] verticesDatumWasseranteil;

	/**
	 * Array mit Vektoren für alle ausgewählten Muskelanteil-Einträge aus der Datenbank (für Muskelanteil-Kurve).
	 */
	private final float[] verticesDatumMuskelanteil;

	/**
	 * indices Array in Form eines ShortBuffer.
	 */
	private final ShortBuffer indicesDatumGewichtBMIBuffer;

	/**
	 * Der Kontext wird benötigt um auf die Strings aus den Ressourcen zugreifen zu können.
	 */
	private final Context context;

	/**
	 * Diese Variable gibt an, welcher Graph gezeichnet werden soll. Standard ist der Gewichts-Graph.
	 */
	private GraphAuswahlEnum graphAuswahlEnum = GraphAuswahlEnum.GEWICHT;

	/**
	 * Hier werden die obigen Arrays in entsprechende Buffer initialisiert. Hier werden auch die Daten aus der Datenbank geladen und entsprechend aufbereitet.
	 */
	public Graph(Context context) {
		this.context = context;
		// Aus den Einstellungen die Anzahl der Tage laden (KEY_ZEITAUSWAHL);
		ZeitAuswahlEnum tmpZeitAuswahlEnum;
		try {
			int dayCount = Integer.parseInt(Einstellungen.getAnwendungsEinstellungen(context).getString(Einstellungen.KEY_ZEITAUSWAHL, "7"));
			tmpZeitAuswahlEnum = ZeitAuswahlEnum.getZeitAuswahlEnumByDayCount(dayCount);
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim Auslesen der Zeitauswahl", e);
			tmpZeitAuswahlEnum = ZeitAuswahlEnum.WOCHE;
		}
		zeitAuswahlEnum = tmpZeitAuswahlEnum;

		bmiSpitzelDatenbank = new BMISpitzelDatenbank(context);
		datumGewichtArrayList = bmiSpitzelDatenbank.getEntriesByZeitAuswahl(zeitAuswahlEnum);

		berechnetMaxUndMinGrenzenDerGraphen(datumGewichtArrayList);

		valueRangeGewicht = maxGewicht - minGewicht;
		skalaRangeGewicht = (MAX_Y - MIN_Y) / valueRangeGewicht;
		valueRangeFettanteil = maxFettanteil - minFettanteil;
		skalaRangeFettanteil = (MAX_Y - MIN_Y) / valueRangeFettanteil;
		valueRangeWasseranteil = maxWasseranteil - minWasseranteil;
		skalaRangeWasseranteil = (MAX_Y - MIN_Y) / valueRangeWasseranteil;
		valueRangeMuskelanteil = maxMuskelanteil - minMuskelanteil;
		skalaRangeMuskelanteil = (MAX_Y - MIN_Y) / valueRangeMuskelanteil;

		// Hier wird ein Vektor-Array für die Punkte aufgebaut
		int i = datumGewichtArrayList.size();
		verticesDatumGewicht = new float[i * 3];// Array für Gewicht-Kurve
		float[] colorsDatumGewicht = new float[i * 4];// Array für Farben der Gewicht-Kurve
		indicesDatumGewichtBmi = new short[i];// Reihenfolge der Kurven-Array (bei Gewicht und BMI gleich)
		verticesDatumFettanteil = new float[i * 3];// Array für Fettanteil-Kurve
		verticesDatumWasseranteil = new float[i * 3];// Array für Wasseranteil-Kurve
		verticesDatumMuskelanteil = new float[i * 3];// Array für Muskelanteil-Kurve

		
		BMIRechner bmiRechner = new BMIRechner(context);
		BMIBereich bmiBereich = null;
		if (bmiRechner.isErrorReadingPreferences()) {
		} else {
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
		}
		
		int y = (i * 4) - 4;
		i = (i * 3) - 1;
		rangeDayX = 4.0f / zeitAuswahlEnum.getDaysOfYearValue();// Unterschied für einen Tag
		int lastDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - zeitAuswahlEnum.getDaysOfYearValue();
		for (DatumGewichtDAOEntry tmpDatumGewichtDAOEntry : datumGewichtArrayList) {
			if (lastDay < 0) {
				// Das kann nach einem Jahrswechse vorkommen, das die ersten Werte noch aus dem Vorjahr stammen.
				if (tmpDatumGewichtDAOEntry.getDatum().get(Calendar.YEAR) < Calendar.getInstance().get(Calendar.YEAR)) {
					// Es ist ein Tag aus dem Vorjahr, also wird 365 vom Datum abgezogen
					verticesDatumGewicht[i - 2] = -((lastDay - tmpDatumGewichtDAOEntry.getDatum().get(Calendar.DAY_OF_YEAR) + getDayCountInYear(tmpDatumGewichtDAOEntry
							.getDatum().get(Calendar.YEAR)))) * rangeDayX;
				} else {
					verticesDatumGewicht[i - 2] = -(lastDay - tmpDatumGewichtDAOEntry.getDatum().get(Calendar.DAY_OF_YEAR)) * rangeDayX;
				}
			} else {
				verticesDatumGewicht[i - 2] = -(lastDay - tmpDatumGewichtDAOEntry.getDatum().get(Calendar.DAY_OF_YEAR)) * rangeDayX;
			}
			// Die Anteile werden auf der X-Achse genau wie der Gewichts-Graph gesetzt.
			verticesDatumFettanteil[i - 2] = verticesDatumGewicht[i - 2];
			verticesDatumWasseranteil[i - 2] = verticesDatumGewicht[i - 2];
			verticesDatumMuskelanteil[i - 2] = verticesDatumGewicht[i - 2];

			String tmpStringValue = tmpDatumGewichtDAOEntry.getGewicht();
			if (tmpStringValue.contains(",")) {
				tmpStringValue = tmpStringValue.replace(",", ".");
			}
			verticesDatumGewicht[i - 1] = (Float.parseFloat(tmpStringValue) - minGewicht) * skalaRangeGewicht;
			
			//Berechnen des BMI und dann den Farbwert für den berechneten Wert berechnen.
			if (bmiBereich != null) {
				//Dann wird die Kurve einfach rot gezeichnet.
				colorsDatumGewicht[y] = 1.0f;
				colorsDatumGewicht[y + 1] = 1.0f;
				colorsDatumGewicht[y + 2] = 0.0f;
				colorsDatumGewicht[y + 3] = 1.0f;
				float berechneterBMI = bmiRechner.berechneBMIAsFlaot(Float.parseFloat(tmpStringValue), bmiRechner.getKoerpergroesse());
				// Den BMI-Label farblich machen
				if (bmiBereich.getBereich1().isWertInBereich(berechneterBMI)) {
					colorsDatumGewicht[y] = 1.0f;
					colorsDatumGewicht[y + 1] = 1.0f;
					colorsDatumGewicht[y + 2] = 0.0f;
					colorsDatumGewicht[y + 3] = 1.0f;
				} else if (bmiBereich.getBereich2().isWertInBereich(berechneterBMI)) {
					colorsDatumGewicht[y] = 0.0f;
					colorsDatumGewicht[y + 1] = 1.0f;
					colorsDatumGewicht[y + 2] = 0.0f;
					colorsDatumGewicht[y + 3] = 1.0f;
				} else if (bmiBereich.getBereich3().isWertInBereich(berechneterBMI)) {
					colorsDatumGewicht[y] = 1.0f;
					colorsDatumGewicht[y + 1] = 1.0f;
					colorsDatumGewicht[y + 2] = 0.0f;
					colorsDatumGewicht[y + 3] = 1.0f;
				} else if (bmiBereich.getBereich4().isWertInBereich(berechneterBMI)) {
					colorsDatumGewicht[y] = 0.99f;
					colorsDatumGewicht[y + 1] = 0.7f;
					colorsDatumGewicht[y + 2] = 0.0f;
					colorsDatumGewicht[y + 3] = 1.0f;
				} else if (bmiBereich.getBereich5().isWertInBereich(berechneterBMI)) {
					colorsDatumGewicht[y] = 1.0f;
					colorsDatumGewicht[y + 1] = 0.0f;
					colorsDatumGewicht[y + 2] = 0.0f;
					colorsDatumGewicht[y + 3] = 1.0f;
				}
			} else {
				//Dann wird die Kurve einfach rot gezeichnet.
				colorsDatumGewicht[y] = 1.0f;
				colorsDatumGewicht[y + 1] = 0.0f;
				colorsDatumGewicht[y + 2] = 0.0f;
				colorsDatumGewicht[y + 3] = 1.0f;
			}
			
			// Anteile (Fett, Wasser und Muskeln) auslesen.
			tmpStringValue = tmpDatumGewichtDAOEntry.getFettanteil();
			if (tmpStringValue.contains(",")) {
				tmpStringValue = tmpStringValue.replace(",", ".");
			}
			if (Float.parseFloat(tmpStringValue) > 0.0) {
				verticesDatumFettanteil[i - 1] = (Float.parseFloat(tmpStringValue) - minFettanteil) * skalaRangeFettanteil;
			}
			tmpStringValue = tmpDatumGewichtDAOEntry.getWasseranteil();
			if (tmpStringValue.contains(",")) {
				tmpStringValue = tmpStringValue.replace(",", ".");
			}
			if (Float.parseFloat(tmpStringValue) > 0.0) {
				verticesDatumWasseranteil[i - 1] = (Float.parseFloat(tmpStringValue) - minWasseranteil) * skalaRangeWasseranteil;
			}
			tmpStringValue = tmpDatumGewichtDAOEntry.getMuskelanteil();
			if (tmpStringValue.contains(",")) {
				tmpStringValue = tmpStringValue.replace(",", ".");
			}
			if (Float.parseFloat(tmpStringValue) > 0.0) {
				verticesDatumMuskelanteil[i - 1] = (Float.parseFloat(tmpStringValue) - minMuskelanteil) * skalaRangeMuskelanteil;
			}
			
			// Z-Achse ist bei allen Graphen immer 0;
			verticesDatumGewicht[i] = 0.0f;
			verticesDatumFettanteil[i] = 0.0f;
			verticesDatumWasseranteil[i] = 0.0f;
			verticesDatumMuskelanteil[i] = 0.0f;

			indicesDatumGewichtBmi[i / 3] = (short) (i / 3);
			i -= 3;
			y -= 4;
		}
	
		/**
		 * Buffer für die die Farben erzeugen.
		 */
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(colorsDatumGewicht.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		colorBuffer = byteBuffer.asFloatBuffer();
		colorBuffer.put(colorsDatumGewicht);
		colorBuffer.position(0);
	
		// Arrays und Buffers für die einzelnen Punkte (Einträge des Graphen).
		byteBuffer = ByteBuffer.allocateDirect(verticesDatumGewicht.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		vertexDatumGewichtBuffer = byteBuffer.asFloatBuffer();
		vertexDatumGewichtBuffer.put(verticesDatumGewicht);
		vertexDatumGewichtBuffer.position(0);

		byteBuffer = ByteBuffer.allocateDirect(indicesDatumGewichtBmi.length * 2);
		byteBuffer.order(ByteOrder.nativeOrder());
		indicesDatumGewichtBMIBuffer = byteBuffer.asShortBuffer();
		indicesDatumGewichtBMIBuffer.put(indicesDatumGewichtBmi);
		indicesDatumGewichtBMIBuffer.position(0);

		if (Einstellungen.getAnwendungsEinstellungen(context).getBoolean(Einstellungen.KEY_FETTANTEIL, false)) {
			// Arrays und Buffers für die einzelnen Punkte (Fettanteil-Einträge des Graphen).
			byteBuffer = ByteBuffer.allocateDirect(verticesDatumFettanteil.length * 4);
			byteBuffer.order(ByteOrder.nativeOrder());
			vertexDatumFettanteilBuffer = byteBuffer.asFloatBuffer();
			vertexDatumFettanteilBuffer.put(verticesDatumFettanteil);
			vertexDatumFettanteilBuffer.position(0);
		} else {
			// Wird nicht benötigt (soll nicht angezeigt werden).
			vertexDatumFettanteilBuffer = null;
		}

		if (Einstellungen.getAnwendungsEinstellungen(context).getBoolean(Einstellungen.KEY_WASSERANTEIL, false)) {
			// Arrays und Buffers für die einzelnen Punkte (Wasseranteil-Einträge des Graphen).
			byteBuffer = ByteBuffer.allocateDirect(verticesDatumWasseranteil.length * 4);
			byteBuffer.order(ByteOrder.nativeOrder());
			vertexDatumWasseranteilBuffer = byteBuffer.asFloatBuffer();
			vertexDatumWasseranteilBuffer.put(verticesDatumWasseranteil);
			vertexDatumWasseranteilBuffer.position(0);
		} else {
			// Wird nicht benötigt (soll nicht angezeigt werden).
			vertexDatumWasseranteilBuffer = null;
		}
		if (Einstellungen.getAnwendungsEinstellungen(context).getBoolean(Einstellungen.KEY_MUSKELANTEIL, false)) {
			// Arrays und Buffers für die einzelnen Punkte (Muskelanteil-Einträge des Graphen).
			byteBuffer = ByteBuffer.allocateDirect(verticesDatumMuskelanteil.length * 4);
			byteBuffer.order(ByteOrder.nativeOrder());
			vertexDatumMuskelanteilBuffer = byteBuffer.asFloatBuffer();
			vertexDatumMuskelanteilBuffer.put(verticesDatumMuskelanteil);
			vertexDatumMuskelanteilBuffer.position(0);
		} else {
			// Wird nicht benötigt (soll nicht angezeigt werden).
			vertexDatumMuskelanteilBuffer = null;
		}

		// ----------------------------------------------------------------------------------------------------------------------------
		// Arrays und Buffers für den Graphen.
		byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuffer.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		byteBuffer = ByteBuffer.allocateDirect(indicesGraph.length * 2);
		byteBuffer.order(ByteOrder.nativeOrder());
		indexGraphBuffer = byteBuffer.asShortBuffer();
		indexGraphBuffer.put(indicesGraph);
		indexGraphBuffer.position(0);

		// Arrays und Buffers für Pfeile am Ende des Graphen.
		byteBuffer = ByteBuffer.allocateDirect(indicesGraphArrow.length * 2);
		byteBuffer.order(ByteOrder.nativeOrder());
		indexGraphBufferArrow = byteBuffer.asShortBuffer();
		indexGraphBufferArrow.put(indicesGraphArrow);
		indexGraphBufferArrow.position(0);
	}

	/**
	 * Diese Funktion zeichnet den Graph auf der übergebenen Zeichenfläche.
	 * 
	 * @param gl
	 *            Zeichenfläche, auf der gezeichnet werden soll.
	 */
	public void draw(GL10 gl) {
		try {
			{
				// Beschriftung der X Achse
				gl.glPushMatrix();
				gl.glTranslatef(-0.2f, -1.2f, 0.0f);
				{
					// Datum ausgeben
					float tmpRangeDayX = rangeDayX * 1.3f;
					if (GraphActivity.isSmallScreenResolution()) {
						tmpRangeDayX *= 0.6f;
					}
					int lastDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - zeitAuswahlEnum.getDaysOfYearValue();
					for (int i = 1; i <= datumGewichtArrayList.size(); i++) {
						int x = datumGewichtArrayList.get(datumGewichtArrayList.size() - i).getDatum().get(Calendar.DAY_OF_YEAR) - lastDay;
						if (lastDay < 0) {
							// Das kann nach einem Jahrswechse vorkommen, das die ersten Werte noch aus dem Vorjahr stammen.
							if (datumGewichtArrayList.get(datumGewichtArrayList.size() - i).getDatum().get(Calendar.YEAR) < Calendar.getInstance().get(
									Calendar.YEAR)) {
								// Es ist ein Tag aus dem Vorjahr, also wird 365 vom Datum abgezogen
								x -= getDayCountInYear(datumGewichtArrayList.get(datumGewichtArrayList.size() - i).getDatum().get(Calendar.YEAR));
							}
						}

						gl.glPushMatrix();
						if (GraphActivity.isSmallScreenResolution()) {
							gl.glTranslatef(-1.4f + tmpRangeDayX * (x), 0.05f, 1.5f);
						} else {
							gl.glTranslatef(-2.3f + tmpRangeDayX * (x), -0.45f, -0.5f);
						}
						gl.glRotatef(70, 0.0f, 0.0f, 1.0f);
						if (zeitAuswahlEnum == ZeitAuswahlEnum.QUARTAL) {
							if ((i % 2) == 0)
								characterManager.paintString(
										gl,
										""
												+ GewichtEingebenActivity.simpleDateFormat.format(
														datumGewichtArrayList.get(datumGewichtArrayList.size() - i).getDatum().getTime()).substring(0, 5),
										Orientation.ORIENTATION_LEFT, 1.0f, 1.0f, 1.0f);
						} else {
							// Bei Wöchentlich und Monatlich werden alle Datumswerte ausgegeben.
							characterManager.paintString(
									gl,
									""
											+ GewichtEingebenActivity.simpleDateFormat.format(
													datumGewichtArrayList.get(datumGewichtArrayList.size() - i).getDatum().getTime()).substring(0, 5),
									Orientation.ORIENTATION_LEFT, 1.0f, 1.0f, 1.0f);
						}
						gl.glPopMatrix();
					}
				}
				if (GraphActivity.isSmallScreenResolution()) {
					gl.glTranslatef(0.1f, 0.8f, 2.5f);
				} else {
					gl.glTranslatef(0.0f, 0.35f, 0.0f);
				}
				characterManager.paintString(gl, context.getString(R.string.datumGewichtsAdapterDate), Orientation.ORIENTATION_LEFT, 1.0f, 1.0f, 1.0f);
				gl.glPopMatrix();
			}

			paintYAxisLabel(gl);

			{
				// Legende-Zeichnen (nur wenn mehr als ein Graph angezeigt wird). Dies ist der Text, der über dem Graphen steht (z.B. Fettanteil)
				if (Einstellungen.getAnwendungsEinstellungen(context).getBoolean(Einstellungen.KEY_FETTANTEIL, false)
						|| Einstellungen.getAnwendungsEinstellungen(context).getBoolean(Einstellungen.KEY_WASSERANTEIL, false)
						|| Einstellungen.getAnwendungsEinstellungen(context).getBoolean(Einstellungen.KEY_MUSKELANTEIL, false)) {
					gl.glPushMatrix();
					if (GraphActivity.isSmallScreenResolution()) {
						gl.glTranslatef(-0.4f, 0.8f, 2.1f);
					} else {
						gl.glTranslatef(-0.4f, 1.4f, 0.0f);
					}

					if (graphAuswahlEnum == GraphAuswahlEnum.GEWICHT) {
						characterManager.paintString(gl, context.getString(R.string.datumGewichtsAdapterGewicht), Orientation.ORIENTATION_LEFT, 1.0f, 0.2f, 0.2f);
					} else if (graphAuswahlEnum == GraphAuswahlEnum.FETTANTEIL) {
						characterManager.paintString(gl, context.getString(R.string.fettanteil), Orientation.ORIENTATION_LEFT, 0.9f, 1.0f, 0.25f);
					} else if (graphAuswahlEnum == GraphAuswahlEnum.WASSERANTEIL) {
						characterManager.paintString(gl, context.getString(R.string.wasseranteil), Orientation.ORIENTATION_LEFT, 0.2f, 1.0f, 0.2f);						
					} else if (graphAuswahlEnum == GraphAuswahlEnum.MUSKELANTEIL) {
						characterManager.paintString(gl, context.getString(R.string.muskelanteil), Orientation.ORIENTATION_LEFT, 0.0f, 0.0f, 0.8f);
					}
					gl.glPopMatrix();
				}
			}

			{
				gl.glPushMatrix();
				gl.glTranslatef(-1.85f, -0.85f, 0.5f);

				gl.glFrontFace(GL10.GL_CCW);
				gl.glEnable(GL10.GL_CULL_FACE);
				gl.glCullFace(GL10.GL_BACK);
				gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
				gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);// Farbe der Achsen
				gl.glLineWidth(3.0f);
				gl.glDrawElements(GL10.GL_LINES, indicesGraph.length, GL10.GL_UNSIGNED_SHORT, indexGraphBuffer);
				gl.glDrawElements(GL10.GL_TRIANGLES, indicesGraphArrow.length, GL10.GL_UNSIGNED_SHORT, indexGraphBufferArrow);

				if (graphAuswahlEnum == GraphAuswahlEnum.GEWICHT) {
					// Vertex Buffer für die Gewichts-Punkte zeichnen.
					gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
					gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
//					gl.glColor4f(0.6f, 0.0f, 0.0f, 1.0f);
					gl.glPointSize(7.0f);
					gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexDatumGewichtBuffer);
					gl.glDrawElements(GL10.GL_POINTS, indicesDatumGewichtBmi.length, GL10.GL_UNSIGNED_SHORT, indicesDatumGewichtBMIBuffer);
//					gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
					gl.glPointSize(5.0f);
					gl.glDrawElements(GL10.GL_POINTS, indicesDatumGewichtBmi.length, GL10.GL_UNSIGNED_SHORT, indicesDatumGewichtBMIBuffer);
//					gl.glColor4f(0.6f, 0.0f, 0.0f, 1.0f);
					gl.glPointSize(3.0f);
					gl.glDrawElements(GL10.GL_POINTS, indicesDatumGewichtBmi.length, GL10.GL_UNSIGNED_SHORT, indicesDatumGewichtBMIBuffer);
//					gl.glColor4f(1.0f, 0.3f, 0.3f, 1.0f);
					gl.glPointSize(0.005f);
					gl.glDrawElements(GL10.GL_LINE_STRIP, indicesDatumGewichtBmi.length, GL10.GL_UNSIGNED_SHORT, indicesDatumGewichtBMIBuffer);
					
					gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
				} else if (graphAuswahlEnum == GraphAuswahlEnum.FETTANTEIL) {
					// Vertex Buffer für die Fettanteil-Punkte zeichnen.
					gl.glColor4f(0.5f, 0.6f, 0.1f, 1.0f);
					gl.glPointSize(7.0f);
					gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexDatumFettanteilBuffer);
					gl.glDrawElements(GL10.GL_POINTS, indicesDatumGewichtBmi.length, GL10.GL_UNSIGNED_SHORT, indicesDatumGewichtBMIBuffer);
					gl.glColor4f(0.9f, 1.0f, 0.25f, 1.0f);
					gl.glPointSize(5.0f);
					gl.glDrawElements(GL10.GL_POINTS, indicesDatumGewichtBmi.length, GL10.GL_UNSIGNED_SHORT, indicesDatumGewichtBMIBuffer);
					gl.glColor4f(0.5f, 0.6f, 0.1f, 1.0f);
					gl.glPointSize(3.0f);
					gl.glDrawElements(GL10.GL_POINTS, indicesDatumGewichtBmi.length, GL10.GL_UNSIGNED_SHORT, indicesDatumGewichtBMIBuffer);
					gl.glColor4f(0.85f, 1.0f, 0.3f, 1.0f);
					gl.glPointSize(0.005f);
					gl.glDrawElements(GL10.GL_LINE_STRIP, indicesDatumGewichtBmi.length, GL10.GL_UNSIGNED_SHORT, indicesDatumGewichtBMIBuffer);
				} else if (graphAuswahlEnum == GraphAuswahlEnum.WASSERANTEIL) {
					// Vertex Buffer für die Wasseranteil-Punkte zeichnen.
					gl.glColor4f(0.0f, 0.6f, 0.0f, 1.0f);
					gl.glPointSize(7.0f);
					gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexDatumWasseranteilBuffer);
					gl.glDrawElements(GL10.GL_POINTS, indicesDatumGewichtBmi.length, GL10.GL_UNSIGNED_SHORT, indicesDatumGewichtBMIBuffer);
					gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
					gl.glPointSize(5.0f);
					gl.glDrawElements(GL10.GL_POINTS, indicesDatumGewichtBmi.length, GL10.GL_UNSIGNED_SHORT, indicesDatumGewichtBMIBuffer);
					gl.glColor4f(0.0f, 0.6f, 0.0f, 1.0f);
					gl.glPointSize(3.0f);
					gl.glDrawElements(GL10.GL_POINTS, indicesDatumGewichtBmi.length, GL10.GL_UNSIGNED_SHORT, indicesDatumGewichtBMIBuffer);
					gl.glColor4f(0.3f, 1.0f, 0.3f, 1.0f);
					gl.glPointSize(0.005f);
					gl.glDrawElements(GL10.GL_LINE_STRIP, indicesDatumGewichtBmi.length, GL10.GL_UNSIGNED_SHORT, indicesDatumGewichtBMIBuffer);
				} else if (graphAuswahlEnum == GraphAuswahlEnum.MUSKELANTEIL) {
					// Vertex Buffer für die Muskelanteil-Punkte zeichnen.
					gl.glColor4f(0.0f, 0.0f, 0.6f, 1.0f);
					gl.glPointSize(7.0f);
					gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexDatumMuskelanteilBuffer);
					gl.glDrawElements(GL10.GL_POINTS, indicesDatumGewichtBmi.length, GL10.GL_UNSIGNED_SHORT, indicesDatumGewichtBMIBuffer);
					gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
					gl.glPointSize(5.0f);
					gl.glDrawElements(GL10.GL_POINTS, indicesDatumGewichtBmi.length, GL10.GL_UNSIGNED_SHORT, indicesDatumGewichtBMIBuffer);
					gl.glColor4f(0.0f, 0.0f, 0.6f, 1.0f);
					gl.glPointSize(3.0f);
					gl.glDrawElements(GL10.GL_POINTS, indicesDatumGewichtBmi.length, GL10.GL_UNSIGNED_SHORT, indicesDatumGewichtBMIBuffer);
					gl.glColor4f(0.3f, 0.3f, 1.0f, 1.0f);
					gl.glPointSize(0.005f);
					gl.glDrawElements(GL10.GL_LINE_STRIP, indicesDatumGewichtBmi.length, GL10.GL_UNSIGNED_SHORT, indicesDatumGewichtBMIBuffer);
				}

				gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
				gl.glDisable(GL10.GL_CULL_FACE);
				gl.glPopMatrix();
			}
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim zeichnen des Grundgerüstes.", e);
		}
	}

	/**
	 * Diese Methode berechnet auf einfache Art und Weise die Anzahl der Tage in einem Jahr.
	 * 
	 * @see http://de.wikipedia.org/wiki/Jahr
	 * @param year
	 *            Jahr von dem die Anzahl der Tage ermittelt werden soll.
	 * @return Liefert die Anzahl der Tage des übergebenen Jahrs zurück.
	 */
	public int getDayCountInYear(int year) {
		if (year % 400 == 0 || ((year % 4 == 0) && !(year % 100 == 0))) {
			return 366;// Schaltjahr
		} else {
			return 365;// normales Jahr
		}
	}

	/**
	 * Setzt den CharacterManager, mit dem Beschriftungen ausgegeben werden.
	 * 
	 * @param graph
	 *            Graph der gezeichnet werden soll.
	 */
	public void setCharacterManager(CharacterManager characterManager) {
		this.characterManager = characterManager;
	}

	/**
	 * Einfache Methode die nach dem auslesen der Datenbank die max und min - Grenzen der Graphen berechnet.
	 * 
	 * @param datumGewichtArrayList
	 *            Liste der Einträge die berechnet werden soll.
	 */
	private void berechnetMaxUndMinGrenzenDerGraphen(ArrayList<DatumGewichtDAOEntry> datumGewichtArrayList) {
		try {
			for (DatumGewichtDAOEntry element : datumGewichtArrayList) {
				// Gewichts-Grenzen berechnen
				String tmpStringValue = element.getGewicht();
				if (tmpStringValue.contains(",")) {
					tmpStringValue = tmpStringValue.replace(",", ".");
				}
				float tmpFloatValue = Float.parseFloat(tmpStringValue);
				if (minGewicht > tmpFloatValue) {
					minGewicht = tmpFloatValue;
				}
				if (maxGewicht < tmpFloatValue) {
					maxGewicht = tmpFloatValue;
				}
//				// Fettanteil-Grenzen berechnen
//				tmpStringValue = element.getFettanteil();
//				if (tmpStringValue.contains(",")) {
//					tmpStringValue = tmpStringValue.replace(",", ".");
//				}
//				tmpFloatValue = Float.parseFloat(tmpStringValue);
//				if (minFettanteil > tmpFloatValue) {
//					minFettanteil = tmpFloatValue;
//				}
//				if (maxFettanteil < tmpFloatValue) {
//					maxFettanteil = tmpFloatValue;
//				}
//				// Wasseranteil-Grenzen berechnen
//				tmpStringValue = element.getFettanteil();
//				if (tmpStringValue.contains(",")) {
//					tmpStringValue = tmpStringValue.replace(",", ".");
//				}
//				tmpFloatValue = Float.parseFloat(tmpStringValue);
//				if (minWasseranteil > tmpFloatValue) {
//					minWasseranteil = tmpFloatValue;
//				}
//				if (maxWasseranteil < tmpFloatValue) {
//					maxWasseranteil = tmpFloatValue;
//				}
//				// Muskelanteil-Grenzen berechnen
//				tmpStringValue = element.getFettanteil();
//				if (tmpStringValue.contains(",")) {
//					tmpStringValue = tmpStringValue.replace(",", ".");
//				}
//				tmpFloatValue = Float.parseFloat(tmpStringValue);
//				if (minMuskelanteil > tmpFloatValue) {
//					minMuskelanteil = tmpFloatValue;
//				}
//				if (maxMuskelanteil < tmpFloatValue) {
//					maxMuskelanteil = tmpFloatValue;
//				}
			}
			
			maxGewicht += RANGE_GEWICHT;
			if (maxGewicht < 0) {
				maxGewicht = 100;
			}
			minGewicht -= RANGE_GEWICHT;
			if (minGewicht > 500) {
				minGewicht = 0;
			}
			maxFettanteil = 100;
			minFettanteil = 0;
			maxWasseranteil = 100;
			minWasseranteil = 0;
			maxMuskelanteil = 100;
			minMuskelanteil = 0;
		} catch (Exception e) {
			Log.e(TAG, "Fehler beim berechnen der max und min - Grenzen der unterschiedlichen Graphen", e);
		}
	}

	/**
	 * Diese Methode ist für die Beschriftung der Y-Achse zuständig.
	 * 
	 * @param gl Graph in dem gezeichnet wird.
	 */
	private void paintYAxisLabel(GL10 gl) {
		String einheitensystem = Einstellungen.getAnwendungsEinstellungen(context).getString(Einstellungen.KEY_EINHEITENSYSTEM, "metrisch");
		// Beschriftung der Y-Achse
		gl.glPushMatrix();
		gl.glTranslatef(-2.3f, 0.0f, 0.0f);
			
		//Max-Wert ausgeben
		gl.glPushMatrix();
		if (GraphActivity.isSmallScreenResolution()) {
			gl.glTranslatef(0.9f, 0.8f, 2.0f);
		} else {
			gl.glTranslatef(-0.25f, 1.35f, -0.3f);
		}
		
		if (graphAuswahlEnum == GraphAuswahlEnum.GEWICHT) {
			// Max-Gewicht ausgeben
			if ("metrisch".equals(einheitensystem)) {
				characterManager.paintString(gl, "" + (int) maxGewicht, Orientation.ORIENTATION_LEFT,  1.0f, 1.0f, 1.0f);
			} else {
				characterManager.paintString(gl, "" + (int) (maxGewicht * BMIRechner.KG_TO_POUND), Orientation.ORIENTATION_LEFT,
						 1.0f, 1.0f, 1.0f);
			}
		} else {
			characterManager.paintString(gl, "100", Orientation.ORIENTATION_LEFT,  1.0f, 1.0f, 1.0f);
		}
		gl.glPopMatrix();
		
		// Min-Wert ausgeben
		gl.glPushMatrix();
		if (GraphActivity.isSmallScreenResolution()) {
			gl.glTranslatef(0.9f, -0.4f, 2.0f);
		} else {
			gl.glTranslatef(-0.2f, -0.9f, -0.2f);
		}
		if (graphAuswahlEnum == GraphAuswahlEnum.GEWICHT) {
			if ("metrisch".equals(einheitensystem)) {
				String vortext = "";
				if (minGewicht < 10) {
					vortext = " ";
				}
				characterManager.paintString(gl, vortext + (int) minGewicht, Orientation.ORIENTATION_LEFT,  1.0f, 1.0f, 1.0f);
			} else {
				gl.glTranslatef(-0.1f, 0f, 0f);
				String vortext = "";
				if (minGewicht < (minGewicht * BMIRechner.KG_TO_POUND)) {
					vortext = " ";
				}
				characterManager.paintString(gl, vortext + (int) (minGewicht * BMIRechner.KG_TO_POUND), Orientation.ORIENTATION_LEFT,
						 1.0f, 1.0f, 1.0f);
			}
		} else {
			characterManager.paintString(gl, " 0", Orientation.ORIENTATION_LEFT,  1.0f, 1.0f, 1.0f);
		}
		gl.glPopMatrix();
		
		//Ausgabe der Einheit KG, Pfund oder %
		if (GraphActivity.isSmallScreenResolution()) {
			gl.glTranslatef(0.8f, 0.2f, 1.9f);
		} else {
			gl.glTranslatef(-0.2f, 0.2f, -0.2f);
		}
		if (graphAuswahlEnum == GraphAuswahlEnum.GEWICHT) {
			//Einheit aus den Einstellungen ausgeben.
			if ("metrisch".equals(einheitensystem)) {
				characterManager.paintString(gl, context.getString(R.string.kilogrammKurztext), Orientation.ORIENTATION_LEFT,  1.0f, 1.0f, 1.0f);
			} else {
				characterManager.paintString(gl, context.getString(R.string.pfundKurztext), Orientation.ORIENTATION_LEFT,  1.0f, 1.0f, 1.0f);
			}
		} else {
			//Bei Fett-, Wasser- oder Muskelanteil wird % ausgegeben.
			gl.glTranslatef(0.65f, 0.0f, 1.0f);
			characterManager.paintString(gl, "%", Orientation.ORIENTATION_LEFT,  1.0f, 1.0f, 1.0f);
		}
		
		
		gl.glPopMatrix();
	}
	
	/**
	 * Diese Methode ermöglicht es, die Auswahl des gewünschten Graphens zu setzen. Diese Auswahl
	 * muss von aussen gesetzt werden, da das onToucheEvent in der Activity bearbeitet wird.
	 * 
	 * @param auswahl Auswahl die als nächstes im Graphen angezeigt werden soll.
	 */
	public void setGraphAuswahlEnum(GraphAuswahlEnum auswahl) {
		graphAuswahlEnum = auswahl;
	}

	public GraphAuswahlEnum getGraphAuswahlEnum() {
		return graphAuswahlEnum;
	}
}
