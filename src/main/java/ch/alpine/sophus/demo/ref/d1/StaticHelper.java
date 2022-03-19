// code by jph
package ch.alpine.sophus.demo.ref.d1;

import ch.alpine.sophus.api.MidpointInterface;
import ch.alpine.sophus.ref.d1.CurveSubdivision;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Integers;

/* package */ enum StaticHelper {
  ;
  static TensorUnaryOperator create(CurveSubdivision curveSubdivision, boolean cyclic) {
    return cyclic //
        ? curveSubdivision::cyclic
        : curveSubdivision::string;
  }

  /** @param control
   * @param levels
   * @param curveSubdivision
   * @param isDual
   * @param cyclic
   * @param midpointInterface
   * @return */
  public static Tensor refine( //
      Tensor control, int levels, CurveSubdivision curveSubdivision, //
      boolean isDual, boolean cyclic, MidpointInterface midpointInterface) {
    TensorUnaryOperator tensorUnaryOperator = create(curveSubdivision, cyclic);
    Tensor refined = control;
    for (int level = 0; level < levels; ++level) {
      Tensor prev = refined;
      refined = tensorUnaryOperator.apply(refined);
      if (isDual && //
          !Integers.isEven(level) && //
          !cyclic && //
          1 < control.length())
        refined = Join.of( //
            Tensors.of(midpointInterface.midpoint(control.get(0), prev.get(0))), //
            refined, //
            Tensors.of(midpointInterface.midpoint(Last.of(prev), Last.of(control))));
    }
    return refined;
  }
}
