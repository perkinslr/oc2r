package li.cil.oc2r.common.inet;

import li.cil.oc2r.api.inet.LayerParameters;
import li.cil.oc2r.api.inet.layer.SessionLayer;
import li.cil.oc2r.api.inet.provider.SessionLayerInternetProvider;

public final class DefaultInternetProvider extends SessionLayerInternetProvider {
    public static final DefaultInternetProvider INSTANCE = new DefaultInternetProvider();

    private DefaultInternetProvider() {
    }

    @Override
    protected SessionLayer provideSessionLayer(final LayerParameters layerParameters) {
        return new DefaultSessionLayer(layerParameters);
    }
}
