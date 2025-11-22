CREATE TABLE Street (
    id INT AUTO_INCREMENT PRIMARY KEY,
    street_id CHAR(7) NOT NULL,
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
    matricula CHAR(7) NOT NULL,
    media_presiones DOUBLE,
    tipo TEXT,
    media_distancias DOUBLE,
    PRIMARY KEY (matricula)
);

CREATE TABLE Bicicleta (
    matricula CHAR(7) NOT NULL,
    media_presiones DOUBLE,
    tipo TEXT,
    media_distancias DOUBLE,
    PRIMARY KEY (matricula)
);

CREATE TABLE Camion (
    matricula CHAR(7) NOT NULL,   -- Se cambia 'matrícula' a ASCII
    media_presiones DOUBLE,
    tipo TEXT,
    media_distancias DOUBLE,
    PRIMARY KEY (matricula)
);

CREATE TABLE Registro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_vehiculo TEXT,
    matricula CHAR(7),
    contador_coches SMALLINT,
    contador_camiones SMALLINT,
    contador_bicicletas SMALLINT,
    contador_gasolinas SMALLINT,
    contador_electricos SMALLINT,
    tipo_emision TEXT,
    street_id CHAR(7) NOT NULL
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
