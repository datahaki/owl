// code by jph
package ch.alpine.sophus.demo.misc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.PointsRender;
import ch.alpine.java.util.DisjointSets;
import ch.alpine.javax.swing.SpinnerLabel;
import ch.alpine.sophus.api.Geodesic;
import ch.alpine.sophus.demo.lev.LogWeightingDemo;
import ch.alpine.sophus.demo.opt.LogWeightings;
import ch.alpine.sophus.fit.MinimumSpanningTree;
import ch.alpine.sophus.fit.MinimumSpanningTree.Edge;
import ch.alpine.sophus.fit.MinimumSpanningTree.EdgeComparator;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

/* package */ class MinimumSpanningTreeDemo extends LogWeightingDemo {
  final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();

  public MinimumSpanningTreeDemo() {
    super(true, ManifoldDisplays.SE2C_SE2_S2_H2_R2, Arrays.asList(LogWeightings.DISTANCES));
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    spinnerRefine.setValue(2);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    Distribution distribution = UniformDistribution.of(-4, 4);
    setControlPointsSe2(RandomVariate.of(distribution, 20, 3));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Geodesic geodesicInterface = manifoldDisplay.geodesic();
    RenderQuality.setQuality(graphics);
    Tensor sequence = getGeodesicControlPoints();
    Tensor domain = Subdivide.of(0.0, 1.0, 10);
    final int splits = spinnerRefine.getValue();
    DisjointSets disjointSets = DisjointSets.allocate(sequence.length());
    if (0 < sequence.length()) {
      Tensor matrix = distanceMatrix(sequence);
      List<Edge> list = MinimumSpanningTree.of(matrix);
      Collections.sort(list, new EdgeComparator(matrix));
      int count = Math.max(0, list.size() - splits);
      {
        for (Edge edge : list.subList(0, count))
          disjointSets.union(edge.i(), edge.j());
      }
      graphics.setColor(Color.BLACK);
      for (Edge edge : list.subList(0, count)) {
        Tensor p = sequence.get(edge.i());
        Tensor q = sequence.get(edge.j());
        ScalarTensorFunction curve = geodesicInterface.curve(p, q);
        Path2D line = geometricLayer.toPath2D(domain.map(curve));
        graphics.draw(line);
      }
    }
    Map<Integer, Integer> map = disjointSets.createMap(new AtomicInteger()::getAndIncrement);
    for (int index = 0; index < sequence.length(); ++index) {
      int unique = map.get(disjointSets.key(index));
      Color color = ColorDataLists._097.cyclic().getColor(unique);
      PointsRender pointsRender = new PointsRender(color, color);
      pointsRender.show(manifoldDisplay()::matrixLift, getControlPointShape(), Tensors.of(sequence.get(index))).render(geometricLayer, graphics);
    }
  }

  public Tensor distanceMatrix(Tensor sequence) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    TensorUnaryOperator tuo = biinvariant().distances(manifoldDisplay.hsManifold(), sequence);
    Tensor matrix = Tensor.of(sequence.stream().map(tuo));
    return SymmetricMatrixQ.of(matrix) //
        ? matrix
        : Symmetrize.of(matrix);
  }

  public static void main(String[] args) {
    new MinimumSpanningTreeDemo().setVisible(1000, 600);
  }
}
