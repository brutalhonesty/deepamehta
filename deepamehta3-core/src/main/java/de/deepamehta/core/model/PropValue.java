package de.deepamehta.core.model;

import java.util.HashMap;
import java.util.Map;



public class PropValue {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private Object value;

    // ---------------------------------------------------------------------------------------------------- Constructors

    /**
      * Called by JAX-RS container to create a PropValue from a @PathParam or @QueryParam
      */
    public PropValue(String value) {
        this.value = value;
    }

    public PropValue(int value) {
        this.value = value;
    }

    public PropValue(long value) {
        this.value = value;
    }

    public PropValue(boolean value) {
        this.value = value;
    }

    public PropValue(Object value) {
        this.value = value;
    }

    // -------------------------------------------------------------------------------------------------- Public Methods

    @Override
    public String toString() {
        return value.toString();
    }

    public int intValue() {
        return (Integer) value;
    }

    public long longValue() {
        return (Long) value;
    }

    public boolean booleanValue() {
        return (Boolean) value;
    }

    public Object value() {
        return value;
    }
}