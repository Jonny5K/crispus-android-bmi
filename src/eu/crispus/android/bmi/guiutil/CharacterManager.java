package eu.crispus.android.bmi.guiutil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

/**
 * Mit dieser Klasse können Texte in OpenGL ausgegeben werden. Die Texte können
 * von der aktuellen Position in 3 Positionen ausgegeben werden: 1. Ab der
 * aktuellen Position nach links (ORIENTATION_LEFT) 2. Das der Text an der
 * aktuellen Position seine Mitte hat (ORIENTATION_CENTER) 3. Das der Text an
 * der aktuellen Position endet (ORIENTATION_RIGHT) Zudem kann man die Farbe
 * angegeben, in dem die Schrift gezeichnet werden soll.
 * 
 * @author Johannes KRaus
 * 
 */
public class CharacterManager {

	/**
	 * Gibt die Breite eines Buchstaben an.
	 */
	private static final float LETTER_WIDTH = 47.0f / 512.0f;

	/**
	 * Gibt die Höhe eines Buchstaben an.
	 */
	private static final float LETTER_HEIGHT = 77.0f / 512.0f;
	
	/**
	 * Höhe der gesamten Texture.
	 */
	private static final float TEXTURE_HEIGHT = LETTER_HEIGHT * 7;

	/**
	 * Buffer für den Graphen.
	 */
	private FloatBuffer verticesBuffer = null;

	/**
	 * Buffer für die Indexe des Graphen.
	 */
	private ShortBuffer indicesBuffer = null;

	/**	
	 * Buffer für die Texture.
	 */
	private FloatBuffer textureBuffer;

	/**
	 * Texture-Id für Groß und Kleinbuchstaben.
	 */
	private int textureIdLetters = -1;

	/**
	 * Texture-Id für Zahlen und Sonderzeichen. 
	 * werden.
	 */
	private int textureIdDigits = -1;

	/**
	 * Bitmap für Groß- und Kleinbuchstaben.
	 */
	private Bitmap bitmapLetters;

	/**
	 * Bitmap für Zahlen und Sonderzeichen.
	 */
	private Bitmap bitmapDigits;

	/**
	 * Gibt an, ob die Texture geladen werden soll.
	 */
	private boolean textureLoad = false; 

	private int numberOfIndices = -1;

	/**
	 * Variable um die Ausgabe auf der X-Achse zu bewegen (Translate).
	 */
	public float x = 0;

	/**
	 * Variable um die Ausgabe auf der Y-Achse zu bewegen (Translate).
	 */
	public float y = 0;

	/**
	 * Variable um die Ausgabe auf der Z-Achse zu bewegen (Translate).
	 */
	public float z = 0;

	/**
	 * Variable um die Ausgabe um die X-Achse zu drehen (Rotate).
	 */
	public float rx = 0;

	/**
	 * Variable um die Ausgabe um die Y-Achse zu drehen (Rotate).
	 */
	public float ry = 0;

	/**
	 * Variable um die Ausgabe um die Z-Achse zu drehen (Rotate).
	 */
	public float rz = 0;

	/**
	 * Graph erzeugen und die Vektoren setzen.
	 * 
	 * @param width
	 *            the width of the plane.
	 * @param height
	 *            the height of the plane.
	 */
	public CharacterManager(float width, float height) {
		float textureCoordinates[] = { 0.0f, 1.0f,
				1.0f, 1.0f, 
				0.0f, 0.0f, 
				1.0f, 0.0f, 
		};

		short[] indices = new short[] { 0, 1, 2, 1, 3, 2 };

		float[] vertices = new float[] { -0.5f, -0.5f, 0.0f, 
				0.5f, -0.5f, 0.0f, 
				-0.5f, 0.5f, 0.0f, 
				0.5f, 0.5f, 0.0f };

		setIndices(indices);
		setVertices(vertices);
		setTextureCoordinates(textureCoordinates);
	}

