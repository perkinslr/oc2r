package li.cil.oc2.common.integration;

import li.cil.oc2.common.integration.projectred.BundledCableHandler;
import net.minecraftforge.fml.ModList;

public class Integrations {
    public static void initialize() {
        if(ModList.get().isLoaded("projectred_transmission"))
            BundledCableHandler.initialize();
    }
}
