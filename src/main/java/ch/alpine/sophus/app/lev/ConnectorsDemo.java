// code by jph
package ch.alpine.sophus.app.lev;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.itp.Interpolation;
import ch.alpine.tensor.itp.LinearInterpolation;

/* package */ class ConnectorsDemo extends AbstractHoverDemo {
  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics, LeversRender leversRender) {
    Tensor weights = operator(leversRender.getSequence()).apply(leversRender.getOrigin());
    leversRender.renderLevers(weights);
    // ---
    leversRender.renderWeights(weights);
    leversRender.renderSequence();
    leversRender.renderOrigin();
    // ---
    Tensor controlPoints = leversRender.getSequence();
    int length = controlPoints.length();
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    graphics.setColor(Color.RED);
    for (int index = 0; index < length; ++index) {
      Tensor blend = UnitVector.of(length, index);
      Interpolation interpolation = LinearInterpolation.of(Tensors.of(weights, blend));
      Tensor map = Tensor.of(Subdivide.of(0.0, 1.0, 20).stream() //
          .map(Scalar.class::cast) //
          .map(interpolation::at) //
          .map(w -> biinvariantMean.mean(controlPoints, w)) //
          .map(geodesicDisplay::toPoint));
      Path2D path2d = geometricLayer.toPath2D(map);
      graphics.draw(path2d);
      // Tensor tensor = weights.get(index);
    }
  }

  public static void main(String[] args) {
    new ConnectorsDemo().setVisible(1200, 900);
  }
}
