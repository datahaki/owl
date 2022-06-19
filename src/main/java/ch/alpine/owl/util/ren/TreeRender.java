// code by jph
package ch.alpine.owl.util.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Objects;

import ch.alpine.ascona.util.ren.EmptyRender;
import ch.alpine.ascona.util.win.RenderInterface;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.data.tree.StateCostNode;
import ch.alpine.owl.math.VectorScalar;
import ch.alpine.sophus.hs.r2.Extract2D;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.lie.r2.ConvexHull;
import ch.alpine.tensor.red.ScalarSummaryStatistics;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clip;

/** renders the edges between nodes
 * 
 * the edges are drawn as straight lines with the color of the cost to root
 * 
 * only real-valued costs are supported
 * in particular costs of type {@link VectorScalar} are not supported
 * 
 * @see EdgeRender */
public class TreeRender implements RenderInterface {
  public static final int LIMIT_DEFAULT = 2500;
  private static final int NODE_WIDTH = 2;
  private static final Color CONVEX_HULL_COLOR = new Color(192, 192, 0, 128);
  // ---
  private final int nodeBound;
  private RenderInterface renderInterface = EmptyRender.INSTANCE;

  public TreeRender(int nodeBound) {
    this.nodeBound = nodeBound;
  }

  public TreeRender() {
    this(LIMIT_DEFAULT);
  }

  public RenderInterface setCollection(Collection<? extends StateCostNode> collection) {
    return renderInterface = Objects.isNull(collection) || collection.isEmpty() //
        ? EmptyRender.INSTANCE
        : new Render(collection);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    renderInterface.render(geometricLayer, graphics);
  }

  private class Render implements RenderInterface {
    private final Collection<? extends StateCostNode> collection;
    private final Tensor polygon;
    private final TreeColor treeColor;
    private final Clip clip;
    private final long count;
    private final Scalar inverse;

    public Render(Collection<? extends StateCostNode> collection) {
      this.collection = collection;
      polygon = ConvexHull.of(collection.stream().map(StateCostNode::state).map(Extract2D.FUNCTION), Chop._10);
      treeColor = TreeColor.ofDimensions(collection.iterator().next().state().length());
      ScalarSummaryStatistics scalarSummaryStatistics = collection.stream() //
          .map(StateCostNode::costFromRoot) //
          .filter(FiniteScalarQ::of) //
          .collect(ScalarSummaryStatistics.collector());
      clip = scalarSummaryStatistics.getClip();
      count = scalarSummaryStatistics.getCount();
      inverse = RealScalar.of(treeColor.nodeColor.length() - 1);
    }

    @Override // from RenderInterface
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      graphics.setColor(CONVEX_HULL_COLOR);
      Path2D path2D = geometricLayer.toPath2D(polygon);
      path2D.closePath();
      graphics.draw(path2D);
      // ---
      if (count <= nodeBound) // don't draw tree beyond certain node count
        for (StateCostNode node : collection) {
          int interp = clip.rescale(node.costFromRoot()).multiply(inverse).number().intValue();
          graphics.setColor(treeColor.nodeColor.getColor(interp));
          final Point2D p1 = geometricLayer.toPoint2D(node.state());
          graphics.fill(new Rectangle2D.Double(p1.getX(), p1.getY(), NODE_WIDTH, NODE_WIDTH));
          StateCostNode parent = node.parent();
          if (Objects.nonNull(parent)) {
            Point2D p2 = geometricLayer.toPoint2D(parent.state());
            graphics.setColor(treeColor.edgeColor.getColor(interp));
            Shape shape = new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            graphics.draw(shape);
          }
        }
    }
  }
}