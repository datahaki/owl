// code by jph
package ch.alpine.owl.bot.delta;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.r2.ImageGradientInterpolation;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owlets.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.sophis.reg.ImageRegion;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.qty.Quantity;

class DeltaStateSpaceModelTest {
  @Test
  void testConstructors() {
    Scalar amp = Quantity.of(-.05, "s^-1");
    Tensor range = Tensors.vector(12.6, 9.1).unmodifiable();
    ImageGradientInterpolation imageGradientInterpolation = //
        ImageGradientInterpolation.nearest(Import.of("/io/delta_uxy.png"), range, amp);
    Tensor obstacleImage = Import.of("/io/delta_free.png"); //
    ImageRegion imageRegion = new ImageRegion(obstacleImage, range, true);
    CatchyTrajectoryRegionQuery.timeInvariant(imageRegion);
    // new DeltaEntity(imageGradientInterpolation, new StateTime(Tensors.vector(10, 3.5), RealScalar.ZERO));
    StateSpaceModel stateSpaceModel = new DeltaStateSpaceModel(imageGradientInterpolation);
    RegionRenderFactory.create(imageRegion);
    Tensor fallback_u = Tensors.fromString("{0[s^-1], 0[s^-1]}");
    StaticHelper.vectorFieldRender(stateSpaceModel, range, imageRegion, fallback_u, Quantity.of(0.5, "s"));
  }
}
