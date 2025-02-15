package li.cil.oc2r.common.inet;

import li.cil.oc2r.api.inet.LayerParameters;
import li.cil.oc2r.api.inet.InternetManager;
import net.minecraft.nbt.Tag;

import java.util.Optional;

public record LayerParametersImpl(Optional<Tag> getSavedState, InternetManager getInternetManager) implements LayerParameters {
}
