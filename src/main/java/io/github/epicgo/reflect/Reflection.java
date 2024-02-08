package io.github.epicgo.reflect;

public final class Reflection {

    /**
     * Interfaz que proporciona un método para invocar un constructor específico de una clase.
     */
    public interface ConstructorInvoker {

        /**
         * Invoca el constructor con los argumentos dados y devuelve una nueva instancia de la clase.
         *
         * @param arguments los argumentos que se pasarán al constructor.
         * @return una nueva instancia de la clase creada por el constructor.
         */
        Object invoke(Object... arguments);

        /**
         * Obtiene los tipos de parámetros del constructor.
         *
         * @return un array de clases que representan los tipos de parámetros del constructor.
         */
        Class<?>[] getParameterTypes();
    }

    /**
     * Interfaz que proporciona un método para invocar un método específico en un objeto.
     */
    public interface MethodInvoker {

        /**
         * Invoca el método especificado en el objeto objetivo con los argumentos dados.
         *
         * @param target    el objeto sobre el cual se invocará el método.
         * @param arguments los argumentos que se pasarán al método.
         * @return el valor devuelto por el método, o null si es void.
         */
        Object invoke(Object target, Object... arguments);

        /**
         * Obtiene el nombre del método.
         *
         * @return el nombre del método.
         */
        String getMethodName();

        /**
         * Obtiene el tipo de retorno del método.
         *
         * @return el tipo de retorno del método.
         */
        Class<?> getReturnType();

        /**
         * Obtiene los tipos de parámetros del método.
         *
         * @return un array de clases que representan los tipos de parámetros del método.
         */
        Class<?>[] getParameterTypes();

        /**
         * Verifica si el método es estático.
         *
         * @return true si el método es estático, false de lo contrario.
         */
        boolean isStatic();
    }

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
