// code by jph
package ch.alpine.sophus.app.bd2;

import java.awt.Graphics2D;
import java.util.List;
import java.util.Objects;

import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.win.RenderInterface;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.sophus.app.lev.LeversRender;
import ch.alpine.sophus.app.opt.LogWeighting;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.tensor.Tensor;

/* package */ abstract class A2ScatteredSetCoordinateDemo extends AbstractExportWeightingDemo {
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  // ---
  private RenderInterface renderInterface;

  public A2ScatteredSetCoordinateDemo(List<LogWeighting> array) {
    super(true, ManifoldDisplays.R2_H2_S2, array);
    // ---
    timerFrame.jToolBar.add(jToggleAxes);
    jToggleHeatmap.setVisible(false);
    jToggleArrows.setVisible(false);
    addMouseRecomputation();
  }

  @Override
  protected final void recompute() {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor sequence = getGeodesicControlPoints();
    renderInterface = manifoldDisplay.dimensions() < sequence.length() //
        ? arrayPlotRender(sequence, refinement(), operator(sequence), magnification())
        : null;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    {
      final Tensor sequence = getGeodesicControlPoints();
      LeversRender leversRender = //
          LeversRender.of(manifoldDisplay(), sequence, null, geometricLayer, graphics);
      leversRender.renderIndexX();
      leversRender.renderIndexP();
    }
    // ---
    if (Objects.isNull(renderInterface))
      recompute();
    if (Objects.nonNull(renderInterface))
      renderInterface.render(geometricLayer, graphics);
  }
}
