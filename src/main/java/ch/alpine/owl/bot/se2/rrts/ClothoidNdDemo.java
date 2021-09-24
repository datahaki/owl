// code by jph
package ch.alpine.owl.bot.se2.rrts;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JScrollPane;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldClip;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.gui.FieldsEditor;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.Transition;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.win.ControlPointsDemo;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.opt.nd.NdBox;

public class ClothoidNdDemo extends ControlPointsDemo {
  private static final int SIZE = 400;
  private static final NdBox ND_BOX = NdBox.of( //
      Tensors.vector(-5, -5, -Math.PI), //
      Tensors.vector(+5, +5, +Math.PI));
  // ---
  private final RrtsNodeCollection rrtsNodeCollection1 = //
      Se2RrtsNodeCollections.of(ClothoidTransitionSpace.ANALYTIC, ND_BOX);
  private final RrtsNodeCollection rrtsNodeCollection2 = //
      ClothoidRrtsNodeCollections.of(RealScalar.ONE, ND_BOX);

  // ---
  public static class Param {
    public Boolean limit = true;
    @FieldInteger
    @FieldClip(min = "1", max = "50")
    public Scalar value = RealScalar.of(3);
  }

  public final Param param = new Param();

  public ClothoidNdDemo() {
    super(false, ManifoldDisplays.CL_ONLY);
    // ---
    Container container = timerFrame.jFrame.getContentPane();
    JScrollPane jScrollPane = new FieldsEditor(param).getJScrollPane();
    jScrollPane.setPreferredSize(new Dimension(120, 200));
    container.add("West", jScrollPane);
    // ---
    setPositioningEnabled(false);
    setMidpointIndicated(false);
    // ---
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(ND_BOX);
    Tensor tensor = RandomSample.of(randomSampleInterface, SIZE);
    for (Tensor state : tensor) {
      rrtsNodeCollection1.insert(RrtsNode.createRoot(state, RealScalar.ZERO));
      rrtsNodeCollection2.insert(RrtsNode.createRoot(state, RealScalar.ZERO));
    }
    setControlPointsSe2(tensor);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    // ---
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor shape = manifoldDisplay.shape().multiply(RealScalar.of(10 / Math.sqrt(SIZE)));
    Color color_fill = new Color(255, 128, 128, 64);
    Color color_draw = new Color(255, 128, 128, 255);
    for (Tensor point : getGeodesicControlPoints()) {
      geometricLayer.pushMatrix(manifoldDisplay.matrixLift(point));
      Path2D path2d = geometricLayer.toPath2D(shape);
      path2d.closePath();
      graphics.setColor(color_fill);
      graphics.fill(path2d);
      graphics.setColor(color_draw);
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    Tensor mouse = timerFrame.geometricComponent.getMouseSe2CState();
    {
      geometricLayer.pushMatrix(manifoldDisplay.matrixLift(mouse));
      Path2D path2d = geometricLayer.toPath2D(shape);
      path2d.closePath();
      graphics.setColor(Color.CYAN);
      graphics.fill(path2d);
      graphics.setColor(Color.BLUE);
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    // ---
    RrtsNodeCollection rrtsNodeCollection = param.limit //
        ? rrtsNodeCollection2
        : rrtsNodeCollection1;
    int _value = Scalars.intValueExact(param.value);
    graphics.setColor(new Color(255, 0, 0, 128));
    ClothoidBuilder clothoidBuilder = (ClothoidBuilder) manifoldDisplay.geodesicInterface();
    Scalar minResolution = RealScalar.of(geometricLayer.pixel2modelWidth(10));
    for (RrtsNode rrtsNode : rrtsNodeCollection.nearTo(mouse, _value)) {
      Tensor other = rrtsNode.state();
      Transition transition = ClothoidTransition.of(clothoidBuilder, other, mouse);
      graphics.draw(geometricLayer.toPath2D(transition.linearized(minResolution)));
    }
    // ---
    graphics.setColor(new Color(0, 255, 0, 128));
    for (RrtsNode rrtsNode : rrtsNodeCollection.nearFrom(mouse, _value)) {
      Tensor other = rrtsNode.state();
      Transition transition = ClothoidTransition.of(clothoidBuilder, mouse, other);
      graphics.draw(geometricLayer.toPath2D(transition.linearized(minResolution)));
    }
  }

  public static void main(String[] args) {
    new ClothoidNdDemo().setVisible(1200, 800);
  }
}
