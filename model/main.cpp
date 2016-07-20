#include <stdio.h>
#include <assert.h>
#include <math.h>

#include <list>
#include <vector>
#include <iterator>
#include <utility>

class Vec2f
{
 private:
  float data_[2];
 public:
  Vec2f() : data_{0.0f, 0.0f} {}
  Vec2f(float x, float y) : data_{x, y} {}
  Vec2f(const Vec2f &other) : data_{other.data_[0], other.data_[1]} {}

  float& operator[](int index)
  {
    return this->data_[index];
  }

  void fprint(FILE *fp)
  {
    fprintf(fp, "(%.2f, %.2f)", this->data_[0], this->data_[1]);
  }
};




class Vec3f
{
 public:
  float x, y, z;

  Vec3f()
    : x(0.0f), y(0.0f), z(0.0f) {}
  Vec3f(float x_, float y_, float z_)
    : x(x_), y(y_), z(z_) {}
  Vec3f(const Vec3f& other)
    : x(other.x), y(other.y), z(other.z) {}


  void fprint(FILE *fp)
  {
    fprintf(fp, "(%.2f, %.2f, %.2f)", this->x, this->y, this->z);
  }

  void set(float x_, float y_, float z_)
  {
    this->x = x_; this->y = y_; this->z = z_;
  }

  Vec3f operator=(const Vec3f& other)
  {
    this->x = other.x; this->y = other.y; this->z = other.z;
    return *this;
  }

  friend Vec3f operator+(const Vec3f &v1, const Vec3f &v2);
  friend Vec3f operator-(const Vec3f &v1, const Vec3f &v2);
  friend Vec3f operator*(const Vec3f &v, float factor);
  friend Vec3f operator/(const Vec3f &v, float factor);

  float& operator[](int index)
  {
    switch (index)
    {
      case 0:
        return x;
      case 1:
        return y;
      case 2:
        return z;
      default:
        return x;
    }
  }

  static Vec3f zero()
  {
    return Vec3f(0.0f, 0.0f, 0.0f);
  }

  static Vec3f valueOf(float x_, float y_, float z_)
  {
    Vec3f result(x_, y_, z_);
    return result;
  }
  static Vec3f sum(Vec3f& a, Vec3f& b)
  {
    return Vec3f::valueOf(a.x + b.x, a.y + b.y, a.z + b.z);
  }
  static Vec3f smult(Vec3f& a, float factor)
  {
    return Vec3f::valueOf(a.x * factor, a.y * factor, a.z * factor);
  }
};
Vec3f operator+(const Vec3f &v1, const Vec3f &v2)
{
  return Vec3f((v1.x + v2.x), (v1.y + v2.y), (v1.z + v2.z));
}
Vec3f operator-(const Vec3f &v1, const Vec3f &v2)
{
  return Vec3f((v1.x - v2.x), (v1.y - v2.y), (v1.z - v2.z));
}
Vec3f operator*(const Vec3f &v, float factor)
{
  return Vec3f((v.x * factor), (v.y * factor), (v.z * factor));
}
Vec3f operator/(const Vec3f &v, float factor)
{
  return Vec3f((v.x / factor), (v.y / factor), (v.z / factor));
}




class Parser
{
 private:
  virtual void on_parse_begin(const char *filename) {}
  virtual void on_parse_end() {}

  virtual void on_patches_begin(int npatches) {}
  virtual void on_patches_entry(int index, int *patch, int count) {}
  virtual void on_patches_end() {}

  virtual void on_vertices_begin(int nvertices) {}
  virtual void on_vertices_entry(int index, float coords[3]) {}
  virtual void on_vertices_end() {}

 public:
  int parse(const char *filename)
  {
    int index;

    FILE *fp = fopen(filename,"r");
    if (!fp)
    {
      fprintf(stderr, "parse: Can't open %s\n", filename);
      return -1;
    }

    this->on_parse_begin(filename);

    int npatches;
    (void)fscanf(fp,"%i\n", &npatches);
    this->on_patches_begin(npatches);

    for (index = 0; index < npatches; index++)
    {
      int patch[16];
      (void)fscanf(fp,"%i, %i, %i, %i,", &patch[0], &patch[1], &patch[2], &patch[3]);
      (void)fscanf(fp,"%i, %i, %i, %i,", &patch[4], &patch[5], &patch[6], &patch[7]);
      (void)fscanf(fp,"%i, %i, %i, %i,", &patch[8], &patch[9], &patch[10], &patch[11]);
      (void)fscanf(fp,"%i, %i, %i, %i\n", &patch[12], &patch[13], &patch[14], &patch[15]);
      this->on_patches_entry(index, patch, 16);
    }
    this->on_patches_end();


    int nverts;
    (void)fscanf(fp,"%i\n", &nverts);
    this->on_vertices_begin(nverts);

    for (index = 1; index <= nverts; index++)
    {
      float vert[3];
      (void)fscanf(fp,"%f, %f, %f\n", &vert[0], &vert[1], &vert[2]);
      this->on_vertices_entry(index, vert);
    }
    this->on_vertices_end();

    this->on_parse_end();

    fclose(fp);
  }
};




