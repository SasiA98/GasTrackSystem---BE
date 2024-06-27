
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
    id                              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                            VARCHAR(255) NOT NULL,
    industry                        VARCHAR(50) CHECK (industry IN ('Railway', 'Automotive', 'Industrial', 'Aerospace and Defence', 'Life Science', 'Fintech', 'Power Utilities and Energy', 'Home Appliance', 'Telecommunication and Media', 'Others', 'Teoresi Staff Activities')),
    bm_trigram                      VARCHAR(255),
    presale_id                      BIGINT NOT NULL,
    dum_id                          BIGINT NOT NULL,
    unit_id                         BIGINT,
    status                          VARCHAR(50) CHECK (status IN ('Pre-Sale', 'To Start', 'In Progress', 'Closed', 'Lost', 'Cancelled')),
    crm_code                        VARCHAR(255),
    project_id                      VARCHAR(255),
    ic                              BOOLEAN NOT NULL,
    start_date                      DATE NOT NULL,
    kom_date                        DATE,

    end_date                        DATE,
    estimated_end_date              DATE NOT NULL,
    pre_sale_scheduled_end_date     DATE NOT NULL,

    pre_sale_fixed_cost             DECIMAL(13,2),
    current_fixed_cost              DECIMAL(13,2),

    is_special                      BOOLEAN NOT NULL,
    note                            VARCHAR(255),
    project_type                    VARCHAR(255) CHECK (project_type IN ("Productive", "Internal", "Presales", "External", "Training", "Interproject")) NOT NULL,

    FOREIGN KEY (presale_id) REFERENCES resource(id),
    FOREIGN KEY (dum_id) REFERENCES resource(id),
    FOREIGN KEY (unit_id) REFERENCES unit(id),

    CONSTRAINT project_id_unique UNIQUE (project_id),

    CONSTRAINT pre_sale_fixed_cost_domain CHECK (pre_sale_fixed_cost >= 0),
    CONSTRAINT current_fixed_cost_domain CHECK (current_fixed_cost >= 0),

    CONSTRAINT dates_check
        CHECK (kom_date IS NULL OR (kom_date <= estimated_end_date)),

    CONSTRAINT start_date_le_pre_sale_scheduled_end_date CHECK (start_date <= pre_sale_scheduled_end_date ),
    CONSTRAINT start_date_le_estimated_end_date CHECK (start_date <= estimated_end_date ),
    CONSTRAINT kom_date_le_end_date CHECK (kom_date <= end_date ),
    CONSTRAINT project_start_date_le_end_date CHECK (start_date <= end_date )
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
    act_hr_cost                DECIMAL(13, 2) CHECK (act_hr_cost >= 0) default 0,
    current_hr_cost            DECIMAL(13, 2) CHECK (current_hr_cost >= 0) default 0,
    pre_sale_hr_cost           DECIMAL(13, 2) CHECK (pre_sale_hr_cost >= 0) default 0,

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
    hours               INT DEFAULT 0,
    pre_import_hours    INT CHECK (pre_import_hours >= 0) DEFAULT 0,
    verified_hours      BOOLEAN DEFAULT NULL,
    cost                DECIMAL(13,2) DEFAULT 0, -- computed
    daily_cost_quota    DECIMAL(13,5) DEFAULT 0, -- computed
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

    CONSTRAINT name_unique UNIQUE (name)
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



/* ----------------------------------      COMPUTED TABLES      ---------------------------------- */


CREATE TABLE daily_resource_load (
    id  BIGINT AUTO_INCREMENT PRIMARY KEY,
	resource_id BIGINT,
	year INT,
	month INT,
	n_week INT,
	n_day INT,
	pre_allocation BOOLEAN NOT NULL,
	is_holiday INT NOT NULL DEFAULT 0,
	commitment_pct INT, -- for real commitment
	hours_pct DECIMAL(13, 5), -- for sale commitment

    UNIQUE(resource_id, pre_allocation, year, n_day),
    FOREIGN KEY (resource_id) REFERENCES resource(id)
);


create table weekly_resource_load (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	resource_id BIGINT,
	unit_id BIGINT,
	year INT,
	month INT,
	n_week INT,
	pre_allocation BOOLEAN NOT NULL,
	mean_commitment_pct INT DEFAULT 0,
	mean_hours_pct INT DEFAULT 0,

    UNIQUE(resource_id, year, month, n_week, pre_allocation),
    FOREIGN KEY (resource_id) REFERENCES resource(id),
    FOREIGN KEY (unit_id) REFERENCES unit(id)
);


CREATE TABLE daily_project_cost (
    id  BIGINT AUTO_INCREMENT PRIMARY KEY,
	project_id BIGINT,
    status  VARCHAR(50) CHECK (status IN ('Pre-Sale', 'To Start', 'In Progress', 'Closed', 'Lost', 'Cancelled')),
	year INT,
	month INT,
	yearweek INT,
	yearmonth INT,
	n_week INT,
	n_day INT,
    est_cost        DECIMAL(13,5) DEFAULT 0, -- for estimated cost
	est_cost_pct    DECIMAL(13,5) DEFAULT 0,
	act_cost        DECIMAL(13,5) DEFAULT 0, -- for actual cost
	act_cost_pct    DECIMAL(13,5) DEFAULT 0,

    UNIQUE(project_id, year, n_day),
    FOREIGN KEY (project_id) REFERENCES project(id)
);


create table weekly_project_cost (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	project_id BIGINT,
	status  VARCHAR(50) CHECK (status IN ('Pre-Sale', 'To Start', 'In Progress', 'Closed', 'Lost', 'Cancelled')),
	unit_id BIGINT,
	year INT,
	month INT,
	n_week INT,
	cumulative_est_cost DECIMAL(13,5) DEFAULT 0 , -- for estimated cost
	cumulative_est_cost_pct DECIMAL(13,5) DEFAULT 0 ,
	cumulative_act_cost DECIMAL(13,5) DEFAULT 0 , -- for actual cost
	cumulative_act_cost_pct DECIMAL(13,5) DEFAULT 0,

    UNIQUE(project_id, year, month, n_week),
    FOREIGN KEY (project_id) REFERENCES project(id),
    FOREIGN KEY (unit_id) REFERENCES unit(id)
);



----------------------------------      FUNCTIONS      ----------------------------------


DELIMITER //

DROP FUNCTION IF EXISTS compute_most_up_to_date_resource_hourly_cost //
CREATE FUNCTION compute_most_up_to_date_resource_hourly_cost(res_id BIGINT, d DATE) RETURNS DECIMAL(13,5) DETERMINISTIC
BEGIN
	DECLARE resource_hc DECIMAL(13,5);

	SELECT cost
	INTO resource_hc
	FROM resource_hourly_cost
	WHERE resource_id = res_id AND start_date = (SELECT MAX(start_date)
												  FROM resource_hourly_cost
												  WHERE resource_id = res_id
														AND start_date <= d);
	RETURN resource_hc;
END//


DELIMITER //

DROP FUNCTION IF EXISTS compute_most_up_to_date_resource_daily_working_time //
CREATE FUNCTION compute_most_up_to_date_resource_daily_working_time(res_id BIGINT, d DATE) RETURNS DECIMAL(13,5) DETERMINISTIC
BEGIN
	DECLARE resource_wt DECIMAL(13,5);

	SELECT working_time
	INTO resource_wt
	FROM resource_working_time
	WHERE resource_id = res_id AND start_date = (SELECT MAX(start_date)
												  FROM resource_working_time
												  WHERE resource_id = res_id
														AND start_date <= d);
	RETURN (resource_wt / 5);
END//


DELIMITER //

DROP FUNCTION IF EXISTS compute_allocation_cost_per_day //
CREATE FUNCTION compute_allocation_cost_per_day(allocation_id BIGINT, date DATE) RETURNS DECIMAL(13,5) DETERMINISTIC
BEGIN
	DECLARE cost DECIMAL(13,5);
	DECLARE a_resource_id BIGINT;
    DECLARE a_daily_work_hours_quota DECIMAL(13,5);

    SELECT daily_work_hours_quota, resource_id
    INTO a_daily_work_hours_quota, a_resource_id
    FROM allocation
    WHERE id = allocation_id;

	SET cost = a_daily_work_hours_quota * compute_most_up_to_date_resource_hourly_cost(a_resource_id, date);

    RETURN cost;
END//


----------------------------------      PROCEDURES      ----------------------------------


DELIMITER //

DROP PROCEDURE IF EXISTS init_resource_load //
CREATE PROCEDURE init_resource_load (IN year INT, IN resource_id BIGINT)
BEGIN
    DECLARE tmp_date DATE;
    DECLARE end_date DATE;
    DECLARE res_hiring_date DATE;
    DECLARE res_leave_date DATE;

	SELECT hiring_date, leave_date
    INTO res_hiring_date, res_leave_date
    FROM resource
    WHERE id = resource_id;

	SET tmp_date = STR_TO_DATE(CONCAT(year, '-01-01'), '%Y-%m-%d');
	SET end_date = STR_TO_DATE(CONCAT(year, '-12-31'), '%Y-%m-%d');

    DELETE FROM daily_resource_load d WHERE d.resource_id = resource_id and d.year = year;

	WHILE tmp_date <= end_date DO

		IF (DAYOFWEEK(tmp_date) <> 1 AND DAYOFWEEK(tmp_date) <> 7)  AND
		   tmp_date >= res_hiring_date AND
		   (res_leave_date IS NULL OR tmp_date <= res_leave_date) THEN

			INSERT INTO daily_resource_load (resource_id, year, month, n_week, n_day, pre_allocation, commitment_pct, hours_pct)
			SELECT resource_id, YEAR(tmp_date), MONTH(tmp_date), weekofyear(tmp_date), dayofyear(tmp_date), 0, 0, 0.0
			FROM dual;

			INSERT INTO daily_resource_load (resource_id, year, month, n_week, n_day, pre_allocation, commitment_pct, hours_pct)
			SELECT resource_id, YEAR(tmp_date), MONTH(tmp_date), weekofyear(tmp_date), dayofyear(tmp_date), 1, 0, 0.0
			FROM dual;

		END IF;

		SET tmp_date = DATE_ADD(tmp_date, INTERVAL 1 DAY);
	END WHILE;

