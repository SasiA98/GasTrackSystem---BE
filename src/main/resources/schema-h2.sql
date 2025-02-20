
CREATE TABLE resource (
    id                              BIGINT AUTO_INCREMENT PRIMARY KEY,
    email                           VARCHAR(255) NOT NULL,
    name                            VARCHAR(255) NOT NULL,
    surname                         VARCHAR(255) NOT NULL,

    CONSTRAINT email_unique UNIQUE (email)
);


CREATE TABLE user (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT ,
    resource_id    BIGINT NOT NULL,
    password      VARCHAR(255) NOT NULL,
    status        VARCHAR(10) DEFAULT 'Enabled' CHECK (status IN ('Enabled', 'Disabled')),

    FOREIGN KEY (resource_id) REFERENCES resource(id)
);


CREATE TABLE resources_roles (
    resource_id    BIGINT NOT NULL,
    role           VARCHAR(15) NOT NULL CHECK (role IN ('ADMIN', 'PM', 'PSM', 'PSL', 'DTL', 'DUM', 'GDM', 'PSE', 'STAFF', 'CONSULTANT')),

    PRIMARY KEY (resource_id, role),
    FOREIGN KEY (resource_id) REFERENCES resource(id)
);


CREATE TABLE company (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,

    owner               VARCHAR(255),
    regione             VARCHAR(255),
    provincia           VARCHAR(255),
    citta               VARCHAR(255),
    address             VARCHAR(255),
    code                VARCHAR(255),
    first_email         VARCHAR(255) NOT NULL,
    second_email        VARCHAR(255),
    third_email         VARCHAR(255),
    phone               VARCHAR(15),
    directory           VARCHAR(255) NOT NULL,
    note                VARCHAR(255),

    CONSTRAINT company_name_unique UNIQUE (name)
);

CREATE TABLE licence (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    note                VARCHAR(255),
    directory           VARCHAR(255) NOT NULL,

    CONSTRAINT licence_name_unique UNIQUE (name)
);

CREATE TABLE company_licence (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    licence_id      BIGINT,
    company_id      BIGINT,
    email_sent      BOOLEAN DEFAULT FALSE,
    expiry_date     DATE,
    directory       VARCHAR(255) NOT NULL,

    FOREIGN KEY (company_id) REFERENCES company(id),
    FOREIGN KEY (licence_id) REFERENCES licence(id)
);
