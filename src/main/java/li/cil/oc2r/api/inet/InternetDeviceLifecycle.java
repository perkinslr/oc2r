package li.cil.oc2r.api.inet;

import net.minecraft.nbt.Tag;

import java.util.Optional;

public interface InternetDeviceLifecycle {
    default Optional<Tag> onSave() {
        return Optional.empty();
    }
    default void onStop() {}
}
