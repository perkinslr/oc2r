/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.bus.device.vm.block;

import net.minecraft.world.item.ItemStack;

public interface FlashMemoryFlasherContainer {
    ItemStack getDiskItemStack();

    void handleDataAccess();
}
