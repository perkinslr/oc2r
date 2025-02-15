/* SPDX-License-Identifier: MIT */

package li.cil.oc2r.common.item;

public final class BlockOperationsModule extends ModItem {
    public static final int DURABILITY = 2500;

    ///////////////////////////////////////////////////////////////////

    public BlockOperationsModule() {
        super(createProperties().durability(DURABILITY));
    }
}