class Patch
{
 public:
  int *points_;
  int npoints_;

 public:
  Patch() : points_(NULL), npoints_(0) {}

  Patch(int *points, int npoints)
  {
    points_ = new int[npoints];
    npoints_ = npoints;
    for (int i = 0; i < npoints; i++)
    {
      this->points_[i] = points[i];
    }
  }

  int length()
  {
    return npoints_;
  }

  int& operator[](int index)
  {
    return this->points_[index];
  }


  void fprint(FILE *fp)
  {
    fprintf(fp, "{");
    for (int i = 0; i < this->npoints_; i++)
    {
      fprintf(fp, "%s%d", ((i > 0) ? ", " : " "), this->points_[i]);
    }
    fprintf(fp, " }");
  }
};













class PatchFile
{
 private:
  int npatches_;
  Patch **patches_;
  int nvertices_;
  Vec3f *vertices_;

 public:
  PatchFile()
    : npatches_(0), patches_(NULL), nvertices_(0), vertices_(NULL)
  {}

  void init_patches(int npatches)
  {
    this->npatches_ = npatches;
    this->patches_ = new Patch*[npatches + 1];
  }
  int num_patches()
  {
    return this->npatches_;
  }
  void set_patch(int index, int *points, int count)
  {
    this->patches_[index] = new Patch(points, count);
  }
  Patch& get_patch(int index)
  {
    return *this->patches_[index];
  }


  void init_vertices(int nvertices)
  {
    this->nvertices_ = nvertices;
    this->vertices_ = new Vec3f[nvertices + 1];
  }
  int num_vertices()
  {
    return this->nvertices_;
  }
  void set_vertex(int index, float coords[3])
  {
    this->vertices_[index] = Vec3f(coords[0], coords[1], coords[2]);
  }
  Vec3f& get_vertex(int index)
  {
    return this->vertices_[index];
  }



  void fprint(FILE *fp)
  {
    fprintf(fp, "vertices:\n");

    int row_size = 6;

    fprintf(fp, "\n{");
    for (int i = 0; i < this->nvertices_ + 1; i++)
    {
      int row_pos = i % row_size;
      int row_num = i / row_size;

      fprintf(fp, "%s%s", ((i > 0) ? "," : ""), ((row_pos == 0) ? "\n   " : "   "));

      Vec3f &v = this->get_vertex(i);
      fprintf(fp, "%.3ff, %.3ff, %.3ff", v.x, v.y, v.z);
    }
    fprintf(fp, "\n}\n");



    fprintf(fp, "\n");
    fprintf(fp, "Patches:\n");
    fprintf(fp, "{\n");
    for (int i = 0; i < this->npatches_; i++)
    {
      Patch &p = this->get_patch(i);
      p.fprint(fp);
      fprintf(fp, ",\n");
    }
    fprintf(fp, "}\n");
  }
};





class MyParser : public Parser
{
 private:
  PatchFile *pf_;

 public:
  MyParser()
  {
  }

  PatchFile *get()
  {
    return this->pf_;
  }

 private:
  void on_parse_begin(const char *filename)
  {
    this->pf_ = new PatchFile();
  }
  void on_parse_end()
  {
    //this->pf_->fprint(stdout);
  }



  void on_patches_begin(int npatches)
  {
    this->pf_->init_patches(npatches);
  }
  void on_patches_entry(int index, int *points, int count)
  {
    this->pf_->set_patch(index, points, count);
  }
  void on_patches_end()
  {
  }


  void on_vertices_begin(int nvertices)
  {
    this->pf_->init_vertices(nvertices);
  }
  void on_vertices_entry(int index, float coords[3])
  {
    this->pf_->set_vertex(index, coords);
  }
  void on_vertices_end()
  {
  }
};


#if 0
int factorial(int n)
{
  assert(n >= 0);
  int result = 1;
  for (int i = n; i > 1; i--)
  {
    result *= i;
  }
  return result;
}
float binomial_coefficient(int i, int n)
{
  assert(i >= 0); assert(n >= 0);
  return 1.0f * factorial(n) / (factorial(i) * factorial(n-i));
}
float bernstein_polynomial(int i, int n, float u)
{
  return binomial_coefficient(i, n) * powf(u, i) * powf((1.0f - u), n-i);
}
#define ORDER 4
Vec3f compute_position(struct vertex control_points_k[][ORDER+1], float u, float v)
{
  Vec3f result(0.0, 0.0, 0.0);
  for (int i = 0; i <= ORDER; i++) {
    for (int j = 0; j <= ORDER; j++) {
      float poly_i = bernstein_polynomial(i, ORDER, u);
      float poly_j = bernstein_polynomial(j, ORDER, v);
      result.x += poly_i * poly_j * control_points_k[i][j].x;
      result.y += poly_i * poly_j * control_points_k[i][j].y;
      result.z += poly_i * poly_j * control_points_k[i][j].z;
    }
  }
  return result;
}
#endif




