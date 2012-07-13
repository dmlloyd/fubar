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

package org.xnio.http;

import org.xnio.Option;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class HttpOptions {

    private HttpOptions() {
    }

    public static final Option<Boolean> HTTP_KEEPALIVE = Option.simple(HttpOptions.class, "HTTP_KEEPALIVE", Boolean.class);

    public static final Option<Boolean> HTTP_PIPELINE = Option.simple(HttpOptions.class, "HTTP_PIPELINE", Boolean.class);

    /**
     * Enable web socket upgrade on a client or server connection.
     */
    public static final Option<Boolean> WEB_SOCKET_ENABLED = Option.simple(HttpOptions.class, "WEB_SOCKET_ENABLED", Boolean.class);

    /**
     * Set when an outbound connection is to be treated as a proxy connection with respect to request URI generation.
     */
    public static final Option<Boolean> PROXY_CONNECTION = Option.simple(HttpOptions.class, "PROXY_CONNECTION", Boolean.class);

    /**
     * The URI scheme to use for connections on a server.  Useful (for example) to specify "https" on a non-SSL socket
     * when the SSL work is offloaded to an external appliance.
     */
    public static final Option<String> URI_SCHEME = Option.simple(HttpOptions.class, "URI_SCHEME", String.class);
}
