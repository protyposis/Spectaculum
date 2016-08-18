package net.protyposis.android.spectaculum.effects;

import net.protyposis.android.spectaculum.SpectaculumView;

/**
 * A simple parameter handler that executes on the rendering thread of the Spectaculum view.
 * Created by Mario on 18.08.2016.
 */
public class ParameterHandler {

    private SpectaculumView mHost;

    public ParameterHandler(SpectaculumView host) {
        mHost = host;
    }

    public void post(Runnable r) {
        mHost.queueEvent(r);
    }
}
