package net.diylabs.teaxplosion;

public class Vec3f
{
  public float[] m_data = new float[3];

  public Vec3f(float x, float y, float z)
  {
    m_data[0] = x; m_data[1] = y; m_data[2] = z;
  }
  public Vec3f(Vec3f other)
  {
    m_data = other.m_data;
  }

  public Vec3f add(Vec3f other)
  {
    return Vec3f.add(this, other);
  }
  public Vec3f smult(float s)
  {
    return Vec3f.smult(this, s);
  }


  public static Vec3f make(float x, float y, float z)
  {
    return new Vec3f(x, y, z);
  }
  public static Vec3f add(Vec3f a, Vec3f b)
  {
    return new Vec3f((a.m_data[0] + b.m_data[0]), (a.m_data[1] + b.m_data[1]), (a.m_data[2] + b.m_data[2]));
  }
  public static Vec3f smult(Vec3f a, float s)
  {
    return new Vec3f((a.m_data[0] * s), (a.m_data[1] * s), (a.m_data[2] * s));
  }
}
