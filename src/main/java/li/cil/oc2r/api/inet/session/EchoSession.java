package li.cil.oc2r.api.inet.session;

public interface EchoSession extends Session {
    int getSequenceNumber();

    int getTtl();
}
