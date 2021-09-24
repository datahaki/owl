// code by jph
package ch.alpine.sophus.app.geo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.opt.nd.NdBox;

enum StaticHelper {
  ;
  public static void draw(NdBox ndBox, GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor lc = ndBox.min();
    Tensor rc = ndBox.max();
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
    graphics.setColor(new Color(0, 128, 0, 16));
    graphics.fill(path2d);
    graphics.setColor(new Color(128, 128, 128, 64));
    graphics.draw(path2d);
  }

  public static void draw(Tensor location, GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(GfxMatrix.translation(location));
    Point2D point2d = geometricLayer.toPoint2D(Tensors.vector(0, 0));
    graphics.setColor(new Color(0, 255, 0, 128));
    graphics.fillRect((int) point2d.getX() - 1, (int) point2d.getY() - 1, 4, 4);
    geometricLayer.popMatrix();
  }
}
