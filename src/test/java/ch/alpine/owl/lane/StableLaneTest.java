// code by jph
package ch.alpine.owl.lane;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.sophus.ref.d1.LaneRiesenfeldCurveSubdivision;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

public class StableLaneTest {
  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();

  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    LaneInterface laneInterface = Serialization.copy(StableLanes.of( //
        Tensors.fromString("{{0[m], 1[m], 2}, {2[m], 0[m], 4}, {-1[m],-3[m], -2}}"), //
        LaneRiesenfeldCurveSubdivision.of(CLOTHOID_BUILDER, 1)::cyclic, 3, Quantity.of(1, "m")));
    assertEquals(laneInterface.controlPoints().length(), 3);
    MatrixQ.require(laneInterface.midLane());
    MatrixQ.require(laneInterface.leftBoundary());
    MatrixQ.require(laneInterface.rightBoundary());
    VectorQ.require(laneInterface.margins());
  }

  @Test
  public void testStraight() throws ClassNotFoundException, IOException {
    LaneInterface laneInterface = Serialization.copy(StableLanes.of( //
        Tensors.fromString("{{0[m], 0[m], 0}, {2[m], 0[m], 0}}"), //
        LaneRiesenfeldCurveSubdivision.of(CLOTHOID_BUILDER, 1)::string, 3, Quantity.of(0.5, "m")));
    assertEquals(laneInterface.margins().get(0), Quantity.of(0.5, "m"));
    {
      Tensor leftBoundary = MatrixQ.require(laneInterface.leftBoundary());
      assertEquals(leftBoundary.get(Tensor.ALL, 1), laneInterface.margins());
      Chop._12.requireAllZero(leftBoundary.get(Tensor.ALL, 2));
    }
    {
      Tensor rightBoundary = MatrixQ.require(laneInterface.rightBoundary());
      assertEquals(rightBoundary.get(Tensor.ALL, 1), laneInterface.margins().negate());
      Chop._12.requireAllZero(rightBoundary.get(Tensor.ALL, 2));
    }
  }
}
