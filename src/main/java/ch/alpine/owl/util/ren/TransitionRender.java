// code by jph
package ch.alpine.owl.util.ren;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Collection;
import java.util.Objects;

import ch.alpine.ascony.ren.EmptyRender;
import ch.alpine.ascony.ren.RenderInterface;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.data.tree.StateCostNode;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.sophis.ts.Transition;
import ch.alpine.sophis.ts.TransitionSpace;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.red.MinMax;
import ch.alpine.tensor.sca.Clip;

/** renders the edges between nodes
 * 
 * the edges are drawn as lines with the color of the cost to root
 * 
 * @see EdgeRender */
public class TransitionRender implements RenderInterface {
  private final TransitionSpace transitionSpace;
  private RenderInterface renderInterface = EmptyRender.INSTANCE;

  public TransitionRender(TransitionSpace transitionSpace) {
    this.transitionSpace = transitionSpace;
  }

  public RenderInterface setCollection(Collection<? extends RrtsNode> collection) {
    return renderInterface = Objects.isNull(collection) || collection.isEmpty() //
        ? EmptyRender.INSTANCE
        : new Render(collection);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    renderInterface.render(geometricLayer, graphics);
  }

  private class Render implements RenderInterface {
    private final ColorDataIndexed colorDataIndexed = TreeColor.LO.edgeColor;
    private final Collection<? extends RrtsNode> collection;
    private final Clip clip;
    private final Scalar inverse;

    public Render(Collection<? extends RrtsNode> collection) {
      this.collection = collection;
      clip = collection.stream() //
          .map(StateCostNode::costFromRoot) //
          .filter(FiniteScalarQ::of) //
          .collect(MinMax.toClip());
      inverse = RealScalar.of(colorDataIndexed.length() - 1);
    }

    @Override // from RenderInterface
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      double pixel2modelWidth = geometricLayer.pixel2modelWidth(5);
      Scalar minResolution = RealScalar.of(pixel2modelWidth); // units!
      for (RrtsNode parent : collection)
        for (RrtsNode child : parent.children()) {
          int interp = clip.rescale(child.costFromRoot()).multiply(inverse).number().intValue();
          graphics.setColor(colorDataIndexed.getColor(interp));
          Transition transition = transitionSpace.connect(parent.state(), child.state());
          Path2D path2d = geometricLayer.toPath2D(transition.linearized(minResolution));
          graphics.draw(path2d);
        }
    }
  }
}
