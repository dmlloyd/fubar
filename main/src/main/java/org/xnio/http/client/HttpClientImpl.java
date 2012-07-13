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

package org.xnio.http.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import org.xnio.FutureResult;
import org.xnio.IoFuture;
import org.xnio.OptionMap;
import org.xnio.XnioWorker;
import org.xnio.dns.Answer;
import org.xnio.dns.Domain;
import org.xnio.dns.RRType;
import org.xnio.dns.Resolver;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
final class HttpClientImpl extends HttpClient {
    private final Resolver resolver;

    HttpClientImpl(final XnioWorker worker, final Resolver resolver) {
        super(worker);
        this.resolver = resolver;
    }

    public IoFuture<HttpClientConnection> connect(final SocketAddress destination, final OptionMap optionMap) {
        if (destination instanceof InetSocketAddress) {
            final FutureResult<HttpClientConnection> futureResult = new FutureResult<>();
            final InetSocketAddress address = (InetSocketAddress) destination;
            final InetAddress inetAddress = address.getAddress();
            if (inetAddress == null) {
                final String hostName = inetAddress.getHostName();
                final IoFuture<Answer> future4 = resolver.resolve(Domain.fromString(hostName), RRType.A);
                final IoFuture<Answer> future6 = resolver.resolve(Domain.fromString(hostName), RRType.AAAA);
                futureResult.addCancelHandler(future4);
                futureResult.addCancelHandler(future6);
                future4.addNotifier(new IoFuture.HandlingNotifier<Answer, FutureResult<HttpClientConnection>>() {
                    public void handleCancelled(final FutureResult<HttpClientConnection> attachment) {
                        // don't care
                    }

                    public void handleFailed(final IOException exception, final FutureResult<HttpClientConnection> attachment) {
                        // only care if both DNS lookups failed
                        if (/* both fail */ true) {
                            futureResult.setException(new IOException("DNS lookup failed", exception));
                        }
                    }

                    public void handleDone(final Answer data, final FutureResult<HttpClientConnection> attachment) {

                    }
                }, futureResult);
            }
        }
        return null;
    }

    public IoFuture<HttpClientRequest> sendRequest(final String method, final String requestUri, final OptionMap optionMap) {
        return null;
    }

    public void close() throws IOException {
    }
}
