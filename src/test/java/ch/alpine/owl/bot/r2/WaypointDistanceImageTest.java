// code by jph
package ch.alpine.owl.bot.r2;

import java.awt.Dimension;
import java.util.List;
import java.util.stream.Collectors;

import ch.alpine.sophus.lie.se2.Se2Geodesic;
import ch.alpine.sophus.ref.d1.BSpline1CurveSubdivision;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.ResourceData;
import junit.framework.TestCase;

public class WaypointDistanceImageTest extends TestCase {
  public void testSimple() {
    Tensor waypoints = ResourceData.of("/dubilab/waypoints/20180425.csv");
    waypoints = new BSpline1CurveSubdivision(Se2Geodesic.INSTANCE).cyclic(waypoints);
    WaypointDistanceImage waypointDistanceImage = //
        new WaypointDistanceImage(waypoints, true, RealScalar.ONE, RealScalar.of(7.5), new Dimension(640, 640));
    Tensor tensor = waypointDistanceImage.image();
    List<Tensor> list = tensor.flatten(-1).distinct().collect(Collectors.toList());
    assertEquals(list.get(0), RealScalar.ONE);
    assertEquals(list.get(1), RealScalar.ZERO);
    assertEquals(list.size(), 2);
  }
}
