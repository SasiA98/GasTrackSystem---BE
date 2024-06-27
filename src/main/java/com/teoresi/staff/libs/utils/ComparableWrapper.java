package com.teoresi.staff.libs.utils;


import com.teoresi.staff.entities.Resource;
import com.teoresi.staff.shared.models.Role;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ComparableWrapper implements Comparable<ComparableWrapper> {
    private final Object value;

    private final Comparator<Role> rolesComparator = new Comparator<Role>() {
        @Override
        public int compare(Role o1, Role o2) {
            return Integer.compare(o1.ordinal(), o2.ordinal());
        }
    };


    public ComparableWrapper(Float value) {
        this.value = value;
    }

    public ComparableWrapper(Set<Role> role) {
        this.value = role;
    }

    public ComparableWrapper(String value) {
        this.value = value;
    }

    public boolean isFloat() {
        return value instanceof Float;
    }

    public boolean isResource() {
        return value instanceof Resource;
    }

    public boolean isString() {
        return value instanceof String;
    }

    public boolean isSetOfRoles() {
        if (value instanceof Set) {
            Set<?> set = (Set<?>) value;
            for (Object element : set) {
                if (!(element instanceof Role)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    public Float getFloatValue() {
        return isFloat() ? (Float) value : null;
    }

    public String getStringValue() {
        return isString() ? (String) value : null;
    }

    public Set<Role> getSetOfRoles() {
        return isSetOfRoles() ? (Set<Role>) value : null;
    }

    @Override
    public int compareTo(@NotNull ComparableWrapper other) {
        if (this.isFloat() && other.isFloat()) {
            return this.getFloatValue().compareTo(other.getFloatValue());
        } else if (this.isString() && other.isString()) {
            return this.getStringValue().compareTo(other.getStringValue());
        } else if (this.isSetOfRoles() && other.isSetOfRoles()) {
            return compareSetOfRoles(other);
        }else {
            throw new IllegalArgumentException("Cannot compare different types");
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }

    // hashCode and equals implementations to ensure proper behavior in collections
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ComparableWrapper that = (ComparableWrapper) obj;
        return value.equals(that.value);
    }


    private int compareSetOfRoles(ComparableWrapper obj){

        if(value == null && obj == null)
            return 0;
        else if(value == null)
            return 1;
        else if(obj == null)
            return -1;

        List<Role> roles1 = new ArrayList<>(getSetOfRoles());
        List<Role> roles2 = new ArrayList<>(obj.getSetOfRoles());

        roles1.sort(rolesComparator);
        roles2.sort(rolesComparator);


        int size = Math.max(roles1.size(), roles2.size());

        for (int i = 0; i < size; i++) {
            Role elem1 = roles1.size() > i ? roles1.get(i) : null;
            Role elem2 = roles2.size() > i ? roles2.get(i) : null;

            if(elem1 == null)
                return 1;
            if(elem2 == null)
                return -1;

            int elementComparison = Integer.compare(elem1.ordinal(), elem2.ordinal());
            if (elementComparison != 0)
                return elementComparison;
        }

        return 0;
    }

}

