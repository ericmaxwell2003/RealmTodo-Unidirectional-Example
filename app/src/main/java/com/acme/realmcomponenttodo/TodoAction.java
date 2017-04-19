package com.acme.realmcomponenttodo;

import java.util.HashMap;
import java.util.Map;

public class TodoAction implements Action {

    // Each supported action type
    public enum Type { CHECK_ALL, UNCHECK_ALL, DELETE_CHECKED,
                       ADD_ITEM, CHECK_ITEM, UNCHECK_ITEM }

    private Type type;

    // Additional Parameters necessary for a specific action.
    private Map<String, Object> input = new HashMap<>();

    public TodoAction(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    // Add an input parameter to the action.
    public TodoAction addInput(String key, Object val) {
        input.put(key, val);
        return this;
    }

    private Object getInput(String key) {
        return input.get(key);
    }

}