END//


DELIMITER //

DROP PROCEDURE IF EXISTS refresh_resource_load //
CREATE PROCEDURE refresh_resource_load (IN year INT, IN resource_id BIGINT)
BEGIN

    UPDATE daily_resource_load rl
    SET hours_pct = COALESCE(
        ( SELECT SUM((a.daily_work_hours_quota * 100 / compute_most_up_to_date_resource_daily_working_time(resource_id, MAKEDATE(rl.year, rl.n_day))))
            FROM allocation a join project p on a.project_id = p.id
            WHERE a.resource_id = rl.resource_id
                AND a.is_real_commitment IS FALSE
                AND a.start_date <= MAKEDATE(rl.year, rl.n_day)
                AND MAKEDATE(rl.year, rl.n_day) <= a.end_date
                AND p.status <> "Lost" AND p.status <> "Cancelled"

        ), 0),

        commitment_pct = COALESCE(
        (
            SELECT SUM(a.commitment_percentage)
            FROM allocation a join project p on a.project_id = p.id
            WHERE a.resource_id = rl.resource_id
                AND a.is_real_commitment IS TRUE
                AND a.start_date <= MAKEDATE(rl.year, rl.n_day)
                AND MAKEDATE(rl.year, rl.n_day) <= a.end_date
                AND p.status <> "Lost" AND p.status <> "Cancelled"

        ), 0)
    WHERE rl.resource_id = resource_id AND rl.year = year
        AND rl.is_holiday = 0 AND pre_allocation = 1;


    UPDATE daily_resource_load rl
    SET hours_pct = COALESCE(
        ( SELECT SUM((a.daily_work_hours_quota * 100 / compute_most_up_to_date_resource_daily_working_time(resource_id, MAKEDATE(rl.year, rl.n_day))))
            FROM allocation a join project p on a.project_id = p.id
            WHERE a.resource_id = rl.resource_id
                AND a.is_real_commitment IS FALSE
                AND a.start_date <= MAKEDATE(rl.year, rl.n_day)
                AND MAKEDATE(rl.year, rl.n_day) <= a.end_date
                AND p.status <> "Pre-Sale" AND p.status <> "To Start" AND p.status <> "Lost" AND p.status <> "Cancelled"
        ), 0),

        commitment_pct = COALESCE(
        (
            SELECT SUM(a.commitment_percentage)
            FROM allocation a join project p on a.project_id = p.id
            WHERE a.resource_id = rl.resource_id
                AND a.is_real_commitment IS TRUE
                AND a.start_date <= MAKEDATE(rl.year, rl.n_day)
                AND MAKEDATE(rl.year, rl.n_day) <= a.end_date
                AND p.status <> "Pre-Sale" AND p.status <> "To Start" AND p.status <> "Lost" AND p.status <> "Cancelled"
        ), 0)
    WHERE rl.resource_id = resource_id AND rl.year = year
        AND rl.is_holiday = 0 AND pre_allocation = 0;

END//



DELIMITER //

DROP PROCEDURE IF EXISTS refresh_weekly_resource_load //
CREATE PROCEDURE refresh_weekly_resource_load(IN year INT, IN resource_id BIGINT)
BEGIN

    DELETE FROM weekly_resource_load d WHERE d.resource_id = resource_id and d.year = year;

	INSERT INTO weekly_resource_load (unit_id, resource_id, year, month, n_week, pre_allocation, mean_commitment_pct, mean_hours_pct)
    SELECT r.unit_id AS unit_id, d.resource_id AS resource_id, d.year AS year,
            d.month AS month, d.n_week AS n_week, d.pre_allocation AS pre_allocation, round(AVG(d.commitment_pct)) AS mean_commitment_pct, round(AVG(d.hours_pct)) AS mean_hours_pct
    FROM daily_resource_load d join resource r on d.resource_id = r.id
    WHERE d.resource_id = resource_id and d.year = year
    GROUP BY r.unit_id, d.resource_id, d.year, d.month, d.n_week, d.pre_allocation;

END//



DELIMITER //

DROP PROCEDURE IF EXISTS init_project_cost //
CREATE PROCEDURE init_project_cost (IN project_id BIGINT)
BEGIN
    DECLARE tmp_date DATE;
    DECLARE end_date DATE;
    DECLARE p_kom_date DATE;
    DECLARE p_status VARCHAR(50);

	SELECT start_date, kom_date, estimated_end_date, status
    INTO tmp_date, p_kom_date, end_date, p_status
    FROM project
    WHERE id = project_id;

    IF p_kom_date IS NOT NULL THEN
        SET tmp_date = p_kom_date;
    END IF;

    DELETE FROM daily_project_cost d WHERE d.project_id = project_id;

	WHILE tmp_date <= end_date DO

		IF (DAYOFWEEK(tmp_date) <> 1 AND DAYOFWEEK(tmp_date) <> 7) THEN
			INSERT INTO daily_project_cost (project_id, status, year, yearweek, yearmonth, month, n_week, n_day)
			SELECT project_id, p_status, YEAR(tmp_date), yearweek(tmp_date), concat(YEAR(tmp_date),  LPAD(MONTH(tmp_date), 2, '0')), month(tmp_date), weekofyear(tmp_date), dayofyear(tmp_date)
			FROM dual;

		END IF;

		SET tmp_date = DATE_ADD(tmp_date, INTERVAL 1 DAY);
	END WHILE;
END//



DELIMITER //

DROP PROCEDURE IF EXISTS refresh_daily_project_cost //
CREATE PROCEDURE refresh_daily_project_cost (IN project_id BIGINT)
BEGIN
    DECLARE tmp_date DATE;
    DECLARE end_date DATE;
	DECLARE p_est_cost DECIMAL(13,5);
    DECLARE p_kom_date DATE;

	SELECT start_date, kom_date, estimated_end_date
    INTO tmp_date, p_kom_date, end_date
    FROM project
    WHERE id = project_id;

    IF p_kom_date IS NOT NULL THEN
        SET tmp_date = p_kom_date;
    END IF;

	UPDATE daily_project_cost rl
    SET est_cost = COALESCE(
        (  SELECT SUM(compute_allocation_cost_per_day(a.id, MAKEDATE(rl.year, rl.n_day)))
            FROM allocation a
            WHERE a.project_id = rl.project_id
                AND a.is_real_commitment IS FALSE
                AND a.start_date <= MAKEDATE(rl.year, rl.n_day)
                AND MAKEDATE(rl.year, rl.n_day) <= a.end_date
        ), 0)

    WHERE rl.project_id = project_id;

	SELECT SUM(w.est_cost)
	INTO p_est_cost
	FROM daily_project_cost w
	WHERE w.project_id = project_id;

    IF p_est_cost > 0 THEN
        UPDATE daily_project_cost rl
        SET act_cost_pct = COALESCE(
            (  SELECT CAST((SUM(t.daily_cost_quota) * 100 / p_est_cost) AS DECIMAL(13, 4))
                FROM time_sheet_project t
                WHERE t.project_id = rl.project_id
                    AND t.start_date <= MAKEDATE(rl.year, rl.n_day)
                    AND MAKEDATE(rl.year, rl.n_day) <= t.end_date
            ), 0),

            act_cost = COALESCE(
                    (  SELECT SUM(t.daily_cost_quota)
                        FROM time_sheet_project t
                        WHERE t.project_id = rl.project_id
                            AND t.start_date <= MAKEDATE(rl.year, rl.n_day)
                            AND MAKEDATE(rl.year, rl.n_day) <= t.end_date
                    ), 0),

            est_cost_pct = COALESCE(
            (  SELECT SUM(compute_allocation_cost_per_day(a.id, MAKEDATE(rl.year, rl.n_day))) * 100 / p_est_cost
                FROM allocation a
                WHERE a.project_id = rl.project_id
                    AND a.is_real_commitment IS FALSE
                    AND a.start_date <= MAKEDATE(rl.year, rl.n_day)
                    AND MAKEDATE(rl.year, rl.n_day) <= a.end_date
            ), 0)

        WHERE rl.project_id = project_id;

    END IF;

END//


DELIMITER //

