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

package org.xnio.http.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import org.xnio.OptionMap;
import org.xnio.http.server.HostHttpContext;
import org.xnio.http.server.HttpServer;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class Test {

    public static void main(String[] args) throws URISyntaxException, IOException {
        final HttpServer server = new HttpServer(OptionMap.EMPTY);
        final HostHttpContext virtualHttpHost = server.bind(new InetSocketAddress(8080), null, OptionMap.EMPTY);
    }
}
