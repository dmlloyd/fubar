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

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.Pool;
import org.xnio.channels.AcceptingChannel;
import org.xnio.channels.ConnectedChannel;
import org.xnio.channels.ConnectedStreamChannel;
import org.xnio.channels.PushBackStreamChannel;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

/**
 * An HTTP server socket binding context.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class HostHttpContext extends AbstractHttpContext {
    private final Pool<ByteBuffer> bufferPool;
    private final SocketAddress bindAddress;
    private final AcceptingChannel<? extends ConnectedStreamChannel> channel;
    private final ChannelListener<AcceptingChannel<? extends ConnectedStreamChannel>> acceptListener = new ChannelListener<AcceptingChannel<? extends ConnectedStreamChannel>>() {
        public void handleEvent(final AcceptingChannel<? extends ConnectedStreamChannel> channel) {
            for (;;) {
                final ConnectedStreamChannel streamChannel;
                try {
                    streamChannel = channel.accept();
                    if (streamChannel == null) {
                        return;
                    }
                    final PushBackStreamChannel pushBackStreamChannel = new PushBackStreamChannel(streamChannel);
                    final HttpServerConnection connection = new HttpServerConnection(streamChannel);
                    final HttpReadListener readListener = new HttpReadListener(connection, bufferPool);
                    pushBackStreamChannel.getReadSetter().set(readListener);
                    pushBackStreamChannel.resumeReads();
                    readListener.handleEvent(pushBackStreamChannel);
                } catch (IOException ignored) {
                    return;
                }
            }
        }
    };

    HostHttpContext(final SocketAddress bindAddress, final AcceptingChannel<? extends ConnectedStreamChannel> channel, final Pool<ByteBuffer> bufferPool) {
        this.bindAddress = bindAddress;
        this.channel = channel;
        this.bufferPool = bufferPool;
    }

    public final SocketAddress getBindAddress() {
        return bindAddress;
    }

    /**
     * Accept and begin processing requests on a new HTTP connection to this host.  Used by special handlers (e.g. {@code STARTTLS}) which
     * use HTTP upgrade to produce new connections.
     *
     * @param channel the channel to use
     * @return the connection
     */
    public HttpServerConnection accept(ConnectedStreamChannel channel) {
        return accept(channel, channel, channel);
    }

    /**
     * Accept and begin processing requests on a new HTTP connection to this host.  Used by special handlers (e.g. {@code STARTTLS}) which
     * use HTTP upgrade to produce new connections.
     *
     * @param connection the connected channel
     * @param sourceChannel the source channel
     * @param sinkChannel the sink channel
     * @return the connection
     */
    public HttpServerConnection accept(ConnectedChannel connection, StreamSourceChannel sourceChannel, StreamSinkChannel sinkChannel) {
        return null;
    }

    public void close() {
        IoUtils.safeClose(channel);
    }

    ChannelListener<AcceptingChannel<? extends ConnectedStreamChannel>> getAcceptListener() {
        return acceptListener;
    }
}
