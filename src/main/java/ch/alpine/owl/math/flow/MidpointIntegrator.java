// code by jph
package ch.alpine.owl.math.flow;

import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** 2nd order RungeKutta
 * integrator requires 2 flow evaluations
 * 
 * Numerical Recipes 3rd Edition (17.1.2) */
public enum MidpointIntegrator implements Integrator {
  INSTANCE;

  @Override // from Integrator
  public Tensor step(StateSpaceModel stateSpaceModel, Tensor x0, Tensor u, Scalar _2h
  // Flow flow, Tensor x0, Scalar _2h
  ) {
    Scalar h = _2h.multiply(RationalScalar.HALF);
    Tensor xm = x0.add(stateSpaceModel.f(x0, u).multiply(h)); // h
    return /**/ x0.add(stateSpaceModel.f(xm, u).multiply(_2h)); // 2h
  }
}
