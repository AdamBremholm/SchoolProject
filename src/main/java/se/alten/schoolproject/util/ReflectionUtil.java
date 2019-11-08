package se.alten.schoolproject.util;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {



    public static List<String> listNullOrEmptyFieldsExceptId(Object object, String excludedField){
        List<String> results = new ArrayList<>();
        for (Field f : object.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.get(object) == null || f.get(object) instanceof String && ((String) f.get(object)).isBlank()) {
                    if(!f.getName().equals(excludedField))
                        results.add(f.getName());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
