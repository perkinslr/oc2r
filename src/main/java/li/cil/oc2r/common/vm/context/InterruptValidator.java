/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.vm.context;

public interface InterruptValidator {
    boolean isMaskValid(int mask);

    int getMaskedInterrupts(int interrupts);
}
