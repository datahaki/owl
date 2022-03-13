// code by jph
package ch.alpine.sophus.demo.ref.d2;

import ch.alpine.sophus.srf.SurfaceMesh;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.r2.CirclePoints;

/** Hint:
 * implementation exists only for evaluation purposes
 * class may be removed in future releases */
/* package */ enum SurfaceMeshExamples {
  ;
  public static SurfaceMesh unitQuad() {
    SurfaceMesh surfaceMesh = new SurfaceMesh();
    CirclePoints.of(4).stream() //
        .map(xy -> xy.append(RealScalar.ZERO)) //
        .forEach(surfaceMesh::addVert);
    surfaceMesh.addFace(0, 1, 2, 3);
    return surfaceMesh;
  }

  public static SurfaceMesh quads2() {
    SurfaceMesh surfaceMesh = new SurfaceMesh();
    surfaceMesh.addVert(Tensors.vector(0, 0, 0)); // 0
    surfaceMesh.addVert(Tensors.vector(0, 2, 0)); // 1
    surfaceMesh.addVert(Tensors.vector(2, 0, 0)); // 2
    surfaceMesh.addVert(Tensors.vector(2, 2, 0)); // 3
    surfaceMesh.addVert(Tensors.vector(4, 0, 0)); // 4
    surfaceMesh.addVert(Tensors.vector(4, 2, 0)); // 5
    surfaceMesh.addFace(0, 2, 3, 1);
    surfaceMesh.addFace(2, 4, 5, 3);
    return surfaceMesh;
  }

  public static SurfaceMesh quads3() {
    SurfaceMesh surfaceMesh = quads2();
    surfaceMesh.addVert(Tensors.vector(0, 4, 0)); // 6
    surfaceMesh.addVert(Tensors.vector(2, 4, 0)); // 7
    surfaceMesh.addFace(1, 3, 7, 6);
    return surfaceMesh;
  }

  public static SurfaceMesh quads4() {
    SurfaceMesh surfaceMesh = quads3();
    surfaceMesh.addVert(Tensors.vector(4, 4, 0)); // 8
    surfaceMesh.addFace(3, 5, 8, 7);
    return surfaceMesh;
  }

  public static SurfaceMesh quads3Tri() {
    SurfaceMesh surfaceMesh = quads3();
    surfaceMesh.addFace(3, 5, 7);
    return surfaceMesh;
  }

  public static SurfaceMesh quads5() {
    SurfaceMesh surfaceMesh = quads3();
    surfaceMesh.addVert(Tensors.vector(3, 6, 0));
    surfaceMesh.addVert(Tensors.vector(4, 4, 0));
    surfaceMesh.addVert(Tensors.vector(6, 4, 0));
    surfaceMesh.addFace(3, 9, 8, 7);
    surfaceMesh.addFace(3, 5, 10, 9);
    return surfaceMesh;
  }

  public static SurfaceMesh quads6() {
    SurfaceMesh surfaceMesh = quads5();
    surfaceMesh.addVert(Tensors.vector(6, 6, 0));
    surfaceMesh.addFace(9, 10, 11, 8);
    return surfaceMesh;
  }
  public static SurfaceMesh quads7() {
    SurfaceMesh surfaceMesh = quads5();
    surfaceMesh.addVert(Tensors.vector(7, 5.5, 0));
    surfaceMesh.addVert(Tensors.vector(5.5, 6.5, 1));
    surfaceMesh.addFace(9, 10, 11, 12, 8);
    return surfaceMesh;
  }
}