	/**
	 * In dieser Methode wird der übergebene String string gezeichnet, der
	 * Parameter orientation gibt an, wie der String ausgerichtet sein soll. mit
	 * colorRed, colorGreen und colorBlue kann man die Farbe der Schrift
	 * bestimmen. Am Ende der Methode wird die Farbe auf weiß gesetzt, da sich
	 * die Farbe nicht mit glPushMatrix und glPopMatrix sichern lässt.
	 * 
	 * @param gl
	 *            Zeichenfläche von OpenGL
	 * @param string
	 *            String der ausgegeben werden soll.
	 * @param orientation
	 *            Ausrichtung (links, rechts, etc.) des Textes.
	 * @param colorRed
	 *            RGB-Farbanteil von rot.
	 * @param colorGreen
	 *            RGB-Farbanteil von grün.
	 * @param colorBlue
	 *            RGB-Farbanteil von blau.
	 */
	public void paintString(final GL10 gl, final String str, Orientation orientation, float colorRed, float colorGreen, float colorBlue) {
		gl.glPushMatrix();
		{
			int row = 0;
			int column = 0;
			
			if (orientation == Orientation.ORIENTATION_CENTER) {
				gl.glTranslatef(-(str.length() * LETTER_WIDTH) / 2, 0.0f, 0.0f);
			} else if (orientation == Orientation.ORIENTATION_RIGHT) {
				gl.glTranslatef(-(str.length() * LETTER_WIDTH), 0.0f, 0.0f);
			}
			gl.glColor4f(colorRed, colorGreen, colorBlue, 1.0f);
				
			// Im Uhrzeigersinn drehen.
			gl.glFrontFace(GL10.GL_CCW);
			// Aktiviere Face-Culling.
			gl.glEnable(GL10.GL_CULL_FACE);
			// Die Rückseite mit Face-Culling löschen.
			gl.glCullFace(GL10.GL_BACK);
			// Aktiviere die VerticesBuffer zum rendern
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			// Aktiviere 2D-Texturen
			gl.glEnable(GL10.GL_TEXTURE_2D);
			// Aktiviere Texturen
						gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			
			if (textureLoad) {
				loadGLTextureLetters(gl);
				loadTextureDigits(gl);
				textureLoad = false;
			}
			
			for (int i = 0; i < str.length(); i++) {
				// Standard maessig wird die Character Texture text1.tga
				// geladen, wenn Zahlen oder Zeichen der anderen Texture
				// benoetigt werden, wird diese Texture dann benutzt.
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIdLetters);
				if ((str.charAt(i) > 64) && (str.charAt(i) < 91)) {
					// Es ist ein Großbuchstabe...
					row = (str.charAt(i) - 65) / 10;
					column = (str.charAt(i) - 65) % 10;
				} else if ((str.charAt(i) > 96) && (str.charAt(i) < 123)) {
					// Es ist ein Kleinbuchstabe
					row = (str.charAt(i) - (97 - 26)) / 10;
					column = (str.charAt(i) - (97 - 26)) % 10;
				} else {
					// Es ist ein anderes zeichen ä, ö, ü, :, [, ], -, +
					// Diese sind alle in der letzten Zeile.
					row = 5;
					switch (str.charAt(i)) {
					case 'ö':
						column = 2;
						break;
					case 'ü':
						column = 3;
						break;
					case 'ä':
						column = 4;
						break;
					case ':':
						column = 5;
						break;
					case '-':
						column = 6;
						break;
					case '+':
						column = 7;
						break;
					case '[':
						column = 8;
						break;
					case ']':
						column = 9;
						break;
					default:
						// Kein Zeichen aus der text1.tga Es wird auf Texture
						// text2.tga umgeschaltet.
						gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIdDigits);
						if ((str.charAt(i) > 47) && (str.charAt(i) < 59)) {
							// Es ist eine Zahl
							row = 0;
							column = (str.charAt(i) - 48) % 10;
						} else {
							switch (str.charAt(i)) {
							case ',':
								row = 1;
								column = 0;
								break;
							case '.':
								row = 1;
								column = 1;
								break;
							case ';':
								row = 1;
								column = 2;
								break;
							case '_':
								row = 1;
								column = 3;
								break;
							case '#':
								row = 1;
								column = 4;
								break;
							case '*':
								row = 1;
								column = 5;
								break;
							case '/':
								row = 1;
								column = 6;
								break;
							case '\\':
								row = 1;
								column = 7;
								break;
							case '(':
								row = 1;
								column = 8;
								break;
							case ')':
								row = 1;
								column = 9;
								break;
							case '!':
								row = 2;
								column = 0;
								break;
							case '?':
								row = 2;
								column = 1;
								break;
							case '=':
								row = 2;
								column = 2;
								break;
							case 'ß':
								row = 2;
								column = 3;
								break;
							case 'Ü':
								row = 2;
								column = 4;
								break;
							case 'Ö':
								row = 2;
								column = 5;
								break;
							case 'Ä':
								row = 2;
								column = 6;
								break;
							case '%':
								row = 2;
								column = 7;
								break;
							default:
								// Es ist kein Zeichen, dass in text1.tga oder
								// text2.tga ist, es
								// wird nichts ausgegeben
								row = 6;
								column = 10;
							}
						}
					}
				}
			
				//Anpassung weil die Texture scheinbar auf dem Kopf steht!
				row = 6 - row;
				
				float texture[] = {
						column * LETTER_WIDTH, TEXTURE_HEIGHT - (row * LETTER_HEIGHT),// links oben
						(column * LETTER_WIDTH) + LETTER_WIDTH, TEXTURE_HEIGHT - (row * LETTER_HEIGHT),// rechts oben
						column * LETTER_WIDTH, TEXTURE_HEIGHT - (row * LETTER_HEIGHT) - LETTER_HEIGHT,// links unten
						(column * LETTER_WIDTH) + LETTER_WIDTH, TEXTURE_HEIGHT - (row * LETTER_HEIGHT) - LETTER_HEIGHT// rechts unten
				};
				
				float vertices[] = {
						-0.05f + (i * LETTER_WIDTH), -0.05f, 0.0f, // unten links
						0.05f + (i * LETTER_WIDTH), -0.05f, 0.0f, // unten rechts
						-0.05f + (i * LETTER_WIDTH), 0.05f, 0.0f, // oben links
						0.05f + (i * LETTER_WIDTH), 0.05f, 0.0f	 // oben rechts
				};
				short[] indices = new short[] { 0, 1, 2, 1, 3, 2 };
				
				setIndices(indices);
				setVertices(vertices);
				setTextureCoordinates(texture);

				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);	
				
