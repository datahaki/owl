// code by jph
package ch.alpine.owl.lane;

import java.io.IOException;

import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.sophus.crv.d2.PolygonRegion;
import ch.alpine.sophus.hs.r2.Extract2D;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.sophus.ref.d1.LaneRiesenfeldCurveSubdivision;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clips;
import junit.framework.TestCase;

public class LaneRandomSampleTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Tensor se2s = Tensors.fromString("{{0[m], 1[m], 2}, {2[m], 0[m], 4}, {-1[m],-3[m], -2}}");
    LaneInterface laneInterface = StableLanes.of( //
        se2s, //
        LaneRiesenfeldCurveSubdivision.of(ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder(), 1)::cyclic, 3, Quantity.of(0.3, "m"));
    Distribution rotDist = UniformDistribution.of(Clips.absoluteOne());
    RandomSampleInterface randomSampleInterface = Serialization.copy(LaneRandomSample.of(laneInterface, rotDist));
    Tensor tensor = RandomSample.of(randomSampleInterface);
    new PolygonRegion(Tensor.of(laneInterface.rightBoundary().stream().map(Extract2D.FUNCTION))).test(tensor);
    new PolygonRegion(Tensor.of(laneInterface.leftBoundary().stream().map(Extract2D.FUNCTION))).test(tensor);
  }

  public void testNonUnit() throws ClassNotFoundException, IOException {
    Tensor se2s = Tensors.fromString("{{0[], 1[], 2}, {2[], 0[], 4}, {-1[],-3[], -2}}");
    LaneInterface laneInterface = StableLanes.of( //
        se2s, //
        LaneRiesenfeldCurveSubdivision.of(ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder(), 1)::cyclic, 3, Quantity.of(0.3, ""));
    Distribution rotDist = UniformDistribution.of(Clips.absoluteOne());
    RandomSampleInterface randomSampleInterface = Serialization.copy(LaneRandomSample.of(laneInterface, rotDist));
    Tensor tensor = RandomSample.of(randomSampleInterface);
    new PolygonRegion(Tensor.of(laneInterface.rightBoundary().stream().map(Extract2D.FUNCTION))).test(tensor);
    new PolygonRegion(Tensor.of(laneInterface.leftBoundary().stream().map(Extract2D.FUNCTION))).test(tensor);
  }
}
