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
import ch.alpine.owl.bot.se2.rrts.ClothoidTransition;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.owl.math.noise.SimplexContinuousNoise;
import ch.alpine.sophus.app.lev.AbstractPlaceDemo;
import ch.alpine.sophus.clt.Clothoid;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidComparators;
import ch.alpine.sophus.clt.PriorityClothoid;
import ch.alpine.sophus.gds.GeodesicDisplays;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Timing;

/* package */ class ClothoidEvolution extends AbstractPlaceDemo {
  private static final Tensor BETAS = Tensors.fromString("{0.05, 0.1, 0.2, 0.3, 0.4, 0.5}");
  private final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();
  private final JToggleButton jToggleAnimate = new JToggleButton("animate");
  private final Timing timing = Timing.started();

  public ClothoidEvolution() {
    super(true, GeodesicDisplays.CLC_ONLY);
    // ---
    jToggleAnimate.setSelected(true);
    timerFrame.jToolBar.add(jToggleAnimate);
    {
      spinnerBeta.setList(BETAS.stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setValue(RealScalar.of(0.05));
      spinnerBeta.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "beta");
    }
    // ---
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
    // ---
    Tensor ctrl = Tensors.fromString( //
        "{{0.017, 0.017, 0.000}, {1.733, 0.967, 4.712}, {3.933, -0.750, -3.665}, {5.567, 1.717, 3.927}, {7.983, 1.500, 4.451}}");
    setControlPointsSe2(ctrl);
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
    Tensor beg = sequence.get(0);
    ClothoidBuilder clothoidBuilder2 = PriorityClothoid.of(ClothoidComparators.CURVATURE_HEAD);
    double time = jToggleAnimate.isSelected() ? timing.seconds() * 0.2 : 0;
    for (int index = 1; index < sequence.length(); ++index) {
      Tensor end = sequence.get(index);
      Clothoid clothoid = clothoidBuilder2.curve(beg, end);
      ClothoidTransition clothoidTransition = ClothoidTransition.of(beg, end, clothoid);
      graphics.draw(geometricLayer.toPath2D(clothoidTransition.linearized(value)));
      double split = 0.5 + 0.1 * SimplexContinuousNoise.FUNCTION.at(time, index);
      beg = clothoidTransition.clothoid().apply(RealScalar.of(split));
    }
    {
      renderControlPoints(geometricLayer, graphics);
    }
  }

  public static void main(String[] args) {
    new ClothoidEvolution().setVisible(1000, 600);
  }
}
