// code by jph
package ch.alpine.sophus.app.lev;

import java.awt.Graphics2D;
import java.util.Optional;

import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.owl.math.noise.SimplexContinuousNoise;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.sophus.lie.LieGroupOps;
import ch.alpine.sophus.math.TensorMapping;
import ch.alpine.sophus.opt.LogWeightings;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Timing;

// TODO refactor with S2AnimationDemo
/* package */ class Se2CoveringAnimationDemo extends LogWeightingDemo {
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  private final JToggleButton jToggleAnimate = new JToggleButton("animate");
  private final Timing timing = Timing.started();
  // ---
  private Tensor snapshotUncentered;
  private Tensor snapshot;

  public Se2CoveringAnimationDemo() {
    super(true, ManifoldDisplays.SE2C_SE2, LogWeightings.list());
    timerFrame.jToolBar.add(jToggleAxes);
    {
      jToggleAnimate.addActionListener(e -> {
        if (jToggleAnimate.isSelected()) {
          snapshotUncentered = getControlPointsSe2();
          Tensor sequence = getGeodesicControlPoints();
          if (0 < sequence.length()) {
            Tensor origin = sequence.get(0);
            LieGroup lieGroup = manifoldDisplay().lieGroup();
            Tensor shift = lieGroup.element(origin).inverse().toCoordinate();
            snapshot = new LieGroupOps(lieGroup).actionL(shift).slash(sequence);
          }
        } else
          setControlPointsSe2(snapshotUncentered);
      });
      timerFrame.jToolBar.add(jToggleAnimate);
    }
    setLogWeighting(LogWeightings.DISTANCES);
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1.5, -1, -1}, {2, 1, 1}, {-0.5, 1.5, 2}, {-1, -1.5, -2}, {-1.5, 0, 0.3}}"));
  }

  private static Tensor random(double toc, int index) {
    return Tensors.vector( //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 0), //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 1), //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 2));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    LieGroup lieGroup = manifoldDisplay.lieGroup();
    LieGroupOps lieGroupOps = new LieGroupOps(lieGroup);
    Optional<Tensor> optional = getOrigin();
    if (optional.isPresent()) {
      if (jToggleAnimate.isSelected())
        setControlPointsSe2(lieGroupOps.conjugation(random(10 + timing.seconds() * 0.1, 0)).slash(snapshot));
      RenderQuality.setQuality(graphics);
      Tensor sequence = getSequence();
      Tensor origin = optional.get();
      LeversHud.render( //
          Bitype.METRIC1, //
          LeversRender.of(manifoldDisplay, sequence, origin, geometricLayer, graphics));
      TensorMapping actionL = lieGroupOps.actionL(Tensors.vector(7, 0, 0));
      LeversHud.render( //
          bitype(), //
          LeversRender.of(manifoldDisplay, actionL.slash(sequence), actionL.apply(origin), geometricLayer, graphics));
    }
  }

  public static void main(String[] args) {
    new Se2CoveringAnimationDemo().setVisible(1200, 600);
  }
}
