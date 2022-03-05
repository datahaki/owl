// code by jph
package ch.alpine.sophus.demo.misc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.stream.IntStream;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.ann.FieldLabel;
import ch.alpine.java.ref.ann.FieldSelectionArray;
import ch.alpine.java.ref.ann.ReflectionMarker;
import ch.alpine.java.ref.util.ToolbarFieldsEditor;
import ch.alpine.java.ren.ImageRender;
import ch.alpine.java.win.LookAndFeels;
import ch.alpine.sophus.bm.MeanDefect;
import ch.alpine.sophus.demo.ControlPointsDemo;
import ch.alpine.sophus.demo.lev.LeversRender;
import ch.alpine.sophus.demo.opt.SnLineDistances;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.hs.HsManifold;
import ch.alpine.sophus.hs.VectorLogManifold;
import ch.alpine.sophus.hs.sn.SnBiinvariantMean;
import ch.alpine.sophus.hs.sn.SnExponential;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.nrm.FrobeniusNorm;
import ch.alpine.tensor.nrm.NormalizeTotal;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Sqrt;

@ReflectionMarker
public class S2DefectNormDemo extends ControlPointsDemo {
  private static final Stroke STROKE = //
      new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  private static final Tensor INITIAL = Tensors.fromString("{{-0.5, 0, 0}, {0.5, 0, 0}, {0, 0.5, 0}, {0, -0.5, 0}}").unmodifiable();
  // ---
  @FieldLabel("S^n line distance method")
  public SnLineDistances snLineDistances = SnLineDistances.DEFAULT;
  @FieldInteger
  @FieldSelectionArray(value = { "20", "30", "50", "75", "100", "150", "200", "250" })
  public Scalar resolution = RealScalar.of(30);
  @FieldLabel("color data gradient")
  public ColorDataGradients colorDataGradients = ColorDataGradients.PARULA;
  public Boolean vector = true;
  @FieldLabel("weights")
  public Tensor user_weights = Tensors.vector(3, 2, -2, 1, 1, 1, 1);

  public S2DefectNormDemo() {
    super(true, ManifoldDisplays.S2_ONLY);
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

  public class TSF implements TensorScalarFunction {
    final Tensor sequence;
    final Tensor weights;

    public TSF() {
      sequence = getGeodesicControlPoints();
      int n = sequence.length();
      weights = NormalizeTotal.FUNCTION.apply(N.DOUBLE.of(user_weights.extract(0, n)));
    }

    @Override
    public Scalar apply(Tensor xyz) {
      MeanDefect meanDefect = new MeanDefect(sequence, weights, new SnExponential(xyz));
      return FrobeniusNorm.of(meanDefect.tangent());
    }
  }

  private BufferedImage bufferedImage(int resolution, VectorLogManifold vectorLogManifold) {
    Tensor matrix = Tensors.matrix(S2ArrayHelper.of(resolution, rad(), new TSF()));
    return ImageFormat.of(matrix.map(colorDataGradients));
  }

  double rad() {
    return 1;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    RenderQuality.setDefault(graphics);
    int res = resolution.number().intValue();
    BufferedImage bufferedImage = bufferedImage(res, manifoldDisplay.hsManifold());
    ImageRender.of(bufferedImage, S2ArrayHelper.pixel2model(bufferedImage, rad())) //
        .render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    // ---
    Geodesic geodesicInterface = manifoldDisplay.geodesic();
    Tensor cp = getGeodesicControlPoints();
    ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(cp.get(0), cp.get(1));
    graphics.setStroke(STROKE);
    // Tensor ms = Tensor.of(GEODESIC_DOMAIN.map(scalarTensorFunction).stream().map(manifoldDisplay::toPoint));
    graphics.setColor(new Color(192, 192, 192));
    // graphics.draw(geometricLayer.toPath2D(ms));
    graphics.setStroke(new BasicStroke());
    // ---
    TSF tsf = new TSF();
    Tensor mean = SnBiinvariantMean.INSTANCE.mean(tsf.sequence, tsf.weights);
    if (vector) {
      double rad = 1;
      Tensor dx = Subdivide.of(-rad, +rad, res);
      Tensor dy = Subdivide.of(+rad, -rad, res);
      int rows = dy.length();
      int cols = dx.length();
      Scalar[][] array = new Scalar[rows][cols];
      IntStream.range(0, rows).forEach(cx -> {
        for (int cy = 0; cy < cols; ++cy) {
          Tensor point = Tensors.of(dx.get(cx), dy.get(cy)); // in R2
          Scalar z2 = RealScalar.ONE.subtract(Vector2NormSquared.of(point));
          if (Sign.isPositive(z2)) {
            Scalar z = Sqrt.FUNCTION.apply(z2);
            Tensor xyz = point.append(z);
            MeanDefect meanDefect = new MeanDefect(tsf.sequence, tsf.weights, new SnExponential(xyz));
            Tensor v = meanDefect.tangent();
            renderTangentsPtoX(geometricLayer, graphics, xyz, v.multiply(RealScalar.of(0.2)));
          } else
            array[cy][cx] = DoubleScalar.INDETERMINATE;
        }
      });
    }
    LeversRender leversRender = LeversRender.of(manifoldDisplay, tsf.sequence, mean, geometricLayer, graphics);
    leversRender.renderOrigin();
    leversRender.renderSequence();
    leversRender.renderWeights(tsf.weights);
  }

  public void renderTangentsPtoX(GeometricLayer geometricLayer, Graphics2D graphics, Tensor p, Tensor v) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    geometricLayer.pushMatrix(manifoldDisplay.matrixLift(p));
    TensorUnaryOperator tangentProjection = manifoldDisplay.tangentProjection(p);
    if (Objects.nonNull(tangentProjection))
      graphics.draw(geometricLayer.toLine2D(tangentProjection.apply(v)));
    // ---
    geometricLayer.popMatrix();
  }

  public final Tensor iterationPath(Tensor sequence, Tensor weights, Tensor shifted, int iter) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    HsManifold hsManifold = manifoldDisplay.hsManifold();
    Tensor tensor = Tensors.empty();
    for (int count = 0; count < iter; ++count) {
      MeanDefect meanDefect = new MeanDefect(sequence, weights, hsManifold.exponential(shifted));
      shifted = meanDefect.shifted();
      tensor.append(shifted);
    }
    return tensor;
  }

  public static void main(String[] args) {
    LookAndFeels.DARK.updateUI();
    new S2DefectNormDemo().setVisible(1200, 800);
  }
}
