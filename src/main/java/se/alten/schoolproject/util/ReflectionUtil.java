package se.alten.schoolproject.util;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReflectionUtil {



    public static List<String> listNullOrEmptyFields(Object object, List<String> excludedFields){


        List<String> results = new ArrayList<>();
        for (Field f : object.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.get(object) == null || f.get(object) instanceof String && ((String) f.get(object)).isBlank()) {
                    AtomicBoolean shouldAdd = new AtomicBoolean(true);
                   excludedFields.forEach(excludedField -> {
                      if(excludedField.equals(f.getName())){
                         shouldAdd.set(false);
                      }
                   });
                   if(shouldAdd.get()){
                       results.add(f.getName());
                   }

                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
