// code by jph
package ch.alpine.sophus.demo.curve;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;
import ch.alpine.sophus.sym.SymLinkImage;
import ch.alpine.sophus.sym.SymLinkImages;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

class GeodesicBSplineFunctionDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new GeodesicBSplineFunctionDemo());
  }

  @Test
  public void testDeBoorRational() {
    Scalar parameter = RationalScalar.of(9, 4);
    SymLinkImage symLinkImage = SymLinkImages.symLinkImageGBSF(4, 20, parameter);
    BufferedImage bufferedImage = symLinkImage.bufferedImage();
    assertTrue(300 < bufferedImage.getWidth());
    assertTrue(200 < bufferedImage.getHeight());
  }

  @Test
  public void testDeBoorDecimal() {
    Scalar parameter = RealScalar.of(5.1);
    SymLinkImage symLinkImage = SymLinkImages.symLinkImageGBSF(5, 20, parameter);
    BufferedImage bufferedImage = symLinkImage.bufferedImage();
    assertTrue(300 < bufferedImage.getWidth());
    assertTrue(200 < bufferedImage.getHeight());
  }
}
