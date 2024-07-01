INSERT INTO unit (id, trigram, type, status)
VALUES
    (1, 'OPR', 'Delivery Unit', 'Enabled'),
    (2, 'D11', 'Delivery Unit', 'Enabled'),
    (3, 'D21', 'Delivery Unit', 'Enabled'),
    (4, 'D31', 'Deliveray Unit', 'Enabled'),
    (5, 'D41', 'Delivery Unit', 'Enabled'),
    (6, 'CD1', 'Delivery Unit', 'Enabled');


INSERT INTO resource (id, employee_id, email, name, surname, birth_date, hiring_date, last_working_time_start_date, last_working_time, leave_date, last_hourly_cost, last_hourly_cost_start_date, unit_id, site, location, trigram, ral, ral_start_date, daily_allowance, daily_allowance_start_date, ccnl_level, ccnl_level_start_date, note)
VALUES
  (1, 0000, 'admin@teoresigroup.com', 'Admin', 'Teoresi', '2000-1-1', '2000-1-1', '2000-1-1', 40, NULL,  0, '2023-11-1', 1, 'Napoli', 'Teoresi Napoli', 'ADM', '23000', '2000-1-1', 6, '2000-1-1', 'D2', '2000-1-1', 'Admin'),
  (2, 0001, 'ernesto.schiano@teoresigroup.com', 'Ernesto', 'Schiano', '1990-06-17', '2022-10-09', '2022-10-09', 40, NULL,  10.00, '2023-5-1', 3, 'Napoli', 'Teoresi Napoli', NULL, '23000', '2022-10-09', 6, '2022-10-09', 'D2', '2022-10-09', 'Risorsa Interna'),
  (4, 0002, 'simone.latino@teoresigroup.com', 'Simone', 'Latino', '1988-09-22', '2022-03-15', '2022-03-15', 40, NULL,   11.00, '2023-6-1', 3, 'Torino',  'Teoresi Torino', NULL,'23000', '2022-03-15', 6, '2022-03-15', 'D2', '2022-03-15', 'Risorsa Interna'),
  (3, 0003, 'salvatore.amodio@teoresigroup.com', 'Salvatore Davide', 'Amodio', '1995-12-10', '2024-01-04', '2024-01-04', 40, NULL,   10.00, '2024-01-04', 3, 'Napoli',  'Teoresi Napoli', NULL, '23000', '2024-01-04', NULL, NULL, 'D2', '2024-01-04', 'Risorsa Interna'),
  (5, 0004, 'carlo.riccio@teoresigroup.com', 'Carlo', 'Riccio', '1992-04-28', '2022-03-15', '2022-03-15', 40, NULL,  6.00, '2022-12-1', 6,  'Napoli',  'Teoresi Napoli', NULL,'23000', '2022-03-15', NULL, NULL, 'D2', '2022-03-15', 'Risorsa Interna'),
  (6, 1557, 'alessandro.serrapicaa@teoresigroup.com', 'Alessandro', 'Serrapica', '1987-07-03', '2022-03-15', '2022-03-15',40, NULL,   8.00, '2022-12-1', 3, 'Napoli',  'Teoresi Napoli', 'ASE', '23000', '2022-03-15', NULL, NULL, 'D2', '2022-03-15', 'Risorsa Interna'),
  (7, 2196, 'fortunato.polverino@teoresigroup.com', 'Fortunato', 'Polverino', '1993-05-14', '2023-03-15', '2023-03-15', 40, NULL,   24.00, '2023-12-1', 3, 'Napoli', 'Teoresi Napoli', NULL, '23000', '2023-03-15', NULL, NULL, 'D2', '2023-03-15', 'Risorsa Interna'),
  (8, 2272, 'giacomo.astarita@teoresigroup.com', 'Giacomo', 'Astarita', '1993-09-01', '2022-03-15', '2022-03-15', 40, NULL,  31.44, '2022-12-1', 3, 'Napoli', 'Teoresi Napoli', NULL, '23000', '2022-03-15', NULL, NULL, 'D2', '2022-03-15', 'Risorsa Interna'),
  (9, 0008, 'simone.fucci@teoresigroup.com', 'Simone', 'Fucci', '1997-08-11', '2023-03-15', '2023-03-15', 40, NULL,   21.00, '2023-12-1', 3, 'Napoli', 'Teoresi Napoli', NULL, '23000', '2023-03-15', NULL, NULL, 'D2', '2023-03-15', 'Risorsa Interna'),
  (10, 0009, 'maria.lettiero@teoresigroup.com', 'Maria', 'Lettiero', '1998-04-17', '2023-03-15', '2023-03-15', 40, NULL,   22.00, '2023-12-1', 4,  'Napoli', 'Teoresi Napoli', NULL, '23000', '2023-03-15', NULL, NULL, 'D2', '2023-03-15', 'Risorsa Interna'),
  (11, 0010, 'mustafa.abuzar@teoresigroup.com', 'Mustafa', 'Abuzar', '1995-07-03', '2023-03-15', '2023-03-15', 40, NULL,  20.00, '2023-12-1', 4,  'Torino Noether', 'Teoresi Torino', NULL, '23000', '2023-03-15', NULL, NULL, 'D2', '2023-03-15', 'Risorsa Interna'),
  (12, 0011, 'darvazi.milad.hemmat.nezhad@teoresigroup.com', 'Darvazi Milad', 'Hemmat Nezhad', '1995-03-11', '2023-03-15', '2023-03-15', 40, NULL,   17.00, '2023-12-1', 4, 'Torino', 'Teoresi Modena', NULL, '23000', '2023-03-15', NULL, NULL, 'D2', '2023-03-15', 'Risorsa Interna'),
  (13, 0012, 'claudio.rivieccioo@teoresigroup.com', 'Claudio', 'Rivieccio', '1989-01-01', '2023-03-15', '2023-03-15', 40, NULL,   9.00, '2023-12-1', 4,  'Napoli', 'Teoresi Napoli', 'CRI', '23000', '2023-03-15', NULL, NULL, 'D2', '2023-03-15', 'Risorsa Interna'),
  (14, 0013, 'vito.catauroo@teoresigroup.com', 'Vito', 'Catauro', '1985-03-02', '2023-03-15',  '2023-03-15', 40, NULL,  15.00, '2023-12-1', 2,  'Torino Noether', 'Teoresi Torino', 'VCA', '23000', '2023-03-15', NULL, NULL, 'D2', '2023-03-15', 'Risorsa Interna'),
  (15, 0014, 'francesco.marzianii@teoresigroup.com', 'Francesco', 'Marziani', '1987-04-25', '2023-03-15', '2023-03-15', 40,  NULL,  13.00, '2023-12-1', 5,  'Torino Ufficio', 'Teoresi Torino', 'FMA', '23000', '2023-03-15', NULL, NULL, 'D2', '2023-03-15', 'Risorsa Interna'),
  (16, 0015, 'angely.jazmin.oyola.suarez@teoresigroup.com', 'Angely Jazmin', 'Oyola Suarez', '1995-04-25', '2024-03-15', '2024-03-15', 40, NULL,   22.00, '2024-03-15', 2,  'Modena', 'Teoresi Modena', NULL, '23000', '2024-03-15', NULL, NULL, 'D2', '2024-03-15', 'Risorsa Interna'),
  (17, 0016, 'francesco.balladoree@teoresigroup.com', 'Francesco', 'Balladore', '1975-04-25', '2024-04-15', '2024-04-15', 40, NULL,  20.00, '2024-04-15', 1, 'Modena', 'Teoresi Modena', 'FBA', '23000', '2024-04-15', NULL, NULL, 'D2', '2024-04-15', 'Risorsa Interna'),
  (18, 0017, 'marco.ariola@teoresigroup.com', 'Marco', 'Ariola', '1975-04-25', '2022-04-15', '2022-04-15', 40, NULL,  18.00, '2022-07-01', 1, 'Napoli', 'Teoresi Napoli', NULL, '23000', '2022-04-15', NULL, NULL, 'D2', '2022-04-15', 'Risorsa Interna');


