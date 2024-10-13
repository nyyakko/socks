package dev.enche.persistence.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/** Basic structure of a query
 *
 * @param <T> Value type
 */

public class Query<T> {

    private Map<UUID, T> data = new LinkedHashMap<>();

    private Query() {

    }

	public Query(Map<UUID, T> data) {
		this.data = data;
	}

    /** Returns data of the query.
     *
     * @return Map&lt;UUID, T&gt; - Data returned
     */

    public Map<UUID, T> getResult() {
        return data;
    }

    /** Returns a class instance with a map with the value corresponded by the ID.
     *
     * @param id ID searched
     * @return Query&lt;T&gt; - Class instance
     */

    public Query<T> is(UUID id) {
        return Optional.ofNullable(data.get(id)).map(entity -> {
            data = Map.of(id, entity);
            return this;
        }).orElse(new Query<>());
    }

    /** Filters data according to the predicate.
     *
     * @param predicate Boolean expression used to filter the map
     * @return Query&lt;T&gt; - The class itself
     */

    public Query<T> is(Predicate<T> predicate) {
        data = data.entrySet().stream().filter(e -> predicate.test(e.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return this;
    }

    /** Based on the projection function, converts data from T to U and filters data according to the predicate.
     *
     * @param predicate Boolean expression used to filter the map
     * @param projection Function used to convert values from T to U
     * @return Query&lt;T&gt; - The class itself
     */

    public <U> Query<T> is(Predicate<U> predicate, Function<T, U> projection) {
        data = data.entrySet().stream().filter(e -> predicate.test(projection.apply(e.getValue()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return this;
    }

    /** Filters data based on being true in the first condition or,
     * if there isn't any true in the first condition, being true on the second condition.
     *
     * @param lhs First boolean condition
     * @param rhs Second boolean condition
     * @return Query&lt;T&gt; - The class itself
     */

    public Query<T> or(Predicate<T> lhs, Predicate<T> rhs) {
        final var result = data.entrySet().stream().filter(e -> lhs.test(e.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        data = !result.isEmpty() ? result : data.entrySet().stream().filter(e -> rhs.test(e.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return this;
    }

    /** Based on the projection function, converts data from T to U and filters data based on being true in the first condition or,
     * if there isn't any true in the first condition, being true on the second condition.
     *
     * @param lhs First boolean condition
     * @param rhs Second boolean condition
     * @param projection Function used to convert values from T to U
     * @return Query&lt;T&gt; - The class itself
     */

    public <U> Query<T> or(Predicate<U> lhs, Predicate<U> rhs, Function<T, U> projection) {
        final var result = data.entrySet().stream().filter(e -> lhs.test(projection.apply(e.getValue()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        data = !result.isEmpty() ? result : data.entrySet().stream().filter(e -> rhs.test(projection.apply(e.getValue()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return this;
    }

}
