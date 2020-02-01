--
-- Drop previus data base almacen if exist
--
DROP DATABASE IF EXISTS Almacen;

CREATE DATABASE Almacen;
USE Almacen;

-- --------------------------------------------------------

DROP TABLE IF EXISTS productos;

CREATE TABLE productos (
  idProducto int(3) ZEROFILL NOT NULL AUTO_INCREMENT,
  nombre varchar(30) NOT NULL,
  cantidad DECIMAL(10,2) NOT NULL,
  codigoProducto tinyint(3) ZEROFILL NOT NULL,
-- ZEROFILL automaticamente convierte a tinyint en UNSIGNED
  PRIMARY KEY (idProducto)
);

--
-- Dumping data for table 'productos'
--

INSERT INTO productos (nombre, cantidad,codigoProducto) VALUES
('Pringles', 100, 225),
('Sandia', 200, 200),
('Naranja', 4000, 10);

SELECT * FROM productos;
