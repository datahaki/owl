// code by jph
package ch.alpine.sophus.sym;

import java.util.OptionalInt;

import ch.alpine.tensor.AbstractScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Drop;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.ExpInterface;
import ch.alpine.tensor.api.LogInterface;
import ch.alpine.tensor.api.PowerInterface;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.SqrtInterface;
import ch.alpine.tensor.api.TrigonometryInterface;
import ch.alpine.tensor.sca.Cos;
import ch.alpine.tensor.sca.Cosh;
import ch.alpine.tensor.sca.Exp;
import ch.alpine.tensor.sca.Log;
import ch.alpine.tensor.sca.Power;
import ch.alpine.tensor.sca.Sin;
import ch.alpine.tensor.sca.Sinh;

/** automatic differentiation */
public class JetScalar extends AbstractScalar implements //
    ExpInterface, LogInterface, PowerInterface, SqrtInterface, TrigonometryInterface {
  private static final JetScalar EMPTY = new JetScalar(Tensors.empty());

  /** @param vector {f[x], f'[x], f''[x], ...}
   * @return */
  public static JetScalar of(Tensor vector) {
    return new JetScalar(VectorQ.require(vector));
  }

  /** TODO important:
   * Distinguish between constants with value v == {v,0,...}
   * ... and variables with value v == {v,1,0,...}
   * 
   * @param value
   * @param n strictly positive
   * @return {value, 1, 0, 0, ...} */
  public static JetScalar of(Scalar value, int n) {
    if (n == 1)
      return new JetScalar(Tensors.of(value));
    Tensor vector = UnitVector.of(n, 1);
    vector.set(value, 0);
    return new JetScalar(vector);
  }

  /** drop function, promote derivatives, and decrease order by 1
   * 
   * @param vector
   * @return */
  private static Tensor opD(Tensor vector) {
    return Drop.head(vector, 1);
  }

  /** keep function and derivatives, and decrease order by 1
   * 
   * @param vector
   * @return */
  private static Tensor opF(Tensor vector) {
    return Drop.tail(vector, 1);
  }

  private static Tensor product(Tensor f, Tensor g) {
    return Tensors.isEmpty(f) && Tensors.isEmpty(g) //
        ? Tensors.empty()
        : Join.of( //
            Tensors.of(f.Get(0).multiply(g.Get(0))), //
            product(opF(f), opD(g)).add(product(opD(f), opF(g))));
  }

  private static Tensor reciprocal(Tensor g) {
    if (Tensors.isEmpty(g))
      return Tensors.empty();
    Tensor opF = opF(g);
    return Join.of( //
        Tensors.of(g.Get(0).reciprocal()), //
        product(opD(g).negate(), reciprocal(product(opF, opF))));
  }

  private static Tensor power(Tensor vector, int n) {
    return n == 0 //
        ? UnitVector.of(vector.length(), 0)
        : product(power(vector, n - 1), vector);
  }

  private static JetScalar chain(Tensor vector, ScalarUnaryOperator f, ScalarUnaryOperator df) {
    if (Tensors.isEmpty(vector))
      return EMPTY;
    return new JetScalar(Join.of( //
        Tensors.of(f.apply(vector.Get(0))), //
        product(((JetScalar) df.apply(new JetScalar(opF(vector)))).vector, opD(vector))));
  }

  // ---
  private final Tensor vector;

  private JetScalar(Tensor vector) {
    this.vector = vector;
  }

  public Tensor vector() {
    return vector.unmodifiable();
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof JetScalar) {
      JetScalar jetScalar = (JetScalar) scalar;
      return new JetScalar(product(vector, jetScalar.vector));
    }
    return new JetScalar(vector.multiply(scalar));
  }

  @Override // from Scalar
  public Scalar negate() {
    return new JetScalar(vector.negate());
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    return new JetScalar(reciprocal(vector));
  }

  @Override // from Scalar
  public Scalar zero() {
    return new JetScalar(vector.map(Scalar::zero));
  }

  @Override // from Scalar
  public Scalar one() {
    Tensor result = vector.map(Scalar::zero);
    result.set(Scalar::one, 0);
    return new JetScalar(result);
  }

  @Override // from Scalar
  public Number number() {
    throw TensorRuntimeException.of(this);
  }

  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof JetScalar) {
      JetScalar jetScalar = (JetScalar) scalar;
      return new JetScalar(vector.add(jetScalar.vector));
    }
    Tensor result = vector.copy();
    result.set(scalar::add, 0); // this + constant scalar
    return new JetScalar(result);
  }

  // ---
  @Override // from ExpInterface
  public Scalar exp() {
    return chain(vector, Exp.FUNCTION, Exp.FUNCTION);
  }

  @Override // from LogInterface
  public Scalar log() {
    return chain(vector, Log.FUNCTION, Scalar::reciprocal);
  }

  @Override // from PowerInterface
  public JetScalar power(Scalar exponent) {
    OptionalInt optionalInt = Scalars.optionalInt(exponent);
    if (optionalInt.isPresent()) {
      int expInt = optionalInt.getAsInt();
      if (0 <= expInt) // TODO exponent == zero!?
        return new JetScalar(power(vector, expInt));
    }
    return chain(vector, Power.function(exponent), //
        scalar -> Power.function(exponent.subtract(RealScalar.ONE)).apply(scalar).multiply(exponent));
  }

  @Override // from SqrtInterface
  public Scalar sqrt() {
    return power(RationalScalar.HALF);
  }

  @Override // from TrigonometryInterface
  public Scalar cos() {
    return chain(vector, Cos.FUNCTION, scalar -> Sin.FUNCTION.apply(scalar).negate());
  }

  @Override // from TrigonometryInterface
  public Scalar cosh() {
    return chain(vector, Cosh.FUNCTION, Sinh.FUNCTION);
  }

  @Override // from TrigonometryInterface
  public Scalar sin() {
    return chain(vector, Sin.FUNCTION, Cos.FUNCTION);
  }

  @Override // from TrigonometryInterface
  public Scalar sinh() {
    return chain(vector, Sinh.FUNCTION, Cosh.FUNCTION);
  }

  // ---
  @Override
  public int hashCode() {
    return vector.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof JetScalar) {
      JetScalar jetScalar = (JetScalar) object;
      return vector.equals(jetScalar.vector);
    }
    return false;
  }

  @Override
  public String toString() {
    return "J" + vector.toString();
  }
}
