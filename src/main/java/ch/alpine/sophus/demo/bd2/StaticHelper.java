// code by jph
package ch.alpine.sophus.demo.bd2;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.java.ren.ArrayPlotRender;
import ch.alpine.sophus.ext.api.ImageReshape;
import ch.alpine.sophus.ext.arp.HsArrayPlot;
import ch.alpine.sophus.ext.dis.GeodesicDisplayRender;
import ch.alpine.sophus.ext.dis.ManifoldDisplay;
import ch.alpine.sophus.ext.dis.S2Display;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Rescale;
import ch.alpine.tensor.img.ColorDataGradient;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.sca.Clip;

/* package */ enum StaticHelper {
  ;
  public static BufferedImage fuseImages(ManifoldDisplay manifoldDisplay, ArrayPlotRender arrayPlotRender, int refinement, int sequence_length) {
    HsArrayPlot geodesicArrayPlot = manifoldDisplay.geodesicArrayPlot();
    BufferedImage foreground = arrayPlotRender.export();
    BufferedImage background = new BufferedImage(foreground.getWidth(), foreground.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = background.createGraphics();
    if (manifoldDisplay instanceof S2Display) {
      Tensor matrix = geodesicArrayPlot.pixel2model(new Dimension(refinement, refinement));
      GeometricLayer geometricLayer = new GeometricLayer(Inverse.of(matrix));
      for (int count = 0; count < sequence_length; ++count) {
        GeodesicDisplayRender.render_s2(geometricLayer, graphics);
        geometricLayer.pushMatrix(GfxMatrix.translation(Tensors.vector(2, 0)));
      }
    }
    graphics.drawImage(foreground, 0, 0, null);
    return background;
  }

  public static ArrayPlotRender arrayPlotFromTensor(Tensor wgs, int magnification, boolean coverZero, ColorDataGradient colorDataGradient) {
    Rescale rescale = new Rescale(ImageReshape.of(wgs));
    Clip clip = rescale.scalarSummaryStatistics().getClip();
    return new ArrayPlotRender( //
        rescale.result(), //
        coverZero //
            ? ClipPointCover.of(clip, RealScalar.ZERO)
            : clip, //
        colorDataGradient, magnification);
  }
}
