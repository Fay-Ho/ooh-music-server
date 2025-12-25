package xyz.fayvox.music.common;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A business-friendly optional container that supports seamless chaining and cross-type null checks,
 * addressing the limitations of JDK's native Optional. It preserves chain continuity and provides
 * intuitive null-safe operations for daily business development.
 *
 * @param <T> the type of value held by this Optional
 */
@SuppressWarnings("unused")
public final class Optional<T> {
    /**
     * The singleton empty instance of Optional (immutable, shared across all type parameters).
     * This instance is returned by {@link #empty()} and represents the absence of a value.
     */
    private static final Optional<?> EMPTY = new Optional<>();

    /**
     * The value contained by this Optional, or null if this is an empty instance.
     * <p>Constraints:
     * <ul>
     *   <li>Non-null if the instance is created via {@link #of(T)} (enforced by constructor)</li>
     *   <li>Null if the instance is the singleton {@link #EMPTY}</li>
     * </ul>
     */
    private final T value;

    /**
     * Private constructor for empty Optional instance.
     */
    private Optional() {
        this.value = null;
    }

    /**
     * Private constructor for non-empty Optional instance.
     *
     * @param value the non-null value to be contained
     */
    private Optional(T value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Returns an empty Optional instance.
     *
     * @param <T> the type of the non-existent value
     * @return an empty Optional
     */
    public static <T> Optional<T> empty() {
        @SuppressWarnings("unchecked")
        Optional<T> t = (Optional<T>) EMPTY;
        return t;
    }

    /**
     * Creates an Optional containing the given non-null value.
     *
     * @param value the non-null value to be wrapped
     * @param <T>   the type of the value
     * @return an Optional wrapping the specified value
     * @throws NullPointerException if the value is null
     */
    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }

    /**
     * Creates an Optional that wraps the given value, or returns an empty Optional if the value is null.
     *
     * @param value the value to wrap (may be null)
     * @param <T>   the type of the value
     * @return Optional.of(value) if value is non-null, otherwise empty()
     */
    public static <T> Optional<T> ofNullable(T value) {
        return value == null ? empty() : new Optional<>(value);
    }

    /**
     * Checks if a value is present in this Optional.
     *
     * @return true if a non-null value is contained, false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isPresent() {
        return value != null;
    }

    /**
     * Performs the given action with the contained value if it is present.
     *
     * @param consumer the action to perform on the contained value
     * @throws NullPointerException if the consumer is null
     */
    public void ifPresent(Consumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        if (value != null) consumer.accept(value);
    }

    /**
     * Returns the contained value if present; throws NoSuchElementException otherwise.
     *
     * @return the non-null contained value
     * @throws NoSuchElementException if no value is present
     */
    public T get() {
        if (value == null) throw new NoSuchElementException("No value present");
        return value;
    }

