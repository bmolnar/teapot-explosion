package net.diylabs.teaxplosion;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import java.util.Arrays;

import android.opengl.GLES20;
import android.opengl.Matrix;

import android.util.Log;

public class Teapot
{
  private static final String TAG = "Teapot";

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

  static final int COORDS_PER_VERTEX = 3;
  private final int vertexStride = COORDS_PER_VERTEX * 4;

  static final float control_points[] =
  {
    0.000f, 0.000f, 0.000f,   1.400f, 0.000f, 2.400f,   1.400f, -0.784f, 2.400f,   0.784f, -1.400f, 2.400f,   0.000f, -1.400f, 2.400f,   1.337f, 0.000f, 2.531f,
    1.337f, -0.749f, 2.531f,   0.749f, -1.337f, 2.531f,   0.000f, -1.337f, 2.531f,   1.438f, 0.000f, 2.531f,   1.438f, -0.805f, 2.531f,   0.805f, -1.438f, 2.531f,
    0.000f, -1.438f, 2.531f,   1.500f, 0.000f, 2.400f,   1.500f, -0.840f, 2.400f,   0.840f, -1.500f, 2.400f,   0.000f, -1.500f, 2.400f,   -0.784f, -1.400f, 2.400f,
    -1.400f, -0.784f, 2.400f,   -1.400f, 0.000f, 2.400f,   -0.749f, -1.337f, 2.531f,   -1.337f, -0.749f, 2.531f,   -1.337f, 0.000f, 2.531f,   -0.805f, -1.438f, 2.531f,
    -1.438f, -0.805f, 2.531f,   -1.438f, 0.000f, 2.531f,   -0.840f, -1.500f, 2.400f,   -1.500f, -0.840f, 2.400f,   -1.500f, 0.000f, 2.400f,   -1.400f, 0.784f, 2.400f,
    -0.784f, 1.400f, 2.400f,   0.000f, 1.400f, 2.400f,   -1.337f, 0.749f, 2.531f,   -0.749f, 1.337f, 2.531f,   0.000f, 1.337f, 2.531f,   -1.438f, 0.805f, 2.531f,
    -0.805f, 1.438f, 2.531f,   0.000f, 1.438f, 2.531f,   -1.500f, 0.840f, 2.400f,   -0.840f, 1.500f, 2.400f,   0.000f, 1.500f, 2.400f,   0.784f, 1.400f, 2.400f,
    1.400f, 0.784f, 2.400f,   0.749f, 1.337f, 2.531f,   1.337f, 0.749f, 2.531f,   0.805f, 1.438f, 2.531f,   1.438f, 0.805f, 2.531f,   0.840f, 1.500f, 2.400f,
    1.500f, 0.840f, 2.400f,   1.750f, 0.000f, 1.875f,   1.750f, -0.980f, 1.875f,   0.980f, -1.750f, 1.875f,   0.000f, -1.750f, 1.875f,   2.000f, 0.000f, 1.350f,
    2.000f, -1.120f, 1.350f,   1.120f, -2.000f, 1.350f,   0.000f, -2.000f, 1.350f,   2.000f, 0.000f, 0.900f,   2.000f, -1.120f, 0.900f,   1.120f, -2.000f, 0.900f,
    0.000f, -2.000f, 0.900f,   -0.980f, -1.750f, 1.875f,   -1.750f, -0.980f, 1.875f,   -1.750f, 0.000f, 1.875f,   -1.120f, -2.000f, 1.350f,   -2.000f, -1.120f, 1.350f,
    -2.000f, 0.000f, 1.350f,   -1.120f, -2.000f, 0.900f,   -2.000f, -1.120f, 0.900f,   -2.000f, 0.000f, 0.900f,   -1.750f, 0.980f, 1.875f,   -0.980f, 1.750f, 1.875f,
    0.000f, 1.750f, 1.875f,   -2.000f, 1.120f, 1.350f,   -1.120f, 2.000f, 1.350f,   0.000f, 2.000f, 1.350f,   -2.000f, 1.120f, 0.900f,   -1.120f, 2.000f, 0.900f,
    0.000f, 2.000f, 0.900f,   0.980f, 1.750f, 1.875f,   1.750f, 0.980f, 1.875f,   1.120f, 2.000f, 1.350f,   2.000f, 1.120f, 1.350f,   1.120f, 2.000f, 0.900f,
    2.000f, 1.120f, 0.900f,   2.000f, 0.000f, 0.450f,   2.000f, -1.120f, 0.450f,   1.120f, -2.000f, 0.450f,   0.000f, -2.000f, 0.450f,   1.500f, 0.000f, 0.225f,
    1.500f, -0.840f, 0.225f,   0.840f, -1.500f, 0.225f,   0.000f, -1.500f, 0.225f,   1.500f, 0.000f, 0.150f,   1.500f, -0.840f, 0.150f,   0.840f, -1.500f, 0.150f,
    0.000f, -1.500f, 0.150f,   -1.120f, -2.000f, 0.450f,   -2.000f, -1.120f, 0.450f,   -2.000f, 0.000f, 0.450f,   -0.840f, -1.500f, 0.225f,   -1.500f, -0.840f, 0.225f,
    -1.500f, 0.000f, 0.225f,   -0.840f, -1.500f, 0.150f,   -1.500f, -0.840f, 0.150f,   -1.500f, 0.000f, 0.150f,   -2.000f, 1.120f, 0.450f,   -1.120f, 2.000f, 0.450f,
    0.000f, 2.000f, 0.450f,   -1.500f, 0.840f, 0.225f,   -0.840f, 1.500f, 0.225f,   0.000f, 1.500f, 0.225f,   -1.500f, 0.840f, 0.150f,   -0.840f, 1.500f, 0.150f,
    0.000f, 1.500f, 0.150f,   1.120f, 2.000f, 0.450f,   2.000f, 1.120f, 0.450f,   0.840f, 1.500f, 0.225f,   1.500f, 0.840f, 0.225f,   0.840f, 1.500f, 0.150f,
    1.500f, 0.840f, 0.150f,   -1.600f, 0.000f, 2.025f,   -1.600f, -0.300f, 2.025f,   -1.500f, -0.300f, 2.250f,   -1.500f, 0.000f, 2.250f,   -2.300f, 0.000f, 2.025f,
    -2.300f, -0.300f, 2.025f,   -2.500f, -0.300f, 2.250f,   -2.500f, 0.000f, 2.250f,   -2.700f, 0.000f, 2.025f,   -2.700f, -0.300f, 2.025f,   -3.000f, -0.300f, 2.250f,
    -3.000f, 0.000f, 2.250f,   -2.700f, 0.000f, 1.800f,   -2.700f, -0.300f, 1.800f,   -3.000f, -0.300f, 1.800f,   -3.000f, 0.000f, 1.800f,   -1.500f, 0.300f, 2.250f,
    -1.600f, 0.300f, 2.025f,   -2.500f, 0.300f, 2.250f,   -2.300f, 0.300f, 2.025f,   -3.000f, 0.300f, 2.250f,   -2.700f, 0.300f, 2.025f,   -3.000f, 0.300f, 1.800f,
    -2.700f, 0.300f, 1.800f,   -2.700f, 0.000f, 1.575f,   -2.700f, -0.300f, 1.575f,   -3.000f, -0.300f, 1.350f,   -3.000f, 0.000f, 1.350f,   -2.500f, 0.000f, 1.125f,
    -2.500f, -0.300f, 1.125f,   -2.650f, -0.300f, 0.938f,   -2.650f, 0.000f, 0.938f,   -2.000f, -0.300f, 0.900f,   -1.900f, -0.300f, 0.600f,   -1.900f, 0.000f, 0.600f,
    -3.000f, 0.300f, 1.350f,   -2.700f, 0.300f, 1.575f,   -2.650f, 0.300f, 0.938f,   -2.500f, 0.300f, 1.125f,   -1.900f, 0.300f, 0.600f,   -2.000f, 0.300f, 0.900f,
    1.700f, 0.000f, 1.425f,   1.700f, -0.660f, 1.425f,   1.700f, -0.660f, 0.600f,   1.700f, 0.000f, 0.600f,   2.600f, 0.000f, 1.425f,   2.600f, -0.660f, 1.425f,
    3.100f, -0.660f, 0.825f,   3.100f, 0.000f, 0.825f,   2.300f, 0.000f, 2.100f,   2.300f, -0.250f, 2.100f,   2.400f, -0.250f, 2.025f,   2.400f, 0.000f, 2.025f,
    2.700f, 0.000f, 2.400f,   2.700f, -0.250f, 2.400f,   3.300f, -0.250f, 2.400f,   3.300f, 0.000f, 2.400f,   1.700f, 0.660f, 0.600f,   1.700f, 0.660f, 1.425f,
    3.100f, 0.660f, 0.825f,   2.600f, 0.660f, 1.425f,   2.400f, 0.250f, 2.025f,   2.300f, 0.250f, 2.100f,   3.300f, 0.250f, 2.400f,   2.700f, 0.250f, 2.400f,
    2.800f, 0.000f, 2.475f,   2.800f, -0.250f, 2.475f,   3.525f, -0.250f, 2.494f,   3.525f, 0.000f, 2.494f,   2.900f, 0.000f, 2.475f,   2.900f, -0.150f, 2.475f,
    3.450f, -0.150f, 2.513f,   3.450f, 0.000f, 2.513f,   2.800f, 0.000f, 2.400f,   2.800f, -0.150f, 2.400f,   3.200f, -0.150f, 2.400f,   3.200f, 0.000f, 2.400f,
    3.525f, 0.250f, 2.494f,   2.800f, 0.250f, 2.475f,   3.450f, 0.150f, 2.513f,   2.900f, 0.150f, 2.475f,   3.200f, 0.150f, 2.400f,   2.800f, 0.150f, 2.400f,
    0.000f, 0.000f, 3.150f,   0.000f, -0.002f, 3.150f,   0.002f, 0.000f, 3.150f,   0.800f, 0.000f, 3.150f,   0.800f, -0.450f, 3.150f,   0.450f, -0.800f, 3.150f,
    0.000f, -0.800f, 3.150f,   0.000f, 0.000f, 2.850f,   0.200f, 0.000f, 2.700f,   0.200f, -0.112f, 2.700f,   0.112f, -0.200f, 2.700f,   0.000f, -0.200f, 2.700f,
    -0.002f, 0.000f, 3.150f,   -0.450f, -0.800f, 3.150f,   -0.800f, -0.450f, 3.150f,   -0.800f, 0.000f, 3.150f,   -0.112f, -0.200f, 2.700f,   -0.200f, -0.112f, 2.700f,
    -0.200f, 0.000f, 2.700f,   0.000f, 0.002f, 3.150f,   -0.800f, 0.450f, 3.150f,   -0.450f, 0.800f, 3.150f,   0.000f, 0.800f, 3.150f,   -0.200f, 0.112f, 2.700f,
    -0.112f, 0.200f, 2.700f,   0.000f, 0.200f, 2.700f,   0.450f, 0.800f, 3.150f,   0.800f, 0.450f, 3.150f,   0.112f, 0.200f, 2.700f,   0.200f, 0.112f, 2.700f,
    0.400f, 0.000f, 2.550f,   0.400f, -0.224f, 2.550f,   0.224f, -0.400f, 2.550f,   0.000f, -0.400f, 2.550f,   1.300f, 0.000f, 2.550f,   1.300f, -0.728f, 2.550f,
    0.728f, -1.300f, 2.550f,   0.000f, -1.300f, 2.550f,   1.300f, 0.000f, 2.400f,   1.300f, -0.728f, 2.400f,   0.728f, -1.300f, 2.400f,   0.000f, -1.300f, 2.400f,
    -0.224f, -0.400f, 2.550f,   -0.400f, -0.224f, 2.550f,   -0.400f, 0.000f, 2.550f,   -0.728f, -1.300f, 2.550f,   -1.300f, -0.728f, 2.550f,   -1.300f, 0.000f, 2.550f,
    -0.728f, -1.300f, 2.400f,   -1.300f, -0.728f, 2.400f,   -1.300f, 0.000f, 2.400f,   -0.400f, 0.224f, 2.550f,   -0.224f, 0.400f, 2.550f,   0.000f, 0.400f, 2.550f,
    -1.300f, 0.728f, 2.550f,   -0.728f, 1.300f, 2.550f,   0.000f, 1.300f, 2.550f,   -1.300f, 0.728f, 2.400f,   -0.728f, 1.300f, 2.400f,   0.000f, 1.300f, 2.400f,
    0.224f, 0.400f, 2.550f,   0.400f, 0.224f, 2.550f,   0.728f, 1.300f, 2.550f,   1.300f, 0.728f, 2.550f,   0.728f, 1.300f, 2.400f,   1.300f, 0.728f, 2.400f,
    0.000f, 0.000f, 0.000f,   1.500f, 0.000f, 0.150f,   1.500f, 0.840f, 0.150f,   0.840f, 1.500f, 0.150f,   0.000f, 1.500f, 0.150f,   1.500f, 0.000f, 0.075f,
    1.500f, 0.840f, 0.075f,   0.840f, 1.500f, 0.075f,   0.000f, 1.500f, 0.075f,   1.425f, 0.000f, 0.000f,   1.425f, 0.798f, 0.000f,   0.798f, 1.425f, 0.000f,
    0.000f, 1.425f, 0.000f,   -0.840f, 1.500f, 0.150f,   -1.500f, 0.840f, 0.150f,   -1.500f, 0.000f, 0.150f,   -0.840f, 1.500f, 0.075f,   -1.500f, 0.840f, 0.075f,
    -1.500f, 0.000f, 0.075f,   -0.798f, 1.425f, 0.000f,   -1.425f, 0.798f, 0.000f,   -1.425f, 0.000f, 0.000f,   -1.500f, -0.840f, 0.150f,   -0.840f, -1.500f, 0.150f,
    0.000f, -1.500f, 0.150f,   -1.500f, -0.840f, 0.075f,   -0.840f, -1.500f, 0.075f,   0.000f, -1.500f, 0.075f,   -1.425f, -0.798f, 0.000f,   -0.798f, -1.425f, 0.000f,
    0.000f, -1.425f, 0.000f,   0.840f, -1.500f, 0.150f,   1.500f, -0.840f, 0.150f,   0.840f, -1.500f, 0.075f,   1.500f, -0.840f, 0.075f,   0.798f, -1.425f, 0.000f,
    1.425f, -0.798f, 0.000f
  };

