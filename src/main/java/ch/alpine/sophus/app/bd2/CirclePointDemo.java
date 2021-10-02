// code by jph
package ch.alpine.sophus.app.bd2;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.sophus.app.lev.LeversHud;
import ch.alpine.sophus.app.lev.LeversRender;
import ch.alpine.sophus.gbc.AffineCoordinate;
import ch.alpine.sophus.gbc.d2.Barycenter;
import ch.alpine.sophus.gbc.d2.ThreePointCoordinate;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.win.ControlPointsDemo;
import ch.alpine.sophus.math.Genesis;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.PadRight;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.mat.LeastSquares;
import ch.alpine.tensor.nrm.Vector2Norm;

/* package */ class CirclePointDemo extends ControlPointsDemo {
  public CirclePointDemo() {
    super(true, ManifoldDisplays.R2_ONLY);
    Tensor sequence = Tensor.of(CirclePoints.of(7).multiply(RealScalar.of(2)).stream().map(PadRight.zeros(3)));
    setControlPointsSe2(sequence);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    renderControlPoints(geometricLayer, graphics);
    // ---
    Tensor sequence = getGeodesicControlPoints();
    Tensor levers = Tensor.of(sequence.stream().map(Vector2Norm.NORMALIZE));
    {
      LeversRender leversRender = LeversRender.of( //
          manifoldDisplay(), sequence, Array.zeros(2), geometricLayer, graphics);
      leversRender.renderSurfaceP();
    }
    {
      graphics.setColor(Color.LIGHT_GRAY);
      graphics.draw(geometricLayer.toPath2D(CirclePoints.of(31), true));
      LeversRender leversRender = LeversRender.of( //
          manifoldDisplay(), levers, Array.zeros(2), geometricLayer, graphics);
      leversRender.renderSequence();
      if (2 < sequence.length()) {
        // ---
        Genesis genesis = ThreePointCoordinate.of(Barycenter.MEAN_VALUE);
        Tensor weights = genesis.origin(levers);
        leversRender.renderWeights(weights);
      }
    }
    geometricLayer.pushMatrix(GfxMatrix.translation(Tensors.vector(5, 0)));
    {
      graphics.setColor(Color.LIGHT_GRAY);
      graphics.draw(geometricLayer.toPath2D(CirclePoints.of(31), true));
      LeversRender leversRender = LeversRender.of( //
          manifoldDisplay(), levers, Array.zeros(2), geometricLayer, graphics);
      leversRender.renderSequence();
      leversRender.renderInfluenceX(LeversHud.COLOR_DATA_GRADIENT);
      if (2 < sequence.length()) {
        Genesis genesis = AffineCoordinate.INSTANCE;
        Tensor weights = genesis.origin(levers);
        leversRender.renderWeights(weights);
        Tensor lhs = Tensor.of(levers.stream().map(lever -> Append.of(lever, RealScalar.ONE)));
        Tensor rhs = weights;
        Tensor sol = LeastSquares.of(lhs, rhs);
        // System.out.println(sol.map(Chop._12));
        Tensor dir = sol.extract(0, 2);
        graphics.setColor(Color.RED);
        graphics.draw(geometricLayer.toLine2D(dir));
      }
    }
    geometricLayer.popMatrix();
  }

  public static void main(String[] args) {
    new CirclePointDemo().setVisible(1300, 900);
  }
}
