/* SPDX-License-Identifier: MIT */

package li.cil.oc2.common.vm;
import li.cil.oc2.api.bus.device.data.BlockDeviceData;
import java.io.IOException;

import net.minecraft.resources.ResourceLocation;

import li.cil.ceres.api.Serialized;
import li.cil.oc2.common.bus.device.data.FileSystems;
import li.cil.oc2.common.vm.context.global.GlobalVMContext;
import li.cil.sedna.api.Interrupt;
import li.cil.sedna.api.device.MemoryMappedDevice;
import li.cil.sedna.device.rtc.GoldfishRTC;
import li.cil.sedna.device.rtc.SystemTimeRealTimeCounter;
import li.cil.sedna.device.serial.UART16550A;
import li.cil.sedna.device.virtio.VirtIOConsoleDevice;
import li.cil.sedna.device.virtio.VirtIOFileSystemDevice;
import li.cil.sedna.device.virtio.VirtIOBlockDevice;
import li.cil.oc2.common.bus.device.data.ResourceBlockDeviceData;
import li.cil.sedna.api.device.BlockDevice;
import li.cil.sedna.buildroot.Buildroot;
import java.io.InputStream;
import li.cil.sedna.device.block.ByteBufferBlockDevice;


import java.util.OptionalLong;
import java.util.function.Function;

public final class BuiltinDevices {
    public static final int RTC_HOST_INTERRUPT = 0x1;
    public static final int RTC_MINECRAFT_INTERRUPT = 0x2;
    public static final int RPC_INTERRUPT = 0x3;
    private static final int UART_INTERRUPT = 0x4;
    private static final int VFS_INTERRUPT = 0x5;
    private static final int BFS_INTERRUPT = 0x6;
    private static final int RFS_INTERRUPT = 0x7;

    ///////////////////////////////////////////////////////////////////

    public final MinecraftRealTimeCounter rtcMinecraft = new MinecraftRealTimeCounter();

    ///////////////////////////////////////////////////////////////////

    @Serialized public VirtIOConsoleDevice rpcSerialDevice;
    @Serialized public UART16550A uart;
    @Serialized public VirtIOFileSystemDevice vfs;
    @Serialized public VirtIOBlockDevice bfs;
    @Serialized public VirtIOBlockDevice rfs;

    ///////////////////////////////////////////////////////////////////

    public BuiltinDevices(final GlobalVMContext context) {
        initialize(context, new GoldfishRTC(SystemTimeRealTimeCounter.get()), RTC_HOST_INTERRUPT, GoldfishRTC::getInterrupt);
        initialize(context, new GoldfishRTC(this.rtcMinecraft), RTC_MINECRAFT_INTERRUPT, GoldfishRTC::getInterrupt);
        rpcSerialDevice = initialize(context, new VirtIOConsoleDevice(context.getMemoryMap()), RPC_INTERRUPT, VirtIOConsoleDevice::getInterrupt);
        uart = initialize(context, new UART16550A(), UART_INTERRUPT, UART16550A::getInterrupt);
        vfs = initialize(context, new VirtIOFileSystemDevice(context.getMemoryMap(), "builtin", FileSystems.getLayeredFileSystem()), VFS_INTERRUPT, VirtIOFileSystemDevice::getInterrupt);
        InputStream ris = Buildroot.getRootFilesystem();
        try {
            var bfsd = FileSystems.getBlockByName("bootfs");
            if (bfsd != null) {
                bfs = initialize(context, new VirtIOBlockDevice(context.getMemoryMap(), bfsd.getBlockDevice()), BFS_INTERRUPT, VirtIOBlockDevice::getInterrupt);
            }
            rfs = initialize(context, new VirtIOBlockDevice(context.getMemoryMap(), ByteBufferBlockDevice.createFromStream(ris, true)), RFS_INTERRUPT, VirtIOBlockDevice::getInterrupt);
        }
        catch(final IOException e) {
            System.out.println("Failed to load lower block device");
        }
    }

    ///////////////////////////////////////////////////////////////////

    private static <T extends MemoryMappedDevice> T initialize(final GlobalVMContext context, final T device, final int interrupt, final Function<T, Interrupt> interruptSupplier) {
        if (!context.getInterruptAllocator().claimInterrupt(interrupt)) throw new IllegalStateException();
        interruptSupplier.apply(device).set(interrupt, context.getInterruptController());
        context.getMemoryRangeAllocator().claimMemoryRange(device);
        return device;
    }
}
