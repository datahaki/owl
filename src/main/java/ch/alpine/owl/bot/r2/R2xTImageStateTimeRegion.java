// code by jph
package ch.alpine.owl.bot.r2;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.function.Supplier;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.win.RenderInterface;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.gui.region.ImageRender;
import ch.alpine.owl.math.region.ImageRegion;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.hs.r2.R2RigidFamily;
import ch.alpine.sophus.math.Extract2D;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** for images only rigid transformations are allowed */
public class R2xTImageStateTimeRegion implements Region<StateTime>, RenderInterface, Serializable {
  // ---
  private final ImageRegion imageRegion;
  private final R2RigidFamily rigidFamily;
  private final Supplier<Scalar> supplier;
  /** image render */
  private final RenderInterface renderInterface;

  /** @param imageRegion
   * @param rigidFamily
   * @param supplier */
  public R2xTImageStateTimeRegion(ImageRegion imageRegion, R2RigidFamily rigidFamily, Supplier<Scalar> supplier) {
    this.imageRegion = imageRegion;
    this.rigidFamily = rigidFamily;
    this.supplier = supplier;
    renderInterface = ImageRender.scale(RegionRenders.image(imageRegion.image()), imageRegion.scale());
  }

  @Override // from Region
  public boolean test(StateTime stateTime) {
    Tensor state = Extract2D.FUNCTION.apply(stateTime.state());
    Scalar time = stateTime.time();
    TensorUnaryOperator rev = rigidFamily.inverse(time);
    return imageRegion.test(rev.apply(state));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Scalar time = supplier.get();
    Tensor matrix = rigidFamily.forward_se2(time);
    geometricLayer.pushMatrix(matrix);
    renderInterface.render(geometricLayer, graphics);
    geometricLayer.popMatrix();
  }
}
