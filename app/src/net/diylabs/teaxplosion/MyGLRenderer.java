package net.diylabs.teaxplosion;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

public class MyGLRenderer implements GLSurfaceView.Renderer
{
  private static final String TAG = "MyGLRenderer";
  private Triangle m_triangle;
  private Square m_square;
  private Teapot m_teapot;
  private float m_angle;

  private final float[] m_matMVP = new float[16];
  private final float[] m_matProjection = new float[16];
  private final float[] m_matView = new float[16];
  private final float[] m_matRotation = new float[16];

  public float getAngle()
  {
    return m_angle;
  }
  public void setAngle(float angle)
  {
    m_angle = angle;
  }


  @Override
  public void onSurfaceCreated(GL10 unused, EGLConfig config)
  {
    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    m_triangle = new Triangle();
    m_square = new Square();
    m_teapot = new Teapot();
  }

  @Override
  public void onDrawFrame(GL10 unused)
  {
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

    Matrix.setLookAtM(m_matView, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    Matrix.multiplyMM(m_matMVP, 0, m_matProjection, 0, m_matView, 0);


    drawCoords(unused, m_matMVP);

    m_square.draw(m_matMVP);


    float[] matScale = new float[16];

    Matrix.setIdentityM(matScale, 0);
    Matrix.scaleM(matScale, 0, 0.3f, 0.3f, 0.3f);


    float[] matTeapot = new float[16];
    Matrix.multiplyMM(matTeapot, 0, m_matMVP, 0, matScale, 0);
    Matrix.rotateM(matTeapot, 0, m_angle, 1.0f, 0.0f, 0.0f);
    m_teapot.draw(matTeapot);


    Matrix.setRotateM(m_matRotation, 0, m_angle, 0, 0, 1.0f);


    //float[] matScratch = new float[16];
    //Matrix.multiplyMM(matScratch, 0, m_matMVP, 0, m_matRotation, 0);
    //m_triangle.draw(matScratch);
  }

  @Override
  public void onSurfaceChanged(GL10 unused, int width, int height)
  {
    GLES20.glViewport(0, 0, width, height);
    float ratio = (float) width / height;
    Matrix.frustumM(m_matProjection, 0, -ratio, ratio, -1, 1, 3, 7);
  }



  protected void drawCoords(GL10 unused, float[] mvpMatrix)
  {
  }




  public static int loadShader(int shaderType, String shaderCode)
  {
    int shader = GLES20.glCreateShader(shaderType);
    GLES20.glShaderSource(shader, shaderCode);
    GLES20.glCompileShader(shader);
    return shader;
  }

  public static void checkGlError(String glOperation)
  {
    int error;
    while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR)
    {
      Log.e(TAG, glOperation + ": glError " + error);
      throw new RuntimeException(glOperation + ": glError " + error);
    }
  }
}
