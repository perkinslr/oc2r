package li.cil.oc2r.common.inet;

import net.minecraft.nbt.Tag;

import java.util.Optional;

public interface InternetConnection {
    Optional<Tag> saveAdapterState();
    void stop();
}