    /**
     * Filters the contained value using the given predicate.
     *
     * @param predicate the predicate to test the contained value
     * @return this Optional if the value is present and matches the predicate, otherwise empty()
     * @throws NullPointerException if the predicate is null
     */
    public Optional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent()) return this;
        else return predicate.test(value) ? this : empty();
    }

    /**
     * Transforms the contained value (if present) using the given mapping function,
     * returning a new Optional containing the transformed value.
     *
     * @param <U>      the type of the transformed value
     * @param function the mapping function to apply to the contained value
     * @return a new Optional wrapping the transformed value if present, otherwise empty()
     * @throws NullPointerException if the function is null
     */
    public <U> Optional<U> map(Function<? super T, ? extends U> function) {
        Objects.requireNonNull(function);
        if (!isPresent()) return empty();
        else return ofNullable(function.apply(value));
    }

    /**
     * Transforms the contained value (if present) using the given mapping function that returns an Optional,
     * avoiding nested Optional instances.
     *
     * @param <U>      the type of the value contained in the Optional returned by the mapping function
     * @param function the mapping function to apply to the contained value
     * @return the Optional returned by the mapping function if present, otherwise empty()
     * @throws NullPointerException if the function is null or returns null
     */
    public <U> Optional<U> flatMap(Function<? super T, Optional<U>> function) {
        Objects.requireNonNull(function);
        if (!isPresent()) return empty();
        else return Objects.requireNonNull(function.apply(value));
    }

    /**
     * Returns this Optional if present, otherwise returns an Optional wrapping the given other value.
     * Preserves chain continuity (unlike JDK's orElse which returns a raw value).
     *
     * @param other the fallback value to wrap if this Optional is empty
     * @return this Optional if non-empty, else Optional.ofNullable(other)
     */
    public Optional<T> orElse(T other) {
        return value != null ? this : ofNullable(other);
    }

    /**
     * Returns this Optional if present, otherwise returns an Optional wrapping the value supplied by the given supplier.
     * Lazy-evaluated alternative to orElse(T other), preserving chain continuity.
     *
     * @param other the supplier providing the fallback value
     * @return this Optional if non-empty, else Optional.ofNullable(other.get())
     * @throws NullPointerException if the supplier is null or supplies null
     */
    public Optional<T> orElseGet(Supplier<? extends T> other) {
        return value != null ? this : ofNullable(other.get());
    }

    /**
     * Falls back to wrapping the given value as a new Optional if the current Optional is empty.
     * Supports cross-type value switching and preserves chain continuity, which is the core feature
     * addressing JDK Optional's chain-breaking limitation.
     *
     * @param <U>   the type of the fallback value to wrap
     * @param value the fallback value (may be null, resulting in an empty Optional)
     * @return the current Optional if non-empty, otherwise Optional.ofNullable(value)
     */
    public <U> Optional<U> orElseNullable(U value) {
        return value == null ? empty() : new Optional<>(value);
    }

    /**
     * Returns the contained value if present, otherwise returns the given default value.
     * Semantically intuitive alternative to JDK's orElse.
     *
     * @param other the default value to return if this Optional is empty
     * @return the contained value if present, else the default value
     */
    public T orDefault(T other) {
        return value != null ? value : other;
    }

    /**
     * Returns the contained value if present, otherwise returns the given default value.
     * Semantically intuitive alternative to JDK's orElse.
     *
     * @param other the default value to return if this Optional is empty
     * @return the contained value if present, else the default value
     */
    public T orDefaultGet(Supplier<? extends T> other) {
        return value != null ? value : other.get();
    }

    /**
     * Returns the contained value if present, otherwise throws the exception supplied by the given supplier.
     *
     * @param <X>                the type of the exception to throw
     * @param exceptionSupplier the supplier providing the exception
     * @return the contained non-null value
     * @throws X                    if no value is present
     * @throws NullPointerException if the exception supplier is null
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) return value;
        else throw exceptionSupplier.get();
    }

    /**
     * Converts a JDK native Optional to this Optional type for ecosystem compatibility.
     *
     * @param optional the JDK native Optional instance
     * @param <T>      the type of the value contained
     * @return this Optional type wrapping the same value as the input JDK Optional
     * @throws NullPointerException if the input optional is null
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Optional<T> fromJDKOptional(java.util.Optional<T> optional) {
        Objects.requireNonNull(optional);
        return optional.map(Optional::of).orElse(empty());
    }

    /**
     * Converts this Optional to a JDK native Optional for ecosystem compatibility.
     *
     * @return a JDK native Optional wrapping the same value as this Optional
     */
    public java.util.Optional<T> toJDKOptional() {
        return java.util.Optional.ofNullable(value);
    }

    /**
     * Compares this Optional with another object for equality.
     * Two Optionals are equal if they are both empty, or both contain equal values.
     *
     * @param obj the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Optional)) return false;
        Optional<?> other = (Optional<?>) obj;
        return Objects.equals(value, other.value);
    }

    /**
     * Returns the hash code of the contained value, or 0 if empty.
     *
     * @return the hash code of the contained value or 0
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * Returns a string representation of this Optional.
     * Format: "Optional[value]" if present, "Optional.empty" if empty.
     *
     * @return a string representation of this Optional
     */
    @Override
    public String toString() {
        return value != null ? String.format("Optional[%s]", value) : "Optional.empty";
    }
}
