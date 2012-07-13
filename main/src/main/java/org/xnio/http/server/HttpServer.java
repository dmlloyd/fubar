/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import org.xnio.BufferAllocator;
import org.xnio.ByteBufferSlicePool;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Pool;
import org.xnio.Xnio;
import org.xnio.XnioWorker;
import org.xnio.channels.AcceptingChannel;
import org.xnio.channels.ConnectedStreamChannel;
import org.xnio.ssl.XnioSsl;

/**
 * An HTTP server.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class HttpServer extends AbstractHttpContext {
    private final XnioWorker xnioWorker;

    HttpServer(final OptionMap optionMap, final Xnio xnio, final Runnable terminationTask) throws IOException {
        this.xnioWorker = xnio.createWorker(null, optionMap, terminationTask);
    }

    public HttpServer(final OptionMap optionMap) throws IOException {
        this(optionMap, Xnio.getInstance(HttpServer.class.getClassLoader()), null);
    }

    /**
     * Get the XNIO worker associated with this server.
     *
     * @return the XNIO worker associated with this server
     */
    public XnioWorker getXnioWorker() {
        return xnioWorker;
    }

    /**
     * Bind this server to a socket address.
     *
     * @param address the address to bind to
     * @param handler the handler to use for the binding, or {@code null} to use the server handler
     * @param optionMap the options to apply to the binding
     * @return the virtual host for the binding
     * @throws IOException if the binding did not succeed
     */
    public HostHttpContext bind(SocketAddress address, HttpHandler handler, OptionMap optionMap) throws IOException {
        return createHost(address, handler, optionMap, xnioWorker.createStreamServer(address, null, optionMap));
    }

    /**
     * Bind this server to a socket address (SSL).
     *
     * @param address the address to bind to
     * @param xnioSsl the SSL configuration to use, or {@code null} to use a default
     * @param handler the handler to use for the binding, or {@code null} to use the server handler
     * @param optionMap the options to apply to the binding
     * @return the virtual host for the binding
     * @throws IOException if the binding did not succeed
     * @throws GeneralSecurityException if SSL configuration failed
     */
    public HostHttpContext bindSsl(InetSocketAddress address, XnioSsl xnioSsl, HttpHandler handler, OptionMap optionMap) throws IOException, GeneralSecurityException {
        if (xnioSsl == null) {
            xnioSsl = xnioWorker.getXnio().getSslProvider(optionMap);
        }
        return createHost(address, handler, optionMap, xnioSsl.createSslTcpServer(xnioWorker, address, null, optionMap));
    }

    private static HostHttpContext createHost(final SocketAddress address, final HttpHandler handler, final OptionMap optionMap, final AcceptingChannel<? extends ConnectedStreamChannel> server) {
        final int bufferSize = optionMap.get(Options.RECEIVE_BUFFER, 8192);
        final Pool<ByteBuffer> bufferPool = new ByteBufferSlicePool(BufferAllocator.BYTE_BUFFER_ALLOCATOR, bufferSize, bufferSize * 16);
        final HostHttpContext host = new HostHttpContext(address, server, bufferPool);
        if (handler != null) host.setHandler(handler);
        server.getAcceptSetter().set(host.getAcceptListener());
        server.resumeAccepts();
        return host;
    }

    public void close() throws IOException {
        xnioWorker.shutdown();
    }
}
