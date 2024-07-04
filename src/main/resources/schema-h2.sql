
/* ----------------------------------     TABLES      ---------------------------------- */

CREATE TABLE unit (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    trigram       CHAR(3) NOT NULL,
    type          VARCHAR(15) NOT NULL CHECK (type IN ('Delivery Unit', 'Business Unit')),
    status        VARCHAR(10) DEFAULT 'Enabled' CHECK (status IN ('Enabled', 'Disabled')),

    CONSTRAINT trigram_unique UNIQUE (trigram)
);


CREATE TABLE resource (
    id                              BIGINT AUTO_INCREMENT PRIMARY KEY,
    email                           VARCHAR(255) NOT NULL,
    employee_id                     BIGINT NOT NULL,
    name                            VARCHAR(255) NOT NULL,
    surname                         VARCHAR(255) NOT NULL,
    birth_date                      DATE NOT NULL,
    hiring_date                     DATE NOT NULL,
    leave_date                      DATE,
    last_hourly_cost                DECIMAL(13, 2),
    last_hourly_cost_start_date     DATE,
    last_working_time_start_date    DATE NOT NULL,
    last_working_time               INT DEFAULT 40,
    unit_id                         BIGINT NOT NULL,
    site                            VARCHAR(255),
    location                        VARCHAR(255),
    trigram                         CHAR(3),
    ral                             VARCHAR(255),
    ral_start_date                  DATE,
    daily_allowance                 BIGINT,
    daily_allowance_start_date      DATE,
    ccnl_level                      VARCHAR(255),
    ccnl_level_start_date           DATE,
    note                            VARCHAR(255),

    FOREIGN KEY (unit_id) REFERENCES unit(id),
    CONSTRAINT last_working_time_domain CHECK (last_working_time > 0 AND last_working_time <= 40),
    CONSTRAINT last_hourly_cost_domain CHECK (last_hourly_cost >= 0),
    CONSTRAINT email_unique UNIQUE (email),
    CONSTRAINT employee_id_unique UNIQUE (employee_id),
    CONSTRAINT hiring_date_le_leave_date CHECK (hiring_date <= leave_date),
    CONSTRAINT last_hourly_cost_start_date_le_leave_date CHECK (last_hourly_cost_start_date <= leave_date)
);


CREATE TABLE project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    industry VARCHAR(50) CHECK (industry IN (
        'Railway', 'Automotive', 'Industrial', 'Aerospace and Defence', 'Life Science', 'Fintech', 'Power Utilities and Energy',
        'Home Appliance', 'Telecommunication and Media', 'Others', 'Teoresi Staff Activities'
    )),
    bm_trigram VARCHAR(255),
    presale_id BIGINT NOT NULL,
    dum_id BIGINT NOT NULL,
    unit_id BIGINT,
    status VARCHAR(50) CHECK (status IN (
        'Pre-Sale', 'To Start', 'In Progress', 'Closed', 'Lost', 'Cancelled'
    )),
    crm_code VARCHAR(255),
    project_id VARCHAR(255),
    ic BOOLEAN NOT NULL,
    start_date DATE NOT NULL,
    kom_date DATE,
    end_date DATE,
    estimated_end_date DATE NOT NULL,
    pre_sale_scheduled_end_date DATE NOT NULL,
    pre_sale_fixed_cost DECIMAL(13,2),
    current_fixed_cost DECIMAL(13,2),
    is_special BOOLEAN NOT NULL,
    note VARCHAR(255),
    project_type VARCHAR(255) CHECK (project_type IN (
        'Productive', 'Internal', 'Presales', 'External', 'Training', 'Interproject'
    ))
);


CREATE TABLE resources_roles (
    resource_id    BIGINT NOT NULL,
    role           VARCHAR(15) NOT NULL CHECK (role IN ('ADMIN', 'PM', 'PSM', 'PSL', 'DTL', 'DUM', 'GDM', 'PSE', 'STAFF', 'CONSULTANT')),

    PRIMARY KEY (resource_id, role),
    FOREIGN KEY (resource_id) REFERENCES resource(id)
);


CREATE TABLE resource_working_time (
    id                              BIGINT AUTO_INCREMENT PRIMARY KEY,
    resource_id                     BIGINT NOT NULL,
    working_time                    INT DEFAULT 40 CHECK (working_time >= 0 AND working_time <= 40),
    start_date                      DATE,

    UNIQUE (start_date, resource_id),
    FOREIGN KEY (resource_id) REFERENCES resource(id)
);


CREATE TABLE resource_hourly_cost (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    cost          DECIMAL(13, 2) CHECK (cost >= 0),
    start_date    DATE, -- constrained by the trigger
    resource_id   BIGINT NOT NULL,

    UNIQUE (start_date, resource_id),
    FOREIGN KEY (resource_id) REFERENCES resource(id)
);

CREATE TABLE resource_salary_details (
    id                              BIGINT AUTO_INCREMENT PRIMARY KEY,
    ral                             VARCHAR(255),
    ral_start_date                  DATE,
    daily_allowance                 BIGINT,
    daily_allowance_start_date      DATE,
    ccnl_level                      VARCHAR(255),
    ccnl_level_start_date           DATE,
    resource_id                     BIGINT NOT NULL,

    FOREIGN KEY (resource_id) REFERENCES resource(id)
);


