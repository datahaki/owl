// code by jph
package ch.alpine.owl.bot.se2.rrts;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.ascona.util.api.ControlPointsDemo;
import ch.alpine.ascona.util.dis.ManifoldDisplay;
import ch.alpine.ascona.util.dis.ManifoldDisplays;
import ch.alpine.ascona.util.ren.AxesRender;
import ch.alpine.ascona.util.ren.LeversRender;
import ch.alpine.ascona.util.win.LookAndFeels;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.ref.ann.FieldClip;
import ch.alpine.bridge.ref.ann.FieldInteger;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.bridge.ref.util.ToolbarFieldsEditor;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeTransition;
import ch.alpine.sophus.crv.dub.DubinsPathComparators;
import ch.alpine.sophus.crv.dub.DubinsTransitionSpace;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;

public class Se2RrtsNodeCollectionDemo extends ControlPointsDemo {
  private static final int SIZE = 400;
  private static final CoordinateBoundingBox ND_BOX_R2 = CoordinateBounds.of( //
      Tensors.vector(-5, -5), //
      Tensors.vector(+5, +5));
  private static final CoordinateBoundingBox ND_BOX_SE2 = CoordinateBounds.of( //
      Tensors.vector(-5, -5, -Math.PI), //
      Tensors.vector(+5, +5, +Math.PI));
  // ---
  private final Se2RrtsNodeCollection se2RrtsNodeCollection = new Se2RrtsNodeCollection( //
      // ClothoidTransitionSpace.ANALYTIC, //
      DubinsTransitionSpace.of(RealScalar.of(0.3), DubinsPathComparators.LENGTH), //
      ND_BOX_R2, 3);

  // ---
  @ReflectionMarker
  public static class Param {
    public Boolean limit = true;
    @FieldInteger
    @FieldClip(min = "1", max = "50")
    public Scalar value = RealScalar.of(3);
  }

  public final Param param = new Param();

  public Se2RrtsNodeCollectionDemo() {
    super(false, ManifoldDisplays.CL_ONLY);
    // DubinsTransitionSpace.of(RealScalar.of(0.3), DubinsPathComparators.LENGTH);
    ToolbarFieldsEditor.add(param, timerFrame.jToolBar);
    // ---
    renderInterface.setPositioningEnabled(false);
    renderInterface.setMidpointIndicated(false);
    // ---
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(ND_BOX_SE2);
    Tensor tensor = RandomSample.of(randomSampleInterface, SIZE);
    for (Tensor state : tensor) {
      se2RrtsNodeCollection.insert(RrtsNode.createRoot(state, RealScalar.ONE));
    }
    setControlPointsSe2(tensor);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    // ---
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor mouse = timerFrame.geometricComponent.getMouseSe2CState();
    LeversRender leversRender = LeversRender.of(manifoldDisplay, getGeodesicControlPoints(), mouse, geometricLayer, graphics);
    leversRender.renderSequence();
    leversRender.renderOrigin();
    // ---
    int _value = Scalars.intValueExact(param.value);
    graphics.setColor(new Color(255, 0, 0, 128));
    Scalar minResolution = RealScalar.of(geometricLayer.pixel2modelWidth(10));
    for (RrtsNodeTransition rrtsNodeTransition : se2RrtsNodeCollection.nearFrom(mouse, _value))
      graphics.draw(geometricLayer.toPath2D(rrtsNodeTransition.transition().linearized(minResolution)));
    // ---
    graphics.setColor(new Color(0, 255, 0, 128));
    for (RrtsNodeTransition rrtsNodeTransition : se2RrtsNodeCollection.nearTo(mouse, _value))
      graphics.draw(geometricLayer.toPath2D(rrtsNodeTransition.transition().linearized(minResolution)));
  }

  public static void main(String[] args) {
    LookAndFeels.LIGHT.tryUpdateUI();
    new Se2RrtsNodeCollectionDemo().setVisible(1200, 800);
  }
}
