package dev.enche.persistence.core;

import java.util.*;
import java.util.function.Function;

public class QueryResult<T> {

    final private List<T> data;

    public QueryResult(Query<T> data) {
        this.data = new ArrayList<>(data.getResult().values().stream().toList());
    }

    public QueryResult(Map<UUID, T> data) {
        this.data = new ArrayList<>(data.values().stream().toList());
    }

    /** Return list data
     *
     * @return List&lt;T&gt; - Data list
     */

    public List<T> getAll() {
        return data;
    }

    /** Returns the first element of the data list. Returns Optional&lt;T&gt; to better handle cases where there are no elements.
     *
     * @return Optional&lt;T&gt; - The first element found or empty
     */

    public Optional<T> getSingle() {
        return data.stream().findFirst();
    }

    public T mustGetSingle() {
         final var result = data.stream().findFirst();
         return result.orElseThrow(() -> new RuntimeException("Entity was not present"));
    }

    /** Sort data based on a comparator expression.
     *
     * @param comparator Function used to sort data
     * @return QueryResult&lt;T&gt; - The class itself
     */

    public QueryResult<T> orderBy(Comparator<T> comparator) {
        data.sort(comparator);
        return this;
    }

    /** Based on the projection function, converts data from T to U and then sort data based on a comparator expression.
     *
     * @param comparator Function used to sort data
     * @param projection Function used to convert data from T to U
     * @return QueryResult&lt;T&gt; - The class itself
     */

    public <U> QueryResult<T> orderBy(Comparator<U> comparator, Function<T, U> projection) {
        data.sort((x, y) -> comparator.compare(projection.apply(x), projection.apply(y)));
        return this;
    }

}
