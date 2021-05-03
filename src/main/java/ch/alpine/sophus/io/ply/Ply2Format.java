// code by jph
package ch.alpine.sophus.io.ply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.alpine.sophus.srf.SurfaceMesh;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.ext.ReadLine;
import ch.alpine.tensor.io.Export;

public enum Ply2Format {
  ;
  public static SurfaceMesh parse(Stream<String> stream) {
    List<String> list = stream.collect(Collectors.toList());
    int vn = Integer.parseInt(list.get(0));
    int fn = Integer.parseInt(list.get(1));
    SurfaceMesh surfaceMesh = new SurfaceMesh();
    for (int count = 0; count < vn; ++count) {
      String line = list.get(2 + count);
      surfaceMesh.addVert(Tensor.of(Stream.of(line.split(" ")).map(Scalars::fromString)));
    }
    int ofs = 2 + vn;
    for (int count = 0; count < fn; ++count) {
      String line = list.get(ofs + count);
      surfaceMesh.ind.append(Tensor.of(Stream.of(line.split(" ")).skip(1).map(Scalars::fromString)));
    }
    return surfaceMesh;
  }

  public static void main(String[] args) throws FileNotFoundException, IOException {
    File file = HomeDirectory.file("doraemon.ply2");
    try (InputStream inputStream = new FileInputStream(file)) {
      SurfaceMesh surfaceMesh = Ply2Format.parse(ReadLine.of(inputStream));
      Export.of(HomeDirectory.file("mesh.v.csv"), surfaceMesh.vrt);
      Export.of(HomeDirectory.file("mesh.i.csv"), surfaceMesh.ind.map(RealScalar.ONE::add));
    }
  }
}
