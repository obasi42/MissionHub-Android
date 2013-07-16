/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package com.missionhub.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

@SuppressWarnings({"serial"})
public class WeakArrayList<T> extends ArrayList<WeakReference<T>> {

    protected List<WeakReference<T>> synchronizedList;

    public WeakArrayList() {
        this(true);
    }

    public WeakArrayList(final boolean isSynchronized) {
        if (isSynchronized) {
            synchronizedList();
        }
    }

    public List<WeakReference<T>> synchronizedList() {
        if (synchronizedList == null) {
            synchronizedList = Collections.synchronizedList(this);
        }
        return synchronizedList;
    }

    public boolean refEquals(final WeakReference<T> ref, final Object o) {
        if (ref == null) {
            return false;
        }
        return ref.get() == o || ref.get() != null && ref.get().equals(o);
    }

    protected boolean findRef(final Object o) {
        for (final WeakReference<T> ref : this) {
            if (refEquals(ref, o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean add(final WeakReference<T> o) {
        if (synchronizedList != null) {
            synchronized (synchronizedList) {
                return super.add(o);
            }
        }
        return super.add(o);
    }

    @Override
    public boolean contains(final Object o) {
        if (o instanceof WeakReference) {
            return super.contains(o);
        }
        if (synchronizedList != null) {
            synchronized (synchronizedList) {
                return findRef(o);
            }
        }
        return findRef(o);
    }

    protected boolean removeRef(final Object o) {
        final Iterator<WeakReference<T>> iter = iterator();
        while (iter.hasNext()) {
            final WeakReference<T> ref = iter.next();
            if (refEquals(ref, o)) {
                iter.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean remove(final Object o) {
        if (o instanceof WeakReference) {
            return super.remove(o);
        }
        if (synchronizedList != null) {
            synchronized (synchronizedList) {
                return removeRef(o);
            }
        }
        return removeRef(o);
    }

    protected class NonNullIterator implements Iterator<T> {
        protected int index;

        public NonNullIterator(final int index) {
            this.index = index;
        }

        protected int getNextIndex() {
            final int size = size();
            for (int i = index; i < size; i++) {
                final WeakReference<T> ref = get(i);
                if (ref != null && ref.get() != null) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public boolean hasNext() {
            if (synchronizedList != null) {
                synchronized (synchronizedList) {
                    return getNextIndex() >= 0;
                }
            } else {
                return getNextIndex() >= 0;
            }
        }

        @Override
        public T next() {
            if (synchronizedList != null) {
                synchronized (synchronizedList) {
                    final int nextIndex = getNextIndex();
                    if (nextIndex < 0) {
                        throw new NoSuchElementException();
                    }
                    index = nextIndex + 1;
                    return get(nextIndex).get();
                }
            } else {
                final int nextIndex = getNextIndex();
                if (nextIndex < 0) {
                    throw new NoSuchElementException();
                }
                index = nextIndex + 1;
                return get(nextIndex).get();
            }
        }

        @Override
        public void remove() {
            if (synchronizedList != null) {
                synchronized (synchronizedList) {
                    WeakArrayList.this.remove(index);
                }
            } else {
                WeakArrayList.this.remove(index);
            }
        }
    }

    public Iterator<T> nonNullIterator() {
        return new NonNullIterator(0);
    }

    public Iterable<T> nonNull() {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return nonNullIterator();
            }
        };
    }
}