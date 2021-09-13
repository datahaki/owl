// code by jph
package ch.alpine.sophus.usr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.opt.hun.BipartiteMatching;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;

// 4 22 35
/* package */ class BipartitionImage {
  BufferedImage bufferedImage = StaticHelper.createWhite(192 * 2);
  Graphics2D graphics = bufferedImage.createGraphics();

  public BipartitionImage(int seed, boolean lines) {
    Random random = new Random(seed);
    Tensor points1 = RandomVariate.of(UniformDistribution.unit(), random, 9, 2);
    Tensor points2 = RandomVariate.of(UniformDistribution.unit(), random, 13, 2);
    // Tensor cost = points1.stream().map(p -> Tensor.of(points2.stream().map(r -> Norm._2.between(p, r))));
    Tensor matrix = Tensors.matrix((i, j) -> Vector2Norm.between(points1.get(i), points2.get(j)), points1.length(), points2.length());
    BipartiteMatching hungarianAlgorithm = BipartiteMatching.of(matrix);
    GeometricLayer geometricLayer = new GeometricLayer(StaticHelper.SE2_2);
    RenderQuality.setQuality(graphics);
    graphics.setColor(new Color(128 * 0, 128 * 0, 255));
    if (lines) {
      int[] m = hungarianAlgorithm.matching();
      for (int index = 0; index < m.length; ++index) {
        Path2D path2d = geometricLayer.toPath2D(Tensors.of(points1.get(index), points2.get(m[index])));
        path2d.closePath();
        graphics.draw(path2d);
      }
    }
    graphics.setColor(Color.RED);
    for (Tensor point : points1) {
      geometricLayer.pushMatrix(Se2Matrix.translation(point));
      Path2D path2d = geometricLayer.toPath2D(StaticHelper.POINT);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    graphics.setColor(Color.GREEN);
    for (Tensor point : points2) {
      geometricLayer.pushMatrix(Se2Matrix.translation(point));
      Path2D path2d = geometricLayer.toPath2D(StaticHelper.POINT);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    // bufferedImage;
  }

  public static void main(String[] args) throws IOException {
    File folder = HomeDirectory.Pictures(BipartitionImage.class.getSimpleName());
    folder.mkdir();
    for (int seed = 0; seed < 50; ++seed) {
      {
        Tensor tensor = ImageFormat.from(new BipartitionImage(seed, false).bufferedImage);
        Export.of(new File(folder, String.format("%03da.png", seed)), tensor);
      }
      {
        Tensor tensor = ImageFormat.from(new BipartitionImage(seed, true).bufferedImage);
        Export.of(new File(folder, String.format("%03db.png", seed)), tensor);
      }
    }
    {
      // Export.of(HomeDirectory.Pictures(BipartitionImage.class.getSimpleName() + ".png"), image(35));
    }
  }
}
