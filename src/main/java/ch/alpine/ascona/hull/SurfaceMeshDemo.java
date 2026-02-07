// code by jph
package ch.alpine.ascona.hull;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.HashSet;
import java.util.Set;

import ch.alpine.ascona.ref.d2.SurfaceMeshRefinements;
import ch.alpine.ascony.dis.ManifoldDisplay;
import ch.alpine.ascony.dis.ManifoldDisplays;
import ch.alpine.ascony.ref.AsconaParam;
import ch.alpine.ascony.ren.PathRender;
import ch.alpine.ascony.ren.SurfaceMeshRender;
import ch.alpine.ascony.win.ControlPointsDemo;
import ch.alpine.bridge.awt.RenderQuality;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.ref.ann.FieldClip;
import ch.alpine.bridge.ref.ann.FieldPreferredWidth;
import ch.alpine.bridge.ref.ann.FieldSlider;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.qhull3d.PlatonicSolid;
import ch.alpine.sophis.crv.d2.Extract2D;
import ch.alpine.sophis.crv.d2.PolygonArea;
import ch.alpine.sophis.ref.d2.SurfaceMeshRefinement;
import ch.alpine.sophis.srf.SurfaceMesh;
import ch.alpine.sophus.hs.GeodesicSpace;
import ch.alpine.sophus.hs.HomogeneousSpace;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.sca.Sign;

public class SurfaceMeshDemo extends ControlPointsDemo {
  public static SurfaceMesh surfaceMesh(PlatonicSolid platonicSolid) {
    return new SurfaceMesh(platonicSolid.vertices(), platonicSolid.faces());
  }

  private static final ColorDataIndexed COLOR_DATA_INDEXED_DRAW = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final ColorDataIndexed COLOR_DATA_INDEXED_FILL = ColorDataLists._097.cyclic().deriveWithAlpha(192);

  @ReflectionMarker
  public static class Param extends AsconaParam {
    public Param() {
      super(false, ManifoldDisplays.SE2C_R2);
    }

    public Boolean ctrl = true;
    public SurfaceMeshRefinements ref = SurfaceMeshRefinements.CATMULL_CLARK;
    @FieldSlider
    @FieldPreferredWidth(100)
    @FieldClip(min = "0", max = "4")
    public Integer refine = 2;
  }

  private final Param param;
  private final SurfaceMesh surfaceMesh; // = SurfaceMeshExamples.mixed11();

  // TODO ASCONA DEMO needs BM
  public SurfaceMeshDemo() {
    this(new Param());
  }

  public SurfaceMeshDemo(Param param) {
    super(param);
    this.param = param;
    // ---
    surfaceMesh = surfaceMesh(PlatonicSolid.ICOSAHEDRON);
    setControlPointsSe2(surfaceMesh.vrt);
    // ---
    timerFrame.geometricComponent.setOffset(100, 600);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    surfaceMesh.vrt = getControlPointsSe2();
    RenderQuality.setQuality(graphics);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    HomogeneousSpace homogeneousSpace = (HomogeneousSpace) manifoldDisplay.geodesicSpace();
    SurfaceMeshRefinement surfaceMeshRefinement = param.ref.operator(homogeneousSpace.biinvariantMean());
    SurfaceMesh refine = surfaceMesh;
    for (int count = 0; count < param.refine; ++count)
      refine = surfaceMeshRefinement.refine(refine);
    if (false) {
      for (int index = 0; index < refine.faces().size(); ++index) {
        Tensor polygon_face = Tensor.of(refine.polygon_face(refine.face(index)).stream().map(Extract2D.FUNCTION));
        Scalar area = PolygonArea.of(polygon_face);
        if (Sign.isNegativeOrZero(area))
          System.err.println("neg");
      }
    }
    if (param.manifoldDisplays.equals(ManifoldDisplays.R2))
      new SurfaceMeshRender(refine).render(geometricLayer, graphics);
    else {
      for (Tensor polygon : refine.polygons()) {
        Path2D path2d = geometricLayer.toPath2D(polygon);
        path2d.closePath();
        graphics.setColor(COLOR_DATA_INDEXED_DRAW.getColor(0));
        graphics.draw(path2d);
        graphics.setColor(COLOR_DATA_INDEXED_FILL.getColor(0));
        graphics.fill(path2d);
      }
    }
    {
      // TODO ASCONA levers render
      graphics.setColor(new Color(192, 192, 192, 192));
      Tensor shape = manifoldDisplay.shape().multiply(RealScalar.of(0.5));
      for (Tensor mean : refine.vrt) {
        geometricLayer.pushMatrix(manifoldDisplay.matrixLift(mean));
        graphics.fill(geometricLayer.toPath2D(shape));
        geometricLayer.popMatrix();
      }
    }
    if (param.ctrl) {
      GeodesicSpace geodesicSpace = manifoldDisplay.geodesicSpace();
      Tensor domain = Subdivide.of(0.0, 1.0, 10);
      Set<Tensor> set = new HashSet<>();
      for (int[] array : surfaceMesh.faces()) {
        for (int index = 0; index < array.length; ++index) {
          int beg = array[index];
          int end = array[(index + 1) % array.length];
          if (set.add(Sort.of(Tensors.vector(beg, end)))) {
            ScalarTensorFunction scalarTensorFunction = //
                geodesicSpace.curve(surfaceMesh.vrt.get(beg), surfaceMesh.vrt.get(end));
            Tensor points = domain.map(scalarTensorFunction);
            new PathRender(new Color(0, 0, 255, 128), 1.5f).setCurve(points, false).render(geometricLayer, graphics);
          }
        }
      }
    }
  }

  static void main() {
    launch();
  }
}
