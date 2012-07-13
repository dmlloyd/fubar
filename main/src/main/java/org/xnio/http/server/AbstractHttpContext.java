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

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * An abstract base class for HTTP contexts.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public abstract class AbstractHttpContext implements HttpContext {
    @SuppressWarnings("unused")
    private volatile HttpHandler handler;

    private static final AtomicReferenceFieldUpdater<AbstractHttpContext, HttpHandler> handlerUpdater = AtomicReferenceFieldUpdater.newUpdater(AbstractHttpContext.class, HttpHandler.class, "handler");

    public AbstractHttpContext(final HttpHandler initialHandler) {
        handlerUpdater.set(this, initialHandler);
    }

    protected AbstractHttpContext() {
    }

    public HttpHandler getHandler() {
        return handlerUpdater.get(this);
    }

    public HttpHandler setHandler(final HttpHandler handler) {
        return handlerUpdater.getAndSet(this, handler);
    }

    public boolean compareAndSetHandler(final HttpHandler oldHandler, final HttpHandler newHandler) {
        return handlerUpdater.compareAndSet(this, oldHandler, newHandler);
    }

    protected void executeNext(final HttpServerExchange exchange) {
        HttpHandler handler = handlerUpdater.get(this);
        if (handler != null) {
            handler.equals(exchange);
        }
    }
}
