// code by jph
package ch.alpine.owl.lane;

import java.io.IOException;

import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.sophus.crv.d2.PolygonRegion;
import ch.alpine.sophus.math.Extract2D;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.sophus.ref.d1.LaneRiesenfeldCurveSubdivision;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clips;
import junit.framework.TestCase;

public class LaneRandomSampleTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    LaneInterface laneInterface = StableLanes.of( //
        Tensors.fromString("{{0[m], 1[m], 2}, {2[m], 0[m], 4}, {-1[m],-3[m], -2}}"), //
        LaneRiesenfeldCurveSubdivision.of(ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder(), 1)::cyclic, 3, Quantity.of(0.3, "m"));
    Distribution rotDist = UniformDistribution.of(Clips.absoluteOne());
    RandomSampleInterface randomSampleInterface = Serialization.copy(LaneRandomSample.of(laneInterface, rotDist));
    Tensor tensor = RandomSample.of(randomSampleInterface);
    new PolygonRegion(Tensor.of(laneInterface.rightBoundary().stream().map(Extract2D.FUNCTION))).test(tensor);
    new PolygonRegion(Tensor.of(laneInterface.leftBoundary().stream().map(Extract2D.FUNCTION))).test(tensor);
  }
}
