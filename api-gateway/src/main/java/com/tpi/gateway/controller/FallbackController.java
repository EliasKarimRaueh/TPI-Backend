package com.tpi.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para manejar fallbacks cuando los servicios no están disponibles.
 * 
 * Este controlador proporciona respuestas de emergencia cuando:
 * - Los servicios backend están caídos
 * - Los Circuit Breakers se activan por timeouts
 * - Hay problemas de conectividad
 * 
 * @author TPI Backend Team
 */
@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    /**
     * Fallback para el servicio de flota
     */
    @GetMapping("/flota")
    @PostMapping("/flota")
    public ResponseEntity<Map<String, Object>> flotaFallback() {
        log.warn("🔶 Circuit breaker activated for Flota service - returning fallback response");
        
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("error", "SERVICE_UNAVAILABLE");
        fallbackResponse.put("message", "El servicio de Flota no está disponible temporalmente");
        fallbackResponse.put("service", "servicio-flota");
        fallbackResponse.put("timestamp", LocalDateTime.now());
        fallbackResponse.put("suggestion", "Por favor intente nuevamente en unos minutos");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(fallbackResponse);
    }

    /**
     * Fallback para el servicio de operaciones
     */
    @GetMapping("/operaciones")
    @PostMapping("/operaciones")
    public ResponseEntity<Map<String, Object>> operacionesFallback() {
        log.warn("🔶 Circuit breaker activated for Operaciones service - returning fallback response");
        
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("error", "SERVICE_UNAVAILABLE");
        fallbackResponse.put("message", "El servicio de Operaciones no está disponible temporalmente");
        fallbackResponse.put("service", "servicio-operaciones");
        fallbackResponse.put("timestamp", LocalDateTime.now());
        fallbackResponse.put("suggestion", "Por favor intente nuevamente en unos minutos");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(fallbackResponse);
    }

    /**
     * Fallback genérico para otros servicios
     */
    @GetMapping("/generic")
    @PostMapping("/generic")
    public ResponseEntity<Map<String, Object>> genericFallback() {
        log.warn("🔶 Circuit breaker activated for unknown service - returning generic fallback response");
        
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("error", "SERVICE_UNAVAILABLE");
        fallbackResponse.put("message", "El servicio solicitado no está disponible temporalmente");
        fallbackResponse.put("service", "unknown");
        fallbackResponse.put("timestamp", LocalDateTime.now());
        fallbackResponse.put("suggestion", "Por favor intente nuevamente en unos minutos");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(fallbackResponse);
    }
}