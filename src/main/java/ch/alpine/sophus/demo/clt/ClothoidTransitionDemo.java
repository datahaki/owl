// code by jph
package ch.alpine.sophus.demo.clt;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.JFreeChart;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.fig.ListPlot;
import ch.alpine.java.fig.VisualRow;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldClip;
import ch.alpine.java.ref.ann.FieldSlider;
import ch.alpine.java.ref.gui.ToolbarFieldsEditor;
import ch.alpine.owl.bot.se2.rrts.ClothoidTransition;
import ch.alpine.sophus.clt.Clothoid;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.demo.ControlPointsDemo;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gds.Se2Display;
import ch.alpine.sophus.lie.rn.RnLineDistance;
import ch.alpine.sophus.math.Extract2D;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.sophus.math.TripleReduceExtrapolation;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;

public class ClothoidTransitionDemo extends ControlPointsDemo {
  private static final Stroke STROKE = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  private static final TripleReduceExtrapolation TRIPLE_REDUCE_EXTRAPOLATION = new TripleReduceExtrapolation() {
    @Override
    protected Scalar reduce(Tensor p, Tensor q, Tensor r) {
      return RnLineDistance.INSTANCE.tensorNorm(p, r).norm(q);
    }
  };
  // ---
  public Boolean ctrl = true;
  @FieldSlider
  @FieldClip(min = "0.01", max = "1")
  public Scalar beta = RealScalar.of(0.2);
  public Boolean smpl = true;
  public Boolean plot = true;

  public ClothoidTransitionDemo() {
    super(true, ManifoldDisplays.CL_ONLY);
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
    // ---
    setControlPointsSe2(RandomVariate.of(UniformDistribution.of(0, 8), 1 * 2, 3));
    timerFrame.geometricComponent.setOffset(100, 700);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor sequence = getGeodesicControlPoints();
    Geodesic geodesic = manifoldDisplay().geodesic();
    ClothoidBuilder clothoidBuilder = (ClothoidBuilder) geodesic;
    VisualSet visualSet = new VisualSet();
    for (int index = 0; index < sequence.length() - 1; index += 2) {
      Tensor cr = sequence.get(index + 0);
      Tensor l1 = sequence.get(index + 1);
      Clothoid clothoid = clothoidBuilder.curve(cr, l1);
      ClothoidTransition clothoidTransition = ClothoidTransition.of(cr, l1, clothoid);
      Tensor samples = clothoidTransition.linearized_samples(beta);
      Tensor linearized = samples.map(clothoid);
      graphics.setColor(ColorDataLists._097.strict().getColor(index / 2));
      graphics.setStroke(new BasicStroke(2));
      graphics.draw(geometricLayer.toPath2D(linearized));
      if (smpl)
        POINTS_RENDER_1.show( //
            Se2Display.INSTANCE::matrixLift, //
            Se2Display.INSTANCE.shape(), //
            linearized).render(geometricLayer, graphics);
      if (plot) {
        Tensor vector = TRIPLE_REDUCE_EXTRAPOLATION.apply(Tensor.of(linearized.stream().map(Extract2D.FUNCTION)));
        VisualRow visualRow = visualSet.add(samples, vector);
        visualRow.setStroke(STROKE);
      }
    }
    if (plot) {
      JFreeChart jFreeChart = ListPlot.of(visualSet, true);
      jFreeChart.draw(graphics, new Rectangle2D.Double(0, 0, 400, 300));
    }
    if (ctrl)
      renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new ClothoidTransitionDemo().setVisible(1000, 800);
  }
}
