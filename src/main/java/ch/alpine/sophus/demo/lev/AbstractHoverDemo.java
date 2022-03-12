// code by jph
package ch.alpine.sophus.demo.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.AxesRender;
import ch.alpine.javax.swing.SpinnerLabel;
import ch.alpine.sophus.ext.api.LogWeightings;
import ch.alpine.sophus.ext.dis.ManifoldDisplay;
import ch.alpine.sophus.ext.dis.ManifoldDisplays;
import ch.alpine.sophus.ext.dis.Se2Display;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.Tensor;

/* package */ abstract class AbstractHoverDemo extends LogWeightingDemo {
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  final SpinnerLabel<Integer> spinnerCount = new SpinnerLabel<>();
  private final JButton jButtonShuffle = new JButton("shuffle");

  public AbstractHoverDemo() {
    super(false, ManifoldDisplays.SE2C_SE2_S2_R2, LogWeightings.list());
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
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    RandomSampleInterface randomSampleInterface = manifoldDisplay.randomSampleInterface();
    setControlPointsSe2(RandomSample.of(randomSampleInterface, n));
  }

  @Override // from RenderInterface
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor mouse = timerFrame.geometricComponent.getMouseSe2CState();
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor sequence = getGeodesicControlPoints();
    Tensor origin = manifoldDisplay.project(mouse);
    LeversRender leversRender = //
        LeversRender.of(manifoldDisplay, sequence, origin, geometricLayer, graphics);
    render(geometricLayer, graphics, leversRender);
  }

  /** @param geometricLayer
   * @param graphics
   * @param leversRender
   * @param weights */
  abstract void render(GeometricLayer geometricLayer, Graphics2D graphics, LeversRender leversRender);
}
