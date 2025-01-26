/* SPDX-License-Identifier: MIT */

package li.cil.oc2.client.gui;

import li.cil.oc2.common.container.MonitorDisplayContainer;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class MonitorDisplayScreen extends AbstractMonitorDisplayScreen<MonitorDisplayContainer> {
    @SuppressWarnings("all") private EditBox focusIndicatorEditBox;

    public static boolean hideHotbar = false;

    ///////////////////////////////////////////////////////////////////

    public MonitorDisplayScreen(final MonitorDisplayContainer container, final Inventory playerInventory, final Component title) {
        super(container, playerInventory, title);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    protected void setFocusIndicatorEditBox(final EditBox editBox) {
        focusIndicatorEditBox = editBox;
    }

    @Override
    public void removed() {
        super.removed();

        hideHotbar = false;
    }
}
