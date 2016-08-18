package net.protyposis.android.spectaculum.effects;

/**
 * Created by Mario on 16.08.2016.
 */
public class EnumParameter<T extends Enum<T>> extends Parameter<T> {

    private T mDefault;
    private T mValue;
    private T[] mValues;

    public EnumParameter(String name, Class<T> enumClass, T init, Delegate<T> delegate, String description) {
        super(name, Type.ENUM, delegate, description);
        mDefault = init;
        mValue = init;

        mValues = enumClass.getEnumConstants();
    }

    public EnumParameter(String name, Class<T> enumClass, T init, Delegate<T> delegate) {
        this(name, enumClass, init, delegate, null);
    }

    public T getValue() {
        return mValue;
    }

    public void setValue(T value) {
        mValue = value;
        setDelegateValue(mValue);
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
        setDelegateValue(mValue);
    }
}
