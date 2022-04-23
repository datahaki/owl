// code by jph
package ch.alpine.sophus.sym;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.pow.Sqrt;

/** Q[Sqrt[n]] */
public class RootScalar extends MultiplexScalar implements //
    AbsInterface, Serializable {
  /** creator with package visibility
   * 
   * @param re neither a {@link ComplexScalar}, or {@link Quantity}
   * @param im neither a {@link ComplexScalar}, or {@link Quantity}
   * @return */
  /* package */ static Scalar of(Scalar re, Scalar im, Scalar ba) {
    return Scalars.isZero(im) || Scalars.isZero(ba) //
        ? re
        : new RootScalar(re, im, ba);
  }

  // ---
  private final Scalar re;
  private final Scalar im;
  private final Scalar ba;

  /* package */ RootScalar(Scalar re, Scalar im, Scalar ba) {
    this.re = re;
    this.im = im;
    this.ba = ba;
  }

  @Override // from Scalar
  public Scalar negate() {
    return new RootScalar(re.negate(), im.negate(), ba);
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof RealScalar)
      // TODO OWL ALG check for exact precision
      return new RootScalar(re.multiply(scalar), im.multiply(scalar), ba);
    if (scalar instanceof RootScalar) {
      RootScalar rootScalar = (RootScalar) scalar;
      if (ba.equals(rootScalar.ba)) {
        return of( //
            re.multiply(rootScalar.re).add(im.multiply(rootScalar.im).multiply(ba)), //
            re.multiply(rootScalar.im).add(im.multiply(rootScalar.re)), //
            ba);
      }
    }
    throw new UnsupportedOperationException();
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    Scalar den = re.multiply(re).subtract(im.multiply(im).multiply(ba));
    return new RootScalar(re.divide(den), im.negate().divide(den), ba);
  }

  @Override // from Scalar
  public Scalar zero() {
    return re.zero().add(im.zero());
  }

  @Override // from Scalar
  public Scalar one() {
    return re.one();
  }

  // ---
  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof RealScalar)
      return new RootScalar(re.add(scalar), im, ba);
    if (scalar instanceof RootScalar) {
      RootScalar rootScalar = (RootScalar) scalar;
      if (ba.equals(rootScalar.ba))
        return of( //
            re.add(rootScalar.re), //
            im.add(rootScalar.im), //
            ba);
    }
    throw new UnsupportedOperationException();
  }

  // ---
  @Override // from AbsInterface
  public Scalar abs() { // "complex modulus"
    return Abs.FUNCTION.apply(re.add(im.multiply(Sqrt.of(ba))));
  }

  @Override // from AbsInterface
  public Scalar absSquared() {
    return Abs.FUNCTION.apply(multiply(this));
  }

  @Override
  public Scalar eachMap(UnaryOperator<Scalar> unaryOperator) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean allMatch(Predicate<Scalar> predicate) {
    // TODO Auto-generated method stub
    return false;
  }

  // ---
  @Override // from AbstractScalar
  public int hashCode() {
    return Objects.hash(re, im, ba);
  }

  @Override // from AbstractScalar
  public boolean equals(Object object) {
    if (object instanceof RootScalar) {
      RootScalar rootScalar = (RootScalar) object;
      return re.equals(rootScalar.re) //
          && im.equals(rootScalar.im) //
          && ba.equals(rootScalar.ba);
    }
    return false;
  }

  @Override // from AbstractScalar
  public String toString() {
    return String.format("(%s, %s[%s])", re, im, ba);
  }
}
