// code by ynager
package ch.alpine.owl.bot.r2;

import java.awt.Dimension;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.lie.r2.CirclePoints;
import junit.framework.TestCase;

public class WaypointDistanceCostTest extends TestCase {
  public void testSimple() {
    Tensor waypoints = ResourceData.of("/dubilab/waypoints/20180425.csv");
    ImageCostFunction imageCostFunction = WaypointDistanceCost.of( //
        waypoints, true, RealScalar.ONE, RealScalar.of(7.5), new Dimension(640, 640));
    for (Tensor waypoint : waypoints)
      assertEquals(imageCostFunction.flipYXTensorInterp.at(waypoint), RealScalar.ZERO);
    assertEquals(imageCostFunction.flipYXTensorInterp.at(Tensors.vector(10, 10)), RealScalar.ONE);
  }

  public void testSynthetic() {
    Tensor waypoints = CirclePoints.of(30).multiply(RealScalar.of(10));
    ImageCostFunction imageCostFunction = WaypointDistanceCost.of( //
        waypoints, true, RealScalar.ONE, RealScalar.of(10), new Dimension(120, 100));
    Tensor range = imageCostFunction.range();
    assertEquals(range, Tensors.vector(12, 10));
    ExactTensorQ.require(range);
  }
}
