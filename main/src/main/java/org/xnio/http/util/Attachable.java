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

package org.xnio.http.util;

import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public abstract class Attachable {
    private final ConcurrentMap<String, Object> attachments = new SecureHashMap<>();

    public Object getAttachment(String name) {
        return attachments.get(name);
    }

    public Object putAttachment(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        return attachments.put(name, value);
    }

    public Object putAttachmentIfAbsent(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        return attachments.putIfAbsent(name, value);
    }

    public Object replaceAttachment(String name, Object newValue) {
        return attachments.replace(name, newValue);
    }

    public Object removeAttachment(String name) {
        return attachments.remove(name);
    }

    public boolean replaceAttachment(String name, Object expectValue, Object newValue) {
        return attachments.replace(name, expectValue, newValue);
    }

    public boolean removeAttachment(String name, Object expectValue) {
        return attachments.remove(name, expectValue);
    }

    public ConcurrentMap<String, Object> getAttachments() {
        return attachments;
    }
}
