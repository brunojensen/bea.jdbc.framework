package org.framework.jdbc;

import java.io.Serializable;
import java.util.List;
import javax.ejb.Local;

@Local
public interface EntityManager extends Serializable {

    public void begin();

    public void close();

    public void commit();

    public <E extends Entity<?>> void delete(final E entity);

    public <E extends Entity<?>> void delete(final List<E> entities);

    public <E extends Entity<?>> void deleteAll(final Class<E> target);

    void executeCall(String call);

    public <E extends Entity<?>> E find(final Class<E> target,
                                        final Serializable id);

    public Object get(final Query query);

    void rollback();

    public <E extends Entity<?>> E save(final E entity);

    public <E extends Entity<?>> void save(final List<E> entities);

    public <P extends Searchable> List<P> search(final Query query);

    public List<Object[]> search(final String nativeQuery,
                                 final List<?> parameters);

    public <E extends Entity<?>> List<String> validate(final E entity);
}