  private final short bezier_patches[][] =
  {
    { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 },
    { 4, 17, 18, 19, 8, 20, 21, 22, 12, 23, 24, 25, 16, 26, 27, 28 },
    { 19, 29, 30, 31, 22, 32, 33, 34, 25, 35, 36, 37, 28, 38, 39, 40 },
    { 31, 41, 42, 1, 34, 43, 44, 5, 37, 45, 46, 9, 40, 47, 48, 13 },
    { 13, 14, 15, 16, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60 },
    { 16, 26, 27, 28, 52, 61, 62, 63, 56, 64, 65, 66, 60, 67, 68, 69 },
    { 28, 38, 39, 40, 63, 70, 71, 72, 66, 73, 74, 75, 69, 76, 77, 78 },
    { 40, 47, 48, 13, 72, 79, 80, 49, 75, 81, 82, 53, 78, 83, 84, 57 },
    { 57, 58, 59, 60, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96 },
    { 60, 67, 68, 69, 88, 97, 98, 99, 92, 100, 101, 102, 96, 103, 104, 105 },
    { 69, 76, 77, 78, 99, 106, 107, 108, 102, 109, 110, 111, 105, 112, 113, 114 },
    { 78, 83, 84, 57, 108, 115, 116, 85, 111, 117, 118, 89, 114, 119, 120, 93 },
    { 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136 },
    { 124, 137, 138, 121, 128, 139, 140, 125, 132, 141, 142, 129, 136, 143, 144, 133 },
    { 133, 134, 135, 136, 145, 146, 147, 148, 149, 150, 151, 152, 69, 153, 154, 155 },
    { 136, 143, 144, 133, 148, 156, 157, 145, 152, 158, 159, 149, 155, 160, 161, 69 },
    { 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177 },
    { 165, 178, 179, 162, 169, 180, 181, 166, 173, 182, 183, 170, 177, 184, 185, 174 },
    { 174, 175, 176, 177, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197 },
    { 177, 184, 185, 174, 189, 198, 199, 186, 193, 200, 201, 190, 197, 202, 203, 194 },
    { 204, 204, 204, 204, 207, 208, 209, 210, 211, 211, 211, 211, 212, 213, 214, 215 },
    { 204, 204, 204, 204, 210, 217, 218, 219, 211, 211, 211, 211, 215, 220, 221, 222 },
    { 204, 204, 204, 204, 219, 224, 225, 226, 211, 211, 211, 211, 222, 227, 228, 229 },
    { 204, 204, 204, 204, 226, 230, 231, 207, 211, 211, 211, 211, 229, 232, 233, 212 },
    { 212, 213, 214, 215, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245 },
    { 215, 220, 221, 222, 237, 246, 247, 248, 241, 249, 250, 251, 245, 252, 253, 254 },
    { 222, 227, 228, 229, 248, 255, 256, 257, 251, 258, 259, 260, 254, 261, 262, 263 },
    { 229, 232, 233, 212, 257, 264, 265, 234, 260, 266, 267, 238, 263, 268, 269, 242 },
    { 270, 270, 270, 270, 279, 280, 281, 282, 275, 276, 277, 278, 271, 272, 273, 274 },
    { 270, 270, 270, 270, 282, 289, 290, 291, 278, 286, 287, 288, 274, 283, 284, 285 },
    { 270, 270, 270, 270, 291, 298, 299, 300, 288, 295, 296, 297, 285, 292, 293, 294 },
    { 270, 270, 270, 270, 300, 305, 306, 279, 297, 303, 304, 275, 294, 301, 302, 271 }
  };

