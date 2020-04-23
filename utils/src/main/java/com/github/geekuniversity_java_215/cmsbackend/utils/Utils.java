package com.github.geekuniversity_java_215.cmsbackend.utils;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pivovarit.function.ThrowingRunnable.unchecked;

@UtilityClass
public class Utils {

    public static boolean isNullOrEmpty(Object object) {

        return object == null || object.getClass() == String.class && ((String)object).trim().isEmpty();
    }


    public static String getNullIfEmpty(String s) {

        if (s != null && s.trim().isEmpty())
            s = null;

        return s;
    }

    public static String getEmptyIfNull(String s) {

        if (s == null)
            s = "";

        return s;
    }

    public static int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    public static String boolToStr(boolean b) {
        return b ? "1" : "0";
    }




    public static Set<String> rolesToSet(Object authorities) {
        //noinspection unchecked
        return new HashSet<>(((List<String>) authorities));
    }

    public static Set<String> grantedAuthorityToSet(Collection<? extends GrantedAuthority> authorities) {

        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }


    public static Set<GrantedAuthority> rolesToGrantedAuthority(Set<String> authorities) {
        //noinspection unchecked
        return authorities
            .stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    public static Set<GrantedAuthority> rolesToGrantedAuthority(Object authorities) {
        //noinspection unchecked
        return ((List<String>)authorities)
               .stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }



    public static long toLong(String s) {

        return Long.parseLong(s);
    }

    public static int toInt(String s) {

        return Integer.parseInt(s);
    }



    /**
     * Get first field of specified class
     * @param source searching in this class
     * @param pattern which class to find
     * @return field name
     */
    public static String getPatternClassFieldName(Class source, Class pattern) {

        String result = null;
        
        outerLoop:
        do {
            for (Field f : source.getDeclaredFields()) {
                if (f.getType() == pattern) {
                    result = f.getName();
                    break outerLoop;
                }
            }
        }
        while((source = source.getSuperclass()) != null);

        return result;
    }









    /**
     * Set object field value
     * @param fieldName
     * @param o object
     * @param value new field value
     */
    public static void fieldSetter(String fieldName, final Object o, final Object value) {

        unchecked(() -> {
            Class<?> clazz = o.getClass();
            Field field = null;
            do {
                try {
                    field = clazz.getDeclaredField(fieldName);
                } catch(Exception ignore) {}
            }
            while((clazz = clazz.getSuperclass()) != null);  // Пролезет через CGLIB proxy

            Assert.notNull(field, "field == null");
            field.setAccessible(true);
            field.set(o, value);
        }).run(); // замена try ... catch(Exception e) {throw new RuntimeException(e)}
    }


    /**
     * Invoke object setter
     * @param setterName setter name
     * @param o object
     * @param paramType setter param type
     * @param value new value
     */
    public static void propertySetter(String setterName, final Object o, Class paramType, final Object value) {

        unchecked(() -> {
            Class<?> clazz = o.getClass();
            Method method = null;
            do {
                try {
                    method = clazz.getDeclaredMethod(setterName, paramType);
                } catch(Exception ignore) {}
            }
            while((clazz = clazz.getSuperclass()) != null);

            Assert.notNull(method, "method == null");
            method.setAccessible(true);
            method.invoke(o, value);
        }).run();

    }


    /**
     * Read object field value
     * @param fieldName field name
     * @param o object
     * @return
     */
    public static Object fieldGetter(String fieldName, final Object o) {

        return com.pivovarit.function.ThrowingSupplier.unchecked(() -> {
            Class<?> clazz = o.getClass();
            Field field = null;
            do {
                try {
                    field = clazz.getDeclaredField(fieldName);
                } catch(Exception ignore) {}
            }
            while((clazz = clazz.getSuperclass()) != null);

            Assert.notNull(field, "field == null");
            field.setAccessible(true);
            return field.get(o);
        }).get();
    }


}

