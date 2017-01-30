package eu.crispus.android.bmi.guiutil;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Einfaches Rechteck in dessen Ecken Farben liegen, dadurch wird ein Farbverlauf gezeichnet.
 *
 * @author Johannes Kraus
 * @version 1.0
 */
public class Hintergrund {

    /**
     * Rechteck für die Farben
     */
    private final float[] vertices = {
            -40.0f, 3.0f, -50.3f, // 0, Oben links
            -40.0f, -25.0f, -50.3f, // 1, unten links
            40.0f, -25.0f, -50.3f, // 2, unten rechts
            40.0f, 25.0f, -50.3f, // 3, oben rechts
    };

    /**
     * Anordnung der Vektoren.
     */
    private final short[] indices = {0, 1, 2, 0, 2, 3};

    /**
     * Farben die auf das Rechteck gemappt werden.
     */
    float[] colors = {
            0f, 0.38f, 0.62f, 1f, // Punkt 0 hellblau
            0f, 0.24f, 0.39f, 1f, // Punkt 1 dunktelblau
            0f, 0.24f, 0.39f, 1f, // Punkt 2 dunktelblau
            0f, 0.38f, 0.62f, 1f, // Punkt 3 hellblau
    };

    /**
     * Verktoren - Puffer.
     */
    private final FloatBuffer vertexBuffer;

    /**
     * Indexe.
     */
    private final ShortBuffer indexBuffer;

    /**
     * Farben - Puffer.
     */
    private final FloatBuffer colorBuffer;

    public Hintergrund() {
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);

        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        colorBuffer = cbb.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);
    }

    /**
     * Diese Funktion zeichnet ein Rechteck und setzt die Farben in dessen Ecken. Da zwei unterschiedliche Farben
     * existieren, wird ein Farbverlauf (Gradient) von dunktelblau nach hellblau gezeichnet.
     *
     * @param gl OpenGL-Objekt auf dem gezeichet werden soll.
     */
    public void draw(GL10 gl) {
        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length,
                GL10.GL_UNSIGNED_SHORT, indexBuffer);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisable(GL10.GL_CULL_FACE);
    }
}
