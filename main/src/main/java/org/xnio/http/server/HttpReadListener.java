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
import java.nio.ByteBuffer;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.Pool;
import org.xnio.Pooled;
import org.xnio.channels.PushBackStreamChannel;
import org.xnio.http.util.HeaderMap;
import org.xnio.http.util.Methods;

/**
 * Listener which reads requests and headers off of an HTTP stream.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
final class HttpReadListener implements ChannelListener<PushBackStreamChannel> {
    private final HttpServerConnection serverConnection;
    private final Pool<ByteBuffer> readBufferPool;
    private int state;
    private String method;
    private String requestUri;
    private StringBuilder builder = new StringBuilder();
    private HeaderMap requestHeaders;
    private String key;

    HttpReadListener(final HttpServerConnection serverConnection, final Pool<ByteBuffer> readBufferPool) {
        this.serverConnection = serverConnection;
        this.readBufferPool = readBufferPool;
    }

    public void handleEvent(final PushBackStreamChannel channel) {
        int res;
        boolean taken = false;
        Pooled<ByteBuffer> pooled = readBufferPool.allocate();
        try {
            final ByteBuffer buffer = pooled.getResource();
            while ((res = channel.read(buffer)) > 0) {
                buffer.flip();
                try {
                    if (! decode(buffer)) {
                        IoUtils.safeClose(channel);
                        return;
                    }
                } finally {
                    buffer.compact();
                }
            }
        } catch (IOException e) {
            shutdownError(channel, e);
            return;
        } finally {
            if (! taken) pooled.free();
        }
        if (res == -1) try {
            channel.shutdownReads();
        } catch (IOException e) {
            shutdownError(channel, e);
            return;
        }
    }

    private static int read(ByteBuffer buffer) {
        return buffer.hasRemaining() ? buffer.get() & 0xff : -1;
    }

    private boolean decode(ByteBuffer buffer) {
        int state = this.state;
        try {
            int c;
            final StringBuilder builder = this.builder;
            out: for (;;) {
                if (! buffer.hasRemaining()) {
                    return true;
                }
                c = buffer.get() & 0xff;
                switch (state) {
                    // --------------------------------------------------------------------
                    // Read HTTP method
                    // --------------------------------------------------------------------
                    case 0:
                        switch (c) {
                            case 'C': state = 2; break;
                            case 'D': state = 20; break;
                            case 'G': state = 40; break;
                            case 'H': state = 60; break;
                            case 'O': state = 80; break;
                            case 'P': state = 100; break;
                            case 'T': state = 140; break;
                            case '\r': break out;
                            default: builder.append(c); state = 1; break;
                        }
                        break;
                    case 1:
                        switch (c) {
                            case ' ': method = builder.toString(); builder.setLength(0); state = 1000; break;
                            case '\r': break out;
                            default: builder.append(c); break;
                        }
                        break;
                    // method = CONNECT
                    case 2:
                        switch (c) {
                            case 'O': state = 3; break;
                            case ' ': method = "C"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append('C').append(c); state = 1; break;
                        }
                        break;
                    case 3:
                        switch (c) {
                            case 'N': state = 4; break;
                            case ' ': method = "CO"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("CO").append(c); state = 1; break;
                        }
                        break;
                    case 4:
                        switch (c) {
                            case 'N': state = 5; break;
                            case ' ': method = "CON"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("CON").append(c); state = 1; break;
                        }
                        break;
                    case 5:
                        switch (c) {
                            case 'E': state = 6; break;
                            case ' ': method = "CONN"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("CONN").append(c); state = 1; break;
                        }
                        break;
                    case 6:
                        switch (c) {
                            case 'C': state = 7; break;
                            case ' ': method = "CONNE"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("CONNE").append(c); state = 1; break;
                        }
                        break;
                    case 7:
                        switch (c) {
                            case 'T': state = 8; break;
                            case ' ': method = "CONNEC"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("CONNEC").append(c); state = 1; break;
                        }
                        break;
                    case 8:
                        switch (c) {
                            case ' ': method = Methods.CONNECT; state = 1000; break;
                            case '\r': break out;
                            default: builder.append(Methods.CONNECT).append(c); state = 1; break;
                        }
                        break;
                    // method = DELETE
                    case 20:
                        switch (c) {
                            case 'E': state = 21; break;
                            case ' ': method = "D"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append('D').append(c); state = 1; break;
                        }
                        break;
                    case 21:
                        switch (c) {
                            case 'L': state = 22; break;
                            case ' ': method = "DE"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("DE").append(c); state = 1; break;
                        }
                        break;
                    case 22:
                        switch (c) {
                            case 'E': state = 23; break;
                            case ' ': method = "DEL"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("DEL").append(c); state = 1; break;
                        }
                        break;
                    case 23:
                        switch (c) {
                            case 'T': state = 24; break;
                            case ' ': method = "DELE"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("DELE").append(c); state = 1; break;
                        }
                        break;
                    case 24:
                        switch (c) {
                            case 'E': state = 25; break;
                            case ' ': method = "DELET"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("DELET").append(c); state = 1; break;
                        }
                        break;
                    case 25:
                        switch (c) {
                            case ' ': method = Methods.DELETE; state = 1000; break;
                            case '\r': break out;
                            default: builder.append(Methods.DELETE).append(c); state = 1; break;
                        }
                        break;
                    // method = GET
                    case 40:
                        switch (c) {
                            case 'E': state = 41; break;
                            case ' ': method = "G"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append('G').append(c); state = 1; break;
                        }
                        break;
                    case 41:
                        switch (c) {
                            case 'T': state = 42; break;
                            case ' ': method = "GE"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("GE").append(c); state = 1; break;
                        }
                        break;
                    case 42:
                        switch (c) {
                            case ' ': method = Methods.GET; state = 1000; break;
                            case '\r': break out;
                            default: builder.append(Methods.GET).append(c); state = 1; break;
                        }
                        break;
                    // method = HEAD
                    case 60:
                        switch (c) {
                            case 'E': state = 61; break;
                            case ' ': method = "H"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append('H').append(c); state = 1; break;
                        }
                        break;
                    case 61:
                        switch (c) {
                            case 'A': state = 62; break;
                            case ' ': method = "HE"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("HE").append(c); state = 1; break;
                        }
                        break;
                    case 62:
                        switch (c) {
                            case 'D': state = 63; break;
                            case ' ': method = "HEA"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("HEA").append(c); state = 1; break;
                        }
                        break;
                    case 63:
                        switch (c) {
                            case ' ': method = Methods.HEAD; state = 1000; break;
                            case '\r': break out;
                            default: builder.append(Methods.HEAD).append(c); state = 1; break;
                        }
                        break;
                    // method = OPTIONS
                    case 80:
                        switch (c) {
                            case 'P': state = 81; break;
                            case ' ': method = "O"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append('O').append(c); state = 1; break;
                        }
                        break;
                    case 81:
                        switch (c) {
                            case 'T': state = 82; break;
                            case ' ': method = "OP"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("OP").append(c); state = 1; break;
                        }
                        break;
                    case 82:
                        switch (c) {
                            case 'I': state = 83; break;
                            case ' ': method = "OPT"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("OPT").append(c); state = 1; break;
                        }
                        break;
                    case 83:
                        switch (c) {
                            case 'O': state = 84; break;
                            case ' ': method = "OPTI"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("OPTI").append(c); state = 1; break;
                        }
                        break;
                    case 84:
                        switch (c) {
                            case 'N': state = 85; break;
                            case ' ': method = "OPTIO"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("OPTIO").append(c); state = 1; break;
                        }
                        break;
                    case 85:
                        switch (c) {
                            case 'S': state = 86; break;
                            case ' ': method = "OPTION"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("OPTION").append(c); state = 1; break;
                        }
                        break;
                    case 86:
                        switch (c) {
                            case ' ': method = Methods.OPTIONS; state = 1000; break;
                            case '\r': break out;
                            default: builder.append(Methods.OPTIONS).append(c); state = 1; break;
                        }
                        break;
                    // method = POST or PUT
                    case 100:
                        switch (c) {
                            case 'O': state = 101; break;
                            case 'U': state = 120; break;
                            case ' ': method = "P"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append('P').append(c); state = 1; break;
                        }
                        break;
                    // method = POST
                    case 101:
                        switch (c) {
                            case 'S': state = 102; break;
                            case ' ': method = "PO"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("PO").append(c); state = 1; break;
                        }
                        break;
                    case 102:
                        switch (c) {
                            case 'T': state = 103; break;
                            case ' ': method = "POS"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("POS").append(c); state = 1; break;
                        }
                        break;
                    case 103:
                        switch (c) {
                            case ' ': method = Methods.POST; state = 1000; break;
                            case '\r': break out;
                            default: builder.append(Methods.POST).append(c); state = 1; break;
                        }
                        break;
                    // method = PUT
                    case 120:
                        switch (c) {
                            case 'T': state = 121; break;
                            case ' ': method = "PU"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("PU").append(c); state = 1; break;
                        }
                        break;
                    case 121:
                        switch (c) {
                            case ' ': method = Methods.PUT; state = 1000; break;
                            case '\r': break out;
                            default: builder.append(Methods.PUT).append(c); state = 1; break;
                        }
                        break;
                    // method = TRACE
                    case 140:
                        switch (c) {
                            case 'R': state = 141; break;
                            case ' ': method = "T"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append('T').append(c); state = 1; break;
                        }
                        break;
                    case 141:
                        switch (c) {
                            case 'A': state = 142; break;
                            case ' ': method = "TR"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("TR").append(c); state = 1; break;
                        }
                        break;
                    case 142:
                        switch (c) {
                            case 'C': state = 143; break;
                            case ' ': method = "TRA"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("TRA").append(c); state = 1; break;
                        }
                        break;
                    case 143:
                        switch (c) {
                            case 'E': state = 144; break;
                            case ' ': method = "TRAC"; state = 1000; break;
                            case '\r': break out;
                            default: builder.append("TRAC").append(c); state = 1; break;
                        }
                        break;
                    case 144:
                        switch (c) {
                            case ' ': method = Methods.TRACE; state = 1000; break;
                            case '\r': break out;
                            default: builder.append(Methods.TRACE).append(c); state = 1; break;
                        }
                        break;

                    // --------------------------------------------------------------------
                    // Read HTTP URL
                    // --------------------------------------------------------------------
                    case 1000:
                        switch (c) {
                            case ' ': break;
                            case '\r': break out;
                            default: builder.append(c); state = 1001; break;
                        }
                        break;
                    case 1001:
                        switch (c) {
                            case ' ': requestUri = builder.toString(); builder.setLength(0);
                        }
                        break;
                    case 1002:
                        switch (c) {
                            case '\n': requestUri = builder.toString(); builder.setLength(0); requestHeaders = new HeaderMap(); state = 2000; break;
                            default: break out;
                        }
                        break;
                    case 1003:
                        switch (c) {

                        }
                        break;

                    // --------------------------------------------------------------------
                    // Read Protocol version
                    // --------------------------------------------------------------------
                    case 1500:
                        switch (c) {
                            case 'H': state = 1501; break;
                            default: break out;
                        }
                        break;

                    // --------------------------------------------------------------------
                    // Read Headers
                    // --------------------------------------------------------------------
                    case 2000:
                        switch (c) {
                            case ' ':
                            case ':': break out;
                            case '\r': state = 2005; break;
                            default: builder.append(c); state = 2001; break;
                        }
                        break;
                    case 2001:
                        switch (c) {
                            case ':': key = builder.toString(); builder.setLength(0); state = 2002; break;
                            case '\r': break out;
                            default: builder.append(c); break;
                        }
                        break;
                    case 2002:
                        switch (c) {
                            case ' ': break;
                            case '\r': requestHeaders.add(key, ""); key = null; state = 2004; break;
                            default: builder.append(c); state = 2003; break;
                        }
                        break;
                    case 2003:
                        switch (c) {
                            case '\r': requestHeaders.add(key, builder.toString()); builder.setLength(0); key = null; state = 2004; break;
                            default: builder.append(c); break;
                        }
                    case 2004:
                        switch (c) {
                            case '\n': state = 2000; break;
                            default: break out;
                        }
                    case 2005:
                        switch (c) {
                            case '\n': state = 0; /* TODO HANDLE REQUEST */; requestHeaders = null; break;
                            default: break out;
                        }
                    default: throw new IllegalStateException();
                }
            }
            return false;
        } finally {
            this.state = state;
        }
    }

    private void shutdownError(final PushBackStreamChannel channel, final IOException e) {
        IoUtils.safeClose(channel);
        // todo log error
    }
}
