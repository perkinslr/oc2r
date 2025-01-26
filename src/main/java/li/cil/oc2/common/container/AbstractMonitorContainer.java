/* SPDX-License-Identifier: MIT */

package li.cil.oc2.common.container;

import li.cil.oc2.common.block.Blocks;
import li.cil.oc2.common.blockentity.MonitorBlockEntity;
import li.cil.oc2.common.bus.CommonDeviceBusController;
import li.cil.oc2.common.network.Network;
import li.cil.oc2.common.network.message.*;
import li.cil.oc2.common.vm.VirtualMachine;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public abstract class AbstractMonitorContainer extends AbstractMachineContainer {
    private final MonitorBlockEntity monitor;

    ///////////////////////////////////////////////////////////////////

    protected AbstractMonitorContainer(final MenuType<?> type, final int id, final Player player, final MonitorBlockEntity monitor, final IntPrecisionContainerData energyInfo) {
        super(type, id, energyInfo);
        this.monitor = monitor;
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    public void switchToInventory() {}

    @Override
    @Nullable
    public VirtualMachine getVirtualMachine() { return null; }

    public MonitorBlockEntity getMonitor() { return monitor; }

    public boolean hasPower() { return monitor.hasPower(); }

    public boolean getPowerState() { return monitor.getPowerState(); }

    public boolean isMounted() { return monitor.isMounted(); }

    @Override
    public void sendPowerStateToServer(final boolean value) {
        Network.sendToServer(new MonitorPowerMessage(monitor, value));
    }

    @Override
    public boolean stillValid(final Player player) {
        if (!monitor.isValid()) {
            return false;
        }
        final Level level = monitor.getLevel();
        return level != null && stillValid(ContainerLevelAccess.create(level, monitor.getBlockPos()), player, Blocks.MONITOR.get());
    }

    ///////////////////////////////////////////////////////////////////

    protected static IntPrecisionContainerData createEnergyInfo(final IEnergyStorage energy, final CommonDeviceBusController busController) {
        return new IntPrecisionContainerData.Server() {
            @Override
            public int getInt(final int index) {
                return switch (index) {
                    case AbstractMachineContainer.ENERGY_STORED_INDEX -> energy.getEnergyStored();
                    case AbstractMachineContainer.ENERGY_CAPACITY_INDEX -> energy.getMaxEnergyStored();
                    case AbstractMachineContainer.ENERGY_CONSUMPTION_INDEX -> busController.getEnergyConsumption();
                    default -> 0;
                };
            }

            @Override
            public int getIntCount() {
                return ENERGY_INFO_SIZE;
            }
        };
    }

    protected static IntPrecisionContainerData createEnergyInfo(final IEnergyStorage energy) {
        return new IntPrecisionContainerData.Server() {
            @Override
            public int getInt(final int index) {
                return switch (index) {
                    case AbstractMachineContainer.ENERGY_STORED_INDEX -> energy.getEnergyStored();
                    case AbstractMachineContainer.ENERGY_CAPACITY_INDEX -> energy.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public int getIntCount() {
                return ENERGY_INFO_SIZE;
            }
        };
    }
}
