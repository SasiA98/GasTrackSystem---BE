
INSERT INTO resource (id, email, name, surname)
VALUES
  (1, 'fraquinta@gmail.com', 'Francesco', 'Quintaluce');

INSERT INTO resources_roles (resource_id, role) VALUES
    (1, 'ADMIN');


INSERT INTO user (id, resource_id, password, status) VALUES
    (1, 1, '$2b$12$d9Wfwl1dXntiX3WDJ79bz.O/tdkH5XPv/tSJxzvHk2wU2bq9hludi','Enabled');


/*

INSERT INTO company (name, first_email, phone, documents_directory) VALUES
    ('Tech Innovations Inc.', 'sasygoll@gmail.com', '123-456-7890', 'tii'),
    ('Green Energy Solutions', 'amodio.bernardo@gmail.com', '987-654-3210', 'ges'),
    ('HealthCare Partners Ltd.', 'sasi.amodio98@gmail.com', '456-789-1234', 'hpl'),
    ('Urban Development Co.', 'sasygoll@gmail.com', '321-654-9870', 'udc'),
    ('Global Finance Group', 'sasi.amodio98@gmail.com', '789-123-4567', 'gpg');


INSERT INTO licence (note, name) VALUES
    ('LIC001', 'Software Development Licence'),
    ('LIC002', 'Environmental Compliance Licence'),
    ('LIC003', 'Medical Practice Licence'),
    ('LIC004', 'Construction Permit'),
    ('LIC005', 'Financial Advisory Licence');

INSERT INTO company_licence (company_id, licence_id, expiry_date) VALUES
    (1, 1, '2023-01-01'),
    (2, 2, '2023-06-15'),
    (3, 3, '2025-09-01'),
    (4, 1, '2024-03-20'),
    (5, 4, '2024-01-01');

*/