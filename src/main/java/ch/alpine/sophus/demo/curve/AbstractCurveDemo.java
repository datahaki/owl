// code by jph
package ch.alpine.sophus.demo.curve;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JSlider;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.ann.FieldSelection;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public abstract class AbstractCurveDemo extends AbstractCurvatureDemo {
  @FieldInteger
  @FieldSelection(array = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" })
  public Scalar degree = RealScalar.of(3);
  @FieldInteger
  @FieldSelection(array = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" })
  public Scalar refine = RealScalar.of(4);
  private final JSlider jSlider = new JSlider(0, 1000, 500);

  public AbstractCurveDemo() {
    this(ManifoldDisplays.ALL);
  }

  public AbstractCurveDemo(List<ManifoldDisplay> list) {
    super(list);
    // ---
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
  }

  @Override
  protected final Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor control = getGeodesicControlPoints();
    if (Tensors.isEmpty(control))
      return Tensors.empty();
    return protected_render(geometricLayer, graphics, degree.number().intValue(), refine.number().intValue(), control);
  }

  protected abstract Tensor protected_render( //
      GeometricLayer geometricLayer, Graphics2D graphics, int degree, int levels, Tensor control);

  public final Scalar sliderRatio() {
    return RationalScalar.of(jSlider.getValue(), jSlider.getMaximum());
  }
}
