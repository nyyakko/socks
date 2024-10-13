package dev.enche.persistence;

import dev.enche.persistence.core.Query;
import dev.enche.persistence.core.QueryContext;
import dev.enche.persistence.core.QueryResult;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public abstract class Persistence<T extends Identifiable> {

	private final Map<UUID, T> data = new LinkedHashMap<>();

	/** Select data filtered by the query function.
	 *
	 * @param operation Query function
	 * @return QueryResult&lt;T&gt; - Class instance with selected data
	 */

	public QueryResult<T> select(Function<QueryContext<T>, Map<UUID, T>> operation) {
		return new QueryResult<>(operation.apply(new QueryContext<>(data)));
	}

	/** Select all data.
	 *
	 * @return QueryResult&lt;T&gt; - Class instance with selected data
	 */

	public QueryResult<T> selectAny() {
		return new QueryResult<>(new Query<>(data));
	}

	/** Select data based on the first query function. If the applied query has no results, select based on the second query function.
	 *
	 * @param lhs First query function
	 * @param rhs Second query function
	 * @return QueryResult&lt;T&gt; - Class instance with selected data
	 */

	public QueryResult<T> selectEither(Function<QueryContext<T>, Query<T>> lhs, Function<QueryContext<T>, Query<T>> rhs) {
		final var context = new QueryContext<>(data);
		final var result = lhs.apply(context);
		return new QueryResult<>(!result.getResult().isEmpty() ? result : rhs.apply(context));
	}

	/** Insert data.
	 *
	 * @param persisted Data to be inserted
	 */

	public void insert(T persisted) {
		if (data.containsValue(persisted)) {
			throw new RuntimeException("Tried to persist an existing entity");
		}
		data.put(persisted.getId(), persisted);
	}

	public void update(UUID id, T persisted) {
		data.put(id, persisted);
	}

	/** Delete the data corresponding to the ID.
	 *
	 * @param id ID
	 */

	public void delete(UUID id) {
		data.remove(id);
	}

}
