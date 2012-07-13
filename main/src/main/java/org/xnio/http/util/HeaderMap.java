/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xnio.http.util;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class HeaderMap implements Iterable<String> {
    // expect a header size of 200-2kB commonly, 700-800 most likely

    static class HeaderValue extends ArrayDeque<String> {
        private final String name;

        HeaderValue(final String name) {
            this.name = name;
        }

        HeaderValue(final String name, final String singleValue) {
            this(name);
            add(singleValue);
        }

        HeaderValue(final String name, final Collection<String> c) {
            super(c);
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    // todo - replace with a collision-limiting variant!
    private final Map<String, HeaderValue> values = new LinkedHashMap<>();

    public Iterator<String> iterator() {
        final Iterator<HeaderValue> iterator = values.values().iterator();
        return new Iterator<String>() {
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public String next() {
                return iterator.next().getName();
            }

            public void remove() {
                iterator.remove();
            }
        };
    }

    public String getFirst(String headerName) {
        final Deque<String> deque = values.get(headerName.toLowerCase(Locale.US));
        return deque == null ? null : deque.peekFirst();
    }

    public String getLast(String headerName) {
        final Deque<String> deque = values.get(headerName.toLowerCase(Locale.US));
        return deque == null ? null : deque.peekLast();
    }

    public Deque<String> get(String headerName) {
        return values.get(headerName.toLowerCase(Locale.US));
    }

    public void add(String headerName, String headerValue) {
        final String key = headerName.toLowerCase(Locale.US);
        final HeaderValue value = values.get(key);
        if (value == null) {
            values.put(key, new HeaderValue(headerName, headerValue));
        } else {
            value.add(headerValue);
        }
    }

    public void addAll(String headerName, Collection<String> headerValues) {
        final String key = headerName.toLowerCase(Locale.US);
        final HeaderValue value = values.get(key);
        if (value == null) {
            values.put(key, new HeaderValue(headerName, headerValues));
        } else {
            value.addAll(headerValues);
        }
    }

    public void addAll(HeaderMap other) {
        for (Map.Entry<String, HeaderValue> entry : other.values.entrySet()) {
            final String key = entry.getKey();
            final HeaderValue value = entry.getValue();
            final HeaderValue target = values.get(key);
            if (target == null) {
                values.put(key, new HeaderValue(value.getName(), value));
            } else {
                target.addAll(value);
            }
        }
    }

    public void put(String headerName, String headerValue) {
        final String key = headerName.toLowerCase(Locale.US);
        final HeaderValue value = new HeaderValue(headerName, headerValue);
        values.put(key, value);
    }

    public void putAll(String headerName, Collection<String> headerValues) {
        final String key = headerName.toLowerCase(Locale.US);
        final HeaderValue deque = new HeaderValue(headerName, headerValues);
        values.put(key, deque);
    }

    public Collection<String> remove(String headerName) {
        return values.remove(headerName);
    }

    /**
     * Lock this header map to make it immutable.  This method is idempotent.
     */
    public void lock() {

    }

    public boolean contains(String headerName) {
        final HeaderValue value = values.get(headerName.toLowerCase(Locale.US));
        return value != null && ! value.isEmpty();
    }
}
