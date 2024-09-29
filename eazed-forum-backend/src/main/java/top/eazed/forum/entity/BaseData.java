package top.eazed.forum.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public interface BaseData {
    default <V> V asViewObject(Class<V> clazz) {
        try {
            Field[] declaredFields = clazz.getDeclaredFields();
            Constructor<V> constructor = clazz.getConstructor();
            V v = constructor.newInstance();
            for (Field declaredField : declaredFields) {
                convert(declaredField, v);
            }
            return v;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }
    
    private void convert(Field field, Object vo) {
        try {
            Field source = this.getClass().getDeclaredField(field.getName());
            field.setAccessible(true);
            source.setAccessible(true);
            if (field.getType().equals(source.getType())) {
                field.set(vo, source.get(this));
            }
        } catch (IllegalAccessException | NoSuchFieldException exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }
}