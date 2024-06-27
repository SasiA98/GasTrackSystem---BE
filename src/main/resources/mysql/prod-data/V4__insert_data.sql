INSERT INTO unit (id, trigram, type, status)
VALUES
    (1, 'OPR', 'Delivery Unit', 'Enabled'),
    (2, 'D11', 'Delivery Unit', 'Enabled'),
    (3, 'D21', 'Delivery Unit', 'Enabled'),
    (4, 'D31', 'Delivery Unit', 'Enabled'),
    (5, 'D41', 'Delivery Unit', 'Enabled'),
    (6, 'CD1', 'Delivery Unit', 'Enabled');


INSERT INTO resource (id, employee_id, email, name, surname, birth_date, hiring_date, last_working_time_start_date, last_working_time, leave_date, last_hourly_cost, last_hourly_cost_start_date, unit_id, site, location, trigram, ral, ral_start_date, daily_allowance, daily_allowance_start_date, ccnl_level, ccnl_level_start_date, note)
VALUES
  (1, 0000, 'admin@teoresigroup.com', 'Admin', 'Teoresi', '2000-1-1', '2000-1-1', '2000-1-1', 40, NULL,  0, '2023-11-1', 1, 'Napoli', 'Teoresi Napoli', 'ADM', '23000', '2000-1-1', 6, '2000-1-1', 'D2', '2000-1-1', 'Admin');

INSERT INTO resources_roles (resource_id, role) VALUES
    (1, 'ADMIN');


INSERT INTO user (id, resource_id, password, status) VALUES
    (1, 1, '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.','Enabled');


-- SPECIAL PROJECTS USED INTO TIMESHEET_PROJECT_SERVICE. DO NOT TOUCH !
INSERT INTO project (id, name, industry, presale_id, dum_id, bm_trigram, status, crm_code, project_id, IC, start_date, estimated_end_date, pre_sale_scheduled_end_date, KoM_date, end_date, is_special, project_type, Note)
VALUES
  (1, 'Time Off', 'Others', 1, 1, NULL, 'In Progress', '', '1', 0, '2000-01-01', '2000-01-02', '2000-01-02', '2000-01-01', NULL, 1, 'Internal', 'Time Off'),
  (2, 'Holidays', 'Others', 1, 1, NULL, 'In Progress', '', '2', 0, '2000-01-01', '2000-01-02', '2000-01-02', '2000-01-01', NULL, 1, 'Internal', 'Holidays');


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

