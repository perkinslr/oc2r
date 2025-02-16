/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.bus.device.data;

import li.cil.oc2r.api.API;
import li.cil.oc2r.api.bus.device.data.BlockDeviceData;
import li.cil.oc2r.api.util.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class BlockDeviceDataRegistry {
    private static final DeferredRegister<BlockDeviceData> INITIALIZER = DeferredRegister.create(Registries.BLOCK_DEVICE_DATA, API.MOD_ID);

    ///////////////////////////////////////////////////////////////////

    private static final Supplier<IForgeRegistry<BlockDeviceData>> REGISTRY = INITIALIZER.makeRegistry(RegistryBuilder::new);

    ///////////////////////////////////////////////////////////////////

    public static final RegistryObject<BlockDeviceData> BUILDROOT = INITIALIZER.register("buildroot", BuildrootBlockDeviceData::new);

    ///////////////////////////////////////////////////////////////////

    public static void initialize() {
        INITIALIZER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @Nullable
    public static ResourceLocation getKey(final BlockDeviceData data) {
        ResourceLocation location = REGISTRY.get().getKey(data);
        if (location == null) {
            location = FileSystems.getKeyByValue(data);
        }
        return location;
    }

    @Nullable
    public static BlockDeviceData getValue(final ResourceLocation location) {
        final BlockDeviceData value = REGISTRY.get().getValue(location);
        if (value != null) {
            return value;
        }
        return FileSystems.getBlockData().get(location);
    }

    public static Stream<BlockDeviceData> values() {
        return Stream.concat(
            REGISTRY.get().getValues().stream(),
            FileSystems.getBlockData().values().stream());
    }
}
