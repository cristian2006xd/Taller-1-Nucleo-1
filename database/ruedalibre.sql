CREATE DATABASE IF NOT EXISTS ruedalibre_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ruedalibre_db;

CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    CONSTRAINT pk_categorias PRIMARY KEY (id),
    CONSTRAINT uq_categorias_nombre UNIQUE (nombre)
);

CREATE TABLE IF NOT EXISTS productos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100),
    descripcion VARCHAR(500),
    precio_compra DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    precio DECIMAL(10,2) NOT NULL,
    cantidad INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 0,
    tipo ENUM('MOTO','REPUESTO','ACCESORIO') NOT NULL,
    cilindraje INT,
    anio INT,
    color VARCHAR(50),
    vin VARCHAR(17),
    estado ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    categoria_id BIGINT,
    CONSTRAINT pk_productos PRIMARY KEY (id),
    CONSTRAINT uq_productos_codigo UNIQUE (codigo),
    CONSTRAINT uq_productos_vin UNIQUE (vin),
    CONSTRAINT fk_productos_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    INDEX idx_productos_tipo (tipo),
    INDEX idx_productos_estado (estado),
    INDEX idx_productos_nombre (nombre)
);

CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    cedula VARCHAR(10) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    correo VARCHAR(150),
    direccion VARCHAR(255),
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_clientes PRIMARY KEY (id),
    CONSTRAINT uq_clientes_cedula UNIQUE (cedula),
    INDEX idx_clientes_apellidos (apellidos)
);

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre_completo VARCHAR(150) NOT NULL,
    nombre_usuario VARCHAR(50) NOT NULL,
    correo VARCHAR(150) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    rol ENUM('ADMIN','EMPLEADO') NOT NULL,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT pk_usuarios PRIMARY KEY (id),
    CONSTRAINT uq_usuarios_nombre_usuario UNIQUE (nombre_usuario),
    CONSTRAINT uq_usuarios_correo UNIQUE (correo)
);

CREATE TABLE IF NOT EXISTS ventas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    numero_comprobante VARCHAR(20) NOT NULL,
    cliente_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    descuento DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    iva DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    metodo_pago ENUM('EFECTIVO','TARJETA','TRANSFERENCIA') NOT NULL,
    estado ENUM('COMPLETADA','ANULADA') NOT NULL DEFAULT 'COMPLETADA',
    CONSTRAINT pk_ventas PRIMARY KEY (id),
    CONSTRAINT uq_ventas_comprobante UNIQUE (numero_comprobante),
    CONSTRAINT fk_ventas_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_ventas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    INDEX idx_ventas_fecha (fecha),
    INDEX idx_ventas_estado (estado)
);

CREATE TABLE IF NOT EXISTS detalle_ventas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    venta_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT pk_detalle_ventas PRIMARY KEY (id),
    CONSTRAINT fk_detalle_venta FOREIGN KEY (venta_id) REFERENCES ventas(id),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

CREATE TABLE IF NOT EXISTS movimientos_inventario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    producto_id BIGINT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cantidad INT NOT NULL,
    tipo ENUM('ENTRADA','SALIDA','AJUSTE','VENTA','ANULACION') NOT NULL,
    motivo VARCHAR(255),
    usuario_id BIGINT,
    CONSTRAINT pk_movimientos PRIMARY KEY (id),
    CONSTRAINT fk_mov_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT fk_mov_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    INDEX idx_mov_fecha (fecha),
    INDEX idx_mov_tipo (tipo)
);

INSERT IGNORE INTO categorias (nombre, descripcion) VALUES
('Motos deportivas', 'Motocicletas de alto rendimiento y velocidad'),
('Scooters', 'Motos de uso urbano y fácil manejo'),
('Motos de trabajo', 'Motocicletas para carga y uso comercial'),
('Cascos', 'Cascos de seguridad para motociclistas'),
('Llantas', 'Llantas y neumáticos para motocicletas'),
('Aceites y lubricantes', 'Aceites de motor y productos de lubricación'),
('Frenos', 'Pastillas, discos y componentes de frenado'),
('Baterías', 'Baterías para motocicletas'),
('Cadenas y transmisión', 'Cadenas, piñones y coronas'),
('Accesorios generales', 'Espejos, manubrios, protectores y más');

