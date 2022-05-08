// code by jph
package ch.alpine.owl.sim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.alpine.ascona.clt.ClothoidDefectContainer;
import ch.alpine.ascona.clt.CustomClothoidBuilder;
import ch.alpine.ascona.lev.LeversRender;
import ch.alpine.ascona.util.api.ControlPointsDemo;
import ch.alpine.ascona.util.dis.ManifoldDisplays;
import ch.alpine.bridge.awt.RenderQuality;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.gfx.GfxMatrix;
import ch.alpine.bridge.swing.SpinnerLabel;
import ch.alpine.bridge.win.AxesRender;
import ch.alpine.bridge.win.GridRender;
import ch.alpine.bridge.win.PathRender;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidContext;
import ch.alpine.sophus.clt.ClothoidSolutions;
import ch.alpine.sophus.clt.ClothoidTransition;
import ch.alpine.sophus.clt.mid.MidpointTangentApproximation;
import ch.alpine.sophus.clt.mid.MidpointTangentOrder2;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Round;

/* package */ class CustomClothoidDemo extends ControlPointsDemo implements ChangeListener {
  private static final Tensor CONFIG = Tensors.fromString("{{0, 0}, {3, 0}}");
  private static final PathRender PATH_RENDER = new PathRender(new Color(0, 0, 255, 128), 2f);
  private static final ClothoidSolutions CLOTHOID_SOLUTIONS = ClothoidSolutions.of(Clips.absolute(20.0));
  private static final Tensor LAMBDAS = CLOTHOID_SOLUTIONS.probes;
  private static final Tensor POINTER = Tensors.fromString("{{0, 0}, {-0.2, -1}, {+0.2, -1}}");
  // ---
  private final JSlider jSlider = new JSlider(0, LAMBDAS.length() - 1, LAMBDAS.length() / 2);
  private final JTextField jTextField = new JTextField(10);
  private final JLabel jLabel = new JLabel();
  private final SpinnerLabel<Integer> spinnerSolution = SpinnerLabel.of(0, 1, 2, 3, 4, 5, 6, 7, 8);
  private static final Scalar MIN_RESOLUTION = RealScalar.of(0.1);
  // ---
  private ClothoidDefectContainer clothoidDefectContainer = null;

  public CustomClothoidDemo() {
    super(false, ManifoldDisplays.SE2C_ONLY);
    {
      jTextField.setPreferredSize(new Dimension(100, 28));
      timerFrame.jToolBar.add(jTextField);
    }
    {
      jSlider.addChangeListener(this);
      jSlider.setPreferredSize(new Dimension(700, 28));
      timerFrame.jToolBar.add(jSlider);
    }
    {
      JButton jButton = new JButton("fit");
      jButton.addActionListener(e -> {
        ClothoidContext clothoidContext = clothoidDefectContainer.clothoidContext;
        Scalar lambda = MidpointTangentOrder2.INSTANCE.apply(clothoidContext.s1(), clothoidContext.s2());
        setLambda(lambda);
      });
      timerFrame.jToolBar.add(jButton);
    }
    spinnerSolution.addSpinnerListener(index -> {
      Tensor lambdas2 = clothoidDefectContainer.search.lambdas();
      try {
        setLambda(lambdas2.Get(index));
      } catch (Exception exception) {
        // ---
      }
    });
    spinnerSolution.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "sol. index");
    {
      timerFrame.jToolBar.add(jLabel);
    }
    // ---
    stateChanged(null);
    setControlPointsSe2(Array.zeros(2, 3));
    timerFrame.geometricComponent.setOffset(300, 700);
    validateContainer();
  }

  private boolean validateContainer() {
    {
      Tensor control = getControlPointsSe2().copy();
      control.set(CONFIG.get(Tensor.ALL, 0), Tensor.ALL, 0);
      control.set(CONFIG.get(Tensor.ALL, 1), Tensor.ALL, 1);
      setControlPointsSe2(control);
    }
    // ---
    Tensor p = getControlPointsSe2().get(0);
    Tensor q = getControlPointsSe2().get(1);
    ClothoidContext clothoidContext = new ClothoidContext(p, q);
    if (Objects.isNull(clothoidDefectContainer) || !clothoidDefectContainer.encodes(clothoidContext)) {
      clothoidDefectContainer = new ClothoidDefectContainer(clothoidContext);
      jLabel.setText("s1=" + clothoidContext.s1().map(Round._4) + " s2=" + clothoidContext.s2().map(Round._4));
      return true;
    }
    return false;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    boolean validated = validateContainer();
    if (validated) {
      graphics.setColor(Color.BLACK);
      graphics.drawString("validated", 0, 30);
    }
    // ---
    ClothoidContext clothoidContext = clothoidDefectContainer.clothoidContext;
    Scalar lambda = LAMBDAS.Get(jSlider.getValue());
    try {
      jTextField.setBackground(new Color(128 + 64, 255, 128 + 64));
      lambda = Scalars.fromString(jTextField.getText());
      if (!(lambda instanceof RealScalar))
        throw new IllegalArgumentException();
    } catch (Exception exception) {
      jTextField.setBackground(new Color(255, 128 + 64, 128 + 64));
    }
    {
      geometricLayer.pushMatrix(GfxMatrix.translation(Tensors.of(clothoidContext.s1(), clothoidContext.s2())));
      graphics.setColor(Color.RED);
      graphics.fill(geometricLayer.toPath2D(CirclePoints.of(8).multiply(RealScalar.of(0.1))));
      geometricLayer.popMatrix();
    }
    Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
    GeometricLayer plotLayer = new GeometricLayer(Tensors.matrix(new Number[][] { //
        { 30, 0, dimension.width / 2 }, //
        { 0, -30, 200 }, //
        { 0, 0, 1 } }));
    for (Tensor _lambda : clothoidDefectContainer.search.lambdas()) {
      ClothoidBuilder clothoidBuilder = CustomClothoidBuilder.of((Scalar) _lambda);
      ClothoidTransition clothoidTransition = ClothoidTransition.of(clothoidBuilder, clothoidContext.p(), clothoidContext.q());
      Tensor points = clothoidTransition.linearized(MIN_RESOLUTION);
      new PathRender(new Color(64, 255, 64, 64)).setCurve(points, false).render(geometricLayer, graphics);
    }
    try {
      ClothoidBuilder clothoidBuilder = CustomClothoidBuilder.of(lambda);
      ClothoidTransition clothoidTransition = ClothoidTransition.of(clothoidBuilder, clothoidContext.p(), clothoidContext.q());
      Tensor points = clothoidTransition.linearized(MIN_RESOLUTION);
      PATH_RENDER.setCurve(points, false).render(geometricLayer, graphics);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("===");
      System.out.println(lambda);
      System.out.println(clothoidContext.p());
      System.out.println(clothoidContext.q());
    }
    // ---
    {
      LeversRender leversRender = LeversRender.of(manifoldDisplay(), getGeodesicControlPoints(), null, geometricLayer, graphics);
      leversRender.renderSequence();
      leversRender.renderIndexP();
    }
    {
      GridRender gridRender = new GridRender(Subdivide.of(-20, 20, 10), Subdivide.of(-3, 3, 6));
      gridRender.render(plotLayer, graphics);
      clothoidDefectContainer.render(plotLayer, graphics);
      graphics.setColor(Color.RED);
      plotLayer.pushMatrix(GfxMatrix.translation(Tensors.of(lambda, lambda.zero())));
      graphics.setStroke(new BasicStroke(2f));
      graphics.fill(plotLayer.toPath2D(POINTER, true));
      plotLayer.popMatrix();
      // ---
      Scalar s1 = clothoidContext.b0().add(clothoidContext.b1()).multiply(RationalScalar.HALF);
      Scalar reifs = MidpointTangentApproximation.ORDER2.apply(clothoidContext.b0(), clothoidContext.b1()).subtract(s1);
      graphics.setColor(Color.CYAN);
      graphics.draw(plotLayer.toLine2D(Tensors.of(reifs, RealScalar.ZERO), Tensors.of(reifs, RealScalar.ONE.negate())));
      graphics.setStroke(new BasicStroke(1f));
    }
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    setLambda(LAMBDAS.Get(jSlider.getValue()));
  }

  public void setLambda(Scalar lambda) {
    jTextField.setText(lambda.map(Round._6).toString());
  }

  public static void main(String[] args) {
    new CustomClothoidDemo().setVisible(1200, 900);
  }
}