CREATE TABLE user (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT ,
    password      VARCHAR(255) NOT NULL,
    resource_id   BIGINT UNIQUE NOT NULL,
    status        VARCHAR(10) DEFAULT 'Enabled' CHECK (status IN ('Enabled', 'Disabled')),

    FOREIGN KEY (resource_id) REFERENCES resource(id)
);


CREATE TABLE project_costs(  -- computed
    id                                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id                           BIGINT NOT NULL UNIQUE,
    act_hr_cost                DECIMAL(13, 2) CHECK (act_hr_cost >= 0),
    current_hr_cost            DECIMAL(13, 2) CHECK (current_hr_cost >= 0),
    pre_sale_hr_cost           DECIMAL(13, 2) CHECK (pre_sale_hr_cost >= 0),

    FOREIGN KEY (project_id) REFERENCES project(id)
);


CREATE TABLE allocation (
    id                          BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id                  BIGINT NOT NULL,
    resource_id                 BIGINT NOT NULL,
    role                        VARCHAR(15) CHECK (role IN ('PM', 'CONSULTANT', 'DTL')),
    start_date                  DATE NOT NULL,
    end_date                    DATE,
    is_real_commitment          BOOLEAN NOT NULL,
    daily_work_hours_quota      DECIMAL(13,5) CHECK (daily_work_hours_quota >= 0), -- computed
    hours                       INT,
    commitment_percentage       INT,

    CONSTRAINT allocation_hours_domain CHECK (hours >= 0),
    CONSTRAINT commitment_percentage_domain CHECK (commitment_percentage BETWEEN 1 AND 100),

    CONSTRAINT start_date_le_end_date CHECK (start_date <= end_date ),

    CONSTRAINT commitment_check
        CHECK ((is_real_commitment IS FALSE AND hours IS NOT NULL AND daily_work_hours_quota IS NOT NULL AND commitment_percentage IS NULL)
               OR (is_real_commitment IS TRUE AND hours IS NULL AND daily_work_hours_quota IS NULL AND commitment_percentage IS NOT NULL)),

    FOREIGN KEY (project_id) REFERENCES project(id),
    FOREIGN KEY (resource_id) REFERENCES resource(id)
);


CREATE TABLE time_sheet (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    resource_id         BIGINT NOT NULL,
    start_date          DATE NOT NULL, -- constrained by the trigger
    end_date            DATE NOT NULL,
    tot_work_hours      INT NOT NULL CHECK (tot_work_hours >= 0), -- constrained by the trigger

    FOREIGN KEY (resource_id) REFERENCES resource(id)
);


CREATE TABLE time_sheet_project (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id          BIGINT,
    time_sheet_id       BIGINT NOT NULL,
    allocation_id       BIGINT,
    start_date          DATE, -- computed
    end_date            DATE, -- computed
    hours               INT,
    pre_import_hours    INT CHECK (pre_import_hours >= 0),
    verified_hours      BOOLEAN DEFAULT NULL,
    cost                DECIMAL(13,2), -- computed
    daily_cost_quota    DECIMAL(13,5), -- computed
    note                VARCHAR(255),

    UNIQUE(project_id, time_sheet_id, allocation_id),

    CONSTRAINT hours_domain CHECK (hours >= 0),

    FOREIGN KEY (project_id) REFERENCES project(id),
    FOREIGN KEY (time_sheet_id) REFERENCES time_sheet(id),
    FOREIGN KEY (allocation_id) REFERENCES allocation(id) ON DELETE SET NULL
);


CREATE TABLE password_reset_token (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    token               VARCHAR(50),
    user_id             BIGINT,
    expiry_date         DATETIME,
    FOREIGN KEY (user_id) REFERENCES user(id)
);


CREATE TABLE operation_manager (
  id    BIGINT NOT NULL AUTO_INCREMENT,
  legal_entity  VARCHAR(255) NULL,
  industry VARCHAR(255) NULL,
  name  VARCHAR(255) NULL,
  trigram   CHAR(3) NULL,
  roles VARCHAR(255) NULL,
  reports_to    CHAR(3) NULL,
  PRIMARY KEY (id));


CREATE TABLE skill_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,

    CONSTRAINT name_unique2 UNIQUE (name)
);


CREATE TABLE skill (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    skill_group_id BIGINT NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,

    CONSTRAINT name_unique UNIQUE (name),
    FOREIGN KEY (skill_group_id) REFERENCES skill_group(id)
);


CREATE TABLE resource_skill (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    skill_id BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,
    rating INT NOT NULL,
    FOREIGN KEY (skill_id) REFERENCES skill(id),
    FOREIGN KEY (resource_id) REFERENCES resource(id)
);


CREATE TABLE company (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    phone         VARCHAR(15) NOT NULL
);


CREATE TABLE company_licence (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    licence_name    VARCHAR(255) NOT NULL,
    company_id      BIGINT,
    expiry_date     DATE NOT NULL,

    FOREIGN KEY (company_id) REFERENCES company(id)
);

