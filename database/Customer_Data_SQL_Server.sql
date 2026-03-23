-- USE db_customer; 


-- ============================================================================
-- Inserts de prueba: 100 registros para tabla customers
-- 70 Personas (P) + 30 Organizaciones (O)
-- Distribución de document_type y residency_status variada
-- ============================================================================

INSERT INTO customers (document_type, document_number, name, paternal_surname, maternal_surname, trade_name, customer_type, residency_status)
VALUES
-- ============================================================================
-- PERSONAS NATURALES (P) — 70 registros
-- ============================================================================
-- document_type: 1=DNI(8), 3=CE(12max), 4=Pasaporte(12max), 2=CarneIdentidad(12max)
-- residency_status: N=Nacional, E=Extranjero

-- DNI - Nacionales (40 registros)
('1', '10301554', N'Carlos',            N'García',          N'López',           NULL, 'P', 'N'),
('1', '45678912', N'María Elena',       N'Rodríguez',       N'Fernández',       NULL, 'P', 'N'),
('1', '72345678', N'José Luis',         N'Martínez',        N'Huamán',          NULL, 'P', 'N'),
('1', '80123456', N'Ana Patricia',      N'Quispe',          N'Mamani',          NULL, 'P', 'N'),
('1', '43216789', N'Pedro',             N'Flores',          N'Castillo',        NULL, 'P', 'N'),
('1', '65432198', N'Rosa María',        N'Torres',          N'Díaz',            NULL, 'P', 'N'),
('1', '78901234', N'Miguel Ángel',      N'Vargas',          N'Rojas',           NULL, 'P', 'N'),
('1', '34567890', N'Lucía',             N'Mendoza',         N'Paredes',         NULL, 'P', 'N'),
('1', '56789012', N'Fernando',          N'Sánchez',         N'Gutiérrez',       NULL, 'P', 'N'),
('1', '12345679', N'Carmen Rosa',       N'Chávez',          N'Morales',         NULL, 'P', 'N'),
('1', '23456781', N'Roberto',           N'Espinoza',        N'Ramos',           NULL, 'P', 'N'),
('1', '34567892', N'Silvia',            N'Cabrera',         N'Vega',            NULL, 'P', 'N'),
('1', '45678903', N'Juan Carlos',       N'Palacios',        N'Herrera',         NULL, 'P', 'N'),
('1', '56789014', N'Gladys',            N'Córdova',         N'Tapia',           NULL, 'P', 'N'),
('1', '67890125', N'Raúl',              N'Huamán',          N'Quispe',          NULL, 'P', 'N'),
('1', '78901236', N'Teresa',            N'Aguilar',         N'Salazar',         NULL, 'P', 'N'),
('1', '89012347', N'Óscar',             N'Peña',            N'Campos',          NULL, 'P', 'N'),
('1', '90123458', N'Patricia',          N'Rivera',          N'Reyes',           NULL, 'P', 'N'),
('1', '11234567', N'Andrés',            N'Villavicencio',   N'Medina',          NULL, 'P', 'N'),
('1', '22345678', N'Juana',             N'Contreras',       N'Delgado',         NULL, 'P', 'N'),
('1', '33456789', N'Luis Alberto',      N'Romero',          N'Navarro',         NULL, 'P', 'N'),
('1', '44567890', N'Beatriz',           N'Pariona',         N'Zúñiga',          NULL, 'P', 'N'),
('1', '55678901', N'Ricardo',           N'Cárdenas',        N'Vera',            NULL, 'P', 'N'),
('1', '66789012', N'Margarita',         N'Bazán',           N'Ortega',          NULL, 'P', 'N'),
('1', '77890123', N'Hugo',              N'Valverde',        N'Arias',           NULL, 'P', 'N'),
('1', '88901234', N'Cecilia',           N'Montes',          N'Carrasco',        NULL, 'P', 'N'),
('1', '99012345', N'Enrique',           N'Lozano',          N'Figueroa',        NULL, 'P', 'N'),
('1', '10234567', N'Norma',             N'Benítez',         N'Cáceres',         NULL, 'P', 'N'),
('1', '21345678', N'Víctor',            N'Choque',          N'Ticona',          NULL, 'P', 'N'),
('1', '32456789', N'Isabel',            N'Apaza',           N'Condori',         NULL, 'P', 'N'),
('1', '43567890', N'Alberto',           N'Guzmán',          NULL,               NULL, 'P', 'N'),
('1', '54678901', N'Mónica',            N'Zavala',          NULL,               NULL, 'P', 'N'),
('1', '65789012', N'Julio César',       N'Bustamante',      N'Oliva',           NULL, 'P', 'N'),
('1', '76890123', N'Pilar',             N'Ccama',           N'Huanca',          NULL, 'P', 'N'),
('1', '87901234', N'Santiago',          N'Atahuachi',       N'Puma',            NULL, 'P', 'N'),
('1', '98012345', N'Doris',             N'Yupanqui',        N'Inga',            NULL, 'P', 'N'),
('1', '19123456', N'Alfredo',           N'Obando',          N'Tejada',          NULL, 'P', 'N'),
('1', '20234567', N'Elena',             N'Valdivia',        N'Cueva',           NULL, 'P', 'N'),
('1', '31345678', N'Marco Antonio',     N'Pacheco',         N'Soto',            NULL, 'P', 'N'),
('1', '42456789', N'Graciela',          N'Suárez',          N'Aliaga',          NULL, 'P', 'N'),

