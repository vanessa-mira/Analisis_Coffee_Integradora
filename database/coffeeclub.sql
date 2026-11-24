-- ============================================================
--  The Coffee Club - Base de datos (MySQL 8)
--  Ejecuta este script completo en MySQL Workbench antes de iniciar el proyecto.
--  OJO: borra y vuelve a crear las tablas.
-- ============================================================
CREATE DATABASE IF NOT EXISTS coffeeclub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE coffeeclub;

DROP TABLE IF EXISTS detalles_venta;
DROP TABLE IF EXISTS ventas;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(100) NOT NULL,
    correo     VARCHAR(120) NOT NULL UNIQUE,
    telefono   VARCHAR(10)  NULL,
    password   VARCHAR(200) NOT NULL,                       -- contraseña cifrada (PBKDF2)
    rol        ENUM('ADMINISTRADOR', 'EMPLEADO') NOT NULL DEFAULT 'EMPLEADO',
    activo     TINYINT(1)   NOT NULL DEFAULT 1
);

CREATE TABLE productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100)  NOT NULL UNIQUE,
    tipo        ENUM('BEBIDA', 'ALIMENTO') NOT NULL,
    precio      DECIMAL(10,2) NOT NULL CHECK (precio > 0),
    descripcion TEXT          NULL,
    activo      TINYINT(1)    NOT NULL DEFAULT 1
);

CREATE TABLE ventas (
    id_venta       INT AUTO_INCREMENT PRIMARY KEY,
    fecha          DATETIME      NOT NULL,
    total          DECIMAL(10,2) NOT NULL,
    metodo_pago    ENUM('EFECTIVO', 'TARJETA') NOT NULL,
    monto_recibido DECIMAL(10,2) NULL,                      -- solo en efectivo
    cambio         DECIMAL(10,2) NULL,                      -- solo en efectivo
    id_usuario     INT           NOT NULL,
    CONSTRAINT fk_venta_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
);

CREATE TABLE detalles_venta (
    id_detalle      INT AUTO_INCREMENT PRIMARY KEY,
    id_venta        INT           NOT NULL,
    id_producto     INT           NOT NULL,
    cantidad        INT           NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(10,2) NOT NULL,                 -- precio al momento de la venta
    subtotal        DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detalle_venta    FOREIGN KEY (id_venta)    REFERENCES ventas (id_venta),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES productos (id_producto)
);

-- ------------------------------------------------------------
--  Datos de ejemplo
--  Administrador: admin@coffeeclub.com  / Admin1234
--  Empleados:     sofia@coffeeclub.com  / Empleado1   (los demás usan la misma contraseña)
-- ------------------------------------------------------------

INSERT INTO usuarios (id_usuario, nombre, correo, telefono, password, rol, activo) VALUES
(1, 'Carlos Mendoza Ríos', 'admin@coffeeclub.com', '7712345678', '65536:a1b2c3d4e5f60718293a4b5c6d7e8f90:4651a61e21601e457b8a48a83ad3baacd11a9361bd02cafa98307844e34a74aa', 'ADMINISTRADOR', 1),
(2, 'Sofía García López', 'sofia@coffeeclub.com', '7712345679', '65536:b2c3d4e5f60718293a4b5c6d7e8f90a1:9d4ab8e522348bbc8233d560b90db986808bf15ebe4ea9d23f1604e511598fc8', 'EMPLEADO', 1),
(3, 'Andrés Torres Vega', 'andres@coffeeclub.com', NULL, '65536:c3d4e5f60718293a4b5c6d7e8f90a1b2:45c54a44670523e7c09b621ea91663126b5e80b70afef8880aee074d134e5175', 'EMPLEADO', 1),
(4, 'Valentina Ruiz', 'valentina@coffeeclub.com', NULL, '65536:d4e5f60718293a4b5c6d7e8f90a1b2c3:922fecc5e090c30c4fa923ad01559595bf18f44ca4101625633cfe61e3c9ed1d', 'EMPLEADO', 0),
(5, 'Lucía Martínez Fuentes', 'lucia@coffeeclub.com', '7712345680', '65536:e5f60718293a4b5c6d7e8f90a1b2c3d4:bb3d59b5eca22f153941088c3c2a3e0355680c91d50946bb2c341edba6ff57e3', 'EMPLEADO', 1);

INSERT INTO productos (id_producto, nombre, tipo, precio, descripcion, activo) VALUES
(1, 'Capuccino', 'BEBIDA', 45.00, 'Espresso con leche vaporizada y espuma', 1),
(2, 'Espresso', 'BEBIDA', 35.00, 'Café corto e intenso', 1),
(3, 'Latte', 'BEBIDA', 48.00, 'Espresso con leche cremosa', 1),
(4, 'Licuado', 'BEBIDA', 38.00, 'Licuado de fruta de temporada', 1),
(5, 'Frappé Moka', 'BEBIDA', 55.00, 'Café frío con chocolate', 1),
(6, 'Té Chai', 'BEBIDA', 42.00, 'Té especiado con leche', 0),
(7, 'Croissant', 'ALIMENTO', 38.00, 'Croissant de mantequilla', 1),
(8, 'Sándwich de Pavo', 'ALIMENTO', 65.00, 'Pan artesanal con pechuga de pavo', 1),
(9, 'Sándwich de Jamón', 'ALIMENTO', 65.00, 'Pan artesanal con jamón y queso', 1),
(10, 'Galleta Chispas', 'ALIMENTO', 25.00, 'Galleta con chispas de chocolate', 1),
(11, 'Bagel con Queso', 'ALIMENTO', 52.00, 'Bagel con queso crema', 1);

INSERT INTO ventas (id_venta, fecha, total, metodo_pago, monto_recibido, cambio, id_usuario) VALUES
(1, '2026-11-03 09:15:00', 83.00, 'EFECTIVO', 100.00, 17.00, 2),
(2, '2026-11-05 10:40:00', 146.00, 'TARJETA', NULL, NULL, 3),
(3, '2026-11-09 12:05:00', 100.00, 'TARJETA', NULL, NULL, 2),
(4, '2026-11-12 16:30:00', 145.00, 'EFECTIVO', 200.00, 55.00, 5),
(5, '2026-11-16 08:50:00', 90.00, 'EFECTIVO', 100.00, 10.00, 1),
(6, '2026-11-19 17:20:00', 98.00, 'EFECTIVO', 100.00, 2.00, 4),
(7, '2026-11-22 11:10:00', 148.00, 'TARJETA', NULL, NULL, 2),
(8, '2026-11-23 18:42:00', 155.00, 'TARJETA', NULL, NULL, 3);

INSERT INTO detalles_venta (id_detalle, id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 1, 1, 45.00, 45.00),
(2, 1, 7, 1, 38.00, 38.00),
(3, 2, 3, 2, 48.00, 96.00),
(4, 2, 10, 2, 25.00, 50.00),
(5, 3, 8, 1, 65.00, 65.00),
(6, 3, 2, 1, 35.00, 35.00),
(7, 4, 5, 1, 55.00, 55.00),
(8, 4, 11, 1, 52.00, 52.00),
(9, 4, 4, 1, 38.00, 38.00),
(10, 5, 1, 2, 45.00, 90.00),
(11, 6, 3, 1, 48.00, 48.00),
(12, 6, 10, 2, 25.00, 50.00),
(13, 7, 9, 1, 65.00, 65.00),
(14, 7, 1, 1, 45.00, 45.00),
(15, 7, 7, 1, 38.00, 38.00),
(16, 8, 1, 2, 45.00, 90.00),
(17, 8, 8, 1, 65.00, 65.00);
