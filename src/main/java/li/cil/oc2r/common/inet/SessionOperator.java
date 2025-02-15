package li.cil.oc2r.common.inet;

import li.cil.oc2r.api.inet.session.Session;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public interface SessionOperator extends Session {
    @Nullable
    byte[] nextReceive();

    void nextSent(final byte[] data);

    default void nextSent(final ByteBuffer data) {
        final byte[] bytes = new byte[data.remaining()];
        data.get(bytes);
        nextSent(bytes);
    }
}
