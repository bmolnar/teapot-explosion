package net.diylabs.teaxplosion;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView
{
  private final float TOUCH_SCALE_FACTOR = 180.0f / 320.0f;
  private float m_prevX, m_prevY;
  private final MyGLRenderer m_renderer;

  public MyGLSurfaceView(Context context)
  {
    super(context);
    setEGLContextClientVersion(2);
    m_renderer = new MyGLRenderer();
    setRenderer(m_renderer);
    setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
  }

  @Override
  public boolean onTouchEvent(MotionEvent e)
  {
    float x = e.getX();
    float y = e.getY();
    switch (e.getAction())
    {
      case MotionEvent.ACTION_MOVE:
        float dx = x - m_prevX;
        float dy = y - m_prevY;

        if (y > getHeight() / 2)
        {
          dx = dx * -1 ;
        }
        if (x < getWidth() / 2)
        {
          dy = dy * -1 ;
        }
        float delta = (dx + dy) * TOUCH_SCALE_FACTOR;
        m_renderer.setAngle(m_renderer.getAngle() + delta);
        requestRender();
    }
    m_prevX = x;
    m_prevY = y;
    return true;
  }
}
