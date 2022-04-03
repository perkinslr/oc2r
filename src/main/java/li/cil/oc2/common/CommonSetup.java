/* SPDX-License-Identifier: MIT */

package li.cil.oc2.common;

import li.cil.oc2.common.bus.device.rpc.RPCMethodParameterTypeAdapters;
import li.cil.oc2.common.inet.InternetManagerImpl;
import li.cil.oc2.common.integration.IMC;
import li.cil.oc2.common.network.Network;
import li.cil.oc2.common.util.ServerScheduler;
import li.cil.oc2.common.vxlan.TunnelManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public final class CommonSetup {
    @SubscribeEvent
    public static void handleSetupEvent(final FMLCommonSetupEvent event) {
        IMC.initialize();
        Network.initialize();
        InternetManagerImpl.initialize();
        RPCMethodParameterTypeAdapters.initialize();
        ServerScheduler.initialize();
        TunnelManager.initialize();
    }
}
