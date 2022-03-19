// code by jph
package ch.alpine.tensor.demo.nd;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import javax.swing.JScrollPane;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.java.ref.ann.FieldClip;
import ch.alpine.java.ref.ann.FieldFuse;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.ann.ReflectionMarker;
import ch.alpine.java.ref.util.PanelFieldsEditor;
import ch.alpine.java.win.AbstractDemo;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.lie.r2.ConvexHull;
import ch.alpine.tensor.opt.nd.Dbscan;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Abs;

public class DbscanDemo extends AbstractDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();
  private static final ColorDataIndexed COLOR_FILL_INDEXED = COLOR_DATA_INDEXED.deriveWithAlpha(96);

  @ReflectionMarker
  public static class Param {
    @FieldInteger
    @FieldClip(min = "1", max = "1000")
    public Scalar count = RealScalar.of(200);
    @FieldInteger
    public Scalar dep = RealScalar.of(5);
    public CenterNorms centerNorms = CenterNorms._2;
    @FieldFuse
    public Boolean shuffle = false;
  }

  private final Param param = new Param();
  private Tensor pointsAll;

  public DbscanDemo() {
    Container container = timerFrame.jFrame.getContentPane();
    PanelFieldsEditor panelFieldsEditor = new PanelFieldsEditor(param);
    panelFieldsEditor.addUniversalListener(() -> {
      if (param.shuffle) {
        param.shuffle = false;
        pointsAll = recomp();
      }
    });
    JScrollPane jScrollPane = panelFieldsEditor.createJScrollPane();
    jScrollPane.setPreferredSize(new Dimension(200, 200));
    container.add(BorderLayout.WEST, jScrollPane);
    timerFrame.geometricComponent.setOffset(100, 600);
    // System.out.println(pointsAll.length());
    pointsAll = recomp();
  }

  private static Tensor recomp() {
    Distribution dist_b = UniformDistribution.of(0, 10);
    Distribution dist_r = NormalDistribution.of(0, 1);
    Tensor points = Tensors.empty();
    Tensor base = RandomVariate.of(dist_b, 5, 2);
    for (int index = 0; index < 20; ++index)
      for (Tensor r : base)
        for (Tensor p : RandomVariate.of(dist_r, 10, 2))
          points.append(r.add(p));
    return points;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(Color.GRAY);
    Tensor points = Tensor.of(pointsAll.stream().limit(param.count.number().intValue()));
    Tensor xya = timerFrame.geometricComponent.getMouseSe2CState();
    Scalar radius = Abs.FUNCTION.apply(xya.Get(2)).multiply(RealScalar.of(0.2));
    Timing timing = Timing.started();
    CenterNorms centerNorms = param.centerNorms;
    Integer[] labels = Dbscan.of(points, centerNorms::ndCenterInterface, radius, param.dep.number().intValue());
    double seconds = timing.seconds();
    graphics.drawString(String.format("%6.4f", seconds), 0, 40);
    {
      Map<Integer, Tensor> map = new HashMap<>();
      IntStream.range(0, labels.length) //
          .forEach(index -> map.computeIfAbsent(labels[index], i -> Tensors.empty()).append(points.get(index)));
      for (Entry<Integer, Tensor> entry : map.entrySet())
        if (Dbscan.NOISE < entry.getKey()) {
          Tensor tensor = ConvexHull.of(entry.getValue());
          graphics.setColor(COLOR_FILL_INDEXED.getColor(entry.getKey()));
          graphics.fill(geometricLayer.toPath2D(tensor, true));
        }
    }
    {
      int index = 0;
      for (Tensor point : points) {
        Point2D point2d = geometricLayer.toPoint2D(point);
        Integer label = labels[index];
        graphics.setColor(label < 0 //
            ? Color.BLACK
            : COLOR_DATA_INDEXED.getColor(label));
        graphics.fillRect((int) point2d.getX() - 2, (int) point2d.getY() - 2, 5, 5);
        ++index;
      }
    }
    {
      graphics.setColor(Color.BLUE);
      geometricLayer.pushMatrix(GfxMatrix.translation(xya.extract(0, 2)));
      graphics.draw(geometricLayer.toPath2D(centerNorms.shape().multiply(radius), true));
      geometricLayer.popMatrix();
    }
  }

  public static void main(String[] args) {
    new DbscanDemo().setVisible(1000, 800);
  }
}