DROP PROCEDURE IF EXISTS refresh_weekly_project_cost //
CREATE PROCEDURE refresh_weekly_project_cost(IN project_id BIGINT)
BEGIN
    DECLARE p_act_cost DECIMAL(13,5);
    DECLARE p_est_cost DECIMAL(13,5);
    DECLARE p_status VARCHAR(50);

    DELETE FROM weekly_project_cost d WHERE d.project_id = project_id;

	INSERT INTO weekly_project_cost (project_id, status, unit_id, year, month, n_week, cumulative_est_cost_pct, cumulative_est_cost, cumulative_act_cost_pct, cumulative_act_cost)
	SELECT
		d.project_id AS project_id, d.status AS status, p.unit_id AS unit_id, d.year AS year, d.month AS month, d.n_week AS n_week,

		(SELECT round(SUM(d2.est_cost_pct))
		 FROM daily_project_cost d2
		 WHERE d2.project_id = d.project_id
		   AND d.yearweek >= d2.yearweek and d.yearmonth >= d2.yearmonth
		) AS cumulative_est_cost_pct,

		(SELECT round(SUM(d2.est_cost))
         FROM daily_project_cost d2
         WHERE d2.project_id = d.project_id
		   AND d.yearweek >= d2.yearweek and d.yearmonth >= d2.yearmonth
        ) AS cumulative_est_cost,

		(SELECT round(SUM(d2.act_cost_pct))
		 FROM daily_project_cost d2
		 WHERE d2.project_id = d.project_id
		   AND d.yearweek >= d2.yearweek and d.yearmonth >= d2.yearmonth
		) AS cumulative_act_cost_pct,

        (SELECT round(SUM(d2.act_cost))
         FROM daily_project_cost d2
         WHERE d2.project_id = d.project_id
		   AND d.yearweek >= d2.yearweek and d.yearmonth >= d2.yearmonth
        ) AS cumulative_act_cost

	FROM daily_project_cost d
	JOIN project p ON d.project_id = p.id

    WHERE d.project_id = project_id
	GROUP BY d.project_id, d.status, d.year, d.yearweek, d.yearmonth, d.month, d.n_week;

	SELECT MAX(w.cumulative_est_cost), MAX(w.cumulative_act_cost)
	INTO p_est_cost, p_act_cost
	FROM weekly_project_cost w
	WHERE w.project_id = project_id;

    SELECT p.status
    INTO p_status
    FROM project p
    WHERE p.id = project_id;

	IF p_status = "In Progress" THEN
        UPDATE project_costs p
        SET  act_hr_cost = p_act_cost, current_hr_cost = p_est_cost
        WHERE p.project_id = project_id;

    ELSEIF p_status = "Pre-Sale" THEN
        UPDATE project_costs p
        SET  act_hr_cost = p_act_cost, pre_sale_hr_cost = p_est_cost, current_hr_cost = p_est_cost
        WHERE p.project_id = project_id;
    END IF;

END//


---------------------------------- INTEGRITY/CONSISTENCY CONSTRAINTS ----------------------------------

DELIMITER //

DROP TRIGGER IF EXISTS check_PMs_consistency_on_projects_trigger //

CREATE TRIGGER check_PMs_consistency_on_projects_trigger
BEFORE INSERT ON allocation
FOR EACH ROW
BEGIN
    DECLARE pm_count INT;
    DECLARE preallocation_count INT;

    SELECT COUNT(*)
    INTO pm_count
    FROM allocation
    WHERE project_id = NEW.project_id AND role = NEW.role
        AND role = 'PM' AND is_real_commitment = NEW.is_real_commitment
        AND ((NEW.start_date <= end_date  AND NEW.end_date  >= start_date) OR
             (end_date  IS NULL AND NEW.end_date  IS NULL) OR
             (NEW.end_date  >= start_date AND end_date  IS NULL) OR
             (NEW.start_date <= end_date  AND NEW.end_date  IS NULL));

    IF pm_count > 0 AND NEW.is_real_commitment = 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'REAL COMMITMENT: PMs cannot be overlapped on a project';
    ELSEIF pm_count > 0 AND NEW.is_real_commitment = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'SALE COMMITMENT: PMs cannot be overlapped on a project';
    END IF;
END//



DELIMITER //

DROP TRIGGER IF EXISTS check_resource_consistency_on_projects_trigger //

CREATE TRIGGER check_resource_consistency_on_projects_trigger
BEFORE INSERT ON allocation
FOR EACH ROW
BEGIN
    DECLARE res_count INT;
    DECLARE project_start_date DATE;
    DECLARE project_kom_date DATE;
    DECLARE estimated_end_date  DATE;
    DECLARE resource_hiring_date DATE;

    SELECT COUNT(*)
    INTO res_count
    FROM allocation
    WHERE project_id = NEW.project_id AND resource_id = NEW.resource_id
        AND is_real_commitment = NEW.is_real_commitment
        AND ((NEW.start_date <= end_date  AND NEW.end_date  >= start_date) OR
             (end_date  IS NULL AND NEW.end_date  IS NULL) OR
             (NEW.end_date  >= start_date AND end_date  IS NULL) OR
             (NEW.start_date <= end_date  AND NEW.end_date  IS NULL));

    SELECT start_date, kom_date, estimated_end_date
    INTO project_start_date, project_kom_date, estimated_end_date
    FROM project
    WHERE id = NEW.project_id;

    SELECT hiring_date
    INTO resource_hiring_date
    FROM resource
    WHERE id = NEW.resource_id;

    IF res_count > 0 AND NEW.is_real_commitment = 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'REAL COMMITMENT: New allocation overlaps with an existing allocation';

    ELSEIF res_count > 0 AND NEW.is_real_commitment = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'SALE COMMITMENT: New allocation overlaps with an existing allocation';

    ELSEIF NEW.start_date < resource_hiring_date AND NEW.is_real_commitment = 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'REAL COMMITMENT: The allocation cannot start before the resource is hired';

    ELSEIF NEW.start_date < resource_hiring_date AND NEW.is_real_commitment = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'SALE COMMITMENT: The allocation cannot start before the resource is hired';

    ELSEIF project_kom_date IS NULL AND NEW.start_date < project_start_date AND NEW.is_real_commitment = 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'REAL COMMITMENT: The allocation start date cannot be earlier than the project start date';

    ELSEIF project_kom_date IS NULL AND NEW.start_date < project_start_date AND NEW.is_real_commitment = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'SALE COMMITMENT: The allocation start date cannot be earlier than the project start date';

    ELSEIF project_kom_date IS NOT NULL AND NEW.start_date < project_kom_date AND NEW.is_real_commitment = 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'REAL COMMITMENT: The allocation start date cannot be earlier than the KoM date';

    ELSEIF project_kom_date IS NOT NULL AND NEW.start_date < project_kom_date AND NEW.is_real_commitment = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'SALE COMMITMENT: The allocation start date cannot be earlier than the KoM date';

    END IF;

END//


DELIMITER //

DROP TRIGGER IF EXISTS check_allocation_end_date_consistency_trigger //

CREATE TRIGGER check_allocation_end_date_consistency_trigger
BEFORE UPDATE ON allocation
FOR EACH ROW
BEGIN
    DECLARE res_end_date_count INT;
    DECLARE res_start_date_count INT;
    DECLARE pm_end_date_count INT;
    DECLARE pm_start_date_count INT;
    DECLARE res_leave_date DATE;
    DECLARE project_status VARCHAR(20);

    SELECT status
    INTO project_status
    FROM project
    WHERE id = NEW.project_id;

    SELECT leave_date
    INTO res_leave_date
    FROM resource
    WHERE id = NEW.resource_id;

    SELECT COUNT(*)
    INTO res_end_date_count
    FROM allocation
    WHERE project_id = NEW.project_id AND resource_id = NEW.resource_id AND id <> NEW.id
        AND is_real_commitment = NEW.is_real_commitment
        AND NEW.end_date >= (SELECT start_date
                                     FROM allocation
                                     WHERE project_id = NEW.project_id AND resource_id = NEW.resource_id AND id <> NEW.id
                                           AND is_real_commitment = NEW.is_real_commitment
                                           AND start_date > OLD.end_date
                                     ORDER BY start_date ASC LIMIT 1);

    SELECT COUNT(*)
    INTO res_start_date_count
    FROM allocation
    WHERE project_id = NEW.project_id AND resource_id = NEW.resource_id AND id <> NEW.id
        AND is_real_commitment = NEW.is_real_commitment
        AND NEW.start_date <= (SELECT end_date
                                     FROM allocation
                                     WHERE project_id = NEW.project_id AND resource_id = NEW.resource_id AND id <> NEW.id
                                           AND is_real_commitment = NEW.is_real_commitment
                                           AND end_date < OLD.start_date
                                     ORDER BY end_date ASC LIMIT 1);

    SELECT COUNT(*)
    INTO pm_end_date_count
    FROM allocation
    WHERE project_id = NEW.project_id AND role = NEW.role AND role = 'PM' AND id <> NEW.id
         AND is_real_commitment = NEW.is_real_commitment
         AND NEW.end_date >= (SELECT start_date
                                      FROM allocation
                                      WHERE project_id = NEW.project_id AND role = NEW.role AND role = 'PM' AND id <> NEW.id
                                            AND is_real_commitment = NEW.is_real_commitment
                                            AND start_date > OLD.end_date
                                      ORDER BY start_date ASC LIMIT 1);

    SELECT COUNT(*)
    INTO pm_start_date_count
    FROM allocation
    WHERE project_id = NEW.project_id AND role = NEW.role AND role = 'PM' AND id <> NEW.id
         AND is_real_commitment = NEW.is_real_commitment
         AND NEW.end_date >= (SELECT start_date
                                      FROM allocation
                                      WHERE project_id = NEW.project_id AND role = NEW.role AND role = 'PM' AND id <> NEW.id
                                            AND is_real_commitment = NEW.is_real_commitment
                                            AND start_date > OLD.end_date
                                      ORDER BY start_date ASC LIMIT 1);



    IF (OLD.end_date <> NEW.end_date OR (OLD.end_date IS NULL AND NEW.end_date IS NOT NULL)) THEN

        IF res_end_date_count > 0 OR res_start_date_count > 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The allocation overlaps with existing allocations';

        ELSEIF pm_end_date_count > 0 OR pm_start_date_count > 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The PMs cannot be overlapped on a project';

        ELSEIF (project_status <> "Pre-Sale" AND project_status <> "In Progress") THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Allocations can be updated only in the Pre-Sale/In Progress project status';

        ELSEIF res_leave_date IS NOT NULL AND NEW.end_date > res_leave_date THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The allocation cannot exceed the leave date of the resource';

       END IF;
    END IF;

END//




DELIMITER //

DROP TRIGGER IF EXISTS change_project_dates_after_update_trigger //

