// code by jph
package ch.alpine.sophus.app.curve;

import ch.alpine.sophus.crv.spline.AbstractBSplineInterpolation;
import ch.alpine.sophus.crv.spline.AbstractBSplineInterpolation.Iteration;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/* package */ enum BSplineInterpolationSequence {
  ;
  public static Tensor of(AbstractBSplineInterpolation abstractBSplineInterpolation) {
    int steps = 10;
    Tensor tensor = Tensors.reserve(steps);
    Iteration iteration = abstractBSplineInterpolation.init();
    for (int count = 0; count < steps; ++count) {
      iteration = iteration.stepGaussSeidel();
      tensor.append(iteration.control());
    }
    return tensor;
  }
}
