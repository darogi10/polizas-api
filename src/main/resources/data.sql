-- Pólizas (ids generados en orden: 1..4)
INSERT INTO polizas (tipo, estado, tomador, canon_mensual, prima, meses_vigencia, fecha_inicio, fecha_fin)
VALUES ('INDIVIDUAL', 'ACTIVA', 'Juan Pérez', 1500000.00, 18000000.00, 12, DATE '2026-01-01', DATE '2027-01-01');

INSERT INTO polizas (tipo, estado, tomador, canon_mensual, prima, meses_vigencia, fecha_inicio, fecha_fin)
VALUES ('INDIVIDUAL', 'ACTIVA', 'María Gómez', 2000000.00, 12000000.00, 6, DATE '2026-06-01', DATE '2026-12-01');

INSERT INTO polizas (tipo, estado, tomador, canon_mensual, prima, meses_vigencia, fecha_inicio, fecha_fin)
VALUES ('COLECTIVA', 'ACTIVA', 'Inmobiliaria Los Andes S.A.S.', 5000000.00, 60000000.00, 12, DATE '2026-02-01', DATE '2027-02-01');

INSERT INTO polizas (tipo, estado, tomador, canon_mensual, prima, meses_vigencia, fecha_inicio, fecha_fin)
VALUES ('COLECTIVA', 'CANCELADA', 'Constructora El Roble Ltda.', 3000000.00, 36000000.00, 12, DATE '2025-09-01', DATE '2026-09-01');

-- Riesgos
INSERT INTO riesgos (poliza_id, direccion_inmueble, arrendatario, estado)
VALUES (1, 'Calle 100 # 15-20 Apto 301, Bogotá', 'Carlos Ramírez', 'ACTIVO');

INSERT INTO riesgos (poliza_id, direccion_inmueble, arrendatario, estado)
VALUES (2, 'Carrera 43A # 1-50 Apto 1204, Medellín', 'Laura Torres', 'ACTIVO');

INSERT INTO riesgos (poliza_id, direccion_inmueble, arrendatario, estado)
VALUES (3, 'Avenida 6N # 23-45 Local 2, Cali', 'Panadería La Espiga S.A.S.', 'ACTIVO');
INSERT INTO riesgos (poliza_id, direccion_inmueble, arrendatario, estado)
VALUES (3, 'Calle 72 # 10-07 Oficina 501, Bogotá', 'Andrés Castillo', 'ACTIVO');
INSERT INTO riesgos (poliza_id, direccion_inmueble, arrendatario, estado)
VALUES (3, 'Carrera 54 # 68-120 Apto 802, Barranquilla', 'Sofía Herrera', 'ACTIVO');

INSERT INTO riesgos (poliza_id, direccion_inmueble, arrendatario, estado)
VALUES (4, 'Calle 50 # 80-15 Bodega 4, Medellín', 'Distribuidora Antioquia Ltda.', 'CANCELADO');
INSERT INTO riesgos (poliza_id, direccion_inmueble, arrendatario, estado)
VALUES (4, 'Carrera 7 # 32-16 Apto 1001, Bogotá', 'Valentina Ruiz', 'CANCELADO');
