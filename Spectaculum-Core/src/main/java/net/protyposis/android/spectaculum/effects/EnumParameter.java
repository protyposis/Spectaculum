package net.protyposis.android.spectaculum.effects;

/**
 * Created by Mario on 16.08.2016.
 */
public class EnumParameter<T extends Enum<T>> extends Parameter {

    public interface Delegate<T> {
        void setValue(T value);
    }

    private T mDefault;
    private T mValue;
    private Delegate mDelegate;
    private T[] mValues;

    public EnumParameter(String name, Class<T> enumClass, T init, Delegate delegate, String description) {
        super(name, Type.ENUM, description);
        mDefault = init;
        mValue = init;
        mDelegate = delegate;

        mValues = enumClass.getEnumConstants();
    }

    public EnumParameter(String name, Class<T> enumClass, T init, Delegate delegate) {
        this(name, enumClass, init, delegate, null);
    }

    public T getValue() {
        return mValue;
    }

    public void setValue(T value) {
        mValue = value;
        setDelegateValue();
    }


    public T getDefault() {
        return mDefault;
    }

    public T[] getEnumValues() {
        return mValues;
    }

    @Override
    public void reset() {
        mValue = mDefault;
        setDelegateValue();
    }

    private void setDelegateValue() {
        mDelegate.setValue(mValue);
        fireParameterChanged();
    }
}