CREATE TRIGGER change_project_dates_after_update_trigger
AFTER UPDATE ON allocation
FOR EACH ROW
BEGIN
    DECLARE project_pre_sale_scheduled_end_date DATE;
    DECLARE project_start_date DATE;
    DECLARE project_kom_date DATE;
    DECLARE project_status VARCHAR(20);
    DECLARE max_all_end_date DATE;

    SELECT max(end_date)
    INTO max_all_end_date
    FROM allocation
    WHERE project_id = NEW.project_id;

    SELECT pre_sale_scheduled_end_date, start_date, kom_date, status
    INTO project_pre_sale_scheduled_end_date, project_start_date, project_kom_date, project_status
    FROM project
    WHERE id = NEW.project_id;

    IF project_status = "Pre-Sale" AND NEW.end_date > project_pre_sale_scheduled_end_date THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The allocation end date cannot be later the pre-sale scheduled end date';

    ELSEIF project_status = "Pre-Sale" AND NEW.start_date < project_start_date THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The allocation start date cannot be before the pre-sale scheduled start date';

    ELSEIF project_status = "In Progress" AND NEW.start_date < project_kom_date THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The allocation start date cannot be before the KoM date';

    ELSEIF project_status = "In Progress" THEN

        UPDATE project
        SET estimated_end_date = max_all_end_date
        WHERE id = NEW.project_id;
    END IF;

END//



DELIMITER //

DROP TRIGGER IF EXISTS change_project_end_dates_after_delete_trigger //

CREATE TRIGGER change_project_end_dates_after_delete_trigger
AFTER DELETE ON allocation
FOR EACH ROW
BEGIN
    DECLARE project_pre_sale_scheduled_end_date DATE;
    DECLARE project_status VARCHAR(20);
    DECLARE max_all_end_date DATE;

    SELECT max(end_date)
    INTO max_all_end_date
    FROM allocation
    WHERE project_id = OLD.project_id;

    SELECT pre_sale_scheduled_end_date, status
    INTO project_pre_sale_scheduled_end_date, project_status
    FROM project
    WHERE id = OLD.project_id;

    IF project_status = "In Progress" THEN

        IF max_all_end_date IS NOT NULL THEN
            UPDATE project
            SET estimated_end_date = max_all_end_date
            WHERE id = OLD.project_id;
        END IF;

    END IF;

END//



DELIMITER //

DROP TRIGGER IF EXISTS check_allocation_dates_consistency_trigger //

CREATE TRIGGER check_allocation_dates_consistency_trigger
BEFORE INSERT ON allocation
FOR EACH ROW
BEGIN
	DECLARE flag BOOL;
	DECLARE has_role INT;
	DECLARE project_status VARCHAR(20);

    SELECT is_special, status
    INTO flag, project_status
    FROM project
    WHERE id = new.project_id;

    SELECT count(*)
    INTO has_role
    FROM resources_roles
    WHERE resource_id = NEW.resource_id AND role = NEW.role;

    IF (flag IS TRUE) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A resource cannot be allocated to this project';

    ELSEIF (project_status <> 'Pre-Sale' AND project_status <> 'In Progress') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Allocations can only be created in the Pre-Sale or In Progress project status';

    ELSEIF (NEW.end_date IS NOT NULL AND NEW.end_date < NEW.start_date) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The end date cannot be earlier than the start date';

    ELSEIF (has_role = 0) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The resource does not have this role';

    END IF;


END//


DELIMITER //

DROP TRIGGER IF EXISTS change_project_end_dates_after_insert_trigger //

CREATE TRIGGER change_project_end_dates_after_insert_trigger
AFTER INSERT ON allocation
FOR EACH ROW
BEGIN
    DECLARE project_pre_sale_scheduled_end_date DATE;
    DECLARE project_status VARCHAR(20);
    DECLARE max_all_end_date DATE;

    SELECT max(end_date)
    INTO max_all_end_date
    FROM allocation
    WHERE project_id = NEW.project_id;

    SELECT pre_sale_scheduled_end_date, status
    INTO project_pre_sale_scheduled_end_date, project_status
    FROM project
    WHERE id = NEW.project_id;

    IF project_status = 'Pre-Sale' AND NEW.end_date > project_pre_sale_scheduled_end_date THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The allocation end date cannot be later than the pre-sale scheduled end date';

    ELSEIF project_status = "In Progress" THEN

        UPDATE project
        SET estimated_end_date = max_all_end_date
        WHERE id = NEW.project_id;
    END IF;


END//



DELIMITER //

DROP TRIGGER IF EXISTS check_resource_leave_date_consistency_trigger //

CREATE TRIGGER check_resource_leave_date_consistency_trigger
BEFORE UPDATE ON resource
FOR EACH ROW
BEGIN
    IF OLD.leave_date <> NEW.leave_date THEN
              SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The leave date cannot be modified';
    END IF;
END//



DELIMITER //

DROP TRIGGER IF EXISTS check_initial_project_status_trigger //

CREATE TRIGGER check_initial_project_status_trigger
BEFORE INSERT ON project
FOR EACH ROW
BEGIN
    IF new.status <> "Pre-Sale" AND NEW.is_special = 0 THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The project must be created in a Pre-Sale status';
	END IF;
END//



DELIMITER //

DROP TRIGGER IF EXISTS check_resources_related_to_project_trigger //

CREATE TRIGGER check_resources_related_to_project_trigger
BEFORE INSERT ON project
FOR EACH ROW
BEGIN

    DECLARE presale_count INT;
    DECLARE dum_count INT;

    IF NEW.is_special = 0 THEN

        SELECT count(*)
        INTO presale_count
        FROM resources_roles
        WHERE resource_id = NEW.presale_id and (role = 'PSE' or role = 'PSM' or role = 'PSL' or role = 'ADMIN');

        SELECT count(*)
        INTO dum_count
        FROM resources_roles
        WHERE resource_id = NEW.dum_id and role = 'DUM';

        IF presale_count = 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The presale must be assigned to a resource with role PSE, PSM, or PSL';

        ELSEIF dum_count = 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The resource does not have the role DUM';
        END IF;


    END IF;
END//


DELIMITER //

DROP TRIGGER IF EXISTS check_project_consistency_trigger //

CREATE TRIGGER check_project_consistency_trigger
BEFORE UPDATE ON project
FOR EACH ROW
BEGIN

    IF  NEW.status <> 'Pre-Sale' AND OLD.pre_sale_scheduled_end_date <> NEW.pre_sale_scheduled_end_date THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The pre-sale scheduled end date can only be changed in the Pre-Sale status';

    ELSEIF NEW.status <> 'Pre-Sale' AND OLD.pre_sale_fixed_cost <> NEW.pre_sale_fixed_cost THEN
                   SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The pre-sale fixed cost can only be changed in the Pre-Sale status';

    ELSEIF NEW.status <> 'Pre-Sale' AND OLD.start_date <> NEW.start_date THEN
                   SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The pre-sales start date can only be changed in the Pre-Sale status';

    ELSEIF OLD.status = 'To Start' AND  NEW.status = 'In Progress' AND NEW.project_id IS NULL THEN
                   SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The project id cannot be null';

    ELSEIF OLD.status = 'To Start' AND  NEW.status = 'In Progress' AND NEW.kom_date IS NULL THEN
                   SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The KoM date cannot be null';

    END IF;

END//





DELIMITER //

DROP TRIGGER IF EXISTS check_KoM_date_consistency_trigger //

CREATE TRIGGER check_KoM_date_consistency_trigger
BEFORE UPDATE ON project
FOR EACH ROW
BEGIN

    DECLARE min_all_start_date DATE;


    IF (OLD.kom_date IS NULL AND NEW.kom_date IS NOT NULL) OR OLD.kom_date <> NEW.kom_date THEN

        SELECT MIN(start_date)
        INTO min_all_start_date
        FROM allocation
        WHERE project_id = NEW.id;

        IF min_all_start_date < NEW.kom_date THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The KoM date cannot be after than any allocation start date';
        END IF;
    END IF;


END//



DELIMITER //

DROP TRIGGER IF EXISTS check_project_status_transition_trigger //

CREATE TRIGGER check_project_status_transition_trigger
BEFORE UPDATE ON project
FOR EACH ROW
BEGIN

    IF old.status <> new.status THEN
        IF OLD.status = 'Cancelled' OR OLD.status = 'Closed' OR OLD.status = 'Lost' THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The status of the project cannot be changed';

        ELSEIF (OLD.status = 'Pre-Sale' AND NOT (NEW.status = 'To Start' OR NEW.status = 'Cancelled' OR NEW.status = 'Lost'))
                OR (OLD.status = 'To Start' AND NOT NEW.status = 'In Progress')
                OR (OLD.status = 'In Progress' AND NOT NEW.status = 'Closed') THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The project cannot move to this state';

        END IF;

        IF NEW.end_date IS NULL AND OLD.status = 'In Progress' AND NEW.status = 'Closed' THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The project can only be closed with an associated end date';
        END IF;
    END IF;


END//



DELIMITER //

DROP TRIGGER IF EXISTS change_allocations_dates_after_on_update_sched_end_date_trigger //

CREATE TRIGGER change_allocations_dates_after_on_update_sched_end_date_trigger
AFTER UPDATE ON project
FOR EACH ROW
BEGIN
    DECLARE done BOOL DEFAULT FALSE;
    DECLARE all_end_date DATE;
    DECLARE all_start_date DATE;
    DECLARE all_id BIGINT;

    DECLARE old_end_date DATE;
    DECLARE new_end_date DATE;
    DECLARE type_of_date VARCHAR(50);
    DECLARE message_text VARCHAR(250);

    DECLARE cur_all CURSOR FOR
        SELECT id, start_date
        FROM allocation
        WHERE project_id = NEW.id and end_date > NEW.pre_sale_scheduled_end_date;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    IF OLD.pre_sale_scheduled_end_date <> NEW.pre_sale_scheduled_end_date THEN

        OPEN cur_all;

        read_loop: LOOP
            FETCH cur_all INTO all_id, all_start_date;

            IF done THEN
            LEAVE read_loop;
            END IF;

            IF all_start_date > NEW.pre_sale_scheduled_end_date THEN
                DELETE FROM allocation WHERE id = all_id;
            ELSE
                UPDATE allocation
                SET end_date = NEW.pre_sale_scheduled_end_date
                WHERE id = all_id;
            END IF;

        END LOOP;
        CLOSE cur_all;

    END IF;

