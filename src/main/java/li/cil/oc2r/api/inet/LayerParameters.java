package li.cil.oc2r.api.inet;

import net.minecraft.nbt.Tag;

import java.util.Optional;

public interface LayerParameters {
    Optional<Tag> getSavedState();
    InternetManager getInternetManager();
}
