/* SPDX-License-Identifier: MIT */

package li.cil.oc2.api.util;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

/**
 * This enum indicates a side of a block device.
 * <p>
 * It is intended to be used by {@link li.cil.oc2.api.bus.device.rpc.RPCDevice} APIs,
 * providing both convenience for the caller by providing a range of aliases, and also
 * stability, in case Mojang decide to rename the enum fields of the {@link Direction}
 * enum at some time in the future.
 */
public enum Side {
    DOWN(Direction.DOWN),
    down(DOWN),
    d(DOWN),

    UP(Direction.UP),
    up(UP),
    u(UP),

    NORTH(Direction.NORTH),
    north(NORTH),
    n(NORTH),
    BACK(NORTH),
    back(NORTH),
    b(NORTH),

    SOUTH(Direction.SOUTH),
    south(SOUTH),
    s(SOUTH),
    FRONT(SOUTH),
    front(SOUTH),
    f(SOUTH),

    WEST(Direction.WEST),
    west(WEST),
    w(WEST),
    LEFT(WEST),
    left(WEST),
    l(WEST),

    EAST(Direction.EAST),
    east(EAST),
    e(EAST),
    RIGHT(EAST),
    right(EAST),
    r(EAST),
    ;

    @Nullable private final Side base;
    private final Direction direction;

    Side(final Direction direction) {
        this.base = null;
        this.direction = direction;
    }

    Side(final Side side) {
        this.base = side;
        this.direction = side.direction;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return base != null ? base.toString() : super.toString();
    }

    public static Direction relativeDirection(BlockPos from, BlockPos to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        int dz = to.getZ() - from.getZ();
        if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > Math.abs(dz)) {
            if (dx > 0) {
                return Direction.EAST;
            }
            else {
                return Direction.WEST;
            }
        }
        else if (Math.abs(dy) > Math.abs(dx) && Math.abs(dy) > Math.abs(dz)) {
            if (dy > 0) {
                return Direction.UP;
            }
            else {
                return Direction.DOWN;
            }
        }
        else {
            if (dz > 0) {
                return Direction.SOUTH;
            }
            else {
                return Direction.NORTH;
            }
        }
    }
}