-- DNI - Nacionales sin apellido materno (5 registros)
('1', '53567890', N'Domingo',           N'Tello',           NULL,               NULL, 'P', 'N'),
('1', '64678901', N'Consuelo',          N'Arce',            NULL,               NULL, 'P', 'N'),
('1', '75789012', N'Ernesto',           N'Meza',            NULL,               NULL, 'P', 'N'),
('1', '86890123', N'Adelina',           N'Pillco',          NULL,               NULL, 'P', 'N'),
('1', '97901234', N'Héctor',            N'Ríos',            NULL,               NULL, 'P', 'N'),

-- Carné de Extranjería - Extranjeros (10 registros)
('3', 'CE2023000001', N'Alejandro',     N'Pérez',           N'Gómez',           NULL, 'P', 'E'),
('3', 'CE2023000002', N'Valentina',     N'Muñoz',           N'Herrera',         NULL, 'P', 'E'),
('3', 'CE2023000003', N'Sebastián',     N'Ramírez',         N'Varela',          NULL, 'P', 'E'),
('3', 'CE2023000004', N'Camila',        N'Orozco',          N'Duarte',          NULL, 'P', 'E'),
('3', 'CE2023000005', N'Mateo',         N'Londoño',         N'Ríos',            NULL, 'P', 'E'),
('3', 'CE2024000001', N'Isabella',      N'Cardona',         NULL,               NULL, 'P', 'E'),
('3', 'CE2024000002', N'Daniel',        N'Aristizábal',     N'Mesa',            NULL, 'P', 'E'),
('3', 'CE2024000003', N'Luciana',       N'Echeverría',      NULL,               NULL, 'P', 'E'),
('3', 'CE2024000004', N'Andrés Felipe', N'Betancourt',      N'Osorio',          NULL, 'P', 'E'),
('3', 'CE2024000005', N'Mariana',       N'Saldarriaga',     N'Correa',          NULL, 'P', 'E'),

-- Pasaporte - Extranjeros (10 registros)
('4', 'AB12345678', N'James',           N'Smith',           NULL,               NULL, 'P', 'E'),
('4', 'CD98765432', N'Sophie',          N'Müller',          NULL,               NULL, 'P', 'E'),
('4', 'EF11223344', N'Takeshi',         N'Yamamoto',        NULL,               NULL, 'P', 'E'),
('4', 'GH55667788', N'Marie Claire',    N'Dubois',          NULL,               NULL, 'P', 'E'),
('4', 'IJ99001122', N'Giovanni',        N'Rossi',           NULL,               NULL, 'P', 'E'),
('4', 'KL33445566', N'Chen Wei',        N'Zhang',           NULL,               NULL, 'P', 'E'),
('4', 'MN77889900', N'Priya',           N'Sharma',          NULL,               NULL, 'P', 'E'),
('4', 'OP12349876', N'Hans',            N'Schneider',       NULL,               NULL, 'P', 'E'),
('4', 'QR56781234', N'Yoon-Seo',        N'Park',            NULL,               NULL, 'P', 'E'),
('4', 'ST90125678', N'Fatima',          N'Al-Rashid',       NULL,               NULL, 'P', 'E'),

-- Carné de Identidad - Menores nacionales (5 registros)
('2', 'CI20230001', N'Matías',          N'García',          N'Torres',          NULL, 'P', 'N'),
('2', 'CI20230002', N'Valentina',       N'Quispe',          N'Flores',          NULL, 'P', 'N'),
('2', 'CI20230003', N'Thiago',          N'Rodríguez',       N'Mamani',          NULL, 'P', 'N'),
('2', 'CI20230004', N'Mía',             N'Huamán',          N'López',           NULL, 'P', 'N'),
('2', 'CI20230005', N'Liam',            N'Martínez',        N'Chávez',          NULL, 'P', 'N'),