END//



DELIMITER //

DROP TRIGGER IF EXISTS check_user_integrity_before_insert_trigger //

CREATE TRIGGER check_user_integrity_before_insert_trigger

BEFORE INSERT ON user
FOR EACH ROW
BEGIN
    DECLARE user_count INT;
	DECLARE auth_roles_count INT;

    SELECT COUNT(*)
    INTO user_count
    FROM user
    WHERE resource_id = NEW.resource_id;

    SELECT COUNT(*)
    INTO auth_roles_count
    FROM resources_roles
    WHERE resource_id = NEW.resource_id and role <> 'CONSULTANT';

    IF user_count > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'There is already a user associated with this resource';
    END IF;

    IF auth_roles_count = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'A resource with only the CONSULTANT role cannot be a user';
    END IF;

END//



DELIMITER //

DROP TRIGGER IF EXISTS check_resource_integrity_before_update_trigger //

CREATE TRIGGER check_resource_integrity_before_update_trigger

AFTER UPDATE ON resource
FOR EACH ROW
BEGIN
    DECLARE is_user INT;
	DECLARE auth_roles_count INT;

    SELECT COUNT(*)
    INTO is_user
    FROM user
    WHERE resource_id = NEW.id;

    SELECT COUNT(*)
    INTO auth_roles_count
    FROM resources_roles
    WHERE resource_id = NEW.id and role <> 'CONSULTANT';

    IF is_user > 0 AND auth_roles_count = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'This resource is a user and therefore must be assigned an authorized role';
    END IF;

END//


DELIMITER //

DROP TRIGGER IF EXISTS check_timesheet_consistency_trigger //

CREATE TRIGGER check_timesheet_consistency_trigger
BEFORE INSERT ON time_sheet
FOR EACH ROW
BEGIN
	DECLARE count_ts INT;

    SELECT COUNT(*)
    INTO count_ts
    FROM time_sheet
    where resource_id = new.resource_id
    AND MONTH(start_date) = MONTH(new.start_date)
    AND YEAR(start_date) = YEAR(new.start_date);

    IF count_ts > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Each resource is limited to having only one timesheet per month and year';
    END IF;
END//



DELIMITER //

DROP TRIGGER IF EXISTS check1_GDM_consistency_trigger //

CREATE TRIGGER check1_GDM_consistency_trigger
BEFORE INSERT ON resources_roles
FOR EACH ROW
BEGIN

	DECLARE gdms_count INT;

	SELECT count(*)
    into gdms_count
	FROM resources_roles
	where role = 'GDM';

    IF NEW.role = "GDM" THEN
		IF gdms_count <> 0 THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'There is already a GDM';
		END IF;
    END IF;
END//



---------------------------------- PROJECT COSTS ----------------------------------

DELIMITER //

DROP TRIGGER IF EXISTS add_real_cost_to_project_trigger //

CREATE TRIGGER add_real_cost_to_project_trigger
AFTER INSERT ON project
FOR EACH ROW
BEGIN
    INSERT INTO project_costs (project_id)
    VALUES (new.id);
END//


---------------------------------- HANDLING HOURLY COSTS ----------------------------------

DELIMITER //

DROP TRIGGER IF EXISTS add_hourly_cost_after_creating_a_new_resource_trigger //

CREATE TRIGGER add_hourly_cost_after_creating_a_new_resource_trigger
AFTER INSERT ON resource
FOR EACH ROW
BEGIN
    DECLARE first_of_the_month DATE;

    IF NEW.last_hourly_cost_start_date IS NOT NULL AND NEW.last_hourly_cost IS NOT NULL THEN

        SET first_of_the_month = DATE_FORMAT(NEW.hiring_date, '%Y-%m-01');

        IF NEW.last_hourly_cost_start_date =  NEW.hiring_date
            OR (NEW.last_hourly_cost_start_date >  first_of_the_month AND DAY(new.last_hourly_cost_start_date) = 1) THEN

                INSERT INTO resource_hourly_cost (cost, start_date, resource_id)
                VALUES (NEW.last_hourly_cost, NEW.last_hourly_cost_start_date, NEW.id);
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Hourly cost start date must match hiring date or start from the first day of the next month';
        END IF;


    ELSEIF NEW.last_hourly_cost_start_date IS NULL AND NEW.last_hourly_cost IS NOT NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The hourly cost start date must be provided';
    ELSEIF NEW.last_hourly_cost_start_date IS NOT NULL AND NEW.last_hourly_cost IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The current hourly cost must be provided';
    END IF;
END//


DELIMITER //

DROP TRIGGER IF EXISTS add_hourly_cost_after_updating_a_resource_trigger //

CREATE TRIGGER add_hourly_cost_after_updating_a_resource_trigger
BEFORE UPDATE ON resource
FOR EACH ROW
BEGIN
    DECLARE first_of_the_month DATE;
    DECLARE count_hc_start_date INT;
    DECLARE count_hc_cost INT;

    SELECT count(*)
    INTO count_hc_cost
    FROM resource_hourly_cost
    WHERE resource_id = NEW.id AND cost = NEW.last_hourly_cost;

    SELECT count(*)
    INTO count_hc_start_date
    FROM resource_hourly_cost
    WHERE resource_id = NEW.id AND start_date = NEW.last_hourly_cost_start_date;


    IF OLD.last_hourly_cost_start_date <> NEW.last_hourly_cost_start_date OR (OLD.last_hourly_cost_start_date IS NULL AND NEW.last_hourly_cost_start_date IS NOT NULL)
        OR OLD.last_hourly_cost <> NEW.last_hourly_cost OR (OLD.last_hourly_cost IS NULL AND NEW.last_hourly_cost IS NOT NULL) THEN

		-- IF OLD.last_hourly_cost_start_date = NEW.last_hourly_cost_start_date THEN
		--	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'There cannot be two hourly costs with the same start date';

		-- ELSEIF OLD.last_hourly_cost = NEW.last_hourly_cost THEN
		-- 	SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The hourly cost must be different';

		IF NEW.last_hourly_cost IS NULL THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The current hourly cost must be provided';

		ELSEIF NEW.last_hourly_cost_start_date IS NULL THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The hourly cost start date must be provided';

		ELSEIF NEW.leave_date IS NOT NULL AND NEW.leave_date < NEW.last_hourly_cost_start_date THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The hourly cost start date must be earlier than the leave date';

	    ELSE

            SET first_of_the_month = DATE_FORMAT(NEW.hiring_date, '%Y-%m-01');

            IF NEW.last_hourly_cost_start_date =  NEW.hiring_date
                OR (NEW.last_hourly_cost_start_date >  first_of_the_month AND DAY(new.last_hourly_cost_start_date) = 1) THEN


                IF count_hc_cost > 0 THEN

                    DELETE FROM resource_hourly_cost WHERE resource_id = NEW.id AND cost = NEW.last_hourly_cost;

                   IF count_hc_start_date > 0 THEN

                        UPDATE resource_hourly_cost
                        SET cost = NEW.last_hourly_cost
                        WHERE resource_id = NEW.id AND start_date = NEW.last_hourly_cost_start_date;
                   ELSE
                        INSERT INTO resource_hourly_cost (cost, start_date, resource_id)
                        VALUES (NEW.last_hourly_cost, NEW.last_hourly_cost_start_date, NEW.id);
                   END IF;

                ELSEIF count_hc_start_date > 0 THEN
                    UPDATE resource_hourly_cost
                    SET cost = NEW.last_hourly_cost
                    WHERE resource_id = NEW.id AND start_date = NEW.last_hourly_cost_start_date;

                ELSE
                    INSERT INTO resource_hourly_cost (cost, start_date, resource_id)
                    VALUES (NEW.last_hourly_cost, NEW.last_hourly_cost_start_date, NEW.id);
                END IF;

			ELSE
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Hourly cost start date must match hiring date or start from the first day of the next month';
			END IF;
        END IF;
	END IF;

END//


---------------------------------- HANDLING RESOURCE WORKING TIME ----------------------------------


DELIMITER //

DROP TRIGGER IF EXISTS add_working_time_creating_a_new_resource_trigger //

CREATE TRIGGER add_working_time_creating_a_new_resource_trigger
AFTER INSERT ON resource
FOR EACH ROW
BEGIN
    DECLARE first_of_the_month DATE;

    IF NEW.last_working_time_start_date IS NOT NULL AND NEW.last_working_time IS NOT NULL THEN

        IF NEW.last_working_time_start_date <> NEW.hiring_date THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The contract start date must be equal to the hiring date';
        END IF;

        INSERT INTO resource_working_time (working_time, start_date, resource_id)
        VALUES (NEW.last_working_time, NEW.last_working_time_start_date, NEW.id);


    ELSEIF NEW.last_working_time_start_date IS NULL AND NEW.last_working_time IS NOT NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The contract start date must be provided';
    ELSEIF NEW.last_working_time_start_date IS NOT NULL AND NEW.last_working_time IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The working time must be provided';
    ELSE
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The contract information must be provided';
    END IF;

END//


DELIMITER //

DROP TRIGGER IF EXISTS add_working_time_after_updating_a_resource_trigger //

