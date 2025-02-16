/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common;

import li.cil.oc2r.common.bus.device.rpc.RPCMethodParameterTypeAdapters;
import li.cil.oc2r.common.inet.InternetManagerImpl;
import li.cil.oc2r.common.integration.IMC;
import li.cil.oc2r.common.network.Network;
import li.cil.oc2r.common.util.ServerScheduler;
import li.cil.oc2r.common.vxlan.TunnelManager;
import li.cil.oc2r.common.integration.Integrations;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public final class CommonSetup {
    @SubscribeEvent
    public static void handleSetupEvent(final FMLCommonSetupEvent event) {
        IMC.initialize();
        Network.initialize();
        InternetManagerImpl.initialize();
        Integrations.initialize();
        RPCMethodParameterTypeAdapters.initialize();
        ServerScheduler.initialize();
        TunnelManager.initialize();
    }
}
