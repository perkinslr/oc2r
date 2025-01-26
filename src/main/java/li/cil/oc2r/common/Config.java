/* SPDX-License-Identifier: MIT */

package li.cil.oc2.common;

import li.cil.oc2.common.ConfigManager.Path;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.UUID;

@SuppressWarnings("FieldMayBeFinal")
public final class Config {
    //TODO: Implement configuration of CPU MHzs
    @Path("vm") public static long maxAllocatedMemory = 512 * Constants.MEGABYTE;

    @Path("energy.blocks") public static double busCableEnergyPerTick = 0.1;
    @Path("energy.blocks") public static double busInterfaceEnergyPerTick = 0.5;
    @Path("energy.blocks") public static int computerEnergyPerTick = 10;
    @Path("energy.blocks") public static int computerEnergyStorage = 2000;
    @Path("energy.blocks") public static int chargerEnergyPerTick = 2500;
    @Path("energy.blocks") public static int chargerEnergyStorage = 10000;
    @Path("energy.blocks") public static int projectorEnergyPerTick = 20;
    @Path("energy.blocks") public static int projectorEnergyStorage = 2000;
    @Path("energy.blocks") public static int monitorEnergyPerTick = 15;
    @Path("energy.blocks") public static int monitorEnergyStorage = 2000;
    @Path("energy.blocks") public static int cardCageEnergyPerTick = 20;
    @Path("energy.blocks") public static int cardCageEnergyStorage = 2000;

    @Path("energy.entities") public static int robotEnergyPerTick = 5;
    @Path("energy.entities") public static int robotEnergyStorage = 750000;

    @Path("energy.items") public static double memoryEnergyPerMegabytePerTick = 0.5;
    @Path("energy.items") public static double hardDriveEnergyPerMegabytePerTick = 1;
    @Path("energy.items") public static double cpuEnergyPerMegahertzPerTick = 0.1;
    @Path("energy.items") public static int redstoneInterfaceCardEnergyPerTick = 1;
    @Path("energy.items") public static int networkInterfaceEnergyPerTick = 1;
    @Path("energy.items") public static int fileImportExportCardEnergyPerTick = 1;
    @Path("energy.items") public static int soundCardEnergyPerTick = 1;
    @Path("energy.items") public static int blockOperationsModuleEnergyPerTick = 2;
    @Path("energy.items") public static int inventoryOperationsModuleEnergyPerTick = 1;
    @Path("energy.items") public static int networkTunnelEnergyPerTick = 2;

    @Path("gameplay") public static ResourceLocation blockOperationsModuleToolTier = TierSortingRegistry.getName(Tiers.DIAMOND);
    @Path("gameplay") public static long soundCardCoolDownSeconds = 2;

    @Path("admin") public static UUID fakePlayerUUID = UUID.fromString("e39dd9a7-514f-4a2d-aa5e-b6030621416d");
    @Path("admin.network") public static int projectorAverageMaxBytesPerSecond = 160 * 1024;
    @Path("admin.virtual_network") public static int ethernetFrameTimeToLive = 12;
    @Path("admin.virtual_network") public static int hubEthernetFramesPerTick = 32;

    @Path("vxlan") public static boolean enable = false;
    @Path("vxlan") public static String remoteHost = "::1";
    @Path("vxlan") public static int remotePort = 4789;
    @Path("vxlan") public static String bindHost = "::1";
    @Path("vxlan") public static int bindPort = 4789;

    public static boolean computersUseEnergy() {
        return computerEnergyPerTick > 0 && computerEnergyStorage > 0;
    }

    public static boolean projectorsUseEnergy() {
        return projectorEnergyStorage > 0 && projectorEnergyPerTick > 0;
    }

    public static boolean cardCagesUseEnergy() {
        return cardCageEnergyStorage > 0 && cardCageEnergyPerTick > 0;
    }

    public static boolean robotsUseEnergy() {
        return robotEnergyPerTick > 0 && robotEnergyStorage > 0;
    }

    public static boolean monitorsUseEnergy() {
        return computerEnergyPerTick > 0 && computerEnergyStorage > 0;
    }
}
