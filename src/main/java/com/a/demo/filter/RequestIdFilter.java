package com.a.demo.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Filtro que intercepta TODOS los requests HTTP que llegan a la API.
 *
 * ¿Por qué @Component acá y no métodos estáticos?
 * Porque Spring necesita conocer esta clase para registrarla automáticamente
 * como filtro del servidor. Spring escanea todos los @Component al arrancar,
 * y cuando encuentra uno que implementa Filter, lo agrega a la cadena de
 * filtros del servidor (Tomcat). Sin @Component, Spring no sabe que existe
 * y nunca lo ejecuta — no hay forma de hacerlo funcionar de otra manera.
 *
 * Flujo de un request con este filtro:
 *   Cliente → [RequestIdFilter] → Controller → Service → Repository → BD
 *                                                                    ↓
 *   Cliente ← [RequestIdFilter] ← Controller ← Service ← Repository ←
 */
@Component
// @Component le dice a Spring: "esta clase es tuya, instanciala y registrala".
// Spring la detecta al arrancar (component scan), la crea UNA sola vez (singleton)
// y la mantiene activa durante toda la vida de la aplicación.
// Al implementar la interfaz Filter, Spring Boot la registra automáticamente
// como filtro HTTP — sin necesidad de configuración extra.
public class RequestIdFilter implements Filter {

    // Nombre de la clave que usamos en MDC (Mapped Diagnostic Context).
    // MDC es un mapa por hilo (thread-local) que Logback/SLF4J usa para
    // agregar datos extra a cada línea de log automáticamente.
    private static final String REQUEST_ID_KEY = "requestId";

    /**
     * Se ejecuta UNA VEZ por cada request HTTP que llega a la API.
     *
     * La firma doFilter(request, response, chain) es la interfaz Filter de Jakarta.
     * "chain" representa al siguiente eslabón en la cadena: puede ser otro filtro
     * o, si no hay más filtros, el DispatcherServlet de Spring (que llama al controller).
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Casteamos a HttpServletRequest para poder leer headers HTTP
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Generamos un ID único para este request.
        // Tomamos solo los primeros 8 caracteres del UUID para que no ocupe
        // demasiado espacio en los logs (ej: "a3f2b1c9").
        String requestId = UUID.randomUUID().toString().substring(0, 8);

        // MDC.put() agrega el requestId al mapa de este hilo (thread).
        // A partir de acá, TODOS los logs que se hagan durante este request
        // van a incluir el requestId automáticamente si el pattern de logging
        // tiene %X{requestId}.
        // Esto permite filtrar los logs de un request específico cuando
        // hay muchos requests simultáneos (muy útil en producción).
        MDC.put(REQUEST_ID_KEY, requestId);

        try {
            // Logueamos el inicio del request con su método HTTP y URL.
            // Como ya pusimos el requestId en MDC, este log va a mostrar algo como:
            // [a3f2b1c9] GET /api/personal-info/1
            String method = httpRequest.getMethod();
            String uri    = httpRequest.getRequestURI();

            // chain.doFilter() le pasa el control al siguiente filtro (o al controller).
            // TODO lo que escribamos ANTES de esta línea se ejecuta al llegar el request.
            // TODO lo que escribamos DESPUÉS se ejecuta cuando el response ya está listo.
            chain.doFilter(request, response);

        } finally {
            // finally garantiza que MDC.clear() se ejecute SIEMPRE,
            // incluso si el controller lanza una excepción.
            // Es CRÍTICO limpiar el MDC después de cada request porque
            // los hilos de Tomcat se reutilizan (thread pool) — si no limpiamos,
            // el próximo request que use este hilo va a heredar el requestId anterior.
            MDC.clear();
        }
    }
}
