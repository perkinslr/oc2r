/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.api.bus.device;

import li.cil.oc2r.api.API;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;

/**
 * Lists built-in device types for convenience.
 */
public final class DeviceTypes {
    public static DeviceType MEMORY = (DeviceType) RegistryObject.create(new ResourceLocation(API.MOD_ID, "memory"), new ResourceLocation("oc2", "device_type"), API.MOD_ID).get();

    public static DeviceType HARD_DRIVE = (DeviceType) RegistryObject.create(new ResourceLocation(API.MOD_ID, "hard_drive"), new ResourceLocation("oc2", "device_type"), API.MOD_ID).get();

    public static DeviceType FLASH_MEMORY = (DeviceType) RegistryObject.create(new ResourceLocation(API.MOD_ID, "flash_memory"), new ResourceLocation("oc2", "device_type"), API.MOD_ID).get();
    public static DeviceType CARD = (DeviceType) RegistryObject.create(new ResourceLocation(API.MOD_ID, "card"), new ResourceLocation("oc2", "device_type"), API.MOD_ID).get();
    public static DeviceType ROBOT_MODULE = (DeviceType) RegistryObject.create(new ResourceLocation(API.MOD_ID, "robot_module"), new ResourceLocation("oc2", "device_type"), API.MOD_ID).get();

    public static DeviceType FLOPPY = (DeviceType) RegistryObject.create(new ResourceLocation(API.MOD_ID, "floppy"), new ResourceLocation("oc2", "device_type"), API.MOD_ID).get();
    public static DeviceType NETWORK_TUNNEL = (DeviceType) RegistryObject.create(new ResourceLocation(API.MOD_ID, "network_tunnel"), new ResourceLocation("oc2", "device_type"), API.MOD_ID).get();
}
