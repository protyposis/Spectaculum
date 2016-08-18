package net.protyposis.android.spectaculum.effects;

/**
 * Created by Mario on 18.08.2016.
 */
public class EffectException extends Exception {
    public EffectException() {
    }

    public EffectException(String detailMessage) {
        super(detailMessage);
    }

    public EffectException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public EffectException(Throwable throwable) {
        super(throwable);
    }
}
