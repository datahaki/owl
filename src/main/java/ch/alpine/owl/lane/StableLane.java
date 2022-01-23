// class by jph, gjoel
package ch.alpine.owl.lane;

import java.io.Serializable;

import ch.alpine.sophus.lie.se2.Se2GroupElement;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;

/** lane of constant width */
public class StableLane implements LaneInterface, Serializable {
  /** @param controlPoints may be null
   * @param refined
   * @param halfWidth */
  public static LaneInterface of(Tensor controlPoints, Tensor refined, Scalar halfWidth) {
    return new StableLane(controlPoints, refined, halfWidth);
  }

  // ---
  private final Tensor controlPoints;
  private final Tensor refined;
  private final Tensor lbound;
  private final Tensor rbound;
  private final Tensor margins;

  private StableLane(Tensor controlPoints, Tensor refined, Scalar halfWidth) {
    this.controlPoints = controlPoints;
    this.refined = refined;
    /** the offset vectors are not magic constants but are multiplied by width */
    Tensor OFS_L = Tensors.of(halfWidth.zero(), halfWidth, RealScalar.ZERO);
    Tensor OFS_R = Tensors.of(halfWidth.zero(), halfWidth.negate(), RealScalar.ZERO);
    lbound = boundary(OFS_L).unmodifiable();
    rbound = boundary(OFS_R).unmodifiable();
    margins = ConstantArray.of(halfWidth, refined.length());
  }

  private Tensor boundary(Tensor base) {
    return Tensor.of(refined.stream() //
        .map(Se2GroupElement::new) //
        .map(se2GroupElement -> se2GroupElement.combine(base)));
  }

  @Override // from LaneInterface
  public Tensor controlPoints() {
    return controlPoints;
  }

  @Override // from LaneInterface
  public Tensor midLane() {
    return refined;
  }

  @Override // from LaneInterface
  public Tensor leftBoundary() {
    return lbound;
  }

  @Override // from LaneInterface
  public Tensor rightBoundary() {
    return rbound;
  }

  @Override // from LaneInterface
  public Tensor margins() {
    return margins;
  }
}
