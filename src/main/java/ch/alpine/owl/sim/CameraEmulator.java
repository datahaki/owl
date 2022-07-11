// code by jph
package ch.alpine.owl.sim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.function.Supplier;

import ch.alpine.ascona.util.ren.RenderInterface;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.sophus.hs.r2.Se2Bijection;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;

// TODO OWL ALG several issues:
// 1) frame rate should be handled outside this class!
// 2) localPoints = Tensors.empty(); // make unmodifiable
// 3) exposure should not happen inside drawing
public class CameraEmulator implements RenderInterface {
  private static final Color CLEAR_COLOR = new Color(192, 255, 192);
  // ---
  private final int resolution;
  private final Scalar interval;
  private final Supplier<StateTime> supplier;
  private final TrajectoryRegionQuery raytraceQuery;
  // ---
  private final BufferedImage bufferedImage;
  private final Tensor localPoints;
  // ---
  private Scalar next = null;

  /** @param resolution of image in pixels along width and height
   * @param frameRate
   * @param supplier
   * @param raytraceQuery */
  public CameraEmulator( //
      int resolution, Scalar frameRate, Supplier<StateTime> supplier, TrajectoryRegionQuery raytraceQuery) {
    this.resolution = resolution;
    this.interval = frameRate.reciprocal();
    this.supplier = supplier;
    this.raytraceQuery = raytraceQuery;
    bufferedImage = new BufferedImage(resolution, resolution, BufferedImage.TYPE_INT_ARGB);
    localPoints = StaticHelper.create(resolution).unmodifiable();
  }

  /** @param stateTime from where to expose
   * @return */
  public BufferedImage exposure(StateTime stateTime) {
    if (Objects.isNull(next) || Scalars.lessThan(next, stateTime.time())) {
      next = stateTime.time().add(interval);
      Se2Bijection se2Bijection = new Se2Bijection(stateTime.state());
      TensorUnaryOperator forward = se2Bijection.forward();
      Graphics graphics = bufferedImage.getGraphics();
      graphics.setColor(Color.DARK_GRAY);
      graphics.fillRect(0, 0, resolution, resolution);
      graphics.setColor(CLEAR_COLOR);
      int x = 0;
      int y = 0;
      for (Tensor probe : localPoints) {
        if (!raytraceQuery.test(new StateTime(forward.apply(probe), stateTime.time())))
          graphics.fillRect(resolution - y - 1, resolution - x - 1, 1, 1);
        ++y;
        if (y == resolution) {
          ++x;
          y = 0;
        }
      }
    }
    return bufferedImage;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    BufferedImage bufferedImage = exposure(supplier.get());
    int SCREEN = resolution * 2;
    int OFFSET = 20;
    graphics.setColor(Color.GREEN);
    graphics.drawRect(0, OFFSET, SCREEN + 1, SCREEN + 1);
    graphics.drawImage(bufferedImage, 1, OFFSET + 1, SCREEN, SCREEN, null);
  }
}
