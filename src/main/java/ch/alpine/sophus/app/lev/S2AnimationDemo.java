// code by jph
package ch.alpine.sophus.app.lev;

import java.awt.Graphics2D;
import java.util.Optional;

import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.sophus.app.opt.LogWeightings;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.lie.so3.Rodrigues;
import ch.alpine.sophus.math.noise.SimplexContinuousNoise;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.sca.Sign;

/* package */ class S2AnimationDemo extends LogWeightingDemo {
  private final JToggleButton jToggleAnimate = new JToggleButton("animate");
  private final Timing timing = Timing.started();
  // ---
  private Tensor snapshotUncentered;
  private Tensor snapshot;

  public S2AnimationDemo() {
    super(true, ManifoldDisplays.S2_ONLY, LogWeightings.list());
    {
      jToggleAnimate.addActionListener(e -> {
        if (jToggleAnimate.isSelected()) {
          snapshotUncentered = getControlPointsSe2();
          Tensor sequence = getGeodesicControlPoints();
          if (0 < sequence.length()) {
            snapshot = snapshotUncentered.copy();
          }
        } else
          setControlPointsSe2(snapshotUncentered);
      });
      timerFrame.jToolBar.add(jToggleAnimate);
    }
    setControlPointsSe2(Tensors.matrix(new Number[][] { //
        { 0.000, 0.000, 0.000 }, { 0.699, -0.521, 0.000 }, { 0.641, 0.634, 0.000 }, //
        { -0.320, 0.734, 0 }, { -0.067, -0.716, 0.000 }, { -0.768, 0.215, 0.000 } }));
  }

  private static Tensor random(double toc, int index) {
    return Tensors.vector( //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 0), //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 1), //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 2)).multiply(RealScalar.of(1));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Optional<Tensor> optional = getOrigin();
    if (optional.isPresent()) {
      if (jToggleAnimate.isSelected()) {
        Tensor vector = random(10 + timing.seconds() * 0.1, 0);
        Tensor vectorExp = Rodrigues.vectorExp(vector);
        Tensor list = Tensors.empty();
        for (Tensor xya : snapshot) {
          Tensor project = vectorExp.dot(manifoldDisplay.project(xya));
          Tensor xya_ = manifoldDisplay.toPoint(project).append(Sign.isNegative(project.Get(2)) //
              ? RealScalar.of(-1)
              : RealScalar.ZERO);
          list.append(xya_);
        }
        setControlPointsSe2(list);
      }
      RenderQuality.setQuality(graphics);
      Tensor sequence = getSequence();
      Tensor origin = optional.get();
      LeversRender leversRender = //
          LeversRender.of(manifoldDisplay, sequence, origin, geometricLayer, graphics);
      LeversHud.render(bitype(), leversRender);
    }
  }

  public static void main(String[] args) {
    new S2AnimationDemo().setVisible(1200, 600);
  }
}