CREATE TRIGGER add_working_time_after_updating_a_resource_trigger
BEFORE UPDATE ON resource
FOR EACH ROW
BEGIN
    DECLARE first_of_the_month DATE;
    DECLARE count_wt_start_date INT;

    SELECT count(*)
    INTO count_wt_start_date
    FROM resource_working_time
    WHERE resource_id = NEW.id AND start_date = NEW.last_working_time_start_date;


    IF OLD.last_working_time_start_date <> NEW.last_working_time_start_date OR (OLD.last_working_time_start_date IS NULL AND NEW.last_working_time_start_date IS NOT NULL)
        OR OLD.last_working_time <> NEW.last_working_time OR (OLD.last_working_time IS NULL AND NEW.last_working_time IS NOT NULL) THEN

		IF NEW.last_working_time IS NULL THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The working time must be provided';

		ELSEIF NEW.last_working_time_start_date IS NULL THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The contract start date must be provided';

		ELSEIF NEW.leave_date IS NOT NULL AND NEW.leave_date < NEW.last_working_time_start_date THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The contract start date must be earlier than the leave date';

	    ELSE

            SET first_of_the_month = DATE_FORMAT(NEW.hiring_date, '%Y-%m-01');

            IF NEW.last_working_time_start_date =  NEW.hiring_date
                OR (NEW.last_working_time_start_date >  first_of_the_month AND DAY(new.last_working_time_start_date) = 1) THEN

                IF count_wt_start_date > 0 THEN

                    UPDATE resource_working_time
                    SET working_time = NEW.last_working_time
                    WHERE resource_id = NEW.id AND start_date = NEW.last_working_time_start_date;

                ELSE
                    INSERT INTO resource_working_time (working_time, start_date, resource_id)
                    VALUES (NEW.last_working_time, NEW.last_working_time_start_date, NEW.id);

                END IF;

			ELSE
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The contract start date must match hiring date or start from the first day of the next month';
			END IF;
        END IF;
	END IF;

END//


---------------------------------- HANDLING TIME SHEETS ----------------------------------

DELIMITER //

DROP TRIGGER IF EXISTS add_regular_projects_to_the_timesheet_trigger //

CREATE TRIGGER add_regular_projects_to_the_timesheet_trigger
AFTER INSERT ON time_sheet
FOR EACH ROW
BEGIN

    -- Insert regular projects
    INSERT INTO time_sheet_project (project_id, time_sheet_id, allocation_id)
    SELECT a.project_id, NEW.id, a.id -- (no DISTINCT, all allocations)
    FROM allocation a
    WHERE a.resource_id = NEW.resource_id
        AND a.is_real_commitment IS FALSE
        AND NOT (a.end_date < NEW.start_date OR a.start_date > NEW.end_date);

END//




DELIMITER //

DROP TRIGGER IF EXISTS add_project_to_the_timesheet_after_an_allocation_trigger //

CREATE TRIGGER add_project_to_the_timesheet_after_an_allocation_trigger
AFTER INSERT ON allocation
FOR EACH ROW
BEGIN
	DECLARE ts_id BIGINT;
	DECLARE res_hire_date DATE;
	DECLARE res_leave_date DATE;
    DECLARE done BOOL DEFAULT FALSE;

	DECLARE cur_time_sheets CURSOR FOR
		SELECT id
		FROM time_sheet
		WHERE resource_id = NEW.resource_id
		    AND (start_date >= NEW.start_date OR MONTH(start_date) = MONTH(NEW.start_date) AND YEAR(start_date) = YEAR(NEW.start_date)) -- from current month
		    AND (NEW.end_date IS NULL OR (NEW.end_date IS NOT NULL AND start_date <= NEW.end_date)) -- till the end
		ORDER BY start_date ASC;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

	SELECT hiring_date, leave_date
    INTO res_hire_date, res_leave_date
    FROM resource
    WHERE id = NEW.resource_id;


	IF (NEW.start_date < res_hire_date) THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The resource cannot be assigned before his or her hiring date';

	ELSEIF (res_leave_date IS NOT NULL AND NEW.end_date > res_leave_date) THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The resource cannot be assigned after his or her leave date';

	ELSEIF ((NEW.start_date < curdate() AND (MONTH(NEW.start_date) <> MONTH(curdate()) OR YEAR(NEW.start_date) <> YEAR(curdate())))
			OR (MONTH(curdate()) = MONTH(NEW.start_date) AND YEAR(curdate()) = YEAR(NEW.start_date)))
			AND NEW.is_real_commitment = 0 THEN


		OPEN cur_time_sheets;
        read_loop: LOOP

            FETCH cur_time_sheets INTO ts_id;

            IF done THEN
            LEAVE read_loop;
            END IF;


			INSERT INTO time_sheet_project (project_id, time_sheet_id, allocation_id)
			VALUES (new.project_id, ts_id, new.id);

        END LOOP;
        CLOSE cur_time_sheets;

    END IF;

END//


DELIMITER //

DROP TRIGGER IF EXISTS update_project_to_the_timesheet_after_update_end_date_trigger //

CREATE TRIGGER update_project_to_the_timesheet_after_update_end_date_trigger
AFTER UPDATE ON allocation
FOR EACH ROW
BEGIN
    DECLARE done BOOL DEFAULT FALSE;
	DECLARE ts_id BIGINT;
	DECLARE tsp_id BIGINT;
	DECLARE ts_start_date DATE;

	DECLARE cur_time_sheets_when_new_end_date_before_old CURSOR FOR
		SELECT s.id
		FROM time_sheet t join time_sheet_project s on t.id = s.time_sheet_id
		WHERE t.resource_id = NEW.resource_id
		    AND t.start_date > NEW.end_date
            AND s.allocation_id = NEW.id
		ORDER BY t.start_date ASC;

	DECLARE cur_time_sheets_when_old_end_date_before_new CURSOR FOR
		SELECT t.id
		FROM time_sheet t
		WHERE t.resource_id = NEW.resource_id
		    AND t.start_date > OLD.end_date
			AND t.start_date <= NEW.end_date
		ORDER BY t.start_date ASC;


    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    IF OLD.end_date > NEW.end_date THEN

        OPEN cur_time_sheets_when_new_end_date_before_old;
        read_loop: LOOP
            FETCH cur_time_sheets_when_new_end_date_before_old INTO tsp_id;

            IF done THEN
            LEAVE read_loop;
            END IF;

            UPDATE time_sheet_project
            SET allocation_id = NULL
            WHERE id = tsp_id;

		END LOOP;
		CLOSE cur_time_sheets_when_new_end_date_before_old;

    ELSEIF NEW.end_date > OLD.end_date THEN

        OPEN cur_time_sheets_when_old_end_date_before_new;
        read_loop: LOOP

            FETCH cur_time_sheets_when_old_end_date_before_new INTO ts_id;

            IF done THEN
            LEAVE read_loop;
            END IF;

			INSERT INTO time_sheet_project (project_id, time_sheet_id, allocation_id)
			VALUES (NEW.project_id, ts_id, NEW.id);

		END LOOP;
		CLOSE cur_time_sheets_when_old_end_date_before_new;

	END IF;


END//



DELIMITER //

DROP TRIGGER IF EXISTS update_project_to_the_timesheet_after_update_start_date_trigger //

CREATE TRIGGER update_project_to_the_timesheet_after_update_start_date_trigger
AFTER UPDATE ON allocation
FOR EACH ROW
BEGIN
    DECLARE done BOOL DEFAULT FALSE;
	DECLARE ts_id BIGINT;
	DECLARE tsp_id BIGINT;


    DECLARE cur_time_sheets_when_old_before_new CURSOR FOR
        SELECT s.id
        FROM time_sheet t join time_sheet_project s on t.id = s.time_sheet_id
        WHERE t.resource_id = NEW.resource_id
            AND t.start_date <= DATE_SUB(NEW.start_date, INTERVAL 1 MONTH)
            AND s.allocation_id = NEW.id
        ORDER BY t.start_date ASC;


	DECLARE cur_time_sheets_when_new_before_old CURSOR FOR
        SELECT t.id
        FROM time_sheet t
        WHERE t.resource_id = NEW.resource_id
            AND t.start_date <= DATE_SUB(OLD.start_date, INTERVAL 1 MONTH)
			AND t.start_date > DATE_SUB(NEW.start_date, INTERVAL 1 MONTH)
        ORDER BY t.start_date ASC;


    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;


    IF OLD.start_date < NEW.start_date THEN

        OPEN cur_time_sheets_when_old_before_new;
        read_loop: LOOP
            FETCH cur_time_sheets_when_old_before_new INTO tsp_id;

            IF done THEN
            LEAVE read_loop;
            END IF;

            UPDATE time_sheet_project
            SET allocation_id = NULL
            WHERE id = tsp_id;

		END LOOP;
		CLOSE cur_time_sheets_when_old_before_new;

    ELSEIF NEW.start_date < OLD.start_date THEN

        OPEN cur_time_sheets_when_new_before_old;
        read_loop: LOOP
            FETCH cur_time_sheets_when_new_before_old INTO ts_id;

            IF done THEN
            LEAVE read_loop;
            END IF;

			INSERT INTO time_sheet_project (project_id, time_sheet_id, allocation_id)
			VALUES (NEW.project_id, ts_id, NEW.id);

		END LOOP;
		CLOSE cur_time_sheets_when_new_before_old;

	END IF;


END//



---------------------------------- TIMESHEET PROJECT ----------------------------------


DELIMITER //

DROP TRIGGER IF EXISTS update_project_to_the_timesheet_after_delete_trigger //

