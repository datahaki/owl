// code by jph
package ch.alpine.sophus.demo.lev;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Deque;
import java.util.Optional;

import org.jfree.chart.JFreeChart;

import ch.alpine.java.fig.ListPlot;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.java.ref.gui.PanelFieldsEditor;
import ch.alpine.java.ren.PathRender;
import ch.alpine.java.ren.PointsRender;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.sophus.crv.d2.OriginEnclosureQ;
import ch.alpine.sophus.demo.bd2.GenesisDequeProperties;
import ch.alpine.sophus.gbc.it.Evaluation;
import ch.alpine.sophus.gbc.it.GenesisDeque;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.hs.HsDesign;
import ch.alpine.sophus.hs.VectorLogManifold;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.PadRight;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.lie.r2.ConvexHull;

/* package */ class TangentSpaceDemo extends AbstractPlaceDemo {
  private static final int WIDTH = 300;
  // ---
  private final GenesisDequeProperties iterativeAffineProperties = new GenesisDequeProperties();

  public TangentSpaceDemo() {
    super(true, ManifoldDisplays.R2_ONLY);
    // ---
    Container container = timerFrame.jFrame.getContentPane();
    PanelFieldsEditor fieldsPanel = new PanelFieldsEditor(iterativeAffineProperties);
    container.add("West", fieldsPanel.getJScrollPane());
    Tensor sequence = Tensor.of(CirclePoints.of(15).multiply(RealScalar.of(2)).stream().skip(5).map(PadRight.zeros(3)));
    sequence.set(Scalar::zero, 0, Tensor.ALL);
    setControlPointsSe2(sequence);
  }

  private static final PointsRender POINTS_RENDER = //
      new PointsRender(new Color(0, 128, 128, 64), new Color(0, 128, 128, 96));

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    Optional<Tensor> optional = getOrigin();
    if (optional.isPresent()) {
      Tensor origin = optional.get();
      VectorLogManifold vectorLogManifold = manifoldDisplay().hsManifold();
      final Tensor sequence = getSequence();
      HsDesign hsDesign = new HsDesign(vectorLogManifold);
      final Tensor levers2 = hsDesign.matrix(sequence, origin);
      {
        Tensor hull = ConvexHull.of(sequence);
        PathRender pathRender = new PathRender(new Color(0, 0, 255, 128));
        pathRender.setCurve(hull, true);
        pathRender.render(geometricLayer, graphics);
      }
      if (OriginEnclosureQ.INSTANCE.test(levers2)) {
        GenesisDeque dequeGenesis = (GenesisDeque) iterativeAffineProperties.genesis();
        Deque<Evaluation> deque = dequeGenesis.deque(levers2);
        {
          Tensor leversVirtual = deque.peekLast().factors().pmul(levers2);
          geometricLayer.pushMatrix(GfxMatrix.translation(origin));
          {
            graphics.setColor(Color.GRAY);
            for (int index = 0; index < levers2.length(); ++index) {
              Line2D line2d = geometricLayer.toLine2D(levers2.get(index), leversVirtual.get(index));
              graphics.draw(line2d);
            }
          }
          {
            LeversRender leversRender = LeversRender.of( //
                manifoldDisplay(), leversVirtual, origin.map(Scalar::zero), geometricLayer, graphics);
            leversRender.renderSequence(POINTS_RENDER);
            // Tensor weights = iterativeAffineCoordinate.origin(deque, levers2);
            // leversRender.renderWeights(weights);
          }
          geometricLayer.popMatrix();
          {
            VisualSet visualSet = new VisualSet();
            visualSet.setPlotLabel("Weights");
            Tensor domain = Range.of(0, deque.size());
            for (int index = 0; index < levers2.length(); ++index) {
              int fi = index;
              visualSet.add(domain, Tensor.of(deque.stream() //
                  .map(Evaluation::weights) //
                  .map(tensor -> tensor.Get(fi))));
            }
            JFreeChart jFreeChart = ListPlot.of(visualSet);
            jFreeChart.draw(graphics, new Rectangle2D.Double(0 * WIDTH, 0, WIDTH, 200));
          }
          {
            VisualSet visualSet = new VisualSet();
            visualSet.setPlotLabel("Factors");
            Tensor domain = Range.of(0, deque.size());
            for (int index = 0; index < levers2.length(); ++index) {
              int fi = index;
              visualSet.add(domain, Tensor.of(deque.stream() //
                  .map(Evaluation::factors) //
                  .map(tensor -> tensor.Get(fi))));
            }
            JFreeChart jFreeChart = ListPlot.of(visualSet);
            jFreeChart.draw(graphics, new Rectangle2D.Double(1 * WIDTH, 0, WIDTH, 200));
          }
        }
      }
      {
        LeversRender leversRender = LeversRender.of( //
            manifoldDisplay(), sequence, origin, geometricLayer, graphics);
        leversRender.renderSequence();
        leversRender.renderOrigin();
      }
    }
  }

  public static void main(String[] args) {
    new TangentSpaceDemo().setVisible(1300, 900);
  }
}
