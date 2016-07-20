package net.diylabs.teaxplosion;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

public class Square
{
  private final String vertexShaderCode =
    // This matrix member variable provides a hook to manipulate
    // the coordinates of the objects that use this vertex shader
    "uniform mat4 uMVPMatrix;" +
    "attribute vec4 vPosition;" +
    "void main() {" +
    // The matrix must be included as a modifier of gl_Position.
    // Note that the uMVPMatrix factor *must be first* in order
    // for the matrix multiplication product to be correct.
    "  gl_Position = uMVPMatrix * vPosition;" +
    "}";

  private final String fragmentShaderCode =
    "precision mediump float;" +
    "uniform vec4 vColor;" +
    "void main() {" +
    "  gl_FragColor = vColor;" +
    "}";

  private final FloatBuffer m_vertexBuffer;
  private final ShortBuffer m_drawListBuffer;
  private final int m_program;
  private int m_hndPosition;
  private int m_hndColor;
  private int m_hndMVPMatrix;

  static final int COORDS_PER_VERTEX = 3;
  static float squareCoords[] = {
    -0.5f,  0.5f, 0.0f,   // top left
    -0.5f, -0.5f, 0.0f,   // bottom left
    0.5f, -0.5f, 0.0f,   // bottom right
    0.5f,  0.5f, 0.0f }; // top right

  private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 };
  private final int vertexStride = COORDS_PER_VERTEX * 4;

  float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };

  public Square()
  {
    ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
    bb.order(ByteOrder.nativeOrder());
    m_vertexBuffer = bb.asFloatBuffer();
    m_vertexBuffer.put(squareCoords);
    m_vertexBuffer.position(0);

    ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
    dlb.order(ByteOrder.nativeOrder());
    m_drawListBuffer = dlb.asShortBuffer();
    m_drawListBuffer.put(drawOrder);
    m_drawListBuffer.position(0);

    int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
    int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

    m_program = GLES20.glCreateProgram();
    GLES20.glAttachShader(m_program, vertexShader);
    GLES20.glAttachShader(m_program, fragmentShader);
    GLES20.glLinkProgram(m_program);
  }

  public void draw(float[] mvpMatrix)
  {
    GLES20.glUseProgram(m_program);
    m_hndPosition = GLES20.glGetAttribLocation(m_program, "vPosition");
    GLES20.glEnableVertexAttribArray(m_hndPosition);
    GLES20.glVertexAttribPointer(
                                 m_hndPosition, COORDS_PER_VERTEX,
                                 GLES20.GL_FLOAT, false,
                                 vertexStride, m_vertexBuffer);

    m_hndColor = GLES20.glGetUniformLocation(m_program, "vColor");
    GLES20.glUniform4fv(m_hndColor, 1, color, 0);

    m_hndMVPMatrix = GLES20.glGetUniformLocation(m_program, "uMVPMatrix");
    MyGLRenderer.checkGlError("glGetUniformLocation");

    GLES20.glUniformMatrix4fv(m_hndMVPMatrix, 1, false, mvpMatrix, 0);
    MyGLRenderer.checkGlError("glUniformMatrix4fv");

    GLES20.glDrawElements(
                          GLES20.GL_TRIANGLES, drawOrder.length,
                          GLES20.GL_UNSIGNED_SHORT, m_drawListBuffer);

    GLES20.glDisableVertexAttribArray(m_hndPosition);
  }
}
