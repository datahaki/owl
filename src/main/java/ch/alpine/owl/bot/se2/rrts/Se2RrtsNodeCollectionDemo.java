// code by jph
package ch.alpine.owl.bot.se2.rrts;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.ascony.dis.ManifoldDisplay;
import ch.alpine.ascony.dis.ManifoldDisplays;
import ch.alpine.ascony.ref.AsconaParam;
import ch.alpine.ascony.ren.LeversRender;
import ch.alpine.ascony.win.ControlPointsDemo;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.ref.ann.FieldClip;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeTransition;
import ch.alpine.sophis.crv.dub.DubinsPathComparators;
import ch.alpine.sophis.ts.DubinsTransitionSpace;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.opt.nd.BoxRandomSample;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;

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
  public static class Param extends AsconaParam {
    public Param() {
      super(false, ManifoldDisplays.CL_ONLY);
    }

    @FieldClip(min = "1", max = "20")
    public Integer value = 3;
  }

  private final Param param;
  private Tensor sequence;

  public Se2RrtsNodeCollectionDemo() {
    this(new Param());
  }

  public Se2RrtsNodeCollectionDemo(Param param) {
    super(param);
    this.param = param;
    // DubinsTransitionSpace.of(RealScalar.of(0.3), DubinsPathComparators.LENGTH);
    // ---
    controlPointsRender.setMidpointIndicated(false);
    // ---
    RandomSampleInterface randomSampleInterface = new BoxRandomSample(ND_BOX_SE2);
    sequence = RandomSample.of(randomSampleInterface, SIZE);
    for (Tensor state : sequence) {
      se2RrtsNodeCollection.insert(RrtsNode.createRoot(state, RealScalar.ONE));
    }
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}}"));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor mouse = getGeodesicControlPoints().get(0);
    LeversRender leversRender = LeversRender.of(manifoldDisplay, sequence, mouse, geometricLayer, graphics);
    leversRender.renderSequence();
    leversRender.renderOrigin();
    // ---
    int _value = param.value;
    graphics.setColor(new Color(255, 0, 0, 128));
    Scalar minResolution = RealScalar.of(geometricLayer.pixel2modelWidth(10));
    for (RrtsNodeTransition rrtsNodeTransition : se2RrtsNodeCollection.nearFrom(mouse, _value))
      graphics.draw(geometricLayer.toPath2D(rrtsNodeTransition.transition().linearized(minResolution)));
    // ---
    graphics.setColor(new Color(0, 255, 0, 128));
    for (RrtsNodeTransition rrtsNodeTransition : se2RrtsNodeCollection.nearTo(mouse, _value))
      graphics.draw(geometricLayer.toPath2D(rrtsNodeTransition.transition().linearized(minResolution)));
  }

  static void main() {
    new Se2RrtsNodeCollectionDemo().runStandalone();
  }
}
