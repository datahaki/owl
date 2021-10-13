// code by jph
package ch.alpine.sophus.demo.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JTextField;
import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.java.ren.AxesRender;
import ch.alpine.sophus.demo.opt.LogWeightings;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gds.Se2Display;
import ch.alpine.sophus.hs.HsDesign;
import ch.alpine.sophus.hs.VectorLogManifold;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.sophus.lie.LieGroupOps;
import ch.alpine.sophus.math.TensorMapping;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.gr.InfluenceMatrix;

/* package */ class Se2CoveringInvarianceDemo extends LogWeightingDemo {
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  private final JTextField jTextField = new JTextField();

  public Se2CoveringInvarianceDemo() {
    super(true, ManifoldDisplays.SE2C_SE2, LogWeightings.list());
    {
      timerFrame.jToolBar.add(jToggleAxes);
    }
    {
      jTextField.setText("{1, 1, 3}");
      jTextField.setPreferredSize(new Dimension(100, 28));
      timerFrame.jToolBar.add(jTextField);
    }
    setGeodesicDisplay(Se2Display.INSTANCE);
    setControlPointsSe2(Tensors.fromString( //
        "{{0, 0, 0}, {3, -2, -1}, {4, 2, 1}, {-1, 3, 2}, {-2, -3, -2}, {-3, 0, 0}}"));
    setControlPointsSe2(Tensors.fromString( //
        "{{0.000, 0.000, 0.000}, {-0.950, 1.750, -2.618}, {0.833, 2.300, -1.047}, {2.667, 0.733, -2.618}, {2.033, -1.800, 2.356}, {-1.217, -0.633, -3.665}}"));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    LieGroup lieGroup = manifoldDisplay.lieGroup();
    Tensor controlPointsAll = getGeodesicControlPoints();
    LieGroupOps lieGroupOps = new LieGroupOps(lieGroup);
    if (0 < controlPointsAll.length()) {
      VectorLogManifold vectorLogManifold = manifoldDisplay.hsManifold();
      {
        Tensor sequence = controlPointsAll.extract(1, controlPointsAll.length());
        Tensor origin = controlPointsAll.get(0);
        Tensor matrix = new HsDesign(vectorLogManifold).matrix(sequence, origin);
        Tensor weights = InfluenceMatrix.of(matrix).leverages_sqrt();
        LeversRender leversRender = //
            LeversRender.of(manifoldDisplay, sequence, origin, geometricLayer, graphics);
        leversRender.renderSequence();
        leversRender.renderLevers();
        leversRender.renderWeights(weights);
        leversRender.renderInfluenceX(LeversHud.COLOR_DATA_GRADIENT);
        leversRender.renderOrigin();
        leversRender.renderIndexX();
        leversRender.renderIndexP();
      }
      geometricLayer.pushMatrix(GfxMatrix.translation(Tensors.vector(10, 0)));
      try {
        TensorMapping lieGroupOR = lieGroupOps.actionR(Tensors.fromString(jTextField.getText()));
        Tensor allR = lieGroupOR.slash(controlPointsAll);
        TensorMapping lieGroupOp = lieGroupOps.actionL(lieGroup.element(allR.get(0)).inverse().toCoordinate());
        Tensor result = lieGroupOp.slash(allR);
        Tensor sequence = result.extract(1, result.length());
        Tensor origin = result.get(0);
        Tensor matrix = new HsDesign(vectorLogManifold).matrix(sequence, origin);
        Tensor weights = InfluenceMatrix.of(matrix).leverages_sqrt();
        LeversRender leversRender = //
            LeversRender.of(manifoldDisplay, sequence, origin, geometricLayer, graphics);
        leversRender.renderSequence();
        leversRender.renderLevers();
        leversRender.renderWeights(weights);
        leversRender.renderInfluenceX(LeversHud.COLOR_DATA_GRADIENT);
        leversRender.renderOrigin();
        leversRender.renderIndexX("x'");
        leversRender.renderIndexP("p'");
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      geometricLayer.popMatrix();
    }
  }

  public static void main(String[] args) {
    new Se2CoveringInvarianceDemo().setVisible(1200, 600);
  }
}
