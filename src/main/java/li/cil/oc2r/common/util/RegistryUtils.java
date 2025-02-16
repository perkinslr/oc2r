/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.util;

import li.cil.oc2r.api.API;
import li.cil.oc2r.api.bus.device.DeviceType;
import li.cil.oc2r.api.bus.device.provider.BlockDeviceProvider;
import li.cil.oc2r.api.bus.device.provider.ItemDeviceProvider;
import li.cil.oc2r.common.bus.device.provider.ProviderRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class RegistryUtils {
    private enum Phase {
        PRE_INIT,
        INIT,
        POST_INIT,
    }

    private static final List<DeferredRegister<?>> ENTRIES = new ArrayList<>();
    private static Phase phase = Phase.PRE_INIT;

    public static <T extends IForgeRegistry<T>> DeferredRegister<T> getInitializerFor(final ResourceKey<Registry<T>> key) {
        if (phase != Phase.INIT) throw new IllegalStateException();

        final DeferredRegister<T> entry = DeferredRegister.create(key, API.MOD_ID);
        ENTRIES.add(entry);
        return entry;
    }

    public static <T extends IForgeRegistry<T>> DeferredRegister<T> getInitializerFor(final IForgeRegistry<T> registry) {
        if (phase != Phase.INIT) throw new IllegalStateException();

        final DeferredRegister<T> entry = DeferredRegister.create(registry, API.MOD_ID);
        ENTRIES.add(entry);
        return entry;
    }

    public static void begin() {
        if (phase != Phase.PRE_INIT) throw new IllegalStateException();
        phase = Phase.INIT;
    }

    public static void finish() {
        if (phase != Phase.INIT) throw new IllegalStateException();
        phase = Phase.POST_INIT;

        for (final DeferredRegister<?> register : ENTRIES) {
            register.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        ENTRIES.clear();
    }

    public static <T> String key(final DeviceType registryEntry) {
        return Objects.requireNonNull(registryEntry.getName()).toString();
    }

    public static <T> Optional<String> optionalKey(final T registryEntry) {
        if(registryEntry == null) {
            return Optional.empty();
        }
        String providerName = null;
        if (BlockDeviceProvider.class.isAssignableFrom(registryEntry.getClass())) {
            providerName = ProviderRegistry.BLOCK_DEVICE_PROVIDER_REGISTRY.get().getRegistryName().toString();
        } else if (ItemDeviceProvider.class.isAssignableFrom(registryEntry.getClass())) {
            providerName = ProviderRegistry.ITEM_DEVICE_PROVIDER_REGISTRY.get().getRegistryName().toString();
        }

        if(providerName == null) {
            return Optional.empty();
        }

        return Optional.of(providerName);
    }

    private RegistryUtils() {
    }
}
