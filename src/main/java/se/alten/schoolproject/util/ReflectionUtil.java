package se.alten.schoolproject.util;


import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;


public class ReflectionUtil {



    public static Set<String> listNullOrEmptyFields(Object object){
        if(object==null)
            throw new IllegalArgumentException("null value in listNullOrEmptyFields");

        Set<String> results = new HashSet<>();
        for (Field f : object.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.get(object) == null || f.get(object) instanceof String && ((String) f.get(object)).isBlank()) {
                    results.add(f.getName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public static Set<String> removeExceptionsFromSet(Set<String> emptyOrNullSet, Set<String> excludedFields){
        if(emptyOrNullSet==null || excludedFields==null)
            throw new IllegalArgumentException("null value in removeExceptionsFromSet()");
         else {
            emptyOrNullSet.removeAll(excludedFields);
            return emptyOrNullSet;
        }

    }
}
