// code by jph
package ch.alpine.sophus.app.lev;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import ch.alpine.owl.gui.region.ImageRender;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.gds.GeodesicArrayPlot;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gui.ren.LegendImage;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Ordering;
import ch.alpine.tensor.img.ColorDataGradient;
import ch.alpine.tensor.img.ColorFormat;
import ch.alpine.tensor.num.Pi;

/* package */ enum OrderingHelper {
  ;
  static final Scalar FACTOR = RealScalar.of(0.3);

  public static void of( //
      ManifoldDisplay geodesicDisplay, //
      Tensor origin, Tensor sequence, Tensor weights, //
      ColorDataGradient cdg, //
      GeometricLayer geometricLayer, Graphics2D graphics) {
    int[] integers = Ordering.INCREASING.of(weights);
    ColorDataGradient colorDataGradientF = cdg.deriveWithOpacity(RationalScalar.HALF);
    ColorDataGradient colorDataGradientD = cdg;
    Tensor shape = geodesicDisplay.shape();
    for (int index = 0; index < sequence.length(); ++index) {
      Tensor point = sequence.get(integers[index]);
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
      Path2D path2d = geometricLayer.toPath2D(shape, true);
      Scalar ratio = RationalScalar.of(index, integers.length);
      graphics.setColor(ColorFormat.toColor(colorDataGradientF.apply(ratio)));
      graphics.fill(path2d);
      graphics.setColor(ColorFormat.toColor(colorDataGradientD.apply(ratio)));
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    {
      // 100
      BufferedImage bufferedImage = LegendImage.of(colorDataGradientD, 130, "far", "near");
      Scalar dy = Pi.VALUE;
      dy = FACTOR.multiply(Pi.VALUE);
      Tensor pixel2model = GeodesicArrayPlot.pixel2model( //
          Tensors.of(Pi.VALUE.add(RealScalar.of(0.4)), dy.negate()), //
          Tensors.of(dy.add(dy), dy.add(dy)), //
          new Dimension(bufferedImage.getHeight(), bufferedImage.getHeight()));
      ImageRender.of(bufferedImage, pixel2model).render(geometricLayer, graphics);
    }
    {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(origin));
      Path2D path2d = geometricLayer.toPath2D(shape, true);
      graphics.setColor(Color.DARK_GRAY);
      graphics.fill(path2d);
      graphics.setColor(Color.BLACK);
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    {
      LeversRender leversRender = LeversRender.of(geodesicDisplay, //
          Tensor.of(IntStream.range(0, 8).limit(integers.length) //
              .map(index -> integers[index]) //
              .mapToObj(sequence::get)), //
          origin, geometricLayer, graphics);
      leversRender.renderLevers();
      leversRender.renderIndexX();
    }
  }
}
