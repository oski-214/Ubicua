CREATE TABLE Street (
    id INT AUTO_INCREMENT PRIMARY KEY,
    street_id CHAR(7) NOT NULL UNIQUE,
    street_length DOUBLE,
    latitude DOUBLE,
    longitude DOUBLE,
    district TEXT,
    neighborhood TEXT,
    postal_code SMALLINT,
    street_name TEXT,
    surface_type TEXT,
    speed_limit SMALLINT
);

CREATE TABLE Coche (
    matricula CHAR(7) NOT NULL PRIMARY KEY,
    media_presiones DOUBLE,
    tipo TEXT,
    media_distancias DOUBLE,
    street_id CHAR(7),
    CONSTRAINT fk_coche_street FOREIGN KEY (street_id) 
        REFERENCES Street(street_id)
);

CREATE TABLE Bicicleta (
    matricula CHAR(7) NOT NULL PRIMARY KEY,
    media_presiones DOUBLE,
    tipo TEXT,
    media_distancias DOUBLE,
    street_id CHAR(7),
    CONSTRAINT fk_bicicleta_street FOREIGN KEY (street_id) 
        REFERENCES Street(street_id)
);

CREATE TABLE Camion (
    matricula CHAR(7) NOT NULL PRIMARY KEY,
    media_presiones DOUBLE,
    tipo TEXT,
    media_distancias DOUBLE,
    street_id CHAR(7),
    CONSTRAINT fk_camion_street FOREIGN KEY (street_id) 
        REFERENCES Street(street_id)
);

CREATE TABLE Registro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_vehiculo TEXT,
    matricula CHAR(7),
    contador_coches SMALLINT DEFAULT 0,
    contador_camiones SMALLINT DEFAULT 0,
    contador_bicicletas SMALLINT DEFAULT 0,
    contador_gasolinas SMALLINT DEFAULT 0,
    contador_electricos SMALLINT DEFAULT 0,
    tipo_emision TEXT,
    street_id CHAR(7) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_registro_street FOREIGN KEY (street_id) 
        REFERENCES Street(street_id) 
);

-- Relaciones Many-to-Many
CREATE TABLE many_Camion_has_many_Registro (
    camion_matricula CHAR(7) NOT NULL,
    registro_id INT NOT NULL,
    PRIMARY KEY (camion_matricula, registro_id),
    FOREIGN KEY (camion_matricula) REFERENCES Camion(matricula),
    FOREIGN KEY (registro_id) REFERENCES Registro(id)
);

CREATE TABLE many_Bicicleta_has_many_Registro (
    bicicleta_matricula CHAR(7) NOT NULL,
    registro_id INT NOT NULL,
    PRIMARY KEY (bicicleta_matricula, registro_id),
    FOREIGN KEY (bicicleta_matricula) REFERENCES Bicicleta(matricula),
    FOREIGN KEY (registro_id) REFERENCES Registro(id)
);

CREATE TABLE many_Coche_has_many_Registro (
    coche_matricula CHAR(7) NOT NULL,
    registro_id INT NOT NULL,
    PRIMARY KEY (coche_matricula, registro_id),
    FOREIGN KEY (coche_matricula) REFERENCES Coche(matricula),
    FOREIGN KEY (registro_id) REFERENCES Registro(id)
);

-- Relaciones 1:N (Registro a Street) pueden representarse con FK directa
ALTER TABLE Registro
    ADD FOREIGN KEY (street_id) REFERENCES Street(street_id);


-- ============================================
-- DATOS DE PRUEBA
-- ============================================

-- Insertar SOLO la calle del proyecto (ST_1678 - Calle del Sol)
INSERT INTO Street (street_id, street_name, street_length, latitude, longitude, district, neighborhood, postal_code, surface_type, speed_limit) VALUES
('ST_1678', 'Calle del Sol', 63.21, 40.4256518, -3.6619545, 'Salamanca', 'Parque de las Avenidas', 28009, 'asphalt', 30);

-- ============================================
-- COCHES (solo tipo GAS o ECO)
-- ============================================

-- Coches GAS (sensor de sonido detectó ruido)
INSERT INTO Coche (matricula, tipo, media_presiones, media_distancias, street_id) VALUES
('1234ABC', 'GAS', 350.5, 12.8, 'ST_1678'),
('5678DEF', 'GAS', 380.2, 13.2, 'ST_1678'),
('9012GHI', 'GAS', 365.7, 12.5, 'ST_1678'),
('2468JKL', 'GAS', 372.0, 13.0, 'ST_1678');

-- Coches ECO (sensor de sonido NO detectó ruido)
INSERT INTO Coche (matricula, tipo, media_presiones, media_distancias, street_id) VALUES
('2345BCD', 'ECO', 340.0, 13.0, 'ST_1678'),
('6789EFG', 'ECO', 355.5, 12.9, 'ST_1678'),
('3579MNO', 'ECO', 345.0, 12.7, 'ST_1678');

-- ============================================
-- BICICLETAS (solo tipo ECO)
-- ============================================

INSERT INTO Bicicleta (matricula, tipo, media_presiones, media_distancias, street_id) VALUES
('BIC0001', 'ECO', 180.5, 18.5, 'ST_1678'),
('BIC0002', 'ECO', 195.0, 17.8, 'ST_1678'),
('BIC0003', 'ECO', 165.0, 19.2, 'ST_1678'),
('BIC0004', 'ECO', 175.5, 18.0, 'ST_1678'),
('BIC0005', 'ECO', 190.0, 18.3, 'ST_1678');

