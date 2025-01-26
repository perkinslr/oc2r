/* SPDX-License-Identifier: MIT */

package li.cil.oc2.api.bus.device.rpc;

import li.cil.oc2.api.bus.DeviceBus;
import li.cil.oc2.api.bus.device.Device;
import li.cil.oc2.api.bus.device.object.ObjectDevice;
import java.util.*;

/**
 * Provides an interface for an RPC event source. Blocks which whish to provide
 * push notifications via the /dev/hvc0 serial devices should implement this.
 * It is generally recommended to *also* provide documentation and a list of
 * events by implementing DocumentedDevice and providing a listEvents() callback
 * <p>
 */
public interface RPCEventSource {

    /**
     * Called to add a {@link IEventSink} to the list of consumers.
     */
    void subscribe(IEventSink dba, UUID sourceid);
    /**
     * Called to remove a specific {@link IEventSink} from the list of consumers.
     */
    void unsubscribe(IEventSink dba);
}
