// code by jph and jl
package ch.alpine.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Set;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.RenderInterface;
import ch.alpine.sophus.math.Extract2D;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.red.Times;

/** keys are projected to the plane */
public class DomainRender implements RenderInterface {
  private static final Color INTERIOR = new Color(192, 192, 192, 64);
  private static final Color BOUNDARY = Color.WHITE;

  public static RenderInterface of(Set<Tensor> map, Tensor eta) {
    return new DomainRender(Tensor.of(map.stream().map(Extract2D.FUNCTION).distinct()), eta);
  }

  // ---
  private final Tensor keys;
  private final Tensor eta_invert;
  private final Tensor ratios;

  DomainRender(Tensor keys, Tensor eta) {
    this.keys = keys;
    eta_invert = Extract2D.FUNCTION.apply(eta).map(Scalar::reciprocal);
    int lo = 0;
    int hi = 1;
    ratios = Tensors.of( //
        Times.of(eta_invert, Tensors.vector(lo, lo)), //
        Times.of(eta_invert, Tensors.vector(hi, lo)), //
        Times.of(eta_invert, Tensors.vector(hi, hi)), //
        Times.of(eta_invert, Tensors.vector(lo, hi)));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    for (Tensor key : keys) {
      Tensor x = Times.of(key, eta_invert);
      Path2D path2d = geometricLayer.toPath2D(Tensor.of(ratios.stream().map(x::add)));
      path2d.closePath();
      graphics.setColor(color(key));
      graphics.fill(path2d);
      graphics.setColor(BOUNDARY);
      graphics.draw(path2d);
    }
  }

  public Color color(Tensor key) {
    return INTERIOR;
  }
}