CREATE TRIGGER update_project_to_the_timesheet_after_delete_trigger
AFTER DELETE ON allocation
FOR EACH ROW
BEGIN
    DECLARE done BOOL DEFAULT FALSE;
	DECLARE ts_id BIGINT;
	DECLARE tsp_id BIGINT;
	DECLARE ts_start_date DATE;

	DECLARE cur_time_sheet_projects CURSOR FOR
		SELECT s.id
		FROM time_sheet t join time_sheet_project s on t.id = s.time_sheet_id
		WHERE t.resource_id = OLD.resource_id
            AND s.allocation_id = OLD.id;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

	OPEN cur_time_sheet_projects;
	read_loop: LOOP
		FETCH cur_time_sheet_projects INTO tsp_id;

		IF done THEN
		LEAVE read_loop;
		END IF;

		UPDATE time_sheet_project
		SET allocation_id = NULL
		WHERE id = tsp_id;

	END LOOP;
	CLOSE cur_time_sheet_projects;


END//


DELIMITER //

DROP TRIGGER IF EXISTS check_project_status_before_compute_start_actual_cost_trigger //

CREATE TRIGGER check_project_status_before_compute_start_actual_cost_trigger
BEFORE UPDATE ON time_sheet_project
FOR EACH ROW
BEGIN
    DECLARE project_status VARCHAR(50);

    SELECT status
    INTO project_status
    FROM project
    WHERE id = new.project_id;

    IF (project_status <> "In Progress" AND
            ((OLD.hours IS NULL AND NEW.hours IS NOT NULL) OR OLD.hours <> NEW.hours)) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The project is not "In-Progress"';
    END IF;
END//



DELIMITER //

DROP TRIGGER IF EXISTS check_project_status_consistency_on_insert_trigger //

CREATE TRIGGER check_project_status_consistency_on_insert_trigger
BEFORE INSERT ON time_sheet_project
FOR EACH ROW
BEGIN
    DECLARE p_status VARCHAR(50);

    SELECT status
    INTO p_status
    FROM project
    WHERE id = NEW.project_id;

	IF p_status NOT IN ('Pre-Sale', 'In Progress') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'This project is not in the Pre-Sale/In Progress status';
    END IF;
END//


DELIMITER //

DROP TRIGGER IF EXISTS check_project_status_consistency_on_update_trigger //

CREATE TRIGGER check_project_status_consistency_on_update_trigger
BEFORE UPDATE ON time_sheet_project
FOR EACH ROW
BEGIN
    DECLARE p_status VARCHAR(50);

    SELECT status
    INTO p_status
    FROM project
    WHERE id = NEW.project_id;

	IF p_status NOT IN ('Pre-Sale', 'In Progress') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'This project is not in the Pre-Sale/In Progress status';
    END IF;
END//


DELIMITER //

DROP TRIGGER IF EXISTS check_project_status_consistency_on_delete_trigger //

CREATE TRIGGER check_project_status_consistency_on_delete_trigger
BEFORE DELETE ON time_sheet_project
FOR EACH ROW
BEGIN
    DECLARE p_status VARCHAR(50);

    SELECT status
    INTO p_status
    FROM project
    WHERE id = OLD.project_id;

	IF p_status NOT IN ('Pre-Sale', 'In Progress') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'This project is not in the Pre-Sale/In Progress status';
    END IF;
END//


