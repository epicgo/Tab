package io.github.epicgo.reflect;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Reflection {

    // Prefijo de las clases de CraftBukkit y Minecraft Server
    private static final String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
    private static final String NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");
    // Versión del servidor de Minecraft
    private static final String VERSION = OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".", "");

    // Patrón para buscar variables en una cadena
    private static final Pattern PATTERN_MATCH_VARIABLE = Pattern.compile("\\{([^\\}]+)\\}");

    /**
     * Obtiene la clase correspondiente al nombre proporcionado, sin conocer el tipo en tiempo de compilación.
     *
     * @param lookupName el nombre de la clase.
     * @return la clase correspondiente.
     */
    public static Class<?> getUntypedClass(String lookupName) {
        return getClass(lookupName);
    }

    /**
     * Obtiene la clase correspondiente al nombre de búsqueda proporcionado, expandiendo las variables si es necesario.
     *
     * @param lookupName el nombre de búsqueda de la clase.
     * @return la clase correspondiente.
     * @throws IllegalArgumentException si no se puede encontrar la clase.
     */
    public static Class<?> getClass(String lookupName) {
        // Expande las variables en el nombre de búsqueda y obtiene la clase correspondiente
        return getCanonicalClass(expandVariables(lookupName));
    }

    /**
     * Obtiene la clase correspondiente a cada uno de los nombres proporcionados, sin conocer el tipo en tiempo de compilación.
     * Si se produce un error al intentar obtener una clase, se registra la excepción y se continúa con el siguiente nombre.
     *
     * @param lookupNames los nombres de las clases.
     * @return la primera clase encontrada, o null si no se encontró ninguna.
     */
    public static Class<?> getUntypedClasses(String... lookupNames) {
        for (String lookupName : lookupNames) {
            try {
                return getUntypedClass(lookupName);
            } catch (IllegalArgumentException e) {
                // Manejar la excepción adecuadamente
                // e.printStackTrace(); // o registrar la excepción en algún lugar
                continue;
            }
        }
        return null;
    }

    /**
     * Obtiene la clase correspondiente al nombre canónico proporcionado.
     *
     * @param canonicalName el nombre canónico de la clase.
     * @return la clase correspondiente.
     * @throws IllegalArgumentException si no se puede encontrar la clase.
     */
    private static Class<?> getCanonicalClass(String canonicalName) {
        try {
            // Intenta cargar la clase utilizando su nombre canónico
            return Class.forName(canonicalName);
        } catch (ClassNotFoundException e) {
            // Si la clase no se encuentra, lanza una excepción con un mensaje descriptivo
            throw new IllegalArgumentException("Cannot find " + canonicalName, e);
        }
    }

    /**
     * Expande las variables en el nombre proporcionado reemplazando las coincidencias con los valores correspondientes.
     *
     * @param name el nombre que puede contener variables a expandir.
     * @return el nombre con las variables expandidas.
     * @throws IllegalArgumentException si se encuentra una variable desconocida.
     */
    private static String expandVariables(String name) {
        // StringBuffer para construir la cadena resultante
        StringBuffer output = new StringBuffer();

        // Matcher para buscar las variables en el nombre
        Matcher matcher = PATTERN_MATCH_VARIABLE.matcher(name);

        // Iterar sobre todas las coincidencias encontradas
        while (matcher.find()) {
            // Obtener el nombre de la variable
            String variable = matcher.group(1);
            // Inicializar el valor de reemplazo
            String replacement = "";

            // Expandir todas las variables detectadas
            if ("nms".equalsIgnoreCase(variable))
                replacement = NMS_PREFIX;
            else if ("obc".equalsIgnoreCase(variable))
                replacement = OBC_PREFIX;
            else if ("version".equalsIgnoreCase(variable))
                replacement = VERSION;
            else
                // Lanzar una excepción si se encuentra una variable desconocida
                throw new IllegalArgumentException("Unknown variable: " + variable);

            // Suponer que las variables expandidas son todas paquetes y agregar un punto
            if (replacement.length() > 0 && matcher.end() < name.length() && name.charAt(matcher.end()) != '.')
                replacement += ".";

            // Reemplazar la variable encontrada con su valor correspondiente
            matcher.appendReplacement(output, Matcher.quoteReplacement(replacement));
        }

        // Agregar el resto del nombre al resultado
        matcher.appendTail(output);
        // Convertir el StringBuffer a String y devolverlo
        return output.toString();
    }

    /**
     * Obtiene la clase de Minecraft correspondiente al nombre proporcionado.
     *
     * @param name el nombre de la clase de Minecraft.
     * @return la clase correspondiente.
     * @throws IllegalArgumentException si no se puede encontrar la clase.
     */
    public static Class<?> getMinecraftClass(String name) {
        // Combina el prefijo de Minecraft con el nombre de la clase y obtiene la clase correspondiente
        return getCanonicalClass(NMS_PREFIX + "." + name);
    }

    /**
     * Obtiene la clase de CraftBukkit correspondiente al nombre proporcionado.
     *
     * @param name el nombre de la clase de CraftBukkit.
     * @return la clase correspondiente.
     * @throws IllegalArgumentException si no se puede encontrar la clase.
     */
    public static Class<?> getCraftBukkitClass(String name) {
        // Combina el prefijo de CraftBukkit con el nombre de la clase y obtiene la clase correspondiente
        return getCanonicalClass(OBC_PREFIX + "." + name);
    }

    /**
     * Obtiene un acceso al campo para la clase, nombre de campo y tipo de campo proporcionados.
     *
     * @param target    la clase de destino.
     * @param name      el nombre del campo.
     * @param fieldType el tipo de campo.
     * @param <T>       el tipo de dato del campo.
     * @return un acceso al campo para la clase, nombre de campo y tipo de campo proporcionados.
     */
    public static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType) {
        return getField(target, name, fieldType, 0);
    }

    /**
     * Obtiene un acceso al campo para el nombre de clase, nombre de campo y tipo de campo proporcionados.
     *
     * @param className el nombre de la clase.
     * @param name      el nombre del campo.
     * @param fieldType el tipo de campo.
     * @param <T>       el tipo de dato del campo.
     * @return un acceso al campo para el nombre de clase, nombre de campo y tipo de campo proporcionados.
     */
    public static <T> FieldAccessor<T> getField(String className, String name, Class<T> fieldType) {
        return getField(getClass(className), name, fieldType, 0);
    }

    /**
     * Obtiene un acceso al campo para la clase, tipo de campo y posición de índice proporcionados.
     *
     * @param target    la clase de destino.
     * @param fieldType el tipo de campo.
     * @param index     el índice del campo.
     * @param <T>       el tipo de dato del campo.
     * @return un acceso al campo para la clase, tipo de campo y posición de índice proporcionados.
     */
    public static <T> FieldAccessor<T> getField(Class<?> target, Class<T> fieldType, int index) {
        return getField(target, null, fieldType, index);
    }

    /**
     * Obtiene un acceso al campo para el nombre de clase, tipo de campo y posición de índice proporcionados.
     *
     * @param className el nombre de la clase.
     * @param fieldType el tipo de campo.
     * @param index     el índice del campo.
     * @param <T>       el tipo de dato del campo.
     * @return un acceso al campo para el nombre de clase, tipo de campo y posición de índice proporcionados.
     */
    public static <T> FieldAccessor<T> getField(String className, Class<T> fieldType, int index) {
        return getField(getClass(className), fieldType, index);
    }

    /**
     * Obtiene un acceso al campo para la clase, nombre de campo, tipo de campo y posición de índice proporcionados.
     *
     * @param target    la clase de destino.
     * @param name      el nombre del campo.
     * @param fieldType el tipo de campo.
     * @param index     el índice del campo.
     * @param <T>       el tipo de dato del campo.
     * @return un acceso al campo para la clase, nombre de campo, tipo de campo y posición de índice proporcionados.
     */
    private static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType, int index) {
        for (final Field field : target.getDeclaredFields()) {
            if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                field.setAccessible(true);

                // Retorna un nuevo objeto FieldAccessor con la lógica adecuada
                return new FieldAccessor<T>() {
                    @Override
                    public T get(Object target) {
                        try {
                            // Obtiene el valor del campo
                            return (T) field.get(target);
                        } catch (IllegalAccessException e) {
                            // Si hay un error al acceder al campo, lanza una excepción
                            throw new RuntimeException("Cannot access reflection.", e);
                        }
                    }

                    @Override
                    public void set(Object target, Object value) {
                        try {
                            // Establece el valor del campo
                            field.set(target, value);
                        } catch (IllegalAccessException e) {
                            // Si hay un error al acceder al campo, lanza una excepción
                            throw new RuntimeException("Cannot access reflection.", e);
                        }
                    }

                    @Override
                    public boolean hasField(Object target) {
                        // Verifica si el campo pertenece a la clase especificada
                        return field.getDeclaringClass().isAssignableFrom(target.getClass());
                    }

                    @Override
                    public String getFieldName() {
                        // Obtiene el nombre del campo
                        return field.getName();
                    }

                    @Override
                    public Class<?> getFieldType() {
                        // Obtiene el tipo de campo
                        return field.getType();
                    }

                    @Override
                    public boolean isStatic() {
                        // Verifica si el campo es estático
                        return Modifier.isStatic(field.getModifiers());
                    }
                };
            }
        }

        // Si no se encuentra ningún campo coincidente, busca en las superclases
        if (target.getSuperclass() != null)
            return getField(target.getSuperclass(), name, fieldType, index);

        // Si no se encuentra ningún campo coincidente en la jerarquía de clases, lanza una excepción
        throw new IllegalArgumentException("Cannot find field with type " + fieldType);
    }

    /**
     * Obtiene un invocador de método para el nombre de clase, nombre de método y parámetros proporcionados.
     *
     * @param className  el nombre de la clase.
     * @param methodName el nombre del método.
     * @param params     los tipos de parámetros del método.
     * @return un invocador de método para el nombre de clase, nombre de método y parámetros proporcionados.
     */
    public static MethodInvoker getMethod(String className, String methodName, Class<?>... params) {
        // Obtiene un invocador de método con la clase correspondiente y la lógica predeterminada
        return getTypedMethod(getClass(className), methodName, null, true, params);
    }

    /**
     * Obtiene un invocador de método para la clase, nombre de método y parámetros proporcionados.
     *
     * @param clazz      el tipo de clase.
     * @param methodName el nombre del método.
     * @param params     los tipos de parámetros del método.
     * @return un invocador de método para la clase, nombre de método y parámetros proporcionados.
     */
    public static MethodInvoker getMethod(Class<?> clazz, String methodName, Class<?>... params) {
        // Obtiene un invocador de método con la clase correspondiente y la lógica predeterminada
        return getTypedMethod(clazz, methodName, null, true, params);
    }

    /**
     * Obtiene un invocador de método para la clase, nombre de método y parámetros proporcionados,
     * pero solo busca en los métodos declarados en esa clase (sin buscar en sus superclases).
     *
     * @param clazz      el tipo de clase.
     * @param methodName el nombre del método.
     * @param params     los tipos de parámetros del método.
     * @return un invocador de método para la clase, nombre de método y parámetros proporcionados.
     */
    public static MethodInvoker getSingleMethod(Class<?> clazz, String methodName, Class<?>... params) {
        // Obtiene un invocador de método con la clase correspondiente y la lógica para buscar un solo método
        return getTypedMethod(clazz, methodName, null, false, params);
    }

    /**
     * Obtiene un invocador de método para la clase, nombre de método, tipo de retorno y parámetros proporcionados.
     *
     * @param clazz      el tipo de clase.
     * @param methodName el nombre del método.
     * @param returnType el tipo de retorno del método.
     * @param declared   un indicador para buscar solo en los métodos declarados en esa clase (sin buscar en sus superclases).
     * @param params     los tipos de parámetros del método.
     * @return un invocador de método para la clase, nombre de método, tipo de retorno y parámetros proporcionados.
     */
    public static MethodInvoker getTypedMethod(Class<?> clazz, String methodName, Class<?> returnType, boolean declared, Class<?>... params) {
        for (final Method method : (declared ? clazz.getDeclaredMethods() : clazz.getMethods())) {
            if ((methodName == null || method.getName().equals(methodName))
                    && (returnType == null || method.getReturnType().equals(returnType))
                    && Arrays.equals(method.getParameterTypes(), params)) {
                method.setAccessible(true);

                // Retorna un nuevo objeto MethodInvoker con la lógica adecuada
                return new MethodInvoker() {
                    @Override
                    public Object invoke(Object target, Object... arguments) {
                        try {
                            return method.invoke(target, arguments);
                        } catch (Exception e) {
                            // Si se produce un error al invocar el método, lanza una excepción
                            throw new RuntimeException("Cannot invoke method " + method, e);
                        }
                    }

                    @Override
                    public String getMethodName() {
                        // Devuelve el nombre del método
                        return method.getName();
                    }

                    @Override
                    public Class<?> getReturnType() {
                        // Devuelve el tipo de retorno del método
                        return method.getReturnType();
                    }

                    @Override
                    public Class<?>[] getParameterTypes() {
                        // Devuelve los tipos de parámetros del método
                        return method.getParameterTypes();
                    }

                    @Override
                    public boolean isStatic() {
                        // Verifica si el método es estático
                        return Modifier.isStatic(method.getModifiers());
                    }
                };
            }
        }

        // Si no se encuentra ningún método coincidente, busca en las superclases
        if (clazz.getSuperclass() != null)
            return getTypedMethod(clazz.getSuperclass(), methodName, returnType, declared, params);

        // Si no se encuentra ningún método coincidente en la jerarquía de clases, lanza una excepción
        throw new IllegalStateException(String.format("Unable to find method %s (%s).", methodName, Arrays.asList(params)));
    }


    /**
     * Obtiene un invocador de constructor para el tipo de clase y los parámetros de constructor proporcionados.
     *
     * @param className el nombre de la clase.
     * @param params    los tipos de parámetros del constructor.
     * @return un invocador de constructor para el tipo de clase y los parámetros de constructor proporcionados.
     * @throws IllegalStateException si no se puede encontrar el constructor.
     */
    public static ConstructorInvoker getConstructor(String className, Class<?>... params) {
        // Obtiene la clase correspondiente al nombre proporcionado y llama al método getConstructor apropiado.
        return getConstructor(getClass(className), params);
    }

    /**
     * Obtiene un invocador de constructor para el tipo de clase y el índice de constructor proporcionados.
     *
     * @param clazz    el tipo de clase.
     * @param indexOf  el índice del constructor dentro de la lista de constructores de la clase.
     * @return un invocador de constructor para el constructor especificado por el índice.
     * @throws IndexOutOfBoundsException si el índice está fuera de rango (menor que 0 o mayor que el número de constructores).
     */
    public static ConstructorInvoker getConstructor(Class<?> clazz, int indexOf) {
        // Obtiene el constructor en la posición especificada del array de constructores
        Constructor<?> constructor = clazz.getDeclaredConstructors()[indexOf];
        // Hace accesible al constructor aunque sea privado
        constructor.setAccessible(true);

        // Devuelve un invocador de constructor para el constructor obtenido
        return createConstructorInvoker(constructor);
    }

    /**
     * Obtiene un invocador de constructor para el tipo de clase y los parámetros de constructor proporcionados.
     *
     * @param clazz  el tipo de clase.
     * @param params los tipos de parámetros del constructor.
     * @return un invocador de constructor para el tipo de clase y los parámetros de constructor proporcionados.
     * @throws IllegalStateException si no se puede encontrar el constructor.
     */
    public static ConstructorInvoker getConstructor(Class<?> clazz, Class<?>... params) {
        // Itera sobre todos los constructores declarados de la clase
        for (final Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            // Compara los tipos de parámetros del constructor con los tipos proporcionados
            if (Arrays.equals(constructor.getParameterTypes(), params)) {
                // Establece el constructor accesible
                constructor.setAccessible(true);
                // Devuelve un invocador de constructor para el constructor encontrado
                return createConstructorInvoker(constructor);
            }
        }

        // Si no se encuentra ningún constructor coincidente, lanza una excepción
        throw new IllegalStateException(String.format("Unable to find constructor for %s (%s).", clazz, Arrays.asList(params)));
    }

    /**
     * Crea un invocador de constructor para el constructor dado.
     *
     * @param constructor el constructor para el cual se creará el invocador.
     * @return un invocador de constructor para el constructor dado.
     */
    private static ConstructorInvoker createConstructorInvoker(Constructor<?> constructor) {
        // Devuelve un nuevo invocador de constructor que instancia el constructor dado
        return new ConstructorInvoker() {
            @Override
            public Object invoke(Object... arguments) {
                try {
                    return constructor.newInstance(arguments);
                } catch (Exception e) {
                    // Si se produce un error al invocar el constructor, lanza una excepción
                    throw new RuntimeException("Cannot invoke constructor " + constructor, e);
                }
            }

            @Override
            public Class<?>[] getParameterTypes() {
                // Devuelve los tipos de parámetros del constructor
                return constructor.getParameterTypes();
            }
        };
    }



    /**
     * Obtiene el valor del enum correspondiente al nombre de la clase de enum y el nombre del enum proporcionados.
     *
     * @param className el nombre de la clase de enum.
     * @param enumName  el nombre del enum.
     * @return el valor del enum correspondiente.
     */
    public static Object getEnum(String className, String enumName) {
        // Expande las variables en el nombre de la clase de enum, obtiene la clase correspondiente y llama al método getEnum apropiado.
        return getEnum(getCanonicalClass(expandVariables(className)), enumName);
    }

    /**
     * Obtiene el valor del enum correspondiente al tipo de enum y el nombre del enum proporcionados.
     *
     * @param enumType el tipo de enum.
     * @param enumName el nombre del enum.
     * @return el valor del enum correspondiente.
     */
    public static Object getEnum(Class<?> enumType, String enumName) {
        try {
            // Intenta obtener el campo declarado del enum con el nombre proporcionado
            Field field = enumType.getDeclaredField(enumName);
            field.setAccessible(true);

            // Si se encuentra el campo
            // Devuelve el valor del campo (enum constante)
            return field.get(null);
        } catch (ReflectiveOperationException e) {
            // Si ocurre alguna excepción de operación reflexiva (por ejemplo, campo no encontrado, excepción de seguridad),
            // imprime la pila de llamadas para propósitos de depuración
            e.printStackTrace();
        }
        // Devuelve null si no se encuentra el enum constante o si ocurre alguna excepción de operación reflexiva
        return null;
    }


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
