// code by jph
package ch.alpine.owl.bot.se2;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.owl.math.region.EllipsoidRegion;
import ch.alpine.owl.math.region.Region;
import ch.alpine.owl.math.region.Regions;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.red.ScalarSummaryStatistics;
import junit.framework.TestCase;

public class Se2PointsVsRegionsTest extends TestCase {
  public void testSimple() {
    Region<Tensor> region = Se2PointsVsRegions.line(Tensors.vector(-2, 1, 0, 5), Regions.emptyRegion());
    assertFalse(region.isMember(Tensors.vector(1, 2, 3, 4))); // interpretation as xya
  }

  public void testFail() {
    AssertFail.of(() -> Se2PointsVsRegions.line(Tensors.vector(-2, 1, 0, 5), null));
  }

  public void testFootprint() {
    Tensor SHAPE = ResourceData.of("/gokart/footprint/20171201.csv");
    ScalarSummaryStatistics scalarSummaryStatistics = //
        SHAPE.stream().map(tensor -> tensor.Get(0)).collect(ScalarSummaryStatistics.collector());
    Tensor x_coords = Subdivide.increasing(scalarSummaryStatistics.getClip(), 3);
    Tensor center = Tensors.vector(2, 0);
    Region<Tensor> region = new EllipsoidRegion(center, Tensors.vector(1, 1));
    Region<Tensor> query = Se2PointsVsRegions.line(x_coords, region);
    assertTrue(query.isMember(Tensors.vector(0, 0, 0)));
    assertFalse(query.isMember(Tensors.vector(0, 0, 3.14 / 2)));
    assertFalse(query.isMember(Tensors.vector(0, 0, 3.14)));
    assertFalse(query.isMember(Tensors.vector(0, 0, 3 * 3.14 / 2)));
  }
}
