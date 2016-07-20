package net.diylabs.teaxplosion;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class TeapotRenderer
{
  private final String vertexShaderCode =
    "uniform mat4 uMVPMatrix;" +
    "attribute vec4 vPosition;" +
    "void main() {" +
    "  gl_Position = uMVPMatrix * vPosition;" +
    "}";

  private final String fragmentShaderCode =
    "precision mediump float;" +
    "uniform vec4 vColor;" +
    "void main() {" +
    "  gl_FragColor = vColor;" +
    "}";

  private final FloatBuffer m_vertexBuffer;
  private final int m_program;
  private int m_hndPosition;
  private int m_hndColor;
  private int m_hndMVPMatrix;

  // number of coordinates per vertex in this array
  static final int COORDS_PER_VERTEX = 3;
  static float triangleCoords[] = {
    // in counterclockwise order:
    0.0f,  0.622008459f, 0.0f,   // top
    -0.5f, -0.311004243f, 0.0f,   // bottom left
    0.5f, -0.311004243f, 0.0f    // bottom right
  };
  private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
  private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

  float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 0.0f };

  public TeapotRenderer()
  {
    ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
    bb.order(ByteOrder.nativeOrder());

    m_vertexBuffer = bb.asFloatBuffer();
    m_vertexBuffer.put(triangleCoords);
    m_vertexBuffer.position(0);

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
    GLES20.glVertexAttribPointer(m_hndPosition, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, m_vertexBuffer);

    m_hndColor = GLES20.glGetUniformLocation(m_program, "vColor");
    GLES20.glUniform4fv(m_hndColor, 1, color, 0);

    m_hndMVPMatrix = GLES20.glGetUniformLocation(m_program, "uMVPMatrix");
    MyGLRenderer.checkGlError("glGetUniformLocation");

    GLES20.glUniformMatrix4fv(m_hndMVPMatrix, 1, false, mvpMatrix, 0);
    MyGLRenderer.checkGlError("glUniformMatrix4fv");

    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    GLES20.glDisableVertexAttribArray(m_hndPosition);
  }
}
