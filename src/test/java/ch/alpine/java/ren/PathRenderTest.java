// code by jph
package ch.alpine.java.ren;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.Tensors;

public class PathRenderTest {
  @Test
  public void testFail() {
    PathRender pathRender = new PathRender(Color.BLACK);
    pathRender.setCurve(Tensors.fromString("{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}"), false);
    AssertFail.of(() -> pathRender.render(null, null));
  }
}
