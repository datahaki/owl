package ch.alpine.owl.sea;

import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.qty.UnitConvert;

enum StaticHelper {
  ;
  public static final ScalarUnaryOperator TO_FT = UnitConvert.SI().to("ft");
  public static final ScalarUnaryOperator TO_FT_SQ = UnitConvert.SI().to("ft^2");
  public static final ScalarUnaryOperator TO_LB = UnitConvert.SI().to("lb");
}
