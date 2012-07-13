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

import java.util.regex.Pattern;

/**
 * An HTTP handler which implements path-based mapping for HTTP requests.  A single handler may be added to more than one
 * HTTP host.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class PathHttpHandler extends AbstractHttpContext implements HttpHandler {

    /**
     * Construct a new instance.
     *
     * @param fallbackHandler the initial fallback handler
     */
    public PathHttpHandler(final HttpHandler fallbackHandler) {
        super(fallbackHandler);
    }

    public void handleRequest(final HttpServerExchange exchange) {
        exchange.sendErrorResponse(500);
    }

    private static final Pattern PATH_PATTERN = Pattern.compile("^(?:/[\\p{Print}&&[^/]]+)+");

    public PathHttpContext registerPath(final String path, final HttpHandler initialHandler) throws IllegalArgumentException {
        if (PATH_PATTERN.matcher(path).matches()) {
            // todo
            return null;
        } else {
            throw new IllegalArgumentException("Invalid path name '" + path + "'");
        }
    }

    void removeHost(final NameVirtualHostContext host) {
        // todo
    }
}