  private final float draw_color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 0.0f };

  //private final FloatBuffer m_vertexBuffer;
  //private final ShortBuffer m_indexBuffer;
  private final int m_program;

  private int m_hndPosition;
  private int m_hndColor;
  private int m_hndMVPMatrix;


  public static class Vec3f
  {
    public float x, y, z;
    public Vec3f()
    {
      x = 0.0f; y = 0.0f; z = 0.0f;
    }
    public Vec3f(float _x, float _y, float _z)
    {
      x = _x; y = _y; z = _z;
    }

    public Vec3f add(Vec3f other)
    {
      return new Vec3f((this.x + other.x), (this.y + other.y), (this.z + other.z));
    }
    public Vec3f smult(float factor)
    {
      return new Vec3f((this.x * factor), (this.y * factor), (this.z * factor));
    }

    public static Vec3f add(Vec3f a, Vec3f b)
    {
      return new Vec3f((a.x + b.x), (a.y + b.y), (a.z + b.z));
    }
    public static Vec3f zero()
    {
      return new Vec3f();
    }

    public static Vec3f[] newArray(int count)
    {
      Vec3f[] result = new Vec3f[count];
      for (int i = 0; i < count; i++)
      {
        result[i] = new Vec3f();
      }
      return result;
    }
  }

  public static class Bezier
  {

    public static Vec3f interpolate1d(Vec3f[] points, float u)
    {
      if (points.length == 1)
      {
        return points[0];
      }
      else if (points.length == 2)
      {
        return Vec3f.add(points[0].smult(1.0f - u), points[1].smult(u));
      }
      else if (points.length > 2)
      {
        Vec3f[] next = new Vec3f[points.length-1];
        for (int i = 0; i < points.length-1; i++)
        {
          Vec3f[] pair = {points[i], points[i+1]};
          next[i] = interpolate1d(pair, u);
        }
        return interpolate1d(next, u);
      }
      else
      {
        return Vec3f.zero();
      }
    }

    public static Vec3f interpolate2d(Vec3f[][] points, float u, float v)
    {
      Vec3f[] vcurve = new Vec3f[points.length];
      for (int vidx = 0; vidx < points.length; vidx++)
      {
        vcurve[vidx] = interpolate1d(points[vidx], u);
      }
      return interpolate1d(vcurve, v);
    }

  }



  public static class VertexBuffer
  {
    private FloatBuffer m_buffer;
    public int pos = 0;

    public VertexBuffer(int capacity)
    {
      //m_buffer = FloatBuffer.allocate(capacity * 3);
      ByteBuffer bb = ByteBuffer.allocateDirect(capacity * 3 * 4);
      bb.order(ByteOrder.nativeOrder());
      m_buffer = bb.asFloatBuffer();
    }


    public final FloatBuffer asFloatBuffer()
    {
      return m_buffer;
    }
    public final int position()
    {
      return m_buffer.position() / 3;
    }
    public final VertexBuffer position(int newPosition)
    {
      m_buffer.position(newPosition * 3);
      return this;
    }
    public VertexBuffer put(Vec3f v)
    {
      m_buffer.put(v.x).put(v.y).put(v.z);
      pos++;
      return this;
    }
    public VertexBuffer putTriangle(Vec3f v1, Vec3f v2, Vec3f v3)
    {
      this.put(v1);
      this.put(v2);
      this.put(v3);
      return this;
    }
    public VertexBuffer putQuad(Vec3f v1, Vec3f v2, Vec3f v3, Vec3f v4)
    {
      this.putTriangle(v1, v2, v3);
      this.putTriangle(v1, v3, v4);
      return this;
    }
    public VertexBuffer putBezierPatch(Vec3f[][] points, int udiv, int vdiv)
    {
      Vec3f[][] mapped = new Vec3f[(udiv+1)][(vdiv+1)];
      for (int vidx = 0; vidx <= vdiv; vidx++)
      {
        float v = ((float) vidx) / vdiv;
        for (int uidx = 0; uidx <= udiv; uidx++)
        {
          float u = ((float) uidx) / udiv;
          mapped[uidx][vidx] = Bezier.interpolate2d(points, u, v);
        }
      }
      for (int vidx = 0; vidx < vdiv; vidx++)
      {
        for (int uidx = 0; uidx < udiv; uidx++)
        {
          this.putQuad(mapped[uidx][vidx], mapped[uidx][vidx+1], mapped[uidx+1][vidx+1], mapped[uidx+1][vidx]);
        }
      }
      return this;
    }
  }



  public static class Fragment
  {
    private float[] m_transform = new float[16];
    private ShortBuffer m_indexBuffer;

    public Fragment(short[] indices)
    {
      Matrix.setIdentityM(m_transform, 0);

      ByteBuffer bb = ByteBuffer.allocateDirect(indices.length * 2);
      bb.order(ByteOrder.nativeOrder());
      m_indexBuffer = bb.asShortBuffer();
      m_indexBuffer.put(indices);
      m_indexBuffer.position(0);
    }
    public void translate(float x, float y, float z)
    {
      Matrix.translateM(m_transform, 0, x, y, z);
    }
  }




  private VertexBuffer m_vertBuffer = new VertexBuffer(65536);
  private int m_vertBufferLen = 0;


  public Teapot()
  {
    for (int pidx = 0; pidx < bezier_patches.length; pidx++)
    {
      short[] patch = bezier_patches[pidx];
      Vec3f[] patchPoints = Vec3f.newArray(16);

      for (int i = 0; i < 16; i++)
      {
        int vidx = patch[i];
        patchPoints[i].x = control_points[3*vidx+0];
        patchPoints[i].y = control_points[3*vidx+1];
        patchPoints[i].z = control_points[3*vidx+2];
      }
      Vec3f[][] patchGrid =
      {
        { patchPoints[0], patchPoints[1], patchPoints[2], patchPoints[3] },
        { patchPoints[4], patchPoints[7], patchPoints[8], patchPoints[7] },
        { patchPoints[8], patchPoints[9], patchPoints[10], patchPoints[11] },
        { patchPoints[12], patchPoints[13], patchPoints[14], patchPoints[15] }
      };
      m_vertBuffer.putBezierPatch(patchGrid, 8, 8);
    }

    // 12288
    Log.i(TAG, "m_vertBuffer.position: " + m_vertBuffer.position());
    m_vertBufferLen = m_vertBuffer.position();
    m_vertBuffer.position(0);





    //ByteBuffer bb = ByteBuffer.allocateDirect(control_points.length * 4);
    //bb.order(ByteOrder.nativeOrder());
    //m_vertexBuffer = bb.asFloatBuffer();
    //m_vertexBuffer.put(control_points);
    //m_vertexBuffer.position(0);

    //ByteBuffer dlb = ByteBuffer.allocateDirect(draw_indices_2.length * 2);
    //dlb.order(ByteOrder.nativeOrder());
    //m_indexBuffer = dlb.asShortBuffer();
    //m_indexBuffer.put(draw_indices_2);
    //m_indexBuffer.position(0);



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
    //GLES20.glVertexAttribPointer(m_hndPosition, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, m_vertexBuffer);
    GLES20.glVertexAttribPointer(m_hndPosition, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, m_vertBuffer.asFloatBuffer());




    m_hndColor = GLES20.glGetUniformLocation(m_program, "vColor");
    GLES20.glUniform4fv(m_hndColor, 1, draw_color, 0);


    m_hndMVPMatrix = GLES20.glGetUniformLocation(m_program, "uMVPMatrix");
    MyGLRenderer.checkGlError("glGetUniformLocation");
    GLES20.glUniformMatrix4fv(m_hndMVPMatrix, 1, false, mvpMatrix, 0);
    MyGLRenderer.checkGlError("glUniformMatrix4fv");





    //GLES20.glDrawElements(GLES20.GL_TRIANGLES, draw_indices_2.length, GLES20.GL_UNSIGNED_SHORT, m_indexBuffer);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, m_vertBufferLen);

    GLES20.glDisableVertexAttribArray(m_hndPosition);
  }

}