/*

---------------------------------- DELETED ----------------------------------

DELIMITER //

DROP TRIGGER IF EXISTS compute_real_project_costs_changing_HR_cost_trigger //

CREATE TRIGGER compute_real_project_costs_changing_HR_cost_trigger
AFTER UPDATE ON time_sheet_project
FOR EACH ROW
BEGIN
	DECLARE is_the_project_special BOOL;
	DECLARE res_id BIGINT;
	DECLARE time_sheet_start_date DATE;

	SELECT is_special
	INTO is_the_project_special
	FROM project
	WHERE id = NEW.project_id;

	IF NEW.project_id IS NOT NULL AND is_the_project_special IS FALSE
	    AND ((OLD.hours IS NULL AND NEW.hours IS NOT NULL) OR OLD.hours <> NEW.hours)  THEN

		SELECT resource_id, start_date
		INTO res_id, time_sheet_start_date
		FROM time_sheet
		WHERE id = NEW.time_sheet_id;

        IF OLD.hours IS NULL THEN
            UPDATE project_cost_by_timesheets
            SET actual_cost = actual_cost + NEW.cost
            WHERE project_id = NEW.project_id;

        ELSE
            UPDATE project_cost_by_timesheets
            SET actual_cost = actual_cost - OLD.cost + NEW.cost
            WHERE project_id = NEW.project_id;
        END IF;

	END IF;
END//


DELIMITER //

DROP TRIGGER IF EXISTS compute_real_project_costs_adding_HR_cost_trigger //

CREATE TRIGGER compute_real_project_costs_adding_HR_cost_trigger
AFTER INSERT ON time_sheet_project
FOR EACH ROW
BEGIN
	DECLARE is_the_project_special BOOL;
	DECLARE res_id BIGINT;
	DECLARE time_sheet_start_date DATE;

	SELECT is_special
	INTO is_the_project_special
	FROM project
	WHERE id = NEW.project_id;

	IF NEW.project_id IS NOT NULL AND is_the_project_special IS FALSE
	    AND NEW.hours IS NOT NULL THEN

		SELECT resource_id, start_date
		INTO res_id, time_sheet_start_date
		FROM time_sheet
		WHERE id = NEW.time_sheet_id;


        UPDATE project_cost_by_timesheets
        SET actual_cost = actual_cost + NEW.cost
        WHERE project_id = NEW.project_id;

	END IF;
END//


DELIMITER //

DROP TRIGGER IF EXISTS compute_real_project_costs_deleting_HR_cost_trigger //

CREATE TRIGGER compute_real_project_costs_deleting_HR_cost_trigger
AFTER DELETE ON time_sheet_project
FOR EACH ROW
BEGIN
	DECLARE is_the_project_special BOOL;
	DECLARE res_id BIGINT;
	DECLARE time_sheet_start_date DATE;

	SELECT is_special
	INTO is_the_project_special
	FROM project
	WHERE id = OLD.project_id;


	IF OLD.project_id IS NOT NULL AND is_the_project_special IS FALSE
	    AND OLD.hours IS NOT NULL THEN

		SELECT resource_id, start_date
		INTO res_id, time_sheet_start_date
		FROM time_sheet
		WHERE id = OLD.time_sheet_id;

		UPDATE project_cost_by_timesheets
		SET actual_cost = actual_cost - OLD.cost
		WHERE project_id = OLD.project_id;

	END IF;
END//


DELIMITER //

DROP TRIGGER IF EXISTS check_work_hours_consistency_on_insert_trigger //

CREATE TRIGGER check_work_hours_consistency_on_insert_trigger
BEFORE INSERT ON time_sheet_project
FOR EACH ROW
BEGIN
    DECLARE tot_work_hours INT;
    DECLARE sum_work_hours INT;

    SELECT tot_work_hours
    INTO tot_work_hours
    FROM time_sheet
    WHERE id = NEW.time_sheet_id;

    SELECT SUM(hours)
    INTO sum_work_hours
    FROM time_sheet_project
    WHERE time_sheet_id = NEW.time_sheet_id;

    IF (sum_work_hours > tot_work_hours) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The total hours dedicated by a resource to projects cannot exceed the available work hours for that month';
    END IF;
END//



DELIMITER //

DROP TRIGGER IF EXISTS check_work_hours_consistency_on_update_trigger //

CREATE TRIGGER check_work_hours_consistency_on_update_trigger
BEFORE UPDATE ON time_sheet_project
FOR EACH ROW
BEGIN
    DECLARE tot_work_hours INT;
    DECLARE sum_work_hours INT;

    SELECT tot_work_hours
    INTO tot_work_hours
    FROM time_sheet
    WHERE id = new.time_sheet_id;

    SELECT SUM(hours)
    INTO sum_work_hours
    FROM time_sheet_project
    WHERE time_sheet_id = new.time_sheet_id;

    IF (sum_work_hours > tot_work_hours) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The total hours spent by a resource on projects cannot exceed the available work hours for that month';
    END IF;
END//


DELIMITER //

DROP FUNCTION IF EXISTS compute_work_days //
CREATE FUNCTION compute_work_days(start_date DATE, end_date DATE) RETURNS INT DETERMINISTIC
BEGIN
	DECLARE work_days INT;

	SET work_days = 0;

	WHILE start_date <= end_date DO
		IF (DAYOFWEEK(start_date) <> 1 AND DAYOFWEEK(start_date) <> 7) THEN
			SET work_days = work_days +1;
		END IF;

		SET start_date = DATE_ADD(start_date, INTERVAL 1 DAY);
	END WHILE;

    RETURN work_days;
END//


DROP TRIGGER IF EXISTS add_salary_detail_after_creating_a_new_resource_trigger //

CREATE TRIGGER add_salary_detail_after_creating_a_new_resource_trigger
AFTER INSERT ON resource
FOR EACH ROW
BEGIN
    DECLARE first_of_the_month DATE;

    IF NEW.ral_start_date IS NOT NULL AND NEW.ral IS NOT NULL
    AND NEW.ccnl_level IS NOT NULL AND NEW.ccnl_level_start_date IS NOT NULL THEN

        SET first_of_the_month = DATE_FORMAT(NEW.hiring_date, '%Y-%m-01');

        IF NEW.ral_start_date = NEW.hiring_date OR (NEW.ral_start_date > first_of_the_month AND DAY(NEW.ral_start_date) = 1) THEN

            IF NEW.ccnl_level_start_date = NEW.hiring_date OR (NEW.ccnl_level_start_date > first_of_the_month AND DAY(NEW.ccnl_level_start_date) = 1) THEN

                IF NEW.daily_allowance IS NULL AND NEW.daily_allowance_start_date IS NOT NULL THEN
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The daily allowance must be provided.';
                ELSEIF NEW.daily_allowance IS NOT NULL AND NEW.daily_allowance_start_date IS NULL THEN
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The daily allowance start date must be provided';
                ELSEIF NEW.daily_allowance IS NOT NULL AND NEW.daily_allowance_start_date IS NOT NULL
                    AND not (NEW.daily_allowance_start_date = NEW.hiring_date OR (NEW.daily_allowance_start_date > first_of_the_month AND DAY(NEW.daily_allowance_start_date) = 1)) THEN
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Daily allowance start date must coincide with the hiring date or be bound from the first of a month following the hiring date';
                END IF;

                INSERT INTO resource_salary_details (ral, ral_start_date, daily_allowance, daily_allowance_start_date, ccnl_level, ccnl_level_start_date, resource_id)
                VALUES (NEW.ral, NEW.ral_start_date, NEW.daily_allowance, NEW.daily_allowance_start_date, NEW.ccnl_level, NEW.ccnl_level_start_date, NEW.id);

            ELSE
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The ccnl level start date must coincide with the hiring date or be bound from the first of a month following the hiring date';
            END IF;

        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The ral start date must coincide with the hiring date or be bound from the first of a month following the hiring date';
        END IF;

    ELSE
        IF NEW.ral_start_date IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The RAL start date must be provided';
        ELSEIF NEW.ral IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The current RAL must be provided';
        ELSEIF NEW.ccnl_level IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The CCNL level must be provided';
        ELSEIF NEW.ccnl_level_start_date IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The CCNL level start date must be provided';
        END IF;
    END IF;
END//

DELIMITER ;


---------------------------------------- Trigger ral --------------------------------------

DELIMITER //

DROP TRIGGER IF EXISTS add_ral_after_updating_a_resource_trigger //

CREATE TRIGGER add_ral_after_updating_a_resource_trigger
AFTER UPDATE ON resource
FOR EACH ROW
BEGIN
    DECLARE first_of_the_month DATE;

    IF OLD.ral_start_date <> NEW.ral_start_date OR (OLD.ral_start_date IS NULL AND NEW.ral_start_date IS NOT NULL)
        OR OLD.ral <> NEW.ral OR (old.ral IS NULL AND new.ral IS NOT NULL)
    then
        IF OLD.ral_start_date = NEW.ral_start_date THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'There cannot be two RAL with the same start date';

        ELSEIF OLD.ral = NEW.ral THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The RAL must be different';

        ELSEIF NEW.ral IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The current RAL must be provided';

        ELSEIF NEW.ral_start_date IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The RAL start date must be provided';

        ELSEIF NEW.leave_date IS NOT NULL AND NEW.leave_date < NEW.ral_start_date THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The RAL start date must be earlier than the leave date';


        ELSE
            SET first_of_the_month = DATE_FORMAT(NEW.hiring_date, '%Y-%m-01');

            IF NEW.ral_start_date =  NEW.hiring_date
              OR (NEW.ral_start_date >  first_of_the_month AND DAY(NEW.ral_start_date) = 1)
                 THEN

                INSERT INTO resource_salary_details (ral, ral_start_date, daily_allowance, daily_allowance_start_date, ccnl_level, ccnl_level_start_date, resource_id)
                VALUES (NEW.ral, NEW.ral_start_date, OLD.daily_allowance, OLD.daily_allowance_start_date, OLD.ccnl_level, OLD.ccnl_level_start_date, OLD.id);
            ELSE
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The RAL start date must coincide with the hiring date or be bound from the first of a month following the hiring date';
            END IF;
        END IF;
    END IF;

END//

----------------------------- Trigger ccnl level ------------------------------------

DELIMITER //

DROP TRIGGER IF EXISTS add_ccnl_level_after_updating_a_resource_trigger //

CREATE TRIGGER add_ccnl_level_after_updating_a_resource_trigger
AFTER UPDATE ON resource
FOR EACH ROW
BEGIN
    DECLARE first_of_the_month DATE;

    IF OLD.ccnl_level_start_date <> NEW.ccnl_level_start_date OR (OLD.ccnl_level_start_date IS NULL AND NEW.ccnl_level_start_date IS NOT NULL)
        OR OLD.ccnl_level <> NEW.ccnl_level OR (OLD.ccnl_level IS NULL AND NEW.ccnl_level IS NOT NULL)
    then
        IF OLD.ccnl_level_start_date = NEW.ccnl_level_start_date THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'There cannot be two ccnl level with the same start date';

        ELSEIF OLD.ccnl_level = NEW.ccnl_level THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The ccnl level must be different';

        ELSEIF NEW.ccnl_level IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The current ccnl level must be provided';

        ELSEIF NEW.ccnl_level_start_date IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The ccnl level start date must be provided';

        ELSEIF NEW.leave_date IS NOT NULL AND NEW.leave_date < NEW.ccnl_level_start_date THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The ccnl level start date must be earlier than the leave date';

        ELSE
            SET first_of_the_month = DATE_FORMAT(NEW.hiring_date, '%Y-%m-01');

            IF NEW.ccnl_level_start_date =  NEW.hiring_date
              OR (NEW.ccnl_level_start_date >  first_of_the_month AND DAY(NEW.ccnl_level_start_date) = 1)
                 THEN

                INSERT INTO resource_salary_details (ral, ral_start_date, daily_allowance, daily_allowance_start_date, ccnl_level, ccnl_level_start_date, resource_id)
                VALUES (old.ral, old.ral_start_date, OLD.daily_allowance, OLD.daily_allowance_start_date, new.ccnl_level, new.ccnl_level_start_date, OLD.id);
            ELSE
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The CCNL level start date must coincide with the hiring date or be bound from the first of a month following the hiring date';
            END IF;
        END IF;
    END IF;

END//

-------------------------- Trigger daily allowance -------------------------------------

DELIMITER //

DROP TRIGGER IF EXISTS add_daily_allowance_after_updating_a_resource_trigger //

CREATE TRIGGER add_daily_allowance_after_updating_a_resource_trigger
AFTER UPDATE ON resource
FOR EACH ROW
BEGIN
    DECLARE first_of_the_month DATE;

    IF OLD.daily_allowance_start_date <> NEW.daily_allowance_start_date OR (OLD.daily_allowance_start_date IS NULL AND NEW.daily_allowance_start_date IS NOT NULL)
        OR OLD.daily_allowance <> NEW.daily_allowance OR (OLD.daily_allowance IS NULL AND NEW.daily_allowance IS NOT NULL)
    then
        IF OLD.daily_allowance_start_date = NEW.daily_allowance_start_date THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'There cannot be two daily allowance with the same start date';

        ELSEIF OLD.daily_allowance = NEW.daily_allowance THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The daily allowance must be different';

        ELSEIF NEW.daily_allowance IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The current daily allowance must be provided';

        ELSEIF NEW.daily_allowance_start_date IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The daily allowance start date must be provided';

        ELSEIF NEW.leave_date IS NOT NULL AND NEW.leave_date < NEW.daily_allowance_start_date THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'The daily allowance start date must be earlier than the leave date';

        ELSE
            SET first_of_the_month = DATE_FORMAT(NEW.hiring_date, '%Y-%m-01');

            IF NEW.daily_allowance_start_date =  NEW.hiring_date
              OR (NEW.daily_allowance_start_date >  first_of_the_month AND DAY(NEW.daily_allowance_start_date) = 1)
                 THEN

                INSERT INTO resource_salary_details (ral, ral_start_date, daily_allowance, daily_allowance_start_date, ccnl_level, ccnl_level_start_date, resource_id)
                VALUES (old.ral, old.ral_start_date, NEW.daily_allowance, new.daily_allowance_start_date, OLD.ccnl_level, OLD.ccnl_level_start_date, OLD.id);
            ELSE
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Daily allowance start date must coincide with the hiring date or be bound from the first of a month following the hiring date';
            END IF;
        END IF;
    END IF;





DELIMITER //

DROP TRIGGER IF EXISTS check_DUM_consistency_trigger //

CREATE TRIGGER check_DUM_consistency_trigger
BEFORE INSERT ON resources_roles
FOR EACH ROW
BEGIN

	DECLARE dums_count INT;
	DECLARE unit_id BIGINT;

	SELECT DISTINCT (r.unit_id)
    into unit_id
	FROM resource r join resources_roles ro on r.id = ro.resource_id
    WHERE r.id = NEW.resource_id
    LIMIT 1;

	SELECT count(*)
    into dums_count
	FROM resource r join unit u on r.unit_id = u.id join resources_roles ro on r.id = ro.resource_id
	where r.unit_id = unit_id and ro.role = 'DUM';

    IF NEW.role = "DUM" THEN
		IF dums_count <> 0 THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'There is already a DUM associated to this unit';
		END IF;
    END IF;
END//

DELIMITER //

DROP TRIGGER IF EXISTS check2_DUM_consistency_trigger //

CREATE TRIGGER check2_DUM_consistency_trigger
BEFORE UPDATE ON resources_roles
FOR EACH ROW
BEGIN

	DECLARE dums_count INT;
	DECLARE unit_id BIGINT;

	SELECT DISTINCT (r.unit_id)
    into unit_id
	FROM resource r join resources_roles ro on r.id = ro.resource_id
    WHERE r.id = NEW.resource_id
    LIMIT 1;

	SELECT count(*)
    into dums_count
	FROM resource r join unit u on r.unit_id = u.id join resources_roles ro on r.id = ro.resource_id
	where r.unit_id = unit_id and ro.role = 'DUM';

    IF NEW.role = "DUM" THEN
		IF dums_count <> 0 THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'There is already a DUM associated to this unit';
		END IF;
    END IF;
END//

*/

