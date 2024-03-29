// code by ynager
package ch.alpine.owl.math;

import java.io.Serializable;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.alpine.owl.math.order.VectorLexicographic;
import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ComplexEmbedding;
import ch.alpine.tensor.api.ConjugateInterface;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.Re;

/** immutable as required by {@link Scalar} interface
 * 
 * <p>multiplication of two {@link VectorScalar}s throws an exception.
 * Careful: multiplication with {@link Quantity} is not commutative !
 * 
 * <p>Comparable<Scalar> is defined only between two instances of {@link VectorScalar}.
 * The lexicographical ordering is applied to the tensors.
 * For instance: [1, 2, 0] < [1, 1, 3]
 * 
 * <p>The sign of a {@link VectorScalar} is not defined. For instance,
 * to check that all entries are non-negative use
 * Scalars.lessEquals(vectorScalar.zero(), vectorScalar);
 * 
 * <p>The string expression of a {@link VectorScalar} is of the form [1, 2, 3]
 * to prevent confusion with standard vectors that are formatted as {1, 2, 3}.
 * 
 * <p>Hint: a {@link VectorScalar} may also have zero entries */
public class VectorScalar extends MultiplexScalar implements //
    ComplexEmbedding, ConjugateInterface, Comparable<Scalar>, Serializable {
  /** @param vector
   * @return
   * @throws Exception if input is not a vector, or contains entries of type {@link VectorScalar} */
  public static Scalar of(Tensor vector) {
    if (vector.stream().map(Scalar.class::cast).anyMatch(VectorScalar.class::isInstance))
      throw new Throw(vector);
    return new VectorScalar(vector.copy());
  }

  /** @param numbers
   * @return */
  public static Scalar of(Number... numbers) {
    return new VectorScalar(Tensors.vector(numbers));
  }

  public static Scalar of(Stream<Scalar> stream) {
    return new VectorScalar(Tensor.of(stream));
  }

  // ---
  private final Tensor vector;

  private VectorScalar(Tensor vector) {
    this.vector = vector;
  }

  /** @return unmodifiable content vector */
  public Tensor vector() {
    return vector.unmodifiable();
  }

  /** @param index
   * @return */
  public Scalar at(int index) {
    return vector.Get(index);
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof VectorScalar)
      throw new Throw(this, scalar);
    return new VectorScalar(vector.multiply(scalar));
  }

  @Override // from Scalar
  public Scalar negate() {
    return new VectorScalar(vector.negate());
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    throw new Throw(this);
  }

  @Override // from Scalar
  public Scalar zero() {
    return new VectorScalar(vector.map(Scalar::zero));
  }

  @Override // from Scalar
  public Scalar one() {
    return RealScalar.ONE;
  }

  // ---
  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof VectorScalar vectorScalar)
      return new VectorScalar(vector.add(vectorScalar.vector));
    throw new Throw(this, scalar);
  }

  @Override // from ComplexEmbedding
  public Scalar conjugate() {
    return new VectorScalar(vector.map(Conjugate.FUNCTION));
  }

  @Override // from ComplexEmbedding
  public Scalar real() {
    return new VectorScalar(vector.map(Re.FUNCTION));
  }

  @Override // from ComplexEmbedding
  public Scalar imag() {
    return new VectorScalar(vector.map(Im.FUNCTION));
  }

  @Override
  public Scalar eachMap(UnaryOperator<Scalar> unaryOperator) {
    return of(vector.stream().map(Scalar.class::cast).map(unaryOperator));
  }

  @Override
  public boolean allMatch(Predicate<Scalar> predicate) {
    return vector.stream().map(Scalar.class::cast).allMatch(predicate);
  }

  // ---
  @Override // from Comparable
  public int compareTo(Scalar scalar) {
    if (scalar instanceof VectorScalar vectorScalar)
      return VectorLexicographic.COMPARATOR.compare(vector, vectorScalar.vector());
    throw new Throw(this, scalar);
  }

  @Override // from Scalar
  public int hashCode() {
    return vector.hashCode();
  }

  @Override // from Scalar
  public boolean equals(Object object) {
    return object instanceof VectorScalar vectorScalar //
        && vector.equals(vectorScalar.vector);
  }

  private static final Collector<CharSequence, ?, String> EMBRACE = Collectors.joining(", ", "[", "]");

  @Override // from Scalar
  public String toString() {
    return vector.stream().map(Tensor::toString).collect(EMBRACE);
  }
}
