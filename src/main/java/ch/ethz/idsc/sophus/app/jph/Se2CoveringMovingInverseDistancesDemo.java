// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Dimension;
import java.util.Arrays;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ class Se2CoveringMovingInverseDistancesDemo extends MovingInverseDistancesDemo {
  private static final Tensor ORIGIN = Arrowhead.of(RealScalar.of(0.2));
  private final SpinnerLabel<Integer> spinnerLength = new SpinnerLabel<>();

  Se2CoveringMovingInverseDistancesDemo() {
    super(GeodesicDisplays.SE2C_ONLY, Se2CoveringBarycentricCoordinates.values());
    // ---
    {
      spinnerLength.addSpinnerListener(this::shufflePoints);
      spinnerLength.setList(Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10));
      spinnerLength.setValue(8);
      spinnerLength.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "number of points");
    }
    shufflePoints(spinnerLength.getValue());
    timerFrame.configCoordinateOffset(300, 500);
  }

  private synchronized void shufflePoints(int n) {
    Distribution distributionp = UniformDistribution.of(-1, 7);
    Distribution distributiona = UniformDistribution.of(-1, 1);
    updateOrigin(Tensors.vector(i -> RandomVariate.of(distributionp, 2).append(RandomVariate.of(distributiona)), n));
  }

  @Override
  void updateOrigin(Tensor originSe2) {
    setControlPointsSe2(originSe2);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor origin = Tensor.of(originSe2.stream().map(geodesicDisplay::project));
    int res = refinement();
    Tensor dx = Subdivide.of(0, 6, res - 1);
    Tensor dy = Subdivide.of(0, 6, res - 1);
    Tensor domain = Tensors.matrix((cx, cy) -> Tensors.of(dx.get(cx), dy.get(cy), RealScalar.ZERO), dx.length(), dy.length());
    movingDomain2D = new MovingDomain2D(origin, barycentricCoordinate(), domain);
  }

  @Override
  Tensor shapeOrigin() {
    return ORIGIN;
  }

  @Override
  BiinvariantMean biinvariantMean() {
    return geodesicDisplay().biinvariantMean();
  }

  public static void main(String[] args) {
    new Se2CoveringMovingInverseDistancesDemo().setVisible(1000, 800);
  }
}
