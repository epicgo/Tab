package io.github.epicgo.reflect;

public final class Reflection {

    /**
     * Interfaz que proporciona métodos para acceder y manipular un campo de una clase.
     *
     * @param <T> el tipo de datos del campo.
     */
    public interface FieldAccessor<T> {

        /**
         * Obtiene el valor del campo en el objeto especificado.
         *
         * @param target el objeto del cual se obtendrá el valor del campo.
         * @return el valor del campo.
         */
        T get(Object target);

        /**
         * Establece el valor del campo en el objeto especificado.
         *
         * @param target el objeto en el cual se establecerá el valor del campo.
         * @param value  el nuevo valor que se asignará al campo.
         */
        void set(Object target, Object value);

        /**
         * Verifica si el objeto especificado tiene el campo.
         *
         * @param target el objeto sobre el cual se verificará la existencia del campo.
         * @return true si el objeto tiene el campo, false de lo contrario.
         */
        boolean hasField(Object target);

        /**
         * Obtiene el nombre del campo.
         *
         * @return el nombre del campo.
         */
        String getFieldName();

        /**
         * Obtiene el tipo del campo.
         *
         * @return el tipo del campo.
         */
        Class<?> getFieldType();

        /**
         * Verifica si el campo es estático.
         *
         * @return true si el campo es estático, false de lo contrario.
         */
        boolean isStatic();
    }
}
