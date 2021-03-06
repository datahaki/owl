// code by jph
package ch.alpine.sophus.gui.ren;

import java.awt.Color;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class PathRenderTest extends TestCase {
  public void testFail() {
    PathRender pathRender = new PathRender(Color.BLACK);
    pathRender.setCurve(Tensors.fromString("{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}"), false);
    AssertFail.of(() -> pathRender.render(null, null));
  }
}
