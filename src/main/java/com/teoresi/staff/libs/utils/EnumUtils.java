package com.teoresi.staff.libs.utils;

import java.util.HashSet;
import java.util.Set;

public class EnumUtils {
    public static <EnumType extends Enum<EnumType>> Set<EnumType> getSimilarEnumValuesIgnoringCase(Class<EnumType> enumClass, String value) {
        Set<EnumType> similarEnumConstants = new HashSet<>();
        for (Enum enum_ : (Enum[])enumClass.getEnumConstants()) {
            if (enum_.name().toUpperCase().contains(value.toUpperCase()))
                similarEnumConstants.add((EnumType)enum_);
        }
        return similarEnumConstants;
    }
}
