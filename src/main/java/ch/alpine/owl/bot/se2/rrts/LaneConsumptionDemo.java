// code by gjoel
package ch.alpine.owl.bot.se2.rrts;

import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.JButton;

import ch.alpine.ascona.curve.AbstractCurveDemo;
import ch.alpine.ascona.lev.LeversRender;
import ch.alpine.ascona.util.dis.ManifoldDisplay;
import ch.alpine.ascona.util.dis.Se2ClothoidDisplay;
import ch.alpine.ascona.util.dis.Se2CoveringClothoidDisplay;
import ch.alpine.ascona.util.dis.Se2CoveringDisplay;
import ch.alpine.ascona.util.dis.Se2Display;
import ch.alpine.ascona.util.win.BaseFrame;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.ref.util.ToolbarFieldsEditor;
import ch.alpine.java.win.DemoInterface;
import ch.alpine.owl.gui.ren.LaneRender;
import ch.alpine.owl.lane.LaneInterface;
import ch.alpine.owl.lane.StableLanes;
import ch.alpine.sophus.ref.d1.LaneRiesenfeldCurveSubdivision;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Serialization;

public class LaneConsumptionDemo extends AbstractCurveDemo implements DemoInterface {
  private final LaneRender laneRender = new LaneRender();
  private LaneInterface lane = null;

  @SafeVarargs
  public LaneConsumptionDemo(Consumer<LaneInterface>... consumers) {
    this(Arrays.asList(consumers));
  }

  public LaneConsumptionDemo(Collection<Consumer<LaneInterface>> consumers) {
    super(Arrays.asList( //
        Se2ClothoidDisplay.ANALYTIC, //
        Se2ClothoidDisplay.LEGENDRE, //
        Se2CoveringClothoidDisplay.INSTANCE, //
        Se2CoveringDisplay.INSTANCE, //
        Se2Display.INSTANCE));
    curvt = false;
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
    // ---
    timerFrame.jToolBar.addSeparator();
    JButton jButtonRun = new JButton("run");
    jButtonRun.addActionListener(l -> {
      if (Objects.nonNull(lane))
        consumers.forEach(consumer -> consumer.accept(lane));
    });
    timerFrame.jToolBar.add(jButtonRun);
  }

  @Override
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics, int degree, int levels, Tensor control) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    {
      LeversRender leversRender = LeversRender.of(manifoldDisplay, control, null, geometricLayer, graphics);
      leversRender.renderSequence();
      leversRender.renderIndexP();
    }
    LaneInterface lane = StableLanes.of( //
        control, //
        LaneRiesenfeldCurveSubdivision.of(manifoldDisplay.geodesicSpace(), degree)::string, //
        levels, width().multiply(RationalScalar.HALF));
    try {
      this.lane = Serialization.copy(lane);
    } catch (Exception exception) {
      // ---
    }
    laneRender.setLane(lane, false);
    laneRender.render(geometricLayer, graphics);
    return lane.midLane();
  }

  public final Scalar width() {
    return ratio.multiply(RealScalar.of(2.5));
  }

  public final Optional<LaneInterface> lane() {
    return Optional.ofNullable(lane);
  }

  public static void main(String[] args) {
    new LaneConsumptionDemo( //
        lane -> System.out.println("control points: " + lane.controlPoints().length()), //
        lane -> System.out.println("refined points: " + lane.midLane().length())).setVisible(1200, 900);
  }

  @Override
  public BaseFrame start() {
    return timerFrame;
  }
}
