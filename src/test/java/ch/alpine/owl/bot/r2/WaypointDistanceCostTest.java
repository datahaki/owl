// code by ynager
package ch.alpine.owl.bot.r2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Dimension;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;

class WaypointDistanceCostTest {
  @Test
  public void testSimple() {
    Tensor waypoints = ResourceData.of("/dubilab/waypoints/20180425.csv");
    ImageCostFunction imageCostFunction = WaypointDistanceCost.of( //
        waypoints, true, RealScalar.ONE, RealScalar.of(7.5), new Dimension(640, 640));
    for (Tensor waypoint : waypoints)
      assertEquals(imageCostFunction.flipYXTensorInterp.at(waypoint), RealScalar.ZERO);
    assertEquals(imageCostFunction.flipYXTensorInterp.at(Tensors.vector(10, 10)), RealScalar.ONE);
  }

  @Test
  public void testSynthetic() {
    Tensor waypoints = CirclePoints.of(30).multiply(RealScalar.of(10));
    ImageCostFunction imageCostFunction = WaypointDistanceCost.of( //
        waypoints, true, RealScalar.ONE, RealScalar.of(10), new Dimension(120, 100));
    CoordinateBoundingBox range = imageCostFunction.range();
    assertEquals(range.max(), Tensors.vector(12, 10));
    ExactTensorQ.require(range.max());
  }
}
