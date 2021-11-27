// code by jph
package ch.alpine.sophus.demo.ref.d1h;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JTextField;

import org.jfree.chart.JFreeChart;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ToolbarFieldsEditor;
import ch.alpine.java.ref.ann.FieldClip;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.ann.FieldPreferredWidth;
import ch.alpine.java.ref.ann.FieldSelection;
import ch.alpine.java.ref.ann.FieldSlider;
import ch.alpine.java.ren.PathRender;
import ch.alpine.sophus.demo.io.GokartPoseDataV2;
import ch.alpine.sophus.demo.io.GokartPoseDatas;
import ch.alpine.sophus.demo.opt.HermiteSubdivisions;
import ch.alpine.sophus.gds.GeodesicDatasetDemo;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.lie.so2.So2Lift;
import ch.alpine.sophus.math.Do;
import ch.alpine.sophus.math.TensorIteration;
import ch.alpine.sophus.ref.d1h.HermiteSubdivision;
import ch.alpine.tensor.NumberQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class HermiteDatasetDemo extends GeodesicDatasetDemo {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 360;
  private static final Color COLOR_CURVE = new Color(255, 128, 128, 255);
  private static final Color COLOR_RECON = new Color(128, 128, 128, 255);
  // ---
  private static final Stroke STROKE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  private final PathRender pathRenderCurve = new PathRender(COLOR_CURVE, STROKE);
  private final PathRender pathRenderShape = new PathRender(COLOR_RECON, 2f);
  // ---
  private final GokartPoseDataV2 gokartPoseDataV2;
  @FieldSelection(array = { "1", "2", "5", "10", "25", "50", "100", "250", "500" })
  public Scalar skips = RealScalar.of(50);
  @FieldSelection(array = { "0", "2", "4", "6", "8", "10", "15", "20" })
  public Scalar shift = RealScalar.of(0);
  public HermiteSubdivisions scheme = HermiteSubdivisions.HERMITE3;
  @FieldSlider
  @FieldPreferredWidth(width = 80)
  @FieldInteger
  @FieldClip(min = "0", max = "8")
  public Scalar level = RealScalar.of(3);
  public Boolean diff = true;
  protected Tensor _control = Tensors.empty();

  public HermiteDatasetDemo(GokartPoseDataV2 gokartPoseData) {
    super(ManifoldDisplays.SE2C_SE2, gokartPoseData);
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar).addUniversalListener(this::updateState);
    this.gokartPoseDataV2 = gokartPoseData;
    timerFrame.geometricComponent.setModel2Pixel(GokartPoseDatas.HANGAR_MODEL2PIXEL);
    timerFrame.jToolBar.addSeparator();
    {
      JTextField jTextField = new JTextField(6);
      jTextField.setText(HermiteSubdivisions.LAMBDA.toString());
      jTextField.addActionListener(e -> {
        try {
          Scalar scalar = Scalars.fromString(jTextField.getText());
          if (NumberQ.of(scalar))
            HermiteSubdivisions.LAMBDA = scalar;
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      });
      jTextField.setPreferredSize(new Dimension(40, 28));
      timerFrame.jToolBar.add(jTextField);
    }
    {
      JTextField jTextField = new JTextField(6);
      jTextField.setText(HermiteSubdivisions.MU.toString());
      jTextField.addActionListener(e -> {
        try {
          Scalar scalar = Scalars.fromString(jTextField.getText());
          if (NumberQ.of(scalar))
            HermiteSubdivisions.MU = scalar;
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      });
      jTextField.setPreferredSize(new Dimension(40, 28));
      timerFrame.jToolBar.add(jTextField);
    }
    {
      JTextField jTextField = new JTextField(6);
      jTextField.setText(HermiteSubdivisions.THETA.toString());
      jTextField.addActionListener(e -> {
        try {
          Scalar scalar = Scalars.fromString(jTextField.getText());
          if (NumberQ.of(scalar))
            HermiteSubdivisions.THETA = scalar;
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      });
      jTextField.setPreferredSize(new Dimension(40, 28));
      timerFrame.jToolBar.add(jTextField);
    }
    {
      JTextField jTextField = new JTextField(6);
      jTextField.setText(HermiteSubdivisions.OMEGA.toString());
      jTextField.addActionListener(e -> {
        try {
          Scalar scalar = Scalars.fromString(jTextField.getText());
          if (NumberQ.of(scalar))
            HermiteSubdivisions.OMEGA = scalar;
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      });
      jTextField.setPreferredSize(new Dimension(40, 28));
      timerFrame.jToolBar.add(jTextField);
    }
    timerFrame.jToolBar.addSeparator();
    updateState();
  }

  @Override
  protected void updateState() {
    int limit = spinnerLabelLimit.getValue();
    String name = spinnerLabelString.getValue();
    Tensor control = gokartPoseDataV2.getPoseVel(name, limit);
    control.set(new So2Lift(), Tensor.ALL, 0, 2);
    Tensor result = Tensors.empty();
    int _skips = skips.number().intValue();
    int offset = shift.number().intValue();
    for (int index = offset; index < control.length(); index += _skips)
      result.append(control.get(index));
    // TensorUnaryOperator centerFilter = //
    // CenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, GaussianWindow.FUNCTION), 4);
    _control = result;
  }

  @SuppressWarnings("unused")
  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    {
      final Tensor shape = manifoldDisplay.shape().multiply(RealScalar.of(1));
      pathRenderCurve.setCurve(_control.get(Tensor.ALL, 0), false).render(geometricLayer, graphics);
      if (_control.length() <= 1000)
        for (Tensor point : _control.get(Tensor.ALL, 0)) {
          geometricLayer.pushMatrix(manifoldDisplay.matrixLift(point));
          Path2D path2d = geometricLayer.toPath2D(shape);
          path2d.closePath();
          graphics.setColor(new Color(255, 128, 128, 64));
          graphics.fill(path2d);
          graphics.setColor(COLOR_CURVE);
          graphics.draw(path2d);
          geometricLayer.popMatrix();
        }
    }
    graphics.setColor(Color.DARK_GRAY);
    Scalar delta = RationalScalar.of(skips.number().intValue(), 50);
    HermiteSubdivision hermiteSubdivision = scheme.supply( //
        manifoldDisplay.hsManifold(), //
        manifoldDisplay.hsTransport(), //
        manifoldDisplay.biinvariantMean());
    TensorIteration tensorIteration = hermiteSubdivision.string(delta, _control);
    int levels = level.number().intValue();
    Tensor refined = Do.of(_control, tensorIteration::iterate, levels);
    pathRenderShape.setCurve(refined.get(Tensor.ALL, 0), false).render(geometricLayer, graphics);
    new Se2HermitePlot(refined, RealScalar.of(0.3)).render(geometricLayer, graphics);
    if (diff) {
      Tensor deltas = refined.get(Tensor.ALL, 1);
      int dims = deltas.get(0).length();
      if (0 < deltas.length()) {
        JFreeChart jFreeChart = StaticHelper.listPlot(deltas, delta, levels);
        Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
        jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
      }
    }
  }

  public static void main(String[] args) {
    new HermiteDatasetDemo(GokartPoseDataV2.RACING_DAY).setVisible(1000, 800);
  }
}
