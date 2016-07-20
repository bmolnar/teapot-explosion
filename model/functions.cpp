



template <class T1, class T2>
class Pair
{
 public:
  T1 first;
  T2 second;
  Pair(T1 _first, T2 _second) : first(_first), second(_second) {}

  static Pair<T1,T2> make(T1 &_first, T2 &_second)
  {
    return Pair(_first, _second);
  }
};





template <class TI, class TO>
class Function
{
  virtual TO apply(TI& in);
};
template <class TI1, class TI2, class TO>
class BiFunction : public Function<Pair<TI1&, TI2&>, TO>
{
  TO apply(TI1& in1, TI2& in2)
  {
    return Pair<TI1&,TI2&>::make(in1, in2);
  }
};

template <class TI, class TO>
class ConstantFunction : public Function<TI,TO>
{
 private:
  TO value_;
 public:
  ConstantFunction(TO& value) : value_(value) {}
  TO apply(TI& input) { return this->value_; }
};

template <class TI, class TO>
class FunctionSum : public Function<TI,TO>
{
 private:
  Function<TI,TO> &f1_, &f2_;
 public:
  FunctionSum(Function<TI,TO> &f1, Function<TI,TO> &f2) : f1_(f1), f2_(f2) {}
  TO apply(TI& input) { return this->f1_.apply(input) + this->f2_.apply(input); }
};

template <class TI, class TT, class TO>
class FunctionComposition : public Function<TI,TO>
{
 private:
  Function<TI,TT> &f1_;
  Function<TT,TO> &f2_;
 public:
  FunctionComposition(Function<TI,TT> &f1, Function<TT,TO> &f2) : f1_(f1), f2_(f2) {}
  TO apply(TI& input) { return this->f2_.apply(this->f1_.apply(input)); }
};

template <class TI, class TTL, class TTR, class TO>
class FunctionOperation : public Function<TI,TO>
{
 private:
  Function<TI,TTL> &f1_;
  Function<TI,TTR> &f2_;
  BiFunction<TTL,TTR,TO> &oper_;
 public:
  FunctionOperation(Function<TI,TTL> &f1, Function<TI,TTR> &f2, BiFunction<TTL,TTR,TO> oper) : f1_(f1), f2_(f2), oper_(oper) {}
  TO apply(TI& input) { return this->oper_.apply(this->f1_.apply(input), this->f2_.apply(input)); }
};

template <class TI, class TO>
class LinearBezier : public Function<float,Function<TI,TO>>
{
 private:
  Function<TI,TO> &f1_;
  Function<TI,TO> &f2_;
  BiFunction<TTL,TTR,TO> &oper_;
 public:
  LinearBezier(Function<TI,TO> &f1, Function<TI,TO> &f2) : f1_(f1), f2_(f2) {}
  Function<TI,TO> apply(float& input) { return this->oper_.apply(this->f1_.apply(input), this->f2_.apply(input)); }
};







class Functions
{

 public:
  static Function<float,TO> linearBezier(Function<TI,TO> &f1, Function<TI,TO> &f2)
  {


  }
};

