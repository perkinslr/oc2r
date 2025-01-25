/* SPDX-License-Identifier: MIT */

package li.cil.oc2.common.bus.device.rpc;

import li.cil.oc2.api.bus.device.object.Callback;
import li.cil.oc2.api.bus.device.object.NamedDevice;
import li.cil.oc2.common.bus.device.util.IdentityProxy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraft.core.Direction;
import li.cil.oc2.common.capabilities.Capabilities;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import li.cil.oc2.api.util.Side;
import li.cil.oc2.common.util.HorizontalBlockUtils;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.Collections;
import li.cil.oc2.api.bus.device.object.Parameter;
import li.cil.oc2.api.bus.device.provider.BlockDeviceQuery;
import com.google.gson.JsonObject;

public final class FluidHandlerDevice extends IdentityProxy<IFluidHandler> implements NamedDevice {
    Optional<BlockDeviceQuery> _query = null;
    public FluidHandlerDevice(final IFluidHandler identity) {
        super(identity);
    }
    public FluidHandlerDevice(final IFluidHandler identity, final BlockDeviceQuery query) {
        super(identity);
        this._query = Optional.of(query);
    }


    @Override
    public Collection<String> getDeviceTypeNames() {
        return Collections.singletonList("fluid_handler");
    }

    @Callback
    public int getFluidTanks() {
        return identity.getTanks();
    }

    @Callback
    public JsonObject getFluidInTank(final int tank) {
        FluidStack fluid = identity.getFluidInTank(tank);

        if (fluid == null) {
            return null;
        }
        JsonObject obj = new JsonObject();
        obj.addProperty("name", ForgeRegistries.FLUIDS.getKey(fluid.getFluid()).toString());
        obj.addProperty("amount", fluid.getAmount());
        return obj;
    }

    @Callback
    public int getFluidTankCapacity(final int tank) {
        return identity.getTankCapacity(tank);
    }

    @Callback
    public int transfer(@Parameter("side") final Side side,
                        @Parameter("count") final int count,
                        @Parameter("simulate") final boolean simulate) {

        if (!this._query.isPresent()) {
            return -1;
        }

        BlockPos fromPos = this._query.get().getQueryPosition();
        final BlockEntity fromBlockEntity = this._query.get().getLevel().getBlockEntity(fromPos);

        final Direction direction = HorizontalBlockUtils.toGlobal(fromBlockEntity.getBlockState(), side);
        final BlockPos toPos = fromPos.relative(direction);

        System.out.println("toPos: " + toPos);
        final BlockEntity toBlockEntity = this._query.get().getLevel().getBlockEntity(toPos);


        if (toBlockEntity != null) {
            final Optional<IFluidHandler> fromCapability = fromBlockEntity.getCapability(Capabilities.fluidHandler(), direction).resolve();
            System.out.println("fromCapability: " + fromCapability);

            final Optional<IFluidHandler> toCapability = toBlockEntity.getCapability(Capabilities.fluidHandler(), direction.getOpposite()).resolve();
            System.out.println("toCapability: " + toCapability);
            if (!fromCapability.isPresent() || !toCapability.isPresent()) {
                return -4;
            }
            // Simulate Pass!
            FluidStack fromStack = fromCapability.get().drain(count, IFluidHandler.FluidAction.SIMULATE);
            int realCount = toCapability.get().fill(fromStack, IFluidHandler.FluidAction.SIMULATE);
            if (simulate) {
                return realCount;
            }

            fromStack = fromCapability.get().drain(realCount, IFluidHandler.FluidAction.EXECUTE);
            realCount = toCapability.get().fill(fromStack, IFluidHandler.FluidAction.EXECUTE);
            return realCount;

        }
        else {
            return -2;
        }


    }
}