INSERT IGNORE INTO productos (codigo, nombre, marca, modelo, descripcion, precio_compra, precio, cantidad, stock_minimo, tipo, cilindraje, anio, color, vin, estado, categoria_id) VALUES
('MOT-001', 'Honda CB190R', 'Honda', 'CB190R', 'Moto deportiva 190cc, excelente para ciudad y carretera', 2800.00, 3200.00, 5, 2, 'MOTO', 190, 2024, 'Rojo', 'JH2PC40J8LM100001', 'ACTIVO', 1),
('MOT-002', 'Yamaha FZ-S Fi', 'Yamaha', 'FZ-S Fi', 'Moto deportiva con inyección de combustible 149cc', 2200.00, 2650.00, 3, 1, 'MOTO', 149, 2024, 'Negro', 'JYARJ10E3LA012345', 'ACTIVO', 1),
('MOT-003', 'Honda Beat 110', 'Honda', 'Beat 110', 'Scooter urbano económico y eficiente', 1400.00, 1750.00, 8, 3, 'MOTO', 110, 2023, 'Blanco', 'JH2JF51N9LM200001', 'ACTIVO', 2),
('MOT-004', 'Bajaj Boxer 150', 'Bajaj', 'Boxer 150', 'Moto de trabajo robusta, ideal para carga liviana', 1800.00, 2100.00, 4, 2, 'MOTO', 150, 2023, 'Azul', 'MD2A30BZ2PWC00001', 'ACTIVO', 3),
('MOT-005', 'TVS Apache RTR 200', 'TVS', 'Apache RTR 200', 'Moto deportiva 200cc con ABS', 3100.00, 3600.00, 2, 1, 'MOTO', 200, 2024, 'Negro mate', 'MD626KJ19L2A00001', 'ACTIVO', 1),
('REP-001', 'Aceite Mobil 4T 20W-50 1L', 'Mobil', '4T 20W-50', 'Aceite mineral para motores de 4 tiempos', 6.50, 9.50, 50, 10, 'REPUESTO', NULL, NULL, NULL, NULL, 'ACTIVO', 6),
('REP-002', 'Filtro de aceite Honda CB190', 'Honda', 'CB190 OEM', 'Filtro de aceite original para Honda CB190R', 4.00, 7.50, 30, 5, 'REPUESTO', NULL, NULL, NULL, NULL, 'ACTIVO', 6),
('REP-003', 'Pastillas de freno delanteras Yamaha FZ', 'Yamaha', 'FZ Series', 'Pastillas de freno OEM delanteras', 8.00, 14.00, 20, 5, 'REPUESTO', NULL, NULL, NULL, NULL, 'ACTIVO', 7),
('REP-004', 'Batería YTX9-BS 12V', 'Yuasa', 'YTX9-BS', 'Batería sellada libre de mantenimiento 12V 9Ah', 28.00, 45.00, 15, 3, 'REPUESTO', NULL, NULL, NULL, NULL, 'ACTIVO', 8),
('ACC-001', 'Casco Integral LS2 FF320', 'LS2', 'FF320 Stream', 'Casco integral certificado DOT, talla M', 65.00, 95.00, 10, 2, 'ACCESORIO', NULL, NULL, 'Negro', NULL, 'ACTIVO', 4);

INSERT IGNORE INTO clientes (cedula, nombres, apellidos, telefono, correo, direccion) VALUES
('1712345678', 'Carlos Andrés', 'Morales Vega', '0991234567', 'carlos.morales@email.com', 'Av. 6 de Diciembre N24-100, Quito'),
('0923456789', 'María Isabel', 'Torres Díaz', '0987654321', 'maria.torres@email.com', 'Calle Olmedo 345, Guayaquil'),
('1034567890', 'Luis Fernando', 'Guerrero Pinto', '0976543210', 'luis.guerrero@email.com', 'Bolívar 12-45, Cuenca'),
('1145678901', 'Ana Patricia', 'Salazar Ruiz', '0965432109', 'ana.salazar@email.com', 'Sucre 8-23, Ambato'),
('0256789012', 'Roberto José', 'Peñafiel Castro', '0954321098', 'roberto.penafiel@email.com', 'García Moreno 56, Loja');