INSERT INTO resources_roles (resource_id, role) VALUES
    (1, 'ADMIN'),
    (2, 'CONSULTANT'),
    (3, 'DUM'),
    (4, 'CONSULTANT'),
    (5, 'PSL'),
    (5, 'PM'),
    (6, 'DUM'),
    (7, 'CONSULTANT'),
    (8, 'CONSULTANT'),
    (8, 'PM'),
    (9, 'CONSULTANT'),
    (10, 'CONSULTANT'),
    (11, 'CONSULTANT'),
    (12, 'CONSULTANT'),
    (13, 'DUM'),
    (14, 'DUM'),
    (15, 'DUM'),
    (16, 'CONSULTANT'),
    (17, 'GDM'),
    (18, 'PSE');



INSERT INTO user (id, resource_id, password, status) VALUES
    (1, 1, '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.','Enabled'),
    (3, 3, '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.','Enabled'),
    (5, 5, '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.','Enabled'),
    (6, 6, '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.','Enabled'),
    (8, 8, '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.','Enabled'),
    (17, 17, '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.','Enabled'),
    (18, 18, '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.','Enabled');


INSERT INTO resource_hourly_cost (cost, start_date, resource_id)
VALUES
    (30.00, '2024-01-01', 2),
    (10.00, '2023-11-09', 2),
    (55.00, '2022-03-15', 3),
    (45.00, '2024-07-04', 4),
    (60.00, '2022-06-1', 5),
    (60.00, '2022-6-1', 6),
    (50.00, '2022-8-1', 6),
    (30.00, '2022-8-1', 7),
    (50.00, '2023-10-1', 11);


