// code by jph
package ch.alpine.sophus.demo.lev;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Optional;

import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.java.ren.GridRender;
import ch.alpine.javax.swing.SpinnerLabel;
import ch.alpine.javax.swing.SpinnerListener;
import ch.alpine.sophus.api.Geodesic;
import ch.alpine.sophus.ext.dis.H2Display;
import ch.alpine.sophus.ext.dis.ManifoldDisplay;
import ch.alpine.sophus.ext.dis.ManifoldDisplays;
import ch.alpine.sophus.ext.dis.R2Display;
import ch.alpine.sophus.ext.dis.S2Display;
import ch.alpine.sophus.ext.dis.Se2AbstractDisplay;
import ch.alpine.sophus.hs.HsDesign;
import ch.alpine.sophus.hs.r2.ArcTan2D;
import ch.alpine.sophus.itp.ArcLengthParameterization;
import ch.alpine.sophus.lie.rn.RnGeodesic;
import ch.alpine.sophus.lie.so2.So2;
import ch.alpine.sophus.ref.d1.CurveSubdivision;
import ch.alpine.sophus.ref.d1.FourPointCurveSubdivision;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Differences;
import ch.alpine.tensor.alg.Drop;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;

/* package */ class LogarithmDemo extends AbstractPlaceDemo implements SpinnerListener<ManifoldDisplay> {
  private static final GridRender GRID_RENDER = new GridRender(Tensors.vector(-1, 0, 1), Color.LIGHT_GRAY);
  private static final Color DOMAIN_F = new Color(192, 192, 64, 64);
  private static final Color DOMAIN_D = new Color(192, 192, 64, 192);
  // ---
  private final SpinnerLabel<Integer> spinnerLength = new SpinnerLabel<>();
  private final JToggleButton jToggleCtrl = new JToggleButton("show ctrl");

  public LogarithmDemo() {
    super(true, ManifoldDisplays.R2_H2_S2);
    {
      spinnerLength.setList(Arrays.asList(5, 9, 10, 11, 12, 15, 20));
      spinnerLength.setValueSafe(11);
      spinnerLength.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "number of points");
    }
    timerFrame.jToolBar.add(jToggleCtrl);
    // ---
    ManifoldDisplay manifoldDisplay = H2Display.INSTANCE;
    setGeodesicDisplay(manifoldDisplay);
    actionPerformed(manifoldDisplay);
    addSpinnerListener(this);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Geodesic geodesicInterface = manifoldDisplay.geodesic();
    Optional<Tensor> optional = getOrigin();
    if (optional.isPresent()) {
      Tensor sequence = getSequence();
      Tensor origin = optional.get();
      {
        LeversRender leversRender = //
            LeversRender.of(manifoldDisplay, sequence, origin, geometricLayer, graphics);
        leversRender.renderOrigin();
        leversRender.renderIndexX();
        // ---
        if (jToggleCtrl.isSelected()) {
          leversRender.renderIndexP();
          leversRender.renderSequence(); // toggle show
        }
      }
      // ---
      CurveSubdivision curveSubdivision = //
          new FourPointCurveSubdivision(geodesicInterface);
      if (2 < sequence.length()) {
        Tensor refined = sequence;
        while (refined.length() < 100)
          refined = curveSubdivision.cyclic(refined);
        // ---
        RenderQuality.setQuality(graphics);
        if (manifoldDisplay instanceof R2Display)
          GRID_RENDER.render(geometricLayer, graphics);
        {
          RenderQuality.setQuality(graphics);
          Path2D path2d = geometricLayer.toPath2D(Tensor.of(refined.stream().map(manifoldDisplay::toPoint)), true);
          graphics.setColor(DOMAIN_F);
          graphics.fill(path2d);
          graphics.setColor(DOMAIN_D);
          graphics.draw(path2d);
        }
        final Tensor domain = Drop.tail(Subdivide.of(0.0, 1.0, spinnerLength.getValue()), 1);
        geometricLayer.pushMatrix(GfxMatrix.translation(Tensors.vector(10, 0)));
        GRID_RENDER.render(geometricLayer, graphics);
        HsDesign hsDesign = new HsDesign(manifoldDisplay.hsManifold());
        Tensor planar = hsDesign.matrix(refined, origin);
        {
          RenderQuality.setQuality(graphics);
          Path2D path2d = geometricLayer.toPath2D(planar, true);
          graphics.setColor(DOMAIN_F);
          graphics.fill(path2d);
          graphics.setColor(DOMAIN_D);
          graphics.draw(path2d);
        }
        Tensor angles_acc = Tensor.of(planar.stream().map(ArcTan2D::of));
        Tensor distances = Tensor.of(Differences.of(angles_acc).stream().map(Scalar.class::cast).map(So2.MOD));
        try {
          ScalarTensorFunction scalarTensorFunction = ArcLengthParameterization.of(distances, RnGeodesic.INSTANCE, planar);
          Tensor border = domain.map(scalarTensorFunction);
          RenderQuality.setQuality(graphics);
          graphics.setColor(Color.BLUE);
          for (Tensor y : border) {
            Line2D line2d = geometricLayer.toLine2D(y);
            graphics.draw(line2d);
          }
        } catch (Exception exception) {
          exception.printStackTrace();
        }
        geometricLayer.popMatrix();
        try {
          ScalarTensorFunction scalarTensorFunction = ArcLengthParameterization.of(distances, geodesicInterface, refined);
          Tensor border = domain.map(scalarTensorFunction);
          LeversRender leversRender = LeversRender.of( //
              manifoldDisplay, //
              border, origin, //
              geometricLayer, graphics);
          leversRender.renderLevers();
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    }
  }

  @Override
  public void actionPerformed(ManifoldDisplay manifoldDisplay) {
    if (manifoldDisplay instanceof R2Display) {
      setControlPointsSe2(Tensors.fromString( //
          "{{0.358, 0.508, 0.000}, {-0.375, -0.567, 0.000}, {0.442, -0.425, 0.000}, {1.142, 0.000, 0.000}, {1.158, 1.108, 0.000}, {0.192, 1.433, 0.000}, {-0.625, 0.342, 0.000}}"));
    } else
      if (manifoldDisplay instanceof H2Display) {
        setControlPointsSe2(Tensors.fromString( //
            "{{1.033, -1.267, 0.000}, {0.567, -2.433, 0.000}, {1.967, -1.967, 0.000}, {2.067, -0.583, 0.000}, {-0.017, -0.450, 0.000}, {-0.700, -1.017, 0.000}}"));
        setControlPointsSe2(Tensors.fromString( //
            "{{1.350, -0.558, 0.000}, {0.558, -1.175, 0.000}, {2.350, -1.233, 0.000}, {1.975, 0.208, 0.000}, {1.600, 1.175, 0.000}, {0.567, 0.467, 0.000}}"));
      } else //
        if (manifoldDisplay instanceof S2Display) {
          setControlPointsSe2(Tensors.fromString( //
              "{{-0.325, -0.500, 0.262}, {-0.225, -0.917, 0.262}, {0.556, -0.496, 0.262}, {0.708, 0.417, 0.262}, {-0.177, 0.088, 0.262}, {-0.792, 0.358, 0.262}, {-0.867, -0.258, 0.000}}"));
        } else //
          if (manifoldDisplay instanceof Se2AbstractDisplay) {
            setControlPointsSe2(Tensors.fromString(
                "{{3.150, -2.700, -0.524}, {-1.950, -3.683, 0.000}, {-1.500, -1.167, 2.094}, {4.533, -0.733, -1.047}, {8.567, -3.300, -1.309}, {2.917, -5.050, -1.047}}"));
          }
  }

  public static void main(String[] args) {
    new LogarithmDemo().setVisible(1200, 600);
  }
}
