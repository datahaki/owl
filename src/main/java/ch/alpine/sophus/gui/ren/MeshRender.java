// code by jph
package ch.alpine.sophus.gui.ren;

import java.awt.Graphics2D;
import java.util.Objects;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.win.RenderInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.img.ColorDataGradient;
import ch.alpine.tensor.img.ColorFormat;

public class MeshRender implements RenderInterface {
  private final Tensor[][] array;
  private final ColorDataGradient colorDataGradient;

  public MeshRender(Tensor[][] array, ColorDataGradient colorDataGradient) {
    this.array = Objects.requireNonNull(array);
    this.colorDataGradient = colorDataGradient;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    for (int i0 = 1; i0 < array.length; ++i0)
      for (int i1 = 1; i1 < array[i0].length; ++i1) {
        Tensor po = array[i0][i1];
        Tensor p0 = array[i0 - 1][i1];
        Tensor p1 = array[i0][i1 - 1];
        Tensor pd = array[i0 - 1][i1 - 1];
        {
          Scalar shading = QuadShading.ANGLE.map(po, p0, p1, pd);
          graphics.setColor(ColorFormat.toColor(colorDataGradient.apply(shading)));
          // graphics.fill(geometricLayer.toPath2D(Unprotect.byRef(po, p0, pd, p1)));
        }
        graphics.draw(geometricLayer.toPath2D(Unprotect.byRef(p0, po)));
        graphics.draw(geometricLayer.toPath2D(Unprotect.byRef(p1, po)));
      }
  }
}
