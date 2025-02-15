/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.vm;

import li.cil.oc2r.api.bus.device.vm.VMDevice;

import java.util.OptionalLong;

public interface BaseAddressProvider {
    OptionalLong getBaseAddress(final VMDevice wrapper);
}
