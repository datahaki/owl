// code by jph
package ch.alpine.sophus.app.clt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.stream.Collectors;

import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.owl.bot.se2.rrts.ClothoidTransition;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.win.ControlPointsDemo;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;

/* package */ class ClothoidDesign extends ControlPointsDemo {
  private static final Tensor BETAS = Tensors.fromString("{0.05, 0.1, 0.2, 0.3, 0.4, 0.5}");
  private final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();
  private final JToggleButton jToggleCtrl = new JToggleButton("ctrl");

  public ClothoidDesign() {
    super(true, ManifoldDisplays.CL_ONLY);
    // ---
    jToggleCtrl.setSelected(true);
    timerFrame.jToolBar.add(jToggleCtrl);
    {
      spinnerBeta.setList(BETAS.stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setValue(RealScalar.of(0.5));
      spinnerBeta.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "beta");
    }
    // ---
    setControlPointsSe2(RandomVariate.of(UniformDistribution.of(0, 10), 3 * 4, 3));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor sequence = getGeodesicControlPoints();
    graphics.setColor(Color.BLUE);
    graphics.setStroke(new BasicStroke(2));
    Scalar value = spinnerBeta.getValue();
    Geodesic geodesicInterface = manifoldDisplay().geodesicInterface();
    ClothoidBuilder clothoidBuilder = (ClothoidBuilder) geodesicInterface;
    for (int index = 0; index < sequence.length() - 2; index += 3) {
      Tensor cr = sequence.get(index + 0);
      Tensor l1 = sequence.get(index + 1);
      Tensor l2 = sequence.get(index + 2);
      graphics.draw(geometricLayer.toPath2D(ClothoidTransition.of(clothoidBuilder, cr, l1).linearized(value)));
      graphics.draw(geometricLayer.toPath2D(ClothoidTransition.of(clothoidBuilder, cr, l2).linearized(value)));
    }
    if (jToggleCtrl.isSelected())
      renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new ClothoidDesign().setVisible(1000, 600);
  }
}
