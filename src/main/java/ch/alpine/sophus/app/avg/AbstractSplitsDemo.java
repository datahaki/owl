// code by jph
package ch.alpine.sophus.app.avg;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Objects;
import java.util.stream.IntStream;

import javax.swing.JCheckBox;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.app.avg.GeometricSymLinkRender.Link;
import ch.alpine.sophus.app.sym.SymLink;
import ch.alpine.sophus.app.sym.SymLinkBuilder;
import ch.alpine.sophus.app.sym.SymLinkImage;
import ch.alpine.sophus.app.sym.SymScalar;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.win.ControlPointsDemo;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/* package */ abstract class AbstractSplitsDemo extends ControlPointsDemo {
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 13);
  private final JCheckBox jCheckBox = new JCheckBox("closest");

  public AbstractSplitsDemo() {
    super(true, ManifoldDisplays.ALL);
    jCheckBox.setSelected(true);
    timerFrame.jToolBar.add(jCheckBox);
  }

  @Override // from RenderInterface
  public synchronized final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor control = getGeodesicControlPoints();
    // ---
    setMidpointIndicated(jCheckBox.isSelected());
    SymScalar symScalar = symScalar(Tensor.of(IntStream.range(0, control.length()).mapToObj(SymScalar::leaf)));
    SymLink symLink = null;
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    if (Objects.nonNull(symScalar)) {
      graphics.drawImage(new SymLinkImage(symScalar, FONT).bufferedImage(), 0, 0, null);
      // ---
      symLink = SymLinkBuilder.of(control, symScalar);
      // ---
      RenderQuality.setQuality(graphics);
      GeometricSymLinkRender geometricSymLinkRender = new GeometricSymLinkRender(manifoldDisplay);
      geometricSymLinkRender.steps = 1;
      Link link = geometricSymLinkRender.new Link(symLink);
      // link.steps=1;
      link.render(geometricLayer, graphics);
      RenderQuality.setDefault(graphics);
    }
    renderControlPoints(geometricLayer, graphics);
    // ---
    if (Objects.nonNull(symLink)) {
      Tensor xya = symLink.getPosition(manifoldDisplay.geodesicInterface());
      renderPoints(manifoldDisplay, Tensors.of(xya), geometricLayer, graphics);
    }
  }

  /** evaluates geodesic average on symbolic leaf sequence
   * 
   * @param vector of length at least 1
   * @return null if computation of geodesic average is not defined for given vector */
  abstract SymScalar symScalar(Tensor vector);
}
