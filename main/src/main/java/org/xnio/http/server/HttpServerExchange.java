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
import org.xnio.channels.AssembledConnectedStreamChannel;
import org.xnio.channels.ConnectedStreamChannel;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.http.util.Attachable;
import org.xnio.http.util.HeaderMap;
import org.xnio.http.util.Protocols;

/**
 * An HTTP server request/response exchange.  An instance of this class is constructed as soon as the request headers are
 * fully parsed.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class HttpServerExchange extends Attachable {
    private final HttpServerConnection connection;
    private HeaderMap requestHeaders;
    private HeaderMap responseHeaders;
    private int responseCode = 200;
    private String requestMethod;
    private String protocol;
    private String requestScheme;
    /**
     * The original request path.
     */
    private String requestPath;
    /**
     * The canonical version of the original path.
     */
    private String canonicalPath;
    /**
     * The remaining unresolved portion of the canonical path.
     */
    private String relativePath;
    private StreamSourceChannel requestChannel;
    private StreamSinkChannel responseChannel;

    protected HttpServerExchange(final HeaderMap requestHeaders, final HeaderMap responseHeaders, final String requestMethod) {
        this.connection = null;
        this.requestHeaders = requestHeaders;
        this.responseHeaders = responseHeaders;
        this.requestMethod = requestMethod;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }

    public final boolean isHttp09() {
        return protocol.equals(Protocols.HTTP_0_9);
    }

    public final boolean isHttp10() {
        return protocol.equals(Protocols.HTTP_1_0);
    }

    public final boolean isHttp11() {
        return protocol.equals(Protocols.HTTP_1_1);
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(final String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestScheme() {
        return requestScheme;
    }

    public void setRequestScheme(final String requestScheme) {
        this.requestScheme = requestScheme;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(final String requestPath) {
        this.requestPath = requestPath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(final String relativePath) {
        this.relativePath = relativePath;
    }

    public String getCanonicalPath() {
        return canonicalPath;
    }

    public void setCanonicalPath(final String canonicalPath) {
        this.canonicalPath = canonicalPath;
    }

    public HttpServerConnection getConnection() {
        return connection;
    }

    /**
     * Upgrade the channel to a raw socket.  This is a convenience method which sets a 101 response code, sends the
     * response headers, and merges the request and response channels into one full-duplex socket stream channel.
     *
     * @return the socket channel
     *
     * @throws IllegalStateException if a response or upgrade was already sent, or if the request body is already being
     * read
     */
    public ConnectedStreamChannel upgradeChannel() throws IllegalStateException, IOException {
        setResponseCode(101);
        startResponse();
        return new AssembledConnectedStreamChannel(getRequestChannel(), getResponseChannel());
    }

    /**
     * Get the source address of the HTTP request.
     *
     * @return the source address of the HTTP request
     */
    public InetSocketAddress getSourceAddress() {
        return connection.getPeerAddress(InetSocketAddress.class);
    }

    /**
     * Get the destination address of the HTTP request.
     *
     * @return the destination address of the HTTP request
     */
    public InetSocketAddress getDestinationAddress() {
        return connection.getLocalAddress(InetSocketAddress.class);
    }

    /**
     * Get the request headers.
     *
     * @return the request headers
     */
    public final HeaderMap getRequestHeaders() {
        return requestHeaders;
    }

    /**
     * Get the response headers.
     *
     * @return the response headers
     */
    public final HeaderMap getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * Get the inbound request.  If there is no request body, calling this method
     * may cause the next request to immediately be processed.  The {@link StreamSourceChannel#close()} or {@link StreamSourceChannel#shutdownReads()}
     * method must be called at some point after the request is processed to prevent resource leakage and to allow
     * the next request to proceed.  Any unread content will be discarded.  Multiple calls to this method will return
     * the same channel.
     *
     * @return the channel for the inbound request
     */
    public final StreamSourceChannel getRequestChannel() {
        return requestChannel;
    }

    /**
     * Change the request channel.  Subsequent calls to {@link #getRequestChannel()} will return the replaced channel.
     * If a channel is replaced, it is the responsibility of the replacing party to ensure that the request is read
     * in full and closed.  This may be done by wrapping the original channel or by handling the request body
     * asynchronously.
     *
     * @param requestChannel the replacement channel
     */
    public final void setRequestChannel(final StreamSourceChannel requestChannel) {
        this.requestChannel = requestChannel;
    }

    /**
     * Force the codec to treat the request as fully read.  Should only be invoked by handlers which downgrade
     * the socket or implement a transfer coding.
     */
    final void terminateRequest() {}

    /**
     * Get the response channel. The {@link StreamSinkChannel#close()} or {@link StreamSinkChannel#shutdownWrites()}
     * method must be called at some point after the request is processed to prevent resource leakage and to allow
     * the next request to proceed.  Closing a fixed-length response before the corresponding number of bytes has
     * been written will cause the connection to be reset and subsequent requests to fail; thus it is important to
     * ensure that the proper content length is delivered when one is specified.  Multiple calls to this method will
     * return the same channel.  The response channel may not be writable until after the response headers have been
     * sent.
     *
     * @return the response channel
     */
    public final StreamSinkChannel getResponseChannel() {
        return responseChannel;
    }

    /**
     * Change the response channel.  Subsequent calls to {@link #getResponseChannel()} will return the replaced channel.
     * If a channel is replaced, it is the responsibility of the replacing party to ensure that the response is written
     * in full and closed.  This may be done by wrapping the original channel or by writing the response body
     * asynchronously.
     *
     * @param responseChannel the replacement channel
     */
    public final void setResponseChannel(final StreamSinkChannel responseChannel) {
        this.responseChannel = responseChannel;
    }

    /**
     * Change the response code for this response.  If not specified, the code will be a {@code 200}.  Setting
     * the response code after the response headers have been transmitted has no effect.
     *
     * @param code the new code
     * @throws IllegalStateException if a response or upgrade was already sent
     */
    public final void setResponseCode(final int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * Get the response code.
     *
     * @return the response code
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Force the codec to treat the response as fully written.  Should only be invoked by handlers which downgrade
     * the socket or implement a transfer coding.
     */
    final void terminateResponse() {}

    /**
     * Transmit the response headers.  After this method successfully returns, the response channel may become writable.
     *  If this method fails, the response channel should have been closed automatically, however it is advisable to add
     * a {@code finally}-block to ensure that it is closed (and also the request channel if the request was not wholly
     * read when the response headers are written).
     *
     * @throws IOException if the response headers could not be sent
     * @throws IllegalStateException if the response headers were already sent
     */
    public void startResponse() throws IOException, IllegalStateException {
        responseHeaders.lock();
        // todo send
    }
}
