// code by jph
package ch.alpine.sophus.app.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.Se2Display;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.sophus.opt.LogWeightings;
import ch.alpine.tensor.Tensor;

/* package */ abstract class AbstractHoverDemo extends LogWeightingDemo {
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  final SpinnerLabel<Integer> spinnerCount = new SpinnerLabel<>();
  private final JButton jButtonShuffle = new JButton("shuffle");

  public AbstractHoverDemo() {
    super(false, ManifoldDisplays.SE2C_SE2_S2_H2_R2, LogWeightings.list());
    setMidpointIndicated(false);
    setPositioningEnabled(false);
    addSpinnerListener(v -> shuffle(spinnerCount.getValue()));
    {
      timerFrame.jToolBar.add(jToggleAxes);
    }
    {
      spinnerCount.setList(Arrays.asList(5, 10, 15, 20, 25, 30, 40));
      spinnerCount.setValue(15);
      spinnerCount.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "magnify");
      spinnerCount.addSpinnerListener(this::shuffle);
    }
    {
      jButtonShuffle.addActionListener(e -> shuffle(spinnerCount.getValue()));
      timerFrame.jToolBar.add(jButtonShuffle);
    }
    shuffle(spinnerCount.getValue());
    setGeodesicDisplay(Se2Display.INSTANCE);
    timerFrame.jToolBar.addSeparator();
  }

  void shuffle(int n) {
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    RandomSampleInterface randomSampleInterface = geodesicDisplay.randomSampleInterface();
    setControlPointsSe2(RandomSample.of(randomSampleInterface, n));
  }

  @Override // from RenderInterface
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    Tensor sequence = getGeodesicControlPoints();
    Tensor origin = geodesicDisplay.project(geometricLayer.getMouseSe2State());
    LeversRender leversRender = //
        LeversRender.of(geodesicDisplay, sequence, origin, geometricLayer, graphics);
    render(geometricLayer, graphics, leversRender);
  }

  /** @param geometricLayer
   * @param graphics
   * @param leversRender
   * @param weights */
  abstract void render(GeometricLayer geometricLayer, Graphics2D graphics, LeversRender leversRender);
}
