// code by jph
package ch.alpine.owl.math.model;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.ext.Integers;

/** the name "double" hints that if the state is (position, velocity) then
 * control u acts as (acceleration).
 * 
 * implementation for linear coordinate system R^n
 * 
 * see also {@link SingleIntegratorStateSpaceModel}
 * 
 * Lipschitz L == 1 */
public enum DoubleIntegratorStateSpaceModel implements StateSpaceModel {
  INSTANCE;

  /** f((p, v), u) == (v, u) */
  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    Integers.requireEquals(x.length(), u.length() << 1);
    Tensor v = x.extract(u.length(), x.length());
    return Join.of(v, u);
  }
}
