// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.awt.Color;

import ch.alpine.sophis.crv.d2.ex.Arrowhead;
import ch.alpine.tensor.Tensor;

abstract class GokartDemo extends Se2Demo {
  static final Tensor ARROWHEAD = Arrowhead.of(0.6);
  static final Color COLOR_WAYPOINT = new Color(64, 192, 64, 64);
}
