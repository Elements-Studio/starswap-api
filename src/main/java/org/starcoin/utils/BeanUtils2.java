package org.starcoin.utils;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Set;

public class BeanUtils2 {

    public static void copySpecificProperties(Object src, Object trg, Set<String> props) {
        String[] excludedProperties =
                Arrays.stream(org.springframework.beans.BeanUtils.getPropertyDescriptors(src.getClass()))
                        .map(PropertyDescriptor::getName)
                        .filter(name -> !props.contains(name))
                        .toArray(String[]::new);

        org.springframework.beans.BeanUtils.copyProperties(src, trg, excludedProperties);
    }

}