-- ============================================
-- CAMIONES (tipo GAS o ECO)
-- ============================================

-- Camiones GAS (diesel/gasolina)
INSERT INTO Camion (matricula, tipo, media_presiones, media_distancias, street_id) VALUES
('7890JKL', 'GAS', 850.0, 8.5, 'ST_1678'),
('8901KLM', 'GAS', 920.5, 7.8, 'ST_1678'),
('9012LMN', 'GAS', 880.0, 8.2, 'ST_1678');

-- Camiones ECO (eléctricos)
INSERT INTO Camion (matricula, tipo, media_presiones, media_distancias, street_id) VALUES
('1234NOP', 'ECO', 860.0, 8.3, 'ST_1678'),
('4680QRS', 'ECO', 870.0, 8.0, 'ST_1678');

-- ============================================
-- REGISTROS (simulando detecciones del Arduino)
-- ============================================

-- Registros de COCHES GAS
INSERT INTO Registro (tipo_vehiculo, matricula, street_id, contador_coches, contador_gasolinas, contador_electricos, tipo_emision, timestamp) VALUES
('Coche', '1234ABC', 'ST_1678', 1, 1, 0, 'GAS', '2025-11-23 14:15:30'),
('Coche', '5678DEF', 'ST_1678', 1, 1, 0, 'GAS', '2025-11-23 14:18:45'),
('Coche', '9012GHI', 'ST_1678', 1, 1, 0, 'GAS', '2025-11-23 14:22:10'),
('Coche', '2468JKL', 'ST_1678', 1, 1, 0, 'GAS', '2025-11-23 14:25:00');

-- Registros de COCHES ECO
INSERT INTO Registro (tipo_vehiculo, matricula, street_id, contador_coches, contador_gasolinas, contador_electricos, tipo_emision, timestamp) VALUES
('Coche', '2345BCD', 'ST_1678', 1, 0, 1, 'ECO', '2025-11-23 14:25:20'),
('Coche', '6789EFG', 'ST_1678', 1, 0, 1, 'ECO', '2025-11-23 14:30:15'),
('Coche', '3579MNO', 'ST_1678', 1, 0, 1, 'ECO', '2025-11-23 14:35:40');

-- Registros de BICICLETAS ECO
INSERT INTO Registro (tipo_vehiculo, matricula, street_id, contador_bicicletas, contador_gasolinas, contador_electricos, tipo_emision, timestamp) VALUES
('Bicicleta', 'BIC0001', 'ST_1678', 1, 0, 1, 'ECO', '2025-11-23 14:16:00'),
('Bicicleta', 'BIC0002', 'ST_1678', 1, 0, 1, 'ECO', '2025-11-23 14:20:30'),
('Bicicleta', 'BIC0003', 'ST_1678', 1, 0, 1, 'ECO', '2025-11-23 14:28:15'),
('Bicicleta', 'BIC0004', 'ST_1678', 1, 0, 1, 'ECO', '2025-11-23 14:33:50'),
('Bicicleta', 'BIC0005', 'ST_1678', 1, 0, 1, 'ECO', '2025-11-23 14:38:20');

-- Registros de CAMIONES GAS
INSERT INTO Registro (tipo_vehiculo, matricula, street_id, contador_camiones, contador_gasolinas, contador_electricos, tipo_emision, timestamp) VALUES
('Camion', '7890JKL', 'ST_1678', 1, 1, 0, 'GAS', '2025-11-23 14:17:30'),
('Camion', '8901KLM', 'ST_1678', 1, 1, 0, 'GAS', '2025-11-23 14:24:00'),
('Camion', '9012LMN', 'ST_1678', 1, 1, 0, 'GAS', '2025-11-23 14:32:45');

-- Registros de CAMIONES ECO
INSERT INTO Registro (tipo_vehiculo, matricula, street_id, contador_camiones, contador_gasolinas, contador_electricos, tipo_emision, timestamp) VALUES
('Camion', '1234NOP', 'ST_1678', 1, 0, 1, 'ECO', '2025-11-23 14:42:55'),
('Camion', '4680QRS', 'ST_1678', 1, 0, 1, 'ECO', '2025-11-23 14:45:30');

-- ============================================
-- RELACIONES MANY-TO-MANY
-- ============================================

-- Relaciones Coche-Registro
INSERT INTO many_Coche_has_many_Registro (coche_matricula, registro_id) VALUES
('1234ABC', 1),
('5678DEF', 2),
('9012GHI', 3),
('2468JKL', 4),
('2345BCD', 5),
('6789EFG', 6),
('3579MNO', 7);

-- Relaciones Bicicleta-Registro
INSERT INTO many_Bicicleta_has_many_Registro (bicicleta_matricula, registro_id) VALUES
('BIC0001', 8),
('BIC0002', 9),
('BIC0003', 10),
('BIC0004', 11),
('BIC0005', 12);

-- Relaciones Camion-Registro
INSERT INTO many_Camion_has_many_Registro (camion_matricula, registro_id) VALUES
('7890JKL', 13),
('8901KLM', 14),
('9012LMN', 15),
('1234NOP', 16),
('4680QRS', 17);
