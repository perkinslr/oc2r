/* SPDX-License-Identifier: MIT */

package li.cil.oc2.api.bus.device;

import li.cil.oc2.api.API;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;

/**
 * Lists built-in device types for convenience.
 */
public final class DeviceTypes {
    public static final DeviceType MEMORY = (DeviceType) RegistryObject.create(new ResourceLocation(API.MOD_ID, "memory"), new ResourceLocation(API.MOD_ID, "device_type"), API.MOD_ID).get();

    public static final DeviceType HARD_DRIVE = (DeviceType) RegistryObject.create(new ResourceLocation(API.MOD_ID, "hard_drive"), new ResourceLocation(API.MOD_ID, "device_type"), API.MOD_ID).get();

    public static final DeviceType FLASH_MEMORY = (DeviceType) RegistryObject.create(new ResourceLocation(API.MOD_ID, "flash_memory"), new ResourceLocation(API.MOD_ID, "device_type"), API.MOD_ID).get();
    public static final DeviceType CARD = (DeviceType) RegistryObject.create(new ResourceLocation(API.MOD_ID, "card"), new ResourceLocation(API.MOD_ID, "device_type"), API.MOD_ID).get();
    public static final DeviceType ROBOT_MODULE = (DeviceType) RegistryObject.create(new ResourceLocation(API.MOD_ID, "robot_module"), new ResourceLocation(API.MOD_ID, "device_type"), API.MOD_ID).get();

    public static final DeviceType FLOPPY = (DeviceType) RegistryObject.create(new ResourceLocation(API.MOD_ID, "floppy"), new ResourceLocation(API.MOD_ID, "device_type"), API.MOD_ID).get();
    public static final DeviceType NETWORK_TUNNEL = (DeviceType) RegistryObject.create(new ResourceLocation(API.MOD_ID, "network_tunnel"), new ResourceLocation(API.MOD_ID, "device_type"), API.MOD_ID).get();
    public static final DeviceType CPU = (DeviceType) RegistryObject.create(new ResourceLocation(API.MOD_ID, "cpu"), new ResourceLocation(API.MOD_ID, "device_type"), API.MOD_ID).get();
}
