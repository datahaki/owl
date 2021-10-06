// code by jph
package ch.alpine.sophus.demo.clt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldSelection;
import ch.alpine.java.ref.gui.ToolbarFieldsEditor;
import ch.alpine.owl.bot.se2.rrts.ClothoidTransition;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.demo.ControlPointsDemo;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;

public class ClothoidDesign extends ControlPointsDemo {
  public Boolean ctrl = true;
  @FieldSelection(array = { "0.05", "0.1", "0.2", "0.3", "0.4", "0.5" })
  public Scalar beta = RealScalar.of(0.5);

  public ClothoidDesign() {
    super(true, ManifoldDisplays.CL_ONLY);
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
    // ---
    setControlPointsSe2(RandomVariate.of(UniformDistribution.of(0, 10), 3 * 4, 3));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor sequence = getGeodesicControlPoints();
    graphics.setColor(Color.BLUE);
    graphics.setStroke(new BasicStroke(2));
    Scalar value = beta;
    Geodesic geodesic = manifoldDisplay().geodesic();
    ClothoidBuilder clothoidBuilder = (ClothoidBuilder) geodesic;
    for (int index = 0; index < sequence.length() - 2; index += 3) {
      Tensor cr = sequence.get(index + 0);
      Tensor l1 = sequence.get(index + 1);
      Tensor l2 = sequence.get(index + 2);
      graphics.draw(geometricLayer.toPath2D(ClothoidTransition.of(clothoidBuilder, cr, l1).linearized(value)));
      graphics.draw(geometricLayer.toPath2D(ClothoidTransition.of(clothoidBuilder, cr, l2).linearized(value)));
    }
    if (ctrl)
      renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new ClothoidDesign().setVisible(1000, 600);
  }
}
