CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    tipo VARCHAR(20) CHECK (tipo IN ('prestatario', 'prestador')) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    documento_identidad VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100),
    telefono VARCHAR(15),
    direccion VARCHAR(150),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	fecha_actualizacion TIMESTAMP
);

CREATE TABLE prestamos (
    id SERIAL PRIMARY KEY,
    id_prestatario INT NOT NULL,
    id_prestador INT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    interes DECIMAL(5,2) NOT NULL,
	meses INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_limite DATE NOT NULL,
    estado VARCHAR(20) CHECK (estado IN ('pendiente', 'pagado', 'vencido')) DEFAULT 'pendiente',
    penalizacion DECIMAL(5,2) DEFAULT 0,
    FOREIGN KEY (id_prestatario) REFERENCES usuarios(id),
    FOREIGN KEY (id_prestador) REFERENCES usuarios(id)
);

CREATE TABLE pagos (
    id SERIAL PRIMARY KEY,
    id_prestamo INT NOT NULL,
    monto_pagado DECIMAL(10,2) NOT NULL,
    fecha_pago DATE NOT NULL,
	estado VARCHAR(20) CHECK (estado IN ('pendiente', 'pagado', 'vencido')) DEFAULT 'pendiente',
	notificacion VARCHAR(20) CHECK (estado IN ('enviado', 'fallido')) DEFAULT 'enviado',
    FOREIGN KEY (id_prestamo) REFERENCES prestamos(id)
);

CREATE TABLE garantias (
    id SERIAL PRIMARY KEY,
    id_prestamo INT NOT NULL,
    tipo VARCHAR(50) CHECK (tipo IN ('Electrodoméstico', 'Joya', 'Vehículo', 'Propiedad', 'Otro')) NOT NULL,
    descripcion TEXT NOT NULL,
    valor_estimado DECIMAL(10,2) NOT NULL,
    ubicacion VARCHAR(255),
    estado VARCHAR(20) CHECK (estado IN ('En custodia', 'Devuelta', 'Ejecutada')) DEFAULT 'En custodia',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_prestamo) REFERENCES prestamos(id)
);

CREATE TABLE imagenes (
    id SERIAL PRIMARY KEY,
	id_prestamo INT NOT NULL,
	id_garantia INT NOT NULL,
	id_pago INT NOT NULL,
    tipo VARCHAR(50) CHECK (tipo IN ('Garantia', 'Contrato', 'Pago', 'Otro')) NOT NULL,
    url_imagen VARCHAR(255) NOT NULL,
	FOREIGN KEY (id_prestamo) REFERENCES prestamos(id),
	FOREIGN KEY (id_pago) REFERENCES pagos(id),
    FOREIGN KEY (id_garantia) REFERENCES garantias(id)
);