class BezierPatch
{
 private:
  Vec3f *points_;
  int nU_, nV_;

 public:
  BezierPatch(Vec3f *points, int nU, int nV)
  {
    points_ = new Vec3f[nU * nV];
    for (int i = 0; i < nU * nV; i++)
    {
      points_[i] = points[i];
    }
  }

  static Vec3f interpolate1d(Vec3f *points, int n, float t)
  {
    if (n == 1)
    {
      return points[0];
    }
    else if (n == 2)
    {
      return points[0] * (1.0f - t) + points[1] * t;
    }
    else
    {
      Vec3f *next = new Vec3f[n-1];
      for (int i = 0; i < n-1; i++)
      {
        next[i] = interpolate1d(&points[i], 2, t);
      }
      return interpolate1d(next, n-1, t);
    }
  }

  static Vec3f interpolate2d(Vec3f *points, int nU, int nV, Vec2f &uv)
  {
    Vec3f curveV[nV];
    for (int iV = 0; iV < nV; iV++)
    {
      curveV[iV] = BezierPatch::interpolate1d(&points[nU * iV], nU, uv[0]);
    }
    return BezierPatch::interpolate1d(curveV, nV, uv[1]);
  }



  Vec3f get(Vec2f &uv)
  {
    return BezierPatch::interpolate2d(this->points_, this->nU_, this->nV_, uv);
  }

  void map(Vec2f &uv, Vec3f *xyz)
  {
    *xyz = BezierPatch::interpolate2d(this->points_, this->nU_, this->nV_, uv);
  }
  void map(Vec2f *uv, Vec3f *xyz, int count)
  {
    for (int i = 0; i < count; i++)
    {
      xyz[i] = BezierPatch::interpolate2d(this->points_, this->nU_, this->nV_, uv[i]);
    }
  }
};










class Vertex
{
 public:
  Vec3f coords;
  std::list<Vertex*> neighbors;
  Vertex(Vec3f _coords) : coords(_coords) {}



  Vertex* next(Vertex *v)
  {
    
  }
};

class Edge
{
  Vertex &a, &z;
};



class Graph
{
  std::list<Vertex*> vertices_;


  
};

class Model
{
  PatchFile *pf_;
  BezierPatch **patches_;
  int npatches_;

  std::list<Vertex*> m_vertices;
  std::list<Edge*> m_edges;


 public:
  Model(PatchFile *pf)
    : pf_(pf)
  {
    npatches_ = pf->num_patches();
    patches_ = new BezierPatch*[npatches_];

    for (int i = 0; i < npatches_; i++)
    {
      Patch& patch = pf->get_patch(i+1);
      Vec3f points[patch.length()];
      for (int j = 0; j < patch.length(); j++)
      {
        points[j] = pf->get_vertex(patch[j]);
      }
      patches_[i] = new BezierPatch(points, 4, 4);
    }
  }

  BezierPatch& get_patch(int index)
  {
    return *patches_[index];
  }


  void fprint(FILE *fp)
  {

    pf_->fprint(fp);

    int udiv = 4;
    int vdiv = 4;

    Vec2f uv[(udiv+1)*(vdiv+1)];
    Vec3f xyz[(udiv+1)*(vdiv+1)];

    for (int iv = 0; iv <= vdiv; iv++)
    {
      for (int iu = 0; iu <= udiv; iu++)
      {
        uv[iv*(udiv+1)+iu] = Vec2f(((float) iu)/((float) udiv), ((float) iv)/((float) vdiv));
      }
    }


    for (int i = 0; i < npatches_; i++)
    {
      BezierPatch& patch = get_patch(i);
      patch.map(uv, xyz, (udiv+1)*(vdiv+1));

      fprintf(fp, "\n{");
      for (int iv = 0; iv <= vdiv; iv++)
      {
        fprintf(fp, "%s{", (iv > 0) ? "," : "");
        for (int iu = 0; iu <= udiv; iu++)
        {
          fprintf(fp, "%s", (iu > 0) ? "," : "");
          xyz[iv*(udiv+1)+iu].fprint(fp);
        }
        fprintf(fp, "}");
      }
      fprintf(fp, "}");
    }
  }
};




int
main(int argc, char *argv[])
{
  //test();



  MyParser parser;
  parser.parse("teapot");
  PatchFile *pf = parser.get();

  pf->fprint(stdout);

  //Model model(pf);
  //model.fprint(stdout);

  return 0;
}


