// code by jph
package ch.alpine.sophus.demo.misc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.ann.FieldLabel;
import ch.alpine.java.ref.ann.FieldSelectionArray;
import ch.alpine.java.ref.ann.ReflectionMarker;
import ch.alpine.java.ref.util.ToolbarFieldsEditor;
import ch.alpine.java.ren.ImageRender;
import ch.alpine.java.win.LookAndFeels;
import ch.alpine.sophus.demo.ControlPointsDemo;
import ch.alpine.sophus.demo.opt.SnLineDistances;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.hs.VectorLogManifold;
import ch.alpine.sophus.math.AppendOne;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.sophus.math.TensorNorm;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.Sqrt;

@ReflectionMarker
public class S2LineDistanceDemo extends ControlPointsDemo {
  private static final Stroke STROKE = //
      new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  private static final Tensor GEODESIC_DOMAIN = Subdivide.of(0.0, 1.0, 11);
  private static final Tensor INITIAL = Tensors.fromString("{{-0.5, 0, 0}, {0.5, 0, 0}}").unmodifiable();
  // ---
  @FieldLabel("S^n line distance method")
  public SnLineDistances snLineDistances = SnLineDistances.DEFAULT;
  @FieldInteger
  @FieldSelectionArray(value = { "20", "30", "50", "75", "100", "150", "200", "250" })
  public Scalar resolution = RealScalar.of(30);
  @FieldLabel("color data gradient")
  public ColorDataGradients colorDataGradients = ColorDataGradients.PARULA;

  public S2LineDistanceDemo() {
    super(false, ManifoldDisplays.S2_ONLY);
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
    // ---
    setControlPointsSe2(INITIAL);
    // ---
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Times.of(Tensors.vector(5, 5, 1), model2pixel));
    // ---
    timerFrame.geometricComponent.setOffset(400, 400);
    setMidpointIndicated(false);
  }

  TensorNorm tensorNorm() {
    Tensor cp = getGeodesicControlPoints();
    return 1 < cp.length() //
        ? snLineDistances.lineDistance().tensorNorm(cp.get(0), cp.get(1))
        : t -> RealScalar.ZERO;
  }

  private Tensor pixel2model(BufferedImage bufferedImage) {
    double rad = rad();
    Tensor range = Tensors.vector(rad, rad).multiply(RealScalar.TWO); // model
    Tensor scale = Times.of(Tensors.vector(bufferedImage.getWidth(), bufferedImage.getHeight()), //
        range.map(Scalar::reciprocal)); // model 2 pixel
    return Dot.of( //
        GfxMatrix.translation(range.multiply(RationalScalar.HALF.negate())), //
        Times.of(AppendOne.FUNCTION.apply(scale.map(Scalar::reciprocal)) // pixel 2 model
            , GfxMatrix.flipY(bufferedImage.getHeight())));
  }

  private BufferedImage bufferedImage(int resolution, VectorLogManifold vectorLogManifold) {
    Tensor matrix = Tensors.matrix(array(resolution, tensorNorm()::norm));
    return ImageFormat.of(matrix.map(colorDataGradients));
  }

  Scalar[][] array(int resolution, TensorScalarFunction tensorScalarFunction) {
    double rad = rad();
    Tensor dx = Subdivide.of(-rad, +rad, resolution);
    Tensor dy = Subdivide.of(+rad, -rad, resolution);
    int rows = dy.length();
    int cols = dx.length();
    Scalar[][] array = new Scalar[rows][cols];
    Clip clip = Clips.unit();
    IntStream.range(0, rows).parallel().forEach(cx -> {
      for (int cy = 0; cy < cols; ++cy) {
        Tensor point = Tensors.of(dx.get(cx), dy.get(cy)); // in R2
        Scalar z2 = RealScalar.ONE.subtract(Vector2NormSquared.of(point));
        if (Sign.isPositive(z2)) {
          Scalar z = Sqrt.FUNCTION.apply(z2);
          Tensor xyz = point.append(z);
          array[cy][cx] = clip.apply(tensorScalarFunction.apply(xyz));
        } else
          array[cy][cx] = DoubleScalar.INDETERMINATE;
      }
    });
    return array;
  }

  double rad() {
    return 1;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    RenderQuality.setDefault(graphics);
    BufferedImage bufferedImage = bufferedImage(resolution.number().intValue(), manifoldDisplay.hsManifold());
    ImageRender.of(bufferedImage, pixel2model(bufferedImage)) //
        .render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    // ---
    Geodesic geodesicInterface = manifoldDisplay.geodesic();
    Tensor cp = getGeodesicControlPoints();
    ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(cp.get(0), cp.get(1));
    graphics.setStroke(STROKE);
    Tensor ms = Tensor.of(GEODESIC_DOMAIN.map(scalarTensorFunction).stream().map(manifoldDisplay::toPoint));
    graphics.setColor(new Color(192, 192, 192));
    graphics.draw(geometricLayer.toPath2D(ms));
    graphics.setStroke(new BasicStroke());
    // ---
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    LookAndFeels.DARK.updateUI();
    new S2LineDistanceDemo().setVisible(1200, 600);
  }
}
