// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.Polygons;
import ch.ethz.idsc.sophus.lie.r2.R2BarycentricCoordinates;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.opt.ConvexHull;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Entrywise;
import ch.ethz.idsc.tensor.red.VectorAngle;

/* package */ class R2BarycentricCoordinatesDemo extends ControlPointsDemo {
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  private static final ColorDataGradient COLOR_DATA_GRADIENT = ColorDataGradients.PARULA.deriveWithOpacity(RationalScalar.HALF);
  // ---
  private final SpinnerLabel<Barycenter> spinnerBarycentric = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerFactor = new SpinnerLabel<>();

  public R2BarycentricCoordinatesDemo() {
    super(true, GeodesicDisplays.SE2C_SE2_R2);
    {
      spinnerBarycentric.setArray(Barycenter.values());
      spinnerBarycentric.setIndex(0);
      spinnerBarycentric.addToComponentReduced(timerFrame.jToolBar, new Dimension(170, 28), "barycentric");
    }
    {
      spinnerRefine.setList(Arrays.asList(10, 15, 20, 25, 30, 35, 40));
      spinnerRefine.setIndex(1);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "refinement");
    }
    {
      spinnerFactor.setList(Arrays.asList(1, 2, 5, 10));
      spinnerFactor.setIndex(0);
      spinnerFactor.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "factor");
    }
    setControlPointsSe2(Tensors.fromString("{{0, -2, 0}, {3, -2, -1}, {4, 2, 1}, {-1, 3, 2}}"));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Scalar factor = RealScalar.of(spinnerFactor.getValue());
    Tensor controlPointsSe2 = getGeodesicControlPoints().multiply(factor);
    renderControlPoints(geometricLayer, graphics);
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    Tensor domain = Tensor.of(controlPointsSe2.stream().map(Extract2D.FUNCTION));
    if (2 < domain.length())
      try {
        GraphicsUtil.setQualityHigh(graphics);
        Tensor hull = ConvexHull.of(domain);
        {
          graphics.setColor(Color.LIGHT_GRAY);
          graphics.setStroke(STROKE);
          Path2D path2d = geometricLayer.toPath2D(hull.divide(factor));
          path2d.closePath();
          graphics.draw(path2d);
          graphics.setStroke(new BasicStroke(1));
        }
        TensorUnaryOperator tensorUnaryOperator = //
            new R2BarycentricCoordinates(spinnerBarycentric.getValue()).of(domain);
        Tensor min = Entrywise.min().of(hull).map(RealScalar.of(0.01)::add);
        Tensor max = Entrywise.max().of(hull).map(RealScalar.of(0.01)::subtract).negate();
        Tensor sX = Subdivide.of(min.Get(0), max.Get(0), spinnerRefine.getValue());
        Tensor sY = Subdivide.of(min.Get(1), max.Get(1), spinnerRefine.getValue());
        Tensor[][] array = new Tensor[sX.length()][sY.length()];
        Tensor wgs = Array.of(l -> DoubleScalar.INDETERMINATE, sX.length(), sY.length(), domain.length());
        int c0 = 0;
        for (Tensor x : sX) {
          int c1 = 0;
          for (Tensor y : sY) {
            Tensor px = Tensors.of(x, y);
            if (Polygons.isInside(domain, px)) {
              Tensor weights = tensorUnaryOperator.apply(px);
              wgs.set(weights, c0, c1);
              Tensor mean = biinvariantMean.mean(controlPointsSe2, weights);
              array[c0][c1] = mean.divide(factor);
            }
            ++c1;
          }
          ++c0;
        }
        // render basis functions
        int pix = 0;
        for (int basis = 0; basis < domain.length(); ++basis) {
          Tensor image = ArrayPlot.of(wgs.get(Tensor.ALL, Tensor.ALL, basis), ColorDataGradients.CLASSIC);
          BufferedImage bufferedImage = ImageFormat.of(image);
          int wid = bufferedImage.getWidth() * 4;
          graphics.drawImage(bufferedImage, pix, 32, wid, bufferedImage.getHeight() * 4, null);
          pix += wid;
        }
        // render grid lines functions
        graphics.setColor(Color.LIGHT_GRAY);
        for (int i0 = 1; i0 < array.length; ++i0)
          for (int i1 = 1; i1 < array.length; ++i1) {
            Tensor c = array[i0][i1];
            if (Objects.nonNull(c)) {
              Tensor p0 = array[i0 - 1][i1];
              Tensor p1 = array[i0][i1 - 1];
              Tensor pc = array[i0 - 1][i1 - 1];
              if (Objects.nonNull(p0))
                graphics.draw(geometricLayer.toPath2D(Tensors.of(p0, c)));
              if (Objects.nonNull(p1))
                graphics.draw(geometricLayer.toPath2D(Tensors.of(p1, c)));
              if (Objects.nonNull(p0) && Objects.nonNull(p1) && Objects.nonNull(pc)) {
                Scalar scalar = VectorAngle.of(p0.subtract(c), p1.subtract(c)).get();
                Tensor rgba = COLOR_DATA_GRADIENT.apply(scalar.divide(Pi.VALUE));
                graphics.setColor(ColorFormat.toColor(rgba));
                graphics.fill(geometricLayer.toPath2D(Unprotect.byRef(c, p0, pc, p1)));
              }
            }
          }
        Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(.5));
        for (int i0 = 0; i0 < array.length; ++i0)
          for (int i1 = 0; i1 < array.length; ++i1) {
            Tensor mean = array[i0][i1];
            if (Objects.nonNull(mean)) {
              Tensor matrix = geodesicDisplay.matrixLift(mean);
              geometricLayer.pushMatrix(matrix);
              graphics.setColor(new Color(128, 128, 128, 64));
              graphics.fill(geometricLayer.toPath2D(shape));
              geometricLayer.popMatrix();
            }
          }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }

  public static void main(String[] args) {
    new R2BarycentricCoordinatesDemo().setVisible(1200, 600);
  }
}
