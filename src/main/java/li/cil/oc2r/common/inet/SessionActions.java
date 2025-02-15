package li.cil.oc2r.common.inet;

public enum SessionActions {
    // Bad session. Drop the whole session
    DROP,

    // Transfer message to session layer on send and to network layer on receive
    FORWARD,

    // Do nothing upon return
    IGNORE,
}
