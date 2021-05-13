// code by jph
package ch.alpine.sophus.app.srf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.sophus.gui.win.ControlPointsDemo;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.sophus.ref.d2.CatmullClarkRefinement;
import ch.alpine.sophus.ref.d2.SurfaceMeshRefinement;
import ch.alpine.sophus.srf.SurfaceMesh;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.io.Primitives;

/* package */ class SurfaceMeshDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED_DRAW = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final ColorDataIndexed COLOR_DATA_INDEXED_FILL = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  // ---
  private final JToggleButton ctrl = new JToggleButton("ctrl");
  private final JToggleButton axes = new JToggleButton("axes");
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final SurfaceMesh surfaceMesh = SurfaceMeshExamples.quads6();

  public SurfaceMeshDemo() {
    super(false, ManifoldDisplays.SE2C_R2);
    ctrl.setSelected(true);
    timerFrame.jToolBar.add(ctrl);
    // ---
    timerFrame.jToolBar.add(axes);
    setControlPointsSe2(surfaceMesh.vrt);
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4));
    spinnerRefine.setValue(2);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (axes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    surfaceMesh.vrt = getControlPointsSe2();
    RenderQuality.setQuality(graphics);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    SurfaceMeshRefinement surfaceMeshRefinement = //
        CatmullClarkRefinement.of(manifoldDisplay.biinvariantMean());
    // surfaceMeshRefinement = DooSabinRefinement.of(geodesicDisplay.biinvariantMean());
    SurfaceMesh refine = surfaceMesh;
    for (int count = 0; count < spinnerRefine.getValue(); ++count)
      refine = surfaceMeshRefinement.refine(refine);
    for (Tensor polygon : refine.polygons()) {
      Path2D path2d = geometricLayer.toPath2D(polygon);
      path2d.closePath();
      graphics.setColor(COLOR_DATA_INDEXED_DRAW.getColor(0));
      graphics.draw(path2d);
      graphics.setColor(COLOR_DATA_INDEXED_FILL.getColor(0));
      graphics.fill(path2d);
    }
    graphics.setColor(new Color(192, 192, 192, 192));
    Tensor shape = manifoldDisplay.shape().multiply(RealScalar.of(0.5));
    for (Tensor mean : refine.vrt) {
      geometricLayer.pushMatrix(manifoldDisplay.matrixLift(mean));
      graphics.fill(geometricLayer.toPath2D(shape));
      geometricLayer.popMatrix();
    }
    if (ctrl.isSelected()) {
      Geodesic geodesicInterface = manifoldDisplay.geodesicInterface();
      Tensor domain = Subdivide.of(0.0, 1.0, 10);
      Set<Tensor> set = new HashSet<>();
      for (Tensor ind : surfaceMesh.ind) {
        int[] array = Primitives.toIntArray(ind);
        for (int index = 0; index < array.length; ++index) {
          int beg = array[index];
          int end = array[(index + 1) % array.length];
          if (set.add(Sort.of(Tensors.vector(beg, end)))) {
            ScalarTensorFunction scalarTensorFunction = //
                geodesicInterface.curve(surfaceMesh.vrt.get(beg), surfaceMesh.vrt.get(end));
            Tensor points = domain.map(scalarTensorFunction);
            new PathRender(new Color(0, 0, 255, 128), 1.5f).setCurve(points, false).render(geometricLayer, graphics);
          }
        }
      }
      renderControlPoints(geometricLayer, graphics);
    }
  }

  public static void main(String[] args) {
    new SurfaceMeshDemo().setVisible(1200, 600);
  }
}