-- ============================================================================
-- ORGANIZACIONES (O) — 30 registros
-- ============================================================================
-- document_type: 6=RUC(11)
-- name = Razón Social, trade_name = Nombre de Fantasía (opcional)

-- RUC - Organizaciones nacionales con trade_name (15 registros)
('6', '20100130204', N'Banco de Crédito del Perú S.A.',                    NULL, NULL, N'BCP',                     'O', 'N'),
('6', '20100047218', N'Banco Internacional del Perú S.A.A.',               NULL, NULL, N'Interbank',               'O', 'N'),
('6', '20100055237', N'Scotiabank Perú S.A.A.',                            NULL, NULL, N'Scotiabank',              'O', 'N'),
('6', '20100043140', N'BBVA Continental S.A.',                             NULL, NULL, N'BBVA',                    'O', 'N'),
('6', '20418896915', N'Telefónica del Perú S.A.A.',                        NULL, NULL, N'Movistar',                'O', 'N'),
('6', '20100128056', N'Alicorp S.A.A.',                                    NULL, NULL, N'Alicorp',                 'O', 'N'),
('6', '20100127912', N'Unión de Cervecerías Peruanas Backus y Johnston',   NULL, NULL, N'Backus',                  'O', 'N'),
('6', '20100154308', N'Southern Peru Copper Corporation',                   NULL, NULL, N'Southern Copper',         'O', 'N'),
('6', '20100116635', N'Cementos Pacasmayo S.A.A.',                         NULL, NULL, N'Pacasmayo',               'O', 'N'),
('6', '20330791412', N'Saga Falabella S.A.',                               NULL, NULL, N'Falabella',               'O', 'N'),
('6', '20109072177', N'Cencosud Retail Perú S.A.',                         NULL, NULL, N'Wong',                    'O', 'N'),
('6', '20492092313', N'Supermercados Peruanos S.A.',                       NULL, NULL, N'Plaza Vea',               'O', 'N'),
('6', '20100190797', N'Gloria S.A.',                                       NULL, NULL, N'Gloria',                  'O', 'N'),
('6', '20100152356', N'Entel Perú S.A.',                                   NULL, NULL, N'Entel',                   'O', 'N'),
('6', '20505072702', N'Latam Airlines Perú S.A.',                          NULL, NULL, N'Latam',                   'O', 'N'),

-- RUC - Organizaciones nacionales sin trade_name (10 registros)
('6', '20601327318', N'Soluciones Digitales del Perú S.A.C.',              NULL, NULL, NULL,                       'O', 'N'),
('6', '20602415679', N'Consultora Andina de Negocios S.R.L.',              NULL, NULL, NULL,                       'O', 'N'),
('6', '20603512890', N'Importadora del Pacífico E.I.R.L.',                 NULL, NULL, NULL,                       'O', 'N'),
('6', '20604678123', N'Constructora Los Andes S.A.',                       NULL, NULL, NULL,                       'O', 'N'),
('6', '20605789234', N'Agroindustrias del Sur S.A.C.',                     NULL, NULL, NULL,                       'O', 'N'),
('6', '20606890345', N'Transportes Unidos del Norte S.R.L.',               NULL, NULL, NULL,                       'O', 'N'),
('6', '20607901456', N'Laboratorio Farmacéutico Nacional S.A.',            NULL, NULL, NULL,                       'O', 'N'),
('6', '20608012567', N'Minera Cordillera Blanca S.A.C.',                   NULL, NULL, NULL,                       'O', 'N'),
('6', '20609123678', N'Textiles Finos del Perú S.A.',                      NULL, NULL, NULL,                       'O', 'N'),
('6', '20610234789', N'Grupo Gastronómico Limeño S.A.C.',                  NULL, NULL, NULL,                       'O', 'N'),

-- RUC - Organizaciones extranjeras (5 registros)
('6', '20501523837', N'Microsoft Perú S.R.L.',                             NULL, NULL, N'Microsoft',               'O', 'E'),
('6', '20503837219', N'Amazon Web Services Perú S.R.L.',                   NULL, NULL, N'AWS',                     'O', 'E'),
('6', '20506478301', N'Google Cloud Perú S.A.C.',                          NULL, NULL, N'Google Cloud',            'O', 'E'),
('6', '20508912456', N'SAP Perú S.R.L.',                                   NULL, NULL, N'SAP',                     'O', 'E'),
('6', '20510345678', N'Oracle del Perú S.R.L.',                            NULL, NULL, N'Oracle',                  'O', 'E');
GO
