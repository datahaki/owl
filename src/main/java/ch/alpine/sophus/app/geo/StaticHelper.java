// code by jph
package ch.alpine.sophus.app.geo;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.opt.nd.NdBounds;

enum StaticHelper {
  ;
  public static void draw(NdBounds ndBounds, GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor lc = ndBounds.lBounds.copy();
    Tensor rc = ndBounds.uBounds.copy();
    Tensor l1 = lc.copy();
    l1.set(rc.Get(1), 1);
    Tensor r1 = rc.copy();
    r1.set(lc.Get(1), 1);
    Tensor tensor = Tensors.of( //
        lc, //
        l1, //
        rc, //
        r1);
    Path2D path2d = geometricLayer.toPath2D(tensor, true);
    graphics.draw(path2d);
  }
}
