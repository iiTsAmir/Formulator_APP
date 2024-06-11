package com.example.formulator.myClass;

// The Comp class represents a component with a name, unit, and unit name
public class Comp {
    // Private member variables to store the properties of the component
    private String name;
    private Float unit;
    private String unitName;

    // Constructor to initialize the component with the specified name, unit, and unit name
    public Comp(String name, Float unit, String unitName) {
        this.name = name;
        this.unit = unit;
        this.unitName = unitName;
    }

    // Getter method to get the name of the component
    public String getName() {
        return name;
    }

    // Setter method to set the name of the component
    public void setName(String name) {
        this.name = name;
    }

    // Getter method to get the unit of the component
    public Float getUnit() {
        return unit;
    }

    // Setter method to set the unit of the component
    public void setUnit(Float unit) {
        this.unit = unit;
    }

    // Getter method to get the unit name of the component
    public String getUnitName() {
        return unitName;
    }

    // Setter method to set the unit name of the component
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
