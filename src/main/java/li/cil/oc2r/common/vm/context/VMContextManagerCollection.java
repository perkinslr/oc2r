/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.vm.context;

public interface VMContextManagerCollection {
    InterruptManager getInterruptManager();

    MemoryRangeManager getMemoryRangeManager();

    EventManager getEventManager();
}
