DROP TABLE IF EXISTS experiences;
DROP TABLE IF EXISTS educations;
DROP TABLE IF EXISTS skills;
DROP TABLE IF EXISTS personal_info;

CREATE TABLE personal_info
(
    id                  SERIAL PRIMARY KEY,
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    title               VARCHAR(255) NOT NULL,
    profile_description TEXT         NOT NULL,
    profile_image_url   VARCHAR(500),
    years_of_experience INT,
    email               VARCHAR(255),
    phone               VARCHAR(50),
    linkedin_url        VARCHAR(500),
    github_url          VARCHAR(500)
);

CREATE TABLE skills
(
    id               SERIAL PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    level_percentage INT,
    icon_class       VARCHAR(100),
    personal_info_id BIGINT       NOT NULL,
    CONSTRAINT fk_skill_personal_info
        FOREIGN KEY (personal_info_id)
            REFERENCES personal_info (id)
            ON DELETE CASCADE
);

CREATE TABLE educations
(
    id               SERIAL PRIMARY KEY,
    degree           VARCHAR(255) NOT NULL,
    institution      VARCHAR(255) NOT NULL,
    start_date       DATE         NOT NULL,
    end_date         DATE,
    description      TEXT,
    personal_info_id BIGINT       NOT NULL,
    CONSTRAINT fk_education_personal_info
        FOREIGN KEY (personal_info_id)
            REFERENCES personal_info (id)
            ON DELETE CASCADE
);

CREATE TABLE experiences
(
    id               SERIAL PRIMARY KEY,
    job_title        VARCHAR(255) NOT NULL,
    company_name     VARCHAR(255) NOT NULL,
    start_date       DATE         NOT NULL,
    end_date         DATE,
    description      TEXT,
    personal_info_id BIGINT       NOT NULL,
    CONSTRAINT fk_experience_personal_info
        FOREIGN KEY (personal_info_id)
            REFERENCES personal_info (id)
            ON DELETE CASCADE
);



INSERT INTO personal_info (first_name, last_name, title, profile_description, profile_image_url, years_of_experience,
                           email, phone, linkedin_url, github_url)
VALUES ('Juan', 'Perez', 'Full Stack Developer',
        'Apasionado por el desarrollo web con experiencia en Java, Spring Boot y React. Disfruto construyendo soluciones robustas y escalables.',
        'img/profile-placeholder.jpg', 5, 'juan.perez@example.com', '+5491112345678',
        'https://linkedin.com/in/juanperez', 'https://github.com/juanperez');

INSERT INTO skills (name, level_percentage, icon_class, personal_info_id)
VALUES ('Java', 90, 'fab fa-java', 1),
       ('Spring Boot', 85, 'fas fa-leaf', 1),
       ('PostgreSQL', 80, 'fas fa-database', 1),
       ('HTML', 95, 'fab fa-html5', 1),
       ('CSS', 90, 'fab fa-css3-alt', 1),
       ('JavaScript', 75, 'fab fa-js-square', 1),
       ('React', 70, 'fab fa-react', 1);

INSERT INTO educations (degree, institution, start_date, end_date, description, personal_info_id)
VALUES ('Ingeniería en Sistemas', 'Universidad XYZ', '2015-03-01', '2020-12-15',
        'Especialización en desarrollo de software y bases de datos.', 1),
       ('Curso de Spring Boot Avanzado', 'Plataforma ABC', '2021-01-10', '2021-06-30',
        'Profundización en microservicios y seguridad.', 1);

INSERT INTO experiences (job_title, company_name, start_date, end_date, description, personal_info_id)
VALUES ('Desarrollador Full Stack Senior', 'Tech Solutions S.A.', '2022-01-01', NULL,
        'Desarrollo y mantenimiento de aplicaciones empresariales. Liderazgo técnico de equipo de 3 personas.', 1),
       ('Desarrollador Backend Junior', 'Innovatech Labs', '2020-09-01', '2021-12-31',
        'Participación en el desarrollo de APIs RESTful y optimización de bases de datos.', 1);