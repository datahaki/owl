// code by jph
package ch.alpine.sophus.demo.bdn;

import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.crv.d2.Arrowhead;
import ch.alpine.sophus.demo.opt.LogWeightings;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Outer;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

// TODO OWL ALG jToggleArrows does not have effect
/* package */ class Se2DeformationDemo extends AbstractDeformationDemo {
  private static final Tensor ORIGIN = Arrowhead.of(RealScalar.of(0.2));

  Se2DeformationDemo() {
    super(ManifoldDisplays.SE2C_SE2, LogWeightings.coordinates());
    // ---
    timerFrame.geometricComponent.setOffset(300, 500);
    shuffleSnap();
  }

  @Override
  synchronized Tensor shufflePointsSe2(int n) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Distribution distributionp = UniformDistribution.of(-1, 7);
    Distribution distributiona = UniformDistribution.of(-1, 1);
    return Tensors.vector(i -> manifoldDisplay.project( //
        RandomVariate.of(distributionp, 2).append(RandomVariate.of(distributiona))), n);
  }

  @Override
  MovingDomain2D updateMovingDomain2D(Tensor movingOrigin) {
    int res = refinement();
    Tensor dx = Subdivide.of(0, 6, res - 1);
    Tensor dy = Subdivide.of(0, 6, res - 1);
    Tensor domain = Outer.of((cx, cy) -> Tensors.of(cx, cy, RealScalar.ZERO), dx, dy);
    return AveragedMovingDomain2D.of(movingOrigin, operator(movingOrigin), domain);
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
    new Se2DeformationDemo().setVisible(1200, 800);
  }
}
