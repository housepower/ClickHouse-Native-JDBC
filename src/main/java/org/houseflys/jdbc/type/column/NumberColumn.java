package org.houseflys.jdbc.type.column;

import org.houseflys.jdbc.type.Column;

public abstract class NumberColumn extends Column {

    public NumberColumn(String name, String type) {
        super(name, type);
    }

    public abstract boolean isSigned();
}
