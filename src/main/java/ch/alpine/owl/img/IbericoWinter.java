package ch.alpine.owl.img;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;

class IbericoWinter {
  final Tensor tensor;
  final Tensor routes;

  IbericoWinter(boolean days, Tensor tensor) {
    this.tensor = tensor;
    NavigableMap<Scalar, Tensor> navigableMap = tensor.stream().collect(Collectors.toMap( //
        r -> r.Get(0), r -> r.extract(1, 3), (r1, r2) -> r1.add(r2), TreeMap::new));
    Tensor total = days //
        ? Tensor.of(navigableMap.entrySet().stream().map(e -> Flatten.of(e.getKey(), e.getValue())))
        : Tensor.of(tensor.stream().map(r -> r.extract(0, 3)));
    routes = Tensor.of(total.stream().map(r -> r.append(r.Get(1).under(r.Get(2)).multiply(RealScalar.TWO))));
    System.out.println(routes.length() + " routes");
  }

  @Override
  public String toString() {
    return "IbWin " + tensor.length() + " " + routes.length();
  }
}
