package org.houseflys.jdbc.settings;

public class Setting {
    private final int version;
    private final String key;
    private final Class type;
    private final Object value;
    private final String describe;

    public Setting(int version, String key, Class type, String describe) {
        this(version, key, type, null, describe);
    }

    public Setting(int version, String key, Object value, String describe) {
        this(version, key, value.getClass(), value, describe);
    }

    private Setting(int version, String key, Class type, Object value, String describe) {
        this.version = version;
        this.key = key;
        this.type = type;
        this.value = value;
        this.describe = describe;
    }

    public Setting changeValue(Object newValue) {
        if (newValue == null || newValue.getClass().getSimpleName().equals(type)) {
            return new Setting(version + 1, key, type, newValue, describe);
        }
        return this;
    }

    public Object value() {
        return value;
    }

    public String key() {
        return key;
    }
}
