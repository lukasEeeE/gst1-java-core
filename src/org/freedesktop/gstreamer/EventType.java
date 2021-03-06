/* 
 * Copyright (c) 2008 Wayne Meissner
 * Copyright (C) 1999,2000 Erik Walthinsen <omega@cse.ogi.edu>
 *                    2000 Wim Taymans <wim.taymans@chello.be>
 *                    2005 Wim Taymans <wim@fluendo.com>
 * 
 * This code is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License 
 * version 3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.freedesktop.gstreamer;

import org.freedesktop.gstreamer.lowlevel.EnumMapper;
import org.freedesktop.gstreamer.lowlevel.GstNative;
import org.freedesktop.gstreamer.lowlevel.IntegerEnum;
import org.freedesktop.gstreamer.lowlevel.annotations.DefaultEnumValue;

/**
 * The standard event types that can be sent in a pipeline.
 * <p>
 * The custom event types can be used for private messages between elements
 * that can't be expressed using normal GStreamer buffer passing semantics. 
 * <p>
 * Custom events carry an arbitrary {@link Structure}.  Specific custom events 
 * are distinguished by the name of the structure.
 */
public enum EventType implements IntegerEnum {
    /** Unknown event */
    @DefaultEnumValue UNKNOWN(0, 0),

    /* bidirectional events */
    /**
     * Start a flush operation. This event clears all data from the pipeline and
     * unblock all streaming threads.
     */
    FLUSH_START(10, Flags.BOTH),
    /**
     * Stop a flush operation. This event resets the running-time of the
     * pipeline.
     */
    FLUSH_STOP(20, Flags.BOTH | Flags.SERIALIZED),

    /* downstream serialized events */
    /**
     * Event to mark the start of a new stream. Sent before any other serialized
     * event and only sent at the start of a new stream, not after flushing
     * seeks.
     */
    STREAM_START(40, Flags.DOWNSTREAM | Flags.SERIALIZED | Flags.STICKY),
    /**
     * #GstCaps event. Notify the pad of a new media type.
     */
    CAPS(50, Flags.DOWNSTREAM | Flags.SERIALIZED | Flags.STICKY),
    /**
     * A new media segment follows in the dataflow. The segment events contains
     * information for clipping buffers and converting buffer timestamps to
     * running-time and stream-time.
     */
    SEGMENT(70, Flags.DOWNSTREAM | Flags.SERIALIZED | Flags.STICKY),
    /**
     * A new set of metadata tags has been found in the stream.
     */
    TAG(80, Flags.DOWNSTREAM | Flags.SERIALIZED | Flags.STICKY | Flags.STICKY_MULTI),
    /**
     * Notification of buffering requirements. Currently not used yet.
     */
    BUFFERSIZE(90, Flags.DOWNSTREAM | Flags.SERIALIZED | Flags.STICKY),
    /**
     * An event that sinks turn into a message. Used to send messages that
     * should be emitted in sync with rendering.
     */
    SINK_MESSAGE(100, Flags.DOWNSTREAM | Flags.SERIALIZED | Flags.STICKY | Flags.STICKY_MULTI),
    /**
     * End-Of-Stream. No more data is to be expected to follow without a SEGMENT
     * event.
     */
    EOS(110, Flags.DOWNSTREAM | Flags.SERIALIZED | Flags.STICKY),
    /**
     * An event which indicates that a new table of contents (TOC) was found or
     * updated.
     */
    TOC(120, Flags.DOWNSTREAM | Flags.SERIALIZED | Flags.STICKY | Flags.STICKY_MULTI),
    /**
     * An event which indicates that new or updated encryption information has
     * been found in the stream.
     */
    PROTECTION(130, Flags.DOWNSTREAM | Flags.SERIALIZED | Flags.STICKY | Flags.STICKY_MULTI),

    /* non-sticky downstream serialized */
    /**
     * Marks the end of a segment playback.
     */
    SEGMENT_DONE(150, Flags.DOWNSTREAM | Flags.SERIALIZED),
    /**
     * Marks a gap in the datastream.
     */
    GAP(160, Flags.DOWNSTREAM | Flags.SERIALIZED),

    /* upstream events */
    /**
     * A quality message. Used to indicate to upstream elements that the
     * downstream elements should adjust their processing rate.
     */
    QOS(190, Flags.UPSTREAM),
    /**
     * A request for a new playback position and rate.
     */
    SEEK(200, Flags.UPSTREAM),
    /**
     * Navigation events are usually used for communicating user requests, such
     * as mouse or keyboard movements, to upstream elements.
     */
    NAVIGATION(210, Flags.UPSTREAM),
    /**
     * Notification of new latency adjustment. Sinks will use the latency
     * information to adjust their synchronisation.
     */
    LATENCY(220, Flags.UPSTREAM),
    /**
     * A request for stepping through the media. Sinks will usually execute the
     * step operation.
     */
    STEP(230, Flags.UPSTREAM),
    /**
     * A request for upstream renegotiating caps and reconfiguring.
     */
    RECONFIGURE(240, Flags.UPSTREAM),
    /**
     * A request for a new playback position based on TOC entry's UID.
     */
    TOC_SELECT(250, Flags.UPSTREAM),

    /* custom events start here */
    /**
     * Upstream custom event
     */
    CUSTOM_UPSTREAM(270, Flags.UPSTREAM),
    /**
     * Downstream custom event that travels in the data flow.
     */
    CUSTOM_DOWNSTREAM(280, Flags.DOWNSTREAM | Flags.SERIALIZED),
    /**
     * Custom out-of-band downstream event.
     */
    CUSTOM_DOWNSTREAM_OOB(290, Flags.DOWNSTREAM),
    /**
     * Custom sticky downstream event.
     */
    CUSTOM_DOWNSTREAM_STICKY(300, Flags.DOWNSTREAM | Flags.SERIALIZED | Flags.STICKY | Flags.STICKY_MULTI),
    /**
     * Custom upstream or downstream event. In-band when travelling downstream.
     */
    CUSTOM_BOTH(310, Flags.BOTH | Flags.SERIALIZED),
    /**
     * Custom upstream or downstream out-of-band event.
     */
    CUSTOM_BOTH_OOB(320, Flags.BOTH)
    ;
    
    private static interface API extends com.sun.jna.Library {
        String gst_event_type_get_name(EventType type);
    }
    private static final API gst = GstNative.load(API.class);
    EventType(int num, int flags) {
        this.value = (num << SHIFT) | flags;
    }
    
    /** Gets the native value of this enum */
    public int intValue() {
        return value;
    }
    /** Gets a human-readable name for this value */
    public String getName() {
        return gst.gst_event_type_get_name(this);
    }
    
    /** Gets the Enum for a native value */
    public static final EventType valueOf(int type) {
        return EnumMapper.getInstance().valueOf(type, EventType.class);
    }
    
    private static final int SHIFT = 8;
    private static final class Flags {
        public static final int UPSTREAM       = 1 << 0;
        public static final int DOWNSTREAM     = 1 << 1;
        public static final int SERIALIZED     = 1 << 2;
        public static final int STICKY         = 1 << 3;
        public static final int STICKY_MULTI   = 1 << 4;
        public static final int BOTH = UPSTREAM | DOWNSTREAM;
    }
    private final int value;
}
