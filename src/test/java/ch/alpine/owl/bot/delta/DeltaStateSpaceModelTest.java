// code by jph
package ch.alpine.owl.bot.delta;

import ch.alpine.owl.bot.r2.ImageGradientInterpolation;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.region.ImageRegion;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ResourceData;
import junit.framework.TestCase;

public class DeltaStateSpaceModelTest extends TestCase {
  public void testConstructors() {
    Scalar amp = RealScalar.of(-.05);
    Tensor range = Tensors.vector(12.6, 9.1).unmodifiable();
    ImageGradientInterpolation imageGradientInterpolation = //
        ImageGradientInterpolation.nearest(ResourceData.of("/io/delta_uxy.png"), range, amp);
    Tensor obstacleImage = ResourceData.of("/io/delta_free.png"); //
    ImageRegion imageRegion = new ImageRegion(obstacleImage, range, true);
    CatchyTrajectoryRegionQuery.timeInvariant(imageRegion);
    // new DeltaEntity(imageGradientInterpolation, new StateTime(Tensors.vector(10, 3.5), RealScalar.ZERO));
    StateSpaceModel stateSpaceModel = new DeltaStateSpaceModel(imageGradientInterpolation);
    RegionRenders.create(imageRegion);
    DeltaHelper.vectorFieldRender(stateSpaceModel, range, imageRegion, RealScalar.of(0.5));
  }
}
