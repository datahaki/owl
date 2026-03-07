package ch.alpine.owl.bot.rn.glc;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Optional;

import ch.alpine.ascony.api.Meshgrid;
import ch.alpine.bridge.fig.DensityPlot;
import ch.alpine.bridge.fig.Show;
import ch.alpine.bridge.fig.Showable;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.sophis.api.SignedDistanceFunction;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;

public class SignedDistRender implements RenderInterface {
  private final CoordinateBoundingBox cbb;
  private final Showable showable;

  public SignedDistRender(CoordinateBoundingBox cbb, int res, SignedDistanceFunction<Tensor> sdf) {
    Tensor matrix = new Meshgrid(cbb).image(res, sdf::signedDistance);
    this.cbb = cbb;
    showable = DensityPlot.of(matrix, cbb, ColorDataGradients.PARULA.deriveWithOpacity(RealScalar.of(0.4)));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Optional<Rectangle> optional = geometricLayer.toRectangle(cbb);
    if (optional.isPresent()) {
      Show show = new Show();
      show.add(showable);
      show.render(graphics, optional.orElseThrow());
    }
  }
}
