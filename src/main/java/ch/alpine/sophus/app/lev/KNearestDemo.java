// code by jph
package ch.alpine.sophus.app.lev;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JTextField;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gds.Se2Display;
import ch.alpine.sophus.hs.VectorLogManifold;
import ch.alpine.sophus.lie.LieGroupOps;
import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.sophus.math.TensorMapping;
import ch.alpine.sophus.opt.LogWeightings;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Ordering;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.sca.Clips;

/* package */ class KNearestDemo extends LogWeightingDemo {
  private final SpinnerLabel<Integer> spinnerLength = new SpinnerLabel<>();
  private final JButton jButton = new JButton("shuffle");
  private final JTextField jTextField = new JTextField();

  public KNearestDemo() {
    super(true, ManifoldDisplays.MANIFOLDS, LogWeightings.list());
    {
      spinnerLength.addSpinnerListener(v -> shuffleSnap());
      spinnerLength.setList(Arrays.asList(9, 15, 20, 25, 50, 75));
      spinnerLength.setValue(9);
      spinnerLength.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "number of points");
    }
    jButton.addActionListener(l -> shuffleSnap());
    timerFrame.jToolBar.add(jButton);
    {
      jTextField.setText("{.3, 0, .6}");
      jTextField.setPreferredSize(new Dimension(100, 28));
      timerFrame.jToolBar.add(jTextField);
    }
    setGeodesicDisplay(Se2Display.INSTANCE);
    setLogWeighting(LogWeightings.DISTANCES);
    shuffleSnap();
  }

  private void shuffleSnap() {
    // Distribution distributionP = NormalDistribution.standard();
    Distribution distributionA = UniformDistribution.of(Clips.absolute(Pi.VALUE));
    Tensor sequence = RandomVariate.of(distributionA, spinnerLength.getValue(), 3);
    // sequence.set(s -> RandomVariate.of(distributionA), Tensor.ALL, 2);
    sequence.set(Scalar::zero, 0, Tensor.ALL);
    setControlPointsSe2(sequence);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Optional<Tensor> optional = getOrigin();
    if (optional.isPresent()) {
      Tensor sequence = getSequence();
      Tensor origin = optional.get();
      // ---
      render(geometricLayer, graphics, sequence, origin, "");
      LieGroupOps lieGroupOps = new LieGroupOps(Se2Group.INSTANCE);
      try {
        Tensor shift = Tensors.fromString(jTextField.getText());
        {
          geometricLayer.pushMatrix(Se2Matrix.translation(Tensors.vector(8, 0)));
          TensorMapping lieGroupOp = lieGroupOps.conjugation(shift);
          render(geometricLayer, graphics, lieGroupOp.slash(sequence), lieGroupOp.apply(origin), "'");
          geometricLayer.popMatrix();
        }
        {
          Tensor invert = lieGroupOps.inversion().apply(shift);
          geometricLayer.pushMatrix(Se2Matrix.translation(Tensors.vector(16, 0)));
          TensorMapping lieGroupOp = lieGroupOps.conjugation(invert);
          render(geometricLayer, graphics, lieGroupOp.slash(sequence), lieGroupOp.apply(origin), "\"");
          geometricLayer.popMatrix();
        }
      } catch (Exception exception) {
        System.err.println(exception);
      }
    }
  }

  public void render(GeometricLayer geometricLayer, Graphics2D graphics, Tensor sequence, Tensor origin, String p) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    VectorLogManifold vectorLogManifold = manifoldDisplay.hsManifold();
    TensorUnaryOperator tensorUnaryOperator = //
        logWeighting().operator(biinvariant(), vectorLogManifold, variogram(), sequence);
    Tensor weights = tensorUnaryOperator.apply(origin);
    // ---
    int[] integers = Ordering.INCREASING.of(weights);
    Tensor shape = manifoldDisplay.shape();
    for (int index = 0; index < sequence.length(); ++index) {
      Tensor point = sequence.get(integers[index]);
      geometricLayer.pushMatrix(manifoldDisplay.matrixLift(point));
      Path2D path2d = geometricLayer.toPath2D(shape, true);
      graphics.setColor(index < 3 ? new Color(64, 192, 64, 64) : new Color(192, 64, 64, 64));
      graphics.fill(path2d);
      graphics.setColor(index < 3 ? new Color(64, 192, 64, 255) : new Color(192, 64, 64, 255));
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    {
      geometricLayer.pushMatrix(manifoldDisplay.matrixLift(origin));
      Path2D path2d = geometricLayer.toPath2D(shape, true);
      graphics.setColor(Color.DARK_GRAY);
      graphics.fill(path2d);
      graphics.setColor(Color.BLACK);
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    LeversRender leversRender = //
        LeversRender.of(manifoldDisplay, sequence, origin, geometricLayer, graphics);
    leversRender.renderIndexX("x" + p);
    leversRender.renderIndexP("p" + p);
  }

  public static void main(String[] args) {
    new KNearestDemo().setVisible(1200, 600);
  }
}