INSERT INTO resource_salary_details (ral, ral_start_date, daily_allowance, daily_allowance_start_date, ccnl_level, ccnl_level_start_date, resource_id)
VALUES
    ('30000', '2024-01-1', 6, '2024-01-1', 'C3', '2024-01-1', 1),
    ('30000', '2024-01-1', 6, '2024-01-1', 'C3', '2024-01-1', 2),
    ('30000', '2024-01-1', 6, '2024-01-1', 'C3', '2024-01-1', 3);




-- SPECIAL PROJECTS USED INTO TIMESHEET_PROJECT_SERVICE. DO NOT TOUCH !
INSERT INTO project (id, name, industry, presale_id, dum_id, bm_trigram, status, crm_code, project_id, IC, start_date, estimated_end_date, pre_sale_scheduled_end_date, KoM_date, end_date, is_special, project_type, Note)
VALUES
  (1, 'Time Off', 'Others', 1, 1, NULL, 'In Progress', '', '1', 0, '2000-01-01', '2000-01-02', '2000-01-02', '2000-01-01', NULL, 1, 'Internal', 'Time Off'),
  (2, 'Holidays', 'Others', 1, 1, NULL, 'In Progress', '', '2', 0, '2000-01-01', '2000-01-02', '2000-01-02', '2000-01-01', NULL, 1, 'Internal', 'Holidays');


-- NORMAL PROJECTS
INSERT INTO project (id, unit_id, name, industry, presale_id, dum_id, bm_trigram, status, crm_code, project_id, IC, start_date, estimated_end_date, pre_sale_scheduled_end_date, KoM_date, end_date, is_special, project_type, Note)
VALUES
  (3, 2, '[TEO] Delivery - STAFF_Resource Allocation Tool', 'Industrial', 5, 6, 'FBM', 'Pre-Sale', 'CRM001', "123", 0, '2023-10-01', '2024-06-30', '2024-06-30', '2023-10-01', NULL, 0, 'Internal', 'Progetto1'),
  (4, 3, '[RW] Alstom - DEV_TRAXX_SA TIA SW ENHANCEMENTS', 'Railway',  18, 13, 'FBM', 'Pre-Sale', 'CRM002', "124", 0, '2024-01-01', '2024-06-30', '2024-06-30', '2024-01-01', NULL, 0, 'Internal', 'Progetto2'),
  (5, 4, '[RW] Alstom - TEST_SBB CCUT HLFG Test And ATP System Test_ASV_6.0.1.0', 'Railway',  18, 14, 'FBM', 'Pre-Sale', 'CRM003', "125", 0, '2024-03-01', '2024-07-20', '2024-07-20', '2024-03-01', NULL, 0, 'Internal', 'Progetto3'),
  (6, 2, '[RW] Sitav - DEV_Orient Express System Engineering', 'Home Appliance',  18, 15, 'FBM', 'Pre-Sale', 'CRM004', "126", 0, '2024-01-11', '2024-05-02', '2024-05-02', '2024-01-11', NULL, 0, 'Internal', 'Progetto4'),
  (7, 3, '[HA] B/S/H/ - DEV_SyMaNA Matter Integration - EXT3', 'Home Appliance',  18, 15, 'FBM', 'Pre-Sale', 'BOH', "2023010231", 0, '2024-01-02', '2024-06-30', '2024-06-30', '2024-01-02', NULL, 0, 'Presales', 'Progetto5');



INSERT INTO allocation (id, project_id, resource_id, is_real_commitment, hours, commitment_percentage, daily_work_hours_quota, role, start_date, end_date)
VALUES
    (7, 4, 8, 0, 155, NULL, 4.2 , 'PM', '2024-04-19', '2024-06-30'),
    (10, 4, 7, 0, 10, NULL, 1, 'CONSULTANT', '2024-04-19', '2024-05-30'),
    (8, 4, 7, 0, 70, NULL, 2.5 , 'CONSULTANT', '2024-01-31', '2024-04-18'),
    (4, 4, 11, 0, 50, NULL, 1, 'CONSULTANT', '2024-01-01', '2024-01-20'),

    (5, 5, 11, 1, NULL, 50, NULL, 'CONSULTANT', '2024-03-01', '2024-07-20'),
    (9, 5, 5, 0, 11, NULL, 8, 'PM', '2024-04-19', '2024-06-30'),

    (1, 6, 8, 0, 120, NULL, 4, 'PM', '2024-01-12', '2024-05-02'),
    (6, 6, 11, 0, 444, NULL, 7.5, 'CONSULTANT', '2024-01-18', '2024-01-24'),

    (11, 7, 8, 0, 200, NULL, 4, 'PM', '2024-01-20', '2024-06-30');



