// code by gjoel, jph
package ch.alpine.owl.bot.se2.rrts;

import java.io.Serializable;

import ch.alpine.owl.rrts.adapter.NdType;
import ch.alpine.sophus.clt.Clothoid;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.opt.nd.NdBox;
import ch.alpine.tensor.opt.nd.NdCenterInterface;
import ch.alpine.tensor.opt.nd.NdCenters;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/* package */ class LimitedClothoidNdType implements NdType, Serializable {
  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();

  /** @param max curvature non-negative
   * @return */
  public static NdType with(Scalar max) {
    return new LimitedClothoidNdType(Clips.absolute(max));
  }

  /** @param max curvature non-negative
   * @return */
  public static NdType with(Number max) {
    return with(RealScalar.of(max));
  }

  // ---
  private final Clip clip;

  /** @param clip non-null */
  private LimitedClothoidNdType(Clip clip) {
    this.clip = clip;
  }

  @Override // from NdType
  public NdCenterInterface ndCenterTo(Tensor center) {
    return new LimitedClothoidNdCenter(center) {
      @Override
      protected Clothoid clothoid(Tensor other) {
        return CLOTHOID_BUILDER.curve(other, center);
      }
    };
  }

  @Override // from NdType
  public NdCenterInterface ndCenterFrom(Tensor center) {
    return new LimitedClothoidNdCenter(center) {
      @Override
      protected Clothoid clothoid(Tensor other) {
        return CLOTHOID_BUILDER.curve(center, other);
      }
    };
  }

  private static Scalar infinity(Scalar cost) {
    return Quantity.of(DoubleScalar.POSITIVE_INFINITY, QuantityUnit.of(cost));
  }

  // ---
  /* package */ abstract class LimitedClothoidNdCenter implements NdCenterInterface, Serializable {
    private final Tensor center;
    private final NdCenterInterface n2;

    public LimitedClothoidNdCenter(Tensor center) {
      this.center = center;
      n2 = NdCenters.VECTOR_2_NORM.apply(center.extract(0, 2));
    }

    @Override // from ClothoidNdCenter
    public final Scalar distance(Tensor p) {
      Clothoid clothoid = clothoid(p);
      Scalar cost = clothoid.length();
      return clip.isInside(clothoid.curvature().absMax()) //
          ? cost
          : infinity(cost);
    }

    @Override
    public Scalar distance(NdBox ndBox) {
      Tensor xy = ndBox.clip(center).extract(0, 2);
      return n2.distance(xy);
    }

    @Override
    public boolean lessThan(int dimension, Scalar median) {
      return dimension < 2 //
          ? n2.lessThan(dimension, median)
          : true;
    }

    /** @param other
     * @return clothoid either from center to other, or from other to center */
    protected abstract Clothoid clothoid(Tensor other);
  }
}
