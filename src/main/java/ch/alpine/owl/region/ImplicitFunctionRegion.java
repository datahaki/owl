// code by jph
package ch.alpine.owl.region;

import ch.alpine.sophis.math.api.SignedDistanceFunction;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.sca.Sign;

/** region {x | f(x) <= 0} defined by the overriding
 * {@link SignedDistanceFunction#signedDistance(Object)}
 * 
 * for instance, the function f can be the distance to
 * and obstacle:
 * <ul>
 * <li>positive when outside the obstacle,
 * <li>zero when in contact with the obstacle, and
 * <li>negative when in collision
 * </ul> */
public abstract class ImplicitFunctionRegion implements MemberQ, SignedDistanceFunction<Tensor> {
  @Override // from MemberQ
  public final boolean test(Tensor tensor) {
    return Sign.isNegativeOrZero(signedDistance(tensor));
  }
}
