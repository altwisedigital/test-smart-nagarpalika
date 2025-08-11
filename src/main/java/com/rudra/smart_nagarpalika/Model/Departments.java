package com.rudra.smart_nagarpalika.Model;

import lombok.Getter;

@Getter
public enum Departments {
    ALL_DEPARTMENTS("All Departments"),
    DRAINAGE_MAINTENANCE("Drainage Maintenance"),
    ROAD_MAINTENANCE("Road Maintenance"),
    WATER_MAINTENANCE("Water Maintenance"),
    LIGHT_MAINTENANCE("Light Maintenance"),
    OTHER("Other");

    private final String displayName;

    Departments(String displayName) {
        this.displayName = displayName;
    }

}