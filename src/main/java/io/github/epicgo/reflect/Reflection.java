package io.github.epicgo.reflect;

import org.bukkit.Bukkit;

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
