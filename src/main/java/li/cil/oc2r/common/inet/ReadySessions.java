package li.cil.oc2r.common.inet;

import li.cil.oc2r.api.inet.session.Session;

import java.util.ArrayDeque;
import java.util.Queue;

public final class ReadySessions {
    private final Queue<Session> toRead = new ArrayDeque<>();
    private final Queue<Session> toWrite = new ArrayDeque<>();
    private final Queue<Session> toConnect = new ArrayDeque<>();

    public Queue<Session> getToRead() {
        return toRead;
    }

    public Queue<Session> getToWrite() {
        return toWrite;
    }

    public Queue<Session> getToConnect() {
        return toConnect;
    }
}
