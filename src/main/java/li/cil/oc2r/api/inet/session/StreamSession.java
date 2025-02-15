package li.cil.oc2r.api.inet.session;

import java.nio.ByteBuffer;

public interface StreamSession extends Session {
    ByteBuffer getSendBuffer();
    ByteBuffer getReceiveBuffer();

    void connect();
}
