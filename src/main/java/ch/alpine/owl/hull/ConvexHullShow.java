// code by jph
package ch.alpine.owl.hull;

import java.awt.Color;
import java.awt.Container;
import java.util.Random;
import java.util.random.RandomGenerator;

import ch.alpine.bridge.fig.ListPlot;
import ch.alpine.bridge.fig.PolygonPlot;
import ch.alpine.bridge.fig.Show;
import ch.alpine.bridge.fig.ShowGridComponent;
import ch.alpine.bridge.pro.ManipulateProvider;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.sophis.crv.d2.alg.ConvexHull2D;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

@ReflectionMarker
class ConvexHullShow implements ManipulateProvider {
  public Integer seed = 2;
  public Integer n = 50;
  public ColorDataLists cdl = ColorDataLists._096;

  @Override
  public Container getContainer() {
    Show show = new Show(cdl.cyclic());
    RandomGenerator randomGenerator = new Random(seed);
    Clip clip = Clips.absolute(1.5);
    Tensor points = RandomVariate.of(NormalDistribution.standard(), randomGenerator, n, 2).maps(clip);
    Tensor hull = ConvexHull2D.of(points);
    show.add(PolygonPlot.of(hull)).setColor(new Color(128, 128, 255, 32));
    show.add(ListPlot.of(points));
    show.add(ListPlot.of(hull));
    show.setPlotLabel("Hull point count = " + hull.length());
    return ShowGridComponent.of(show);
  }

  static void main() {
    new ConvexHullShow().runStandalone();
  }
}
