package com.borunovv.jetpreter.web.core.wsserver.protocol;

import com.borunovv.jetpreter.web.core.wsserver.nio.RWSession;

public abstract class AbstractMessage {
    private RWSession session;

    public AbstractMessage(RWSession session) {
        this.session = session;
    }

    public RWSession getSession() {
        return session;
    }
}