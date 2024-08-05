package org.lazy.common;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConstructorArguments {

    private final Map<Integer, Object> indexedArgumentValues = new LinkedHashMap<>();


    public void addIndexedArgumentValue(int index, Object definition) {
        this.indexedArgumentValues.put(index, definition);
    }


    public Object getArgumentValue(int index) {
        return this.indexedArgumentValues.get(index);
    }


}