				gl.glDrawElements(GL10.GL_TRIANGLES, numberOfIndices, GL10.GL_UNSIGNED_SHORT, indicesBuffer);
			}
			//Deaktiviere Texturen
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			//Deaktiviere 2D-Texturen
			gl.glDisable(GL10.GL_TEXTURE_2D);
			// Aktiviere die VerticesBuffer zum rendern
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			// Deaktiviere Face-Culling
			gl.glDisable(GL10.GL_CULL_FACE);
		}
		// Farbe auf weiß setzten, da sonst der TextureManager gegebenfalls
		// Probleme bekommt.
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glPopMatrix();
	}

	/**
	 * Set the vertices.
	 * 
	 * @param vertices
	 */
	protected void setVertices(float[] vertices) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		verticesBuffer = byteBuffer.asFloatBuffer();
		verticesBuffer.put(vertices);
		verticesBuffer.position(0);
	}

	/**
	 * Methode zum setzen der Indexe
	 * 
	 * @param indices
	 *            Neue Indexe.
	 */
	protected void setIndices(short[] indices) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(indices.length * 2);
		byteBuffer.order(ByteOrder.nativeOrder());
		indicesBuffer = byteBuffer.asShortBuffer();
		indicesBuffer.put(indices);
		indicesBuffer.position(0);
		numberOfIndices = indices.length;
	}

	/**
	 * Methode zum setzten der Texture-Koordinaten.
	 * 
	 * @param textureCoords
	 *            Neue Texture-Koordinaten.
	 */
	protected void setTextureCoordinates(float[] textureCoords) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(textureCoords.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuffer.asFloatBuffer();
		textureBuffer.put(textureCoords);
		textureBuffer.position(0);
	}

	/**
	 * Mit dieser Methode wird ein Bitmap zum zeichnen von Zahlen und
	 * Sonderzeichen gesetzt. Diese Bild wird als Texture gesetzt.
	 * 
	 * @param bitmap
	 *            Grafik die geladen werden soll.
	 */
	public void loadBitmapLetters(Bitmap bitmap) {
		bitmapLetters = bitmap;
		textureLoad = true;
	}

	/**
	 * Mit dieser Methode wird ein Bitmap zum zeichnen von Zahlen und
	 * Sonderzeichen gesetzt. Diese Bild wird als Texture gesetzt.
	 * 
	 * @param bitmap
	 *            Grafik die geladen werden soll.
	 */
	public void loadBitmapDigits(Bitmap bitmap) {
		bitmapDigits = bitmap;
		textureLoad = true;
	}

	/**
	 * Laden der Texture für Groß und Kleinbuchstaben.
	 * 
	 * @param gl
	 *            OpenGL-Context auf dem gerendert wird.
	 */
	private void loadGLTextureLetters(GL10 gl) {
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		textureIdLetters = textures[0];

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIdLetters);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapLetters, 0);
	}

	/**
	 * Laden der Texture für Zahlen und Sonderzeichen.
	 * 
	 * @param gl
	 *            OpenGL-Context auf dem gerendert wird.
	 */
	private void loadTextureDigits(GL10 gl) {
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		textureIdDigits = textures[0];

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIdDigits);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapDigits, 0);
	}

	/**
	 * Enum für die unterschiedlichen Ausrichtungen, die der Text haben kann.
	 * 
	 * @author Johannes Kraus
	 * @version 1.0
	 */
	public enum Orientation {
		ORIENTATION_LEFT, ORIENTATION_RIGHT, ORIENTATION_CENTER
	};
}
