USE appointment_db;

CREATE TABLE IF NOT EXISTS User (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    nickname VARCHAR(255),
    phone VARCHAR(20),
    image_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role VARCHAR(50) DEFAULT 'user'
);

CREATE TABLE IF NOT EXISTS Pets (
    pet_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    pet_name VARCHAR(255) NOT NULL,
    species VARCHAR(255) NOT NULL,
    bloodtype VARCHAR(10),
    birth_date DATE,
    weight DECIMAL(5,2),
    allergy TEXT,
    breed VARCHAR(255),
    gender VARCHAR(10),
    image_url TEXT,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

CREATE TABLE IF NOT EXISTS Doctor (
    doc_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    specialization VARCHAR(255),
    work_time TEXT,
    is_available TEXT,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

CREATE TABLE IF NOT EXISTS Appointments (
    app_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    pet_id INT,
    doc_id INT,
    reason TEXT,
    status VARCHAR(50) DEFAULT 'Waiting',
    app_date DATE,
    app_time TIME,
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (pet_id) REFERENCES Pets(pet_id),
    FOREIGN KEY (doc_id) REFERENCES Doctor(doc_id)
);

CREATE TABLE IF NOT EXISTS MedicalRecords (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT,
    doc_id INT,
    app_id INT,
    diagnosis TEXT,
    treatment_detail TEXT,
    cost DECIMAL(10,2),
    treatment_date DATE,
    treatment_time TIME,
    FOREIGN KEY (pet_id) REFERENCES Pets(pet_id),
    FOREIGN KEY (doc_id) REFERENCES Doctor(doc_id),
    FOREIGN KEY (app_id) REFERENCES Appointments(app_id)
);

CREATE TABLE IF NOT EXISTS Fact_table (
    fact_id INT AUTO_INCREMENT PRIMARY KEY,
    app_id INT,
    user_id INT,
    pet_id INT,
    doc_id INT,
    record_id INT,
    FOREIGN KEY (app_id) REFERENCES Appointments(app_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (pet_id) REFERENCES Pets(pet_id),
    FOREIGN KEY (doc_id) REFERENCES Doctor(doc_id),
    FOREIGN KEY (record_id) REFERENCES MedicalRecords(record_id)
);