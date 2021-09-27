// code by jph
package ch.alpine.owl.gui.win;

import java.awt.geom.Point2D;

/* package */ enum PointUtil {
  ;
  public static double inftyNorm(Point2D p1, Point2D p2) {
    return Math.max(Math.abs(p1.getX() - p2.getX()), Math.abs(p1.getY() - p2.getY()));
  }
}
