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

package org.xnio.http.server;

import java.util.Locale;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import org.xnio.http.util.SecureHashMap;

/**
 * An HTTP handler which implements name-based virtual hosts.  A single handler may be added to more than one
 * HTTP host.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class NameVirtualHostHttpHandler implements HttpHandler {
    private volatile HttpHandler fallbackHandler;
    // wildcard virtual hosts start with .
    private final ConcurrentMap<String, NameVirtualHostContext> hosts = new SecureHashMap<String, NameVirtualHostContext>();

    /**
     * Construct a new instance.
     *
     * @param fallbackHandler the initial fallback handler
     */
    public NameVirtualHostHttpHandler(final HttpHandler fallbackHandler) {
        this.fallbackHandler = fallbackHandler;
    }

    public void handleRequest(final HttpServerExchange exchange) {
        final String host = exchange.getRequestURI().getHost();
        String testHost = host == null ? null : host.toLowerCase(Locale.US);
        NameVirtualHostContext matched;
        HttpHandler handler;
        while (testHost != null && ! host.isEmpty()) {
            matched = hosts.get(testHost);
            if (matched != null) {
                handler = matched.getHandler();
                if (handler != null) {
                    handler.handleRequest(exchange);
                    return;
                }
            }
            final int dot = testHost.indexOf('.');
            if (dot != -1) {
                testHost = testHost.substring(dot);
                matched = hosts.get(testHost);
                if (matched != null) {
                    handler = matched.getHandler();
                    if (handler != null) {
                        handler.handleRequest(exchange);
                        return;
                    }
                }
                testHost = testHost.substring(1);
            }
        }
        handler = fallbackHandler;
        if (handler != null) {
            handler.handleRequest(exchange);
        }
        exchange.sendErrorResponse(500);
    }

    public HttpHandler getFallbackHandler() {
        return fallbackHandler;
    }

    public void setFallbackHandler(final HttpHandler fallbackHandler) {
        this.fallbackHandler = fallbackHandler;
    }

    private static final Pattern HOST_PATTERN = Pattern.compile("^(?:(?:\\*\\.)?[a-z0-9][-a-z0-9]*)(?:\\.[a-z0-9][-a-z0-9]*)*");

    public NameVirtualHostContext registerHost(final String name, final HttpHandler initialHandler) throws IllegalArgumentException {
        final String canonName = name.toLowerCase(Locale.US);
        if (HOST_PATTERN.matcher(canonName).matches()) {
            final NameVirtualHostContext host = new NameVirtualHostContext(this, canonName, initialHandler);
            final String key = canonName.charAt(0) == '*' ? canonName.substring(1) : canonName;
            if (hosts.putIfAbsent(key, host) != null) {
                throw new IllegalArgumentException("Host name '" + canonName + "' already registered");
            }
            return host;
        } else {
            throw new IllegalArgumentException("Invalid host name '" + name + "'");
        }
    }

    void removeHost(final NameVirtualHostContext host) {
        hosts.remove(host.getName(), host);
    }
}
