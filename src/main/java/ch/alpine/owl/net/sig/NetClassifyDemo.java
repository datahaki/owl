// code by jph
package ch.alpine.owl.net.sig;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import ch.alpine.ascony.dis.ManifoldDisplay;
import ch.alpine.ascony.ren.ColorPairIndexed;
import ch.alpine.ascony.ren.PointsRender;
import ch.alpine.ascony.win.ControlPointType;
import ch.alpine.ascony.win.EuclideanPlaneDemo;
import ch.alpine.bridge.awt.AwtUtil;
import ch.alpine.bridge.fig.Show;
import ch.alpine.bridge.fig.plt.DensityPlot;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.pro.ManipulateProvider;
import ch.alpine.bridge.ref.ann.FieldFuse;
import ch.alpine.bridge.ref.ann.FieldSelectionArray;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.col.ColorDataGradients;
import ch.alpine.tensor.col.ColorDataIndexed;
import ch.alpine.tensor.col.ColorDataLists;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class NetClassifyDemo extends EuclideanPlaneDemo {
  @ReflectionMarker
  static class Param0 {
    @FieldSelectionArray({ "10", "20", "30", "40", "50" })
    public Integer size = 20;
    @FieldFuse
    public transient Boolean shuffle = false;
  }

  @ReflectionMarker
  static class Param1 {
    @FieldSelectionArray({ "4", "5", "6", "7", "8", "9", "10", "15" })
    public Integer hidden = 7;
    public Scalar learningRate = RealScalar.of(0.2);
    public Integer batch_size = 2;
    public Boolean animate = false;
    @FieldFuse
    public transient Boolean train = false;
  }

  @ReflectionMarker
  static class Param2 {
    public ColorDataGradients cdg = ColorDataGradients.COPPER;
    public ColorDataLists cdl = ColorDataLists._097;
  }

  private final Param0 param0;
  private final Param1 param1;
  private final Param2 param2;
  // ---
  protected Tensor target;
  private NetClassify netClassify;

  public NetClassifyDemo() {
    super(param0 = new Param0(), param1 = new Param1(), param2 = new Param2());
    geometricComponent().setRotatable(false);
    fieldsEditor(param0).addUniversalListener(this::shuffle);
    fieldsEditor(param1).addUniversalListener(this::train);
    shuffle();
  }

  @Override
  protected ControlPointType controlPointType() {
    return ControlPointType.DELEGATED;
  }

  private void shuffle() {
    int n = param0.size;
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor tensor = Tensor.of(RandomSample.of(manifoldDisplay.randomSampleInterface(), n).stream() //
        .map(manifoldDisplay::point2xya));
    setControlPointsSe2(tensor);
    // assignment of random labels to points
    int k = 2;
    target = RandomVariate.of(DiscreteUniformDistribution.forArray(k), n, 1);
    train();
  }

  private void train() {
    Tensor xdata = getGeodesicControlPoints();
    netClassify = new NetClassify(xdata, target, param1.hidden);
    netClassify.cdg = param2.cdg;
    netClassify.run(param1.learningRate, param1.batch_size);
    if (param1.animate) {
      ImageIcon imageIcon = netClassify.imageIconRecorder.getIconImage();
      ManipulateProvider manipulateProvider = new ManipulateProvider() {
        @Override
        public Container getContainer() {
          return AwtUtil.iconAsLabel(imageIcon);
        }
      };
      manipulateProvider.runStandalone();
    }
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor xdata = getGeodesicControlPoints();
    CoordinateBoundingBox cbb = CoordinateBounds.of(xdata);
    Rectangle rectangle = geometricLayer.toRectangle(cbb).orElseThrow();
    {
      Show show = new Show();
      show.add(DensityPlot.of((x, y) -> netClassify.netLayer.forward(Tensors.of(x, y)).Get(0), cbb, param2.cdg));
      show.render(graphics, rectangle);
    } // ---
    render(geometricLayer, graphics, manifoldDisplay, getGeodesicControlPoints(), Flatten.of(target), param2.cdl.cyclic());
    Dimension dimension = geometricComponent().getSize();
    dimension.width /= 2;
    dimension.height /= 2;
    {
      Show show = netClassify.showTParam();
      show.render_autoIndent(graphics, new Rectangle(dimension.width, 0, dimension.width, dimension.height));
    }
    {
      Show show = netClassify.showLoss();
      // show.setPlotLabel("Error: " + error.maps(Round._3));
      show.render_autoIndent(graphics, new Rectangle(dimension.width, dimension.height, dimension.width, dimension.height));
    }
  }

  static void render(GeometricLayer geometricLayer, Graphics2D graphics, ManifoldDisplay manifoldDisplay, Tensor sequence, Tensor vector,
      ColorDataIndexed colorDataIndexedT) {
    Tensor shape = manifoldDisplay.shape().multiply(RealScalar.of(1.0));
    int index = 0;
    ColorPairIndexed colorPairIndexed = new ColorPairIndexed(colorDataIndexedT, 128, 255);
    for (Tensor point : sequence) {
      int label = vector.Get(index).number().intValue();
      new PointsRender(colorPairIndexed.getColorPair(label), manifoldDisplay::matrixLift, shape, Tensors.of(point)) //
          .render(geometricLayer, graphics);
      ++index;
    }
  }

  static void main() {
    new NetClassifyDemo().runStandalone();
  }
}
