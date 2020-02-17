// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.RnBarycentricCoordinates;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.r2.ConvexHull;
import ch.ethz.idsc.sophus.lie.r2.Polygons;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Boole;
import ch.ethz.idsc.tensor.red.Entrywise;
import ch.ethz.idsc.tensor.red.VectorAngle;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class R2BarycentricCoordinateDemo extends ScatteredSetCoordinateDemo {
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  // ---
  private final JToggleButton jToggleEntire = new JToggleButton("entire");

  public R2BarycentricCoordinateDemo() {
    super(true, GeodesicDisplays.SE2C_SPD2_S2_Rn, RnBarycentricCoordinates.values());
    {
      timerFrame.jToolBar.add(jToggleEntire);
    }
    setGeodesicDisplay(S2GeodesicDisplay.INSTANCE);
    setControlPointsSe2(Tensors.fromString("{{0, -2, 0}, {3, -2, -1}, {4, 2, 1}, {-1, 3, 2}}"));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ColorDataGradient colorDataGradient = colorDataGradient();
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor controlPoints = getGeodesicControlPoints();
    renderControlPoints(geometricLayer, graphics);
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    if (2 < controlPoints.length()) {
      Tensor domain = Tensor.of(controlPoints.stream().map(geodesicDisplay::toPoint));
      GraphicsUtil.setQualityHigh(graphics);
      Tensor hull = ConvexHull.of(domain);
      {
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.setStroke(STROKE);
        Path2D path2d = geometricLayer.toPath2D(hull);
        path2d.closePath();
        graphics.draw(path2d);
        graphics.setStroke(new BasicStroke(1));
      }
      BarycentricCoordinate barycentricCoordinate = barycentricCoordinate();
      Tensor min = Entrywise.min().of(hull).map(RealScalar.of(0.01)::add);
      Tensor max = Entrywise.max().of(hull).map(RealScalar.of(0.01)::subtract).negate();
      final int n = refinement();
      Tensor sX = Subdivide.of(min.Get(0), max.Get(0), n - 1);
      Tensor sY = Subdivide.of(min.Get(1), max.Get(1), n - 1);
      Tensor[][] array = new Tensor[n][n];
      Tensor wgs = Array.of(l -> DoubleScalar.INDETERMINATE, n, n, domain.length());
      Tensor neg = Array.of(l -> DoubleScalar.INDETERMINATE, n, n);
      boolean[][] nag = new boolean[n][n];
      IntStream.range(0, sX.length()).parallel().forEach(c0 -> {
        Scalar x = sX.Get(c0);
        int c1 = 0;
        for (Tensor y : sY) {
          Tensor px = Tensors.of(x, y);
          if (jToggleEntire.isSelected() || Polygons.isInside(domain, px)) {
            Tensor weights = barycentricCoordinate.weights(domain, px);
            wgs.set(weights, n - c1 - 1, c0);
            boolean anyNegative = weights.stream().map(Scalar.class::cast).anyMatch(Sign::isNegative);
            neg.set(Boole.of(anyNegative), n - c1 - 1, c0);
            nag[c0][c1] = anyNegative;
            Tensor mean = biinvariantMean.mean(controlPoints, weights);
            array[c0][c1] = mean;
          }
          ++c1;
        }
        // ++c0;
      });
      if (jToggleHeatmap.isSelected()) { // render basis functions
        final int magnify = 3;
        List<Integer> dims = Dimensions.of(wgs);
        Tensor _wgs = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
        ArrayPlotRender arrayPlotRender = new ArrayPlotRender(_wgs, colorDataGradient, 0, 32, magnify);
        arrayPlotRender.render(geometricLayer, graphics);
        {
          // BufferedImage bufferedImage = ImageFormat.of(ArrayPlot.of(_wgs, colorDataGradient));
          // graphics.drawImage(bufferedImage, //
          // 0, 32, //
          // bufferedImage.getWidth() * 2, bufferedImage.getHeight() * 2, null);
          // pix = bufferedImage.getWidth() * 2;
        }
        {
          Tensor image = ArrayPlot.of(neg, ColorDataGradients.TEMPERATURE);
          BufferedImage bufferedImage = ImageFormat.of(image);
          // int wid = ;
          graphics.drawImage(bufferedImage, 0, 32 + arrayPlotRender.height(), //
              bufferedImage.getWidth() * magnify, //
              bufferedImage.getHeight() * magnify, null);
        }
      }
      // render grid lines functions
      ColorDataGradient cdg = colorDataGradient.deriveWithOpacity(RationalScalar.HALF);
      for (int i0 = 1; i0 < n; ++i0)
        for (int i1 = 1; i1 < n; ++i1) {
          Tensor ao = array[i0][i1];
          if (Objects.nonNull(ao)) {
            Tensor po = geodesicDisplay.toPoint(ao);
            Tensor a0 = array[i0 - 1][i1];
            Tensor a1 = array[i0][i1 - 1];
            Tensor ac = array[i0 - 1][i1 - 1];
            if (Objects.nonNull(a0) && Objects.nonNull(a1) && Objects.nonNull(ac)) {
              Tensor p0 = geodesicDisplay.toPoint(a0);
              Tensor p1 = geodesicDisplay.toPoint(a1);
              Tensor pc = geodesicDisplay.toPoint(ac);
              Scalar scalar = VectorAngle.of(p0.subtract(po), p1.subtract(po)).get();
              Tensor rgba = cdg.apply(scalar.divide(Pi.VALUE));
              graphics.setColor(ColorFormat.toColor(rgba));
              graphics.fill(geometricLayer.toPath2D(Unprotect.byRef(po, p0, pc, p1)));
            }
            if (Objects.nonNull(a0))
              graphics.draw(geometricLayer.toPath2D(Tensors.of(geodesicDisplay.toPoint(a0), po)));
            if (Objects.nonNull(a1))
              graphics.draw(geometricLayer.toPath2D(Tensors.of(geodesicDisplay.toPoint(a1), po)));
          }
        }
      if (jToggleArrows.isSelected()) {
        Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(.5));
        for (int i0 = 0; i0 < n; ++i0)
          for (int i1 = 0; i1 < n; ++i1) {
            Tensor mean = array[i0][i1];
            if (Objects.nonNull(mean)) {
              geometricLayer.pushMatrix(geodesicDisplay.matrixLift(mean));
              graphics.setColor(nag[i0][i1] ? new Color(255, 128, 128, 128 + 32) : new Color(128, 128, 128, 64));
              graphics.fill(geometricLayer.toPath2D(shape));
              geometricLayer.popMatrix();
            }
          }
      }
    }
  }

  public static void main(String[] args) {
    new R2BarycentricCoordinateDemo().setVisible(1200, 600);
  }
}
