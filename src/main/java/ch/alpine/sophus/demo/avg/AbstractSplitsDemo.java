// code by jph
package ch.alpine.sophus.demo.avg;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Objects;
import java.util.stream.IntStream;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.sophus.demo.avg.GeometricSymLinkRender.Link;
import ch.alpine.sophus.ext.api.ControlPointsDemo;
import ch.alpine.sophus.ext.dis.ManifoldDisplay;
import ch.alpine.sophus.ext.dis.ManifoldDisplays;
import ch.alpine.sophus.sym.SymLink;
import ch.alpine.sophus.sym.SymLinkBuilder;
import ch.alpine.sophus.sym.SymLinkImage;
import ch.alpine.sophus.sym.SymScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public abstract class AbstractSplitsDemo extends ControlPointsDemo {
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 13);
  public Boolean closest = true;

  public AbstractSplitsDemo() {
    super(true, ManifoldDisplays.ALL);
  }

  @Override // from RenderInterface
  public synchronized final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor control = getGeodesicControlPoints();
    // ---
    setMidpointIndicated(closest);
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
      Tensor xya = symLink.getPosition(manifoldDisplay.geodesic());
      renderPoints(manifoldDisplay, Tensors.of(xya), geometricLayer, graphics);
    }
  }

  /** evaluates geodesic average on symbolic leaf sequence
   * 
   * @param vector of length at least 1
   * @return null if computation of geodesic average is not defined for given vector */
  abstract SymScalar symScalar(Tensor vector);
}