INSERT INTO operation_manager (id, legal_entity, industry, name, trigram, roles, reports_to) VALUES
(1, 'Teoresi S.p.A.', 'Telecommunication and Media', 'Alessandro Esposito', 'AES', 'BM', 'DFI'),
(2, 'Teoresi SA', 'Railway', 'Luigi Luciano', 'LLU', 'BM', '?'),
(3, 'Teoresi S.p.A.', '*', 'Francesco Marziani', 'FRM', 'DUM', 'FBA'),
(4, 'Teoresi S.p.A.', 'Automotive', 'Andrea Dimiccoli', 'AND', 'BM', 'LSC'),
(5, 'Teoresi S.p.A.', 'Automotive', 'Christian Beretta', 'CBE', 'IM', 'NGE'),
(6, 'Teoresi S.p.A.', '*', 'Claudio D’avino', 'CDA', 'PSM', 'FBA'),
(7, 'Teoresi S.p.A.', '*', 'Emiliano Santillo', 'ESA', 'PSM', 'FBA'),
(8, 'Teoresi GmbH', '*', 'Aydogan Aydindag', 'AAY', 'BM', 'DCO'),
(9, 'Teoresi GmbH', '*', 'Daniel Comarella', 'DCO', 'COO', '*'),
(10, 'Teoresi S.p.A.', '*', 'Claudio Rivieccio', 'CRI', 'DUM', 'FBA'),
(11, 'Teoresi S.p.A.', '*', 'Davide Ferri', 'DFE', 'COO Inc. / COO SA / Global Business Development Director', 'DTO'),
(12, 'Teoresi S.p.A.', '*', 'Diego Tornese', 'DTO', 'COO Global / COO S.p.A.', '*'),
(13, 'Teoresi S.p.A.', '*', 'Alessandro Serrapica', 'ALS', 'DUM', 'FBA'),
(14, 'Teoresi S.p.A.', 'Telecommunication and Media', 'Donato Fiorella', 'DFI', 'IM', 'UAL'),
(15, 'Teoresi S.p.A.', 'Automotive', 'Klaudia PajaK', 'KPA', 'BM', '?'),
(16, 'Teoresi SA', '*', 'Davide La Conte', 'DLA', 'CIM', '*'),
(17, 'Teoresi S.p.A.', 'Automotive', 'Domenico Motta', 'DMO', 'IM', 'NGE'),
(18, 'Teoresi S.p.A.', '*', 'Francesco Balladore', 'FBA', 'CDM/GDM', 'DTO'),
(19, 'Teoresi S.p.A.', 'Home Appliance Life Science', 'Federico Cursano', 'FEC', 'IM', 'UAL'),
(20, 'Teoresi S.p.A.', 'Automotive', 'Francesco Maria Testi', 'FTE', 'BM', '?'),
(21, 'Teoresi S.p.A.', 'Automotive', 'Ivan Giurato', 'IGI', 'BM', '?'),
(22, 'Teoresi Inc.', '?', 'Gianluca Buonincontri', 'GBU', 'BM', '?'),
(23, 'Teoresi Inc.', 'Automotive', 'Gaia D’Antoni', 'GDA', 'BM', '?'),
(24, 'Teoresi S.p.A.', '*', 'Gianluca Cerio', 'GCE', 'Global Technical Development Manager', 'DFE'),
(25, 'Teoresi S.p.A.', '*', 'Gianluca Toscano', 'GTO', 'Global Solution Manager', 'DTO'),
(26, 'Teoresi S.p.A.', 'Industrial', 'Giovanni Savoca', 'GIS', 'IM', 'GLO'),
(27, 'Teoresi S.p.A.', 'Home Appliance', 'Giacomo Sinigaglia', 'GSI', 'BM', 'FEC'),
(28, 'Teoresi S.p.A.', 'Automotive', 'Paola Donato', 'PDO', 'BM', '?'),
(29, 'Teoresi S.p.A.', 'Industrial', 'Giuseppe Lo Giudice', 'GLO', 'GIM/CIM', 'DTO'),
(30, 'Teoresi S.p.A.', 'Automotive', 'Giuseppe Verrina', 'GVE', 'IM', 'NGE'),
(31, 'Teoresi S.p.A.', 'Automotive', 'Klaudia Pajak', 'KPA', 'BM', '?'),
(32, 'Teoresi S.p.A.', 'Industrial', 'Ivan Zizzo', 'IZI', 'BM', '?'),
(33, 'Teoresi S.p.A.', '*', 'Luca Broglio', 'LBR', 'Operations quality and Assurance Manager', 'DTO'),
(34, 'Teoresi S.p.A.', 'Railway', 'Luca Guaia', 'LGU', 'BM', 'RPI'),
(35, 'Teoresi S.p.A.', '*', 'Luca Monfrini', 'LMO', 'Business Development Digital Transformation', 'DTO'),
(36, 'Teoresi S.p.A.', 'Automotive', 'Alfio Spampinato', 'ASP', 'BM', '?'),
(37, 'Teoresi S.p.A.', 'Aerospace and Defense', 'Lorenzo Platania', 'LPL', 'BM', '?'),
(38, 'Teoresi S.p.A.', 'Aerospace and Defense', 'Antonio Zagaria', 'AZA', 'BM', '?'),
(39, 'Teoresi S.p.A.', 'Industrial', 'Francesco Del Vecchio', 'FDV', 'BM', 'RMO'),
(40, 'Teoresi S.p.A.', 'Automotive', 'Ludovico Scarpa', 'LSC', 'IM', 'NGE'),
(41, 'Teoresi S.p.A.', 'Railway', 'Marco Albano', 'MAL', 'GIM', 'DTO'),
(42, 'Teoresi S.p.A.', 'Aerospace and Defense Industrial', 'Marco Di Falco', 'MDI', 'BM', '?'),
(43, 'Teoresi S.p.A.', 'Automotive', 'Michele Lauricella', 'MLA', 'BM', '?'),
(44, 'Teoresi GmbH', 'Railway', 'Marco Nozzetti', 'MN4', 'BM', 'DCO'),
(45, 'Teoresi Inc.', 'Automotive', 'Marcella Sanapo', 'MSA', 'BM', '?'),
(46, 'Teoresi S.p.A.', 'Automotive', 'Natale Gentile', 'NGE', 'GIM/CIM', 'DTO'),
(47, 'Teoresi Inc.', 'Automotive', 'Pierluca Laudani', 'PLA', 'BM', '?'),
(48, 'Teoresi S.p.A.', 'Automotive', 'Gianluca Niccoli', 'NGI', 'BM', '?'),
(49, 'Teoresi S.p.A.', 'Automotive', 'Patrizia Rizzi', 'PRI', 'IM', 'NGE'),
(50, 'Teoresi S.p.A.', 'Industrial', 'Roberto Moncada', 'ROM', 'IM', 'GLO'),
(51, 'Teoresi S.p.A.', '*', 'Silvano Tieghi', 'STI', 'Global Business Development Product', 'DTO'),
(52, 'Teoresi S.p.A.', '*', 'Rosa Maria Molteni', '?', 'Global Deal Development', 'DFE'),
(53, 'Teoresi S.p.A.', 'Railway', 'Roberto Picariello', 'RPI', 'IM', 'MAL'),
(54, 'Teoresi S.p.A.', 'HPI', 'Ugo Alberti', 'UAL', 'CIM', 'DTO'),
(55, 'Teoresi GmbH', '?', 'Dennis Claudio Tomasi', 'DET', 'BM', 'DCO'),
(56, 'Teoresi S.p.A.', 'Aerospace and Defense', 'Umberto Loreto', 'ULO', 'IM', 'GLO'),
(57, 'Teoresi S.p.A.', '*', 'Vito Catauro', 'VCA', 'DUM', 'FBA'),
(58, 'Teoresi S.p.A.', 'Home Appliance', 'Vincenzo Scilingo', 'VSC', 'BM', 'FEC'),
(59, 'Teoresi S.p.A.', 'Fintech', 'Valentina Raspanti', 'VRA', 'BM', 'DFI'),
(60, 'Teoresi GmbH', 'Automotive', 'Yannick Affogbolo', 'YAF', 'BM', 'DCO'),
(61, 'Teoresi Inc.', 'Railway', 'Zachary W. Duncan', 'ZDU', 'BM', '?'),
(62, 'Teoresi S.p.A.', 'Automotive', 'Angelo Terlizzi', 'ATE', 'BM', 'LSC'),
(63, 'Teoresi S.p.A.', 'Railway', 'Federico Pagano', 'FEP', 'BM', 'RPI');

