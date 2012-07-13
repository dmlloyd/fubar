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
import org.xnio.ChannelListener;
import org.xnio.channels.AcceptingChannel;
import org.xnio.channels.ConnectedStreamChannel;
import org.xnio.channels.PushBackStreamChannel;
import org.xnio.channels.StreamChannel;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
final class HttpAcceptListener implements ChannelListener<AcceptingChannel<? extends ConnectedStreamChannel>> {

    public void handleEvent(final AcceptingChannel<? extends ConnectedStreamChannel> channel) {
        try {
            StreamChannel httpChannel = channel.accept();
            final PushBackStreamChannel pushBackStreamChannel = new PushBackStreamChannel(httpChannel);

            HttpReadListener readListener = new HttpReadListener(null, null);
            pushBackStreamChannel.getReadSetter().set(readListener);
            readListener.handleEvent(pushBackStreamChannel);
            httpChannel.resumeReads();
        } catch (IOException e) {
            // todo log it
        }
    }
}
