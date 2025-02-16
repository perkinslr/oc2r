/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.api.bus.device.vm;

/**
 * This interface serves as a marker for devices that load firmware.
 * <p>
 * It is used exclusively to check if some firmware will be loaded early in the
 * startup process, to provide a useful error to the user if none is present.
 */
public interface FirmwareLoader extends VMDevice { }
