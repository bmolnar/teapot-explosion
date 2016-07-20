package net.diylabs.teaxplosion;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class OpenGLES20Activity extends Activity
{
  private GLSurfaceView m_view;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    m_view = new MyGLSurfaceView(this);
    setContentView(m_view);
  }

  @Override
  protected void onPause()
  {
    super.onPause();
    m_view.onPause();
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    m_view.onResume();
  }
}
