/*
The MIT License (MIT)
Copyright (c) 2013 B&A Tecnologia

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions 
of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED 
TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
IN THE SOFTWARE.
 */
package org.framework.jdbc;

import java.io.Serializable;
import java.util.List;
import javax.ejb.Local;

@Local
public interface EntityManager extends Serializable {

    public void close();

    public <E extends Entity<?>> void delete(final E entity);

    public <E extends Entity<?>> void delete(final List<E> entities);

    public <E extends Entity<?>> void deleteAll(final Class<E> target);

    void executeCall(String call);

    public <E extends Entity<?>> E find(final Class<E> target, final Serializable id);

    public Long get(final QueryBuilder query);

    public <E extends Entity<?>> E save(final E entity);

    public <E extends Entity<?>> void save(final List<E> entities);

    public <P extends Searchable> List<P> search(final QueryBuilder query);

    public List<Object[]> search(final String nativeQuery, final List<?> parameters);

    public <S extends Searchable> List<S> search(final String nativeQuery,
                                                 final List<?> parameters,
                                                 final Class<?> targetClass);

    public <E extends Entity<?>> List<String> validate(final E entity);
}
