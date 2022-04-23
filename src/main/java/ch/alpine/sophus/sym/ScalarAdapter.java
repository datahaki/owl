// code by jph
package ch.alpine.sophus.sym;

import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.Scalar;

public abstract class ScalarAdapter extends MultiplexScalar {
  @Override
  public Scalar multiply(Scalar scalar) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Scalar negate() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Scalar reciprocal() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Scalar zero() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Scalar one() {
    throw new UnsupportedOperationException();
  }

  @Override
  protected Scalar plus(Scalar scalar) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int hashCode() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(Object object) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException();
  }
}
