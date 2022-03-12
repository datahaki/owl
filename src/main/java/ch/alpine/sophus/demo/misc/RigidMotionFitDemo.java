// code by jph
package ch.alpine.sophus.demo.misc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.AxesRender;
import ch.alpine.java.ren.PathRender;
import ch.alpine.java.ren.PointsRender;
import ch.alpine.javax.swing.SpinnerLabel;
import ch.alpine.sophus.api.GroupElement;
import ch.alpine.sophus.ext.api.ControlPointsDemo;
import ch.alpine.sophus.ext.dis.ManifoldDisplays;
import ch.alpine.sophus.ext.dis.R2Display;
import ch.alpine.sophus.ext.dis.Se2Display;
import ch.alpine.sophus.hs.r2.Se2Bijection;
import ch.alpine.sophus.hs.r2.Se2RigidMotionFit;
import ch.alpine.sophus.lie.se2c.Se2CoveringGeodesic;
import ch.alpine.sophus.lie.se2c.Se2CoveringGroup;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Clips;

/* package */ class RigidMotionFitDemo extends ControlPointsDemo {
  private static final Tensor ORIGIN = CirclePoints.of(3).multiply(RealScalar.of(0.2));
  private static final PointsRender POINTS_RENDER_RESULT = //
      new PointsRender(new Color(128, 128, 255, 64), new Color(128, 128, 255, 255));
  private static final PointsRender POINTS_RENDER_POINTS = //
      new PointsRender(new Color(64, 255, 64, 64), new Color(64, 255, 64, 255));
  // ---
  private final SpinnerLabel<Integer> spinnerLength = new SpinnerLabel<>();
  private Tensor points;

  public RigidMotionFitDemo() {
    super(false, ManifoldDisplays.R2_ONLY);
    setMidpointIndicated(false);
    // ---
    spinnerLength.addSpinnerListener(this::shufflePoints);
    spinnerLength.setList(Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10));
    spinnerLength.setValue(5);
    spinnerLength.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    shufflePoints(spinnerLength.getValue());
  }

  private synchronized void shufflePoints(int n) {
    Distribution distribution = NormalDistribution.of(0, 2);
    points = RandomVariate.of(distribution, n, 2);
    Tensor xya = RandomVariate.of(distribution, 3);
    setControlPointsSe2(Tensor.of(points.stream() //
        .map(new Se2Bijection(xya).forward()) //
        .map(row -> row.append(RealScalar.ZERO))));
  }

  @Override // from RenderInterface
  public synchronized void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    {
      Tensor target = Tensor.of(getGeodesicControlPoints().stream().map(R2Display.INSTANCE::project));
      Tensor solve = Se2RigidMotionFit.of(points, target);
      POINTS_RENDER_RESULT //
          .show(Se2Display.INSTANCE::matrixLift, Se2Display.INSTANCE.shape(), Tensors.of(solve)) //
          .render(geometricLayer, graphics);
      {
        Tensor domain = Subdivide.increasing(Clips.unit(), 10);
        GroupElement groupElement = Se2CoveringGroup.INSTANCE.element(solve);
        for (Tensor p : points) {
          Tensor xya_0 = Append.of(p, RealScalar.ZERO);
          Tensor xya_1 = groupElement.combine(xya_0);
          ScalarTensorFunction scalarTensorFunction = Se2CoveringGeodesic.INSTANCE.curve(xya_0, xya_1);
          Tensor tensor = domain.map(scalarTensorFunction);
          new PathRender(Color.CYAN, 1.5f).setCurve(tensor, false).render(geometricLayer, graphics);
        }
      }
      graphics.setColor(Color.RED);
      for (int index = 0; index < points.length(); ++index)
        graphics.draw(geometricLayer.toLine2D(points.get(index), target.get(index)));
    }
    renderControlPoints(geometricLayer, graphics);
    POINTS_RENDER_POINTS //
        .show(R2Display.INSTANCE::matrixLift, ORIGIN, points) //
        .render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new RigidMotionFitDemo().setVisible(1000, 800);
  }
}
