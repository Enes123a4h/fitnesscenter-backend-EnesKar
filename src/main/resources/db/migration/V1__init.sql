CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password VARCHAR(255) NOT NULL,
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    start_time DATETIME,
    end_time DATETIME,
    capacity INT,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    on_waitlist BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_enrollments UNIQUE (user_id, course_id),
    CONSTRAINT fk_enrollments_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_enrollments_course FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE INDEX idx_enrollments_course ON enrollments(course_id);

CREATE TABLE members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50) NOT NULL,
    email VARCHAR(150) NOT NULL,
    contract_type VARCHAR(30) NOT NULL,
    contract_start DATE NOT NULL,
    contract_end DATE NOT NULL,
    payment_status VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT uq_members_email UNIQUE (email),
    CONSTRAINT ck_members_contract_dates CHECK (contract_end >= contract_start)
);

CREATE INDEX idx_members_status ON members(status);

CREATE TABLE employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT uq_employees_email UNIQUE (email)
);

CREATE TABLE employee_permissions (
    employee_id BIGINT NOT NULL,
    permission VARCHAR(100) NOT NULL,
    CONSTRAINT pk_employee_permissions PRIMARY KEY (employee_id, permission),
    CONSTRAINT fk_employee_permissions_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE training_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    trainer_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_training_plans_trainer FOREIGN KEY (trainer_id) REFERENCES employees(id)
);

CREATE INDEX idx_training_plans_trainer ON training_plans(trainer_id);

CREATE TABLE member_training_plans (
    member_id BIGINT NOT NULL,
    training_plan_id BIGINT NOT NULL,
    CONSTRAINT pk_member_training_plans PRIMARY KEY (member_id, training_plan_id),
    CONSTRAINT fk_mtp_member FOREIGN KEY (member_id) REFERENCES members(id),
    CONSTRAINT fk_mtp_plan FOREIGN KEY (training_plan_id) REFERENCES training_plans(id)
);

CREATE INDEX idx_member_training_plans_plan ON member_training_plans(training_plan_id);

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATE NOT NULL,
    method VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_payments_member FOREIGN KEY (member_id) REFERENCES members(id)
);

CREATE INDEX idx_payments_member ON payments(member_id);

CREATE TABLE check_ins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    checkin_date DATE NOT NULL,
    checkin_time TIME NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_check_ins_member FOREIGN KEY (member_id) REFERENCES members(id)
);

CREATE INDEX idx_check_ins_member ON check_ins(member_id);
