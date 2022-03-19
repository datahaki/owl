// code by jph
package ch.alpine.sophus.demo.bdn;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JToggleButton;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.RenderInterface;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.ext.api.Box2D;
import ch.alpine.sophus.ext.api.MixedLogWeightings;
import ch.alpine.sophus.ext.dis.ManifoldDisplays;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Outer;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

/** moving least squares */
/* package */ class R2DeformationDemo extends AbstractDeformationDemo {
  private static final double EXTENT = 5.0;
  private static final Tensor ORIGIN = CirclePoints.of(3).multiply(RealScalar.of(0.1));
  // ---
  private final JToggleButton jToggleRigidMotionFit = new JToggleButton("MLS");
  private final RenderInterface renderInterface = new RenderInterface() {
    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      Tensor box = Box2D.SQUARE.multiply(RealScalar.of(EXTENT));
      Path2D path2d = geometricLayer.toPath2D(box, true);
      graphics.setColor(Color.LIGHT_GRAY);
      graphics.draw(path2d);
    }
  };

  R2DeformationDemo() {
    super(ManifoldDisplays.R2_ONLY, MixedLogWeightings.scattered());
    // ---
    {
      jToggleRigidMotionFit.addActionListener(l -> recompute());
      timerFrame.jToolBar.add(jToggleRigidMotionFit);
    }
    timerFrame.geometricComponent.setOffset(300, 500);
    timerFrame.geometricComponent.addRenderInterfaceBackground(renderInterface);
    setControlPointsSe2(Tensors.fromString( //
        "{{0.650, 4.183, 0.000}, {3.517, 4.650, 0.000}, {2.233, 2.733, 0.000}, {4.217, 2.917, 0.000}, {1.767, 1.150, 0.000}, {0.600, 0.317, 0.000}, {4.450, 0.550, 0.000}}"));
    // deformed to:
    // "{{1.400, 4.067, 0.000}, {2.867, 4.167, 0.000}, {1.667, 2.283, 0.000}, {3.983, 2.283, 0.000}, {2.617, 1.200, 0.000}, {0.600, 0.350, 0.000}, {3.917,
    // 1.183, 0.000}}"
    snap();
  }

  @Override
  synchronized Tensor shufflePointsSe2(int n) {
    Distribution distribution = UniformDistribution.of(0, EXTENT);
    return Tensor.of(RandomVariate.of(distribution, n, 2).stream() //
        .map(Tensor::copy) //
        .map(row -> row.append(RealScalar.ZERO)));
  }

  @Override
  MovingDomain2D updateMovingDomain2D(Tensor movingOrigin) {
    int res = refinement();
    // TODO OWL ALG meshgrid functionality is already(?)/should be generalized
    Tensor dx = Subdivide.of(0.0, EXTENT, res - 1);
    Tensor dy = Subdivide.of(0.0, EXTENT, res - 3);
    Tensor domain = Outer.of(Tensors::of, dx, dy);
    TensorUnaryOperator tensorUnaryOperator = operator(movingOrigin);
    return jToggleRigidMotionFit.isSelected() //
        ? RnFittedMovingDomain2D.of(movingOrigin, tensorUnaryOperator, domain)
        : AveragedMovingDomain2D.of(movingOrigin, tensorUnaryOperator, domain);
  }

  @Override
  Tensor shapeOrigin() {
    return ORIGIN;
  }

  @Override
  BiinvariantMean biinvariantMean() {
    return manifoldDisplay().biinvariantMean();
  }

  public static void main(String[] args) {
    new R2DeformationDemo().setVisible(1300, 800);
  }
}
