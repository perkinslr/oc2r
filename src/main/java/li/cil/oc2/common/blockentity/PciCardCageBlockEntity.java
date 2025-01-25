/* SPDX-License-Identifier: MIT */

package li.cil.oc2.common.blockentity;

import li.cil.oc2.common.Config;
import li.cil.oc2.common.block.PciCardCageBlock;
import li.cil.oc2.common.bus.device.vm.block.PciCardCageDevice;
import li.cil.oc2.common.capabilities.Capabilities;
import li.cil.oc2.common.energy.FixedEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;


public final class PciCardCageBlockEntity extends ModBlockEntity implements TickableBlockEntity {

    private static final String ENERGY_TAG_NAME = "energy";
    private static final String HAS_ENERGY_TAG_NAME = "has_energy";

    ///////////////////////////////////////////////////////////////

    private final PciCardCageDevice cardCageDevice = new PciCardCageDevice(this, this::handleMountedChanged);
    private boolean isMounted, hasEnergy;
    private final FixedEnergyStorage energy = new FixedEnergyStorage(Config.cardCageEnergyStorage);


    ///////////////////////////////////////////////////////////////

    public PciCardCageBlockEntity(final BlockPos pos, final BlockState state) {
        super(BlockEntities.PCI_CARD_CAGE.get(), pos, state);
    }

    ///////////////////////////////////////////////////////////////

    private void handleMountedChanged(final boolean value) {

    }


    public boolean hasEnergy() {
        return hasEnergy;
    }

    @Override
    public void serverTick() {
        if (!isMounted) {
            return;
        }

        final boolean isPowered;
        if (Config.cardCagesUseEnergy()) {
            isPowered = energy.extractEnergy(Config.cardCageEnergyPerTick, true) >= Config.cardCageEnergyPerTick;
            if (isPowered) {
                energy.extractEnergy(Config.cardCageEnergyPerTick, false);
            }
        } else {
            isPowered = true;
        }


    }

    @Override
    public CompoundTag getUpdateTag() {
        final CompoundTag tag = super.getUpdateTag();

        tag.putBoolean(HAS_ENERGY_TAG_NAME, hasEnergy);

        return tag;
    }

    @Override
    public void handleUpdateTag(final CompoundTag tag) {
        super.handleUpdateTag(tag);

        hasEnergy = tag.getBoolean(HAS_ENERGY_TAG_NAME);
    }

    @Override
    protected void saveAdditional(final CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put(ENERGY_TAG_NAME, energy.serializeNBT());
    }

    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);

        energy.deserializeNBT(tag.getCompound(ENERGY_TAG_NAME));
    }


    @SuppressWarnings("deprecation")
    @Override
    public void setBlockState(final BlockState state) {
        super.setBlockState(state);

    }

    ///////////////////////////////////////////////////////////////

    @Override
    protected void collectCapabilities(final CapabilityCollector collector, @Nullable final Direction direction) {
        if (Config.cardCagesUseEnergy()) {
            collector.offer(Capabilities.energyStorage(), energy);
        }

        if (direction == getBlockState().getValue(PciCardCageBlock.FACING).getOpposite()) {
            collector.offer(Capabilities.device(), cardCageDevice);
        }
    }

    ///////////////////////////////////////////////////////////////




}
