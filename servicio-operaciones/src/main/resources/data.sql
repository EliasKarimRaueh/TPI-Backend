-- ============================================
-- DATOS DE PRUEBA PARA SERVICIO-OPERACIONES
-- ============================================

-- ============================================
-- CLIENTES
-- ============================================
INSERT INTO cliente (nombre, direccion, telefono, email) 
VALUES ('Empresa Logística SA', 'Av. Colón 1500, Córdoba, Argentina', '+54 351 4567890', 'contacto@logistica.com');

INSERT INTO cliente (nombre, direccion, telefono, email) 
VALUES ('Transportes del Sur SRL', 'Calle San Martín 234, Rosario, Santa Fe', '+54 341 7654321', 'ventas@transportessur.com');

-- ============================================
-- RUTAS
-- ============================================
-- Ruta 1: Córdoba a Buenos Aires
INSERT INTO ruta (origen, destino, latitud_origen, longitud_origen, latitud_destino, longitud_destino, distancia_km, tiempo_estimado_horas) 
VALUES ('Córdoba, Argentina', 'Buenos Aires, Argentina', -31.4201, -64.1888, -34.6037, -58.3816, 0, 0);

-- Ruta 2: Rosario a Mendoza
INSERT INTO ruta (origen, destino, latitud_origen, longitud_origen, latitud_destino, longitud_destino, distancia_km, tiempo_estimado_horas) 
VALUES ('Rosario, Santa Fe', 'Mendoza, Argentina', -32.9442, -60.6505, -32.8895, -68.8458, 0, 0);

-- ============================================
-- CONTENEDORES
-- ============================================
INSERT INTO contenedor (numero, estado, peso, volumen, cliente_id) 
VALUES ('CONT-001', 'EN_ORIGEN', 5000.0, 25.0, 1);

INSERT INTO contenedor (numero, estado, peso, volumen, cliente_id) 
VALUES ('CONT-002', 'EN_DEPOSITO', 3500.0, 18.0, 1);

INSERT INTO contenedor (numero, estado, peso, volumen, cliente_id) 
VALUES ('CONT-003', 'EN_ORIGEN', 7500.0, 40.0, 2);

-- ============================================
-- SOLICITUDES
-- ============================================
INSERT INTO solicitud (contenedor_id, ruta_id, cliente_id, fecha_solicitud, estado, costo_estimado, tiempo_estimado, costo_final, tiempo_real) 
VALUES (1, 1, 1, '2025-10-20', 'PENDIENTE', 0, 0, 0, 0);

INSERT INTO solicitud (contenedor_id, ruta_id, cliente_id, fecha_solicitud, estado, costo_estimado, tiempo_estimado, costo_final, tiempo_real) 
VALUES (3, 2, 2, '2025-10-21', 'PENDIENTE', 0, 0, 0, 0);
