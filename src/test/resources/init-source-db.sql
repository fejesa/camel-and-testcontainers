CREATE TABLE university (
    id bigint,
    name character varying(255),
    code character varying(10),
    creation_time timestamp without time zone
);

CREATE TABLE department (
    id bigint,
    name character varying(255),
    code character varying(10),
    creation_time timestamp without time zone
);

CREATE TABLE applicant (
    id bigint,
    u_id bigint,
    d_id bigint,
    app_id character varying(7),
    email character varying(255),
    first_name character varying(50),
    last_name character varying(50),
    birth_date date,
    phone_number character varying(25),
    creation_time timestamp without time zone
);

CREATE TABLE applicant_message (
    id bigint,
    a_id bigint,
    subject character varying(255),
    text_msg character varying(7000),
    read_time timestamp without time zone,
    creation_time timestamp without time zone
);

INSERT INTO university VALUES (1, 'Massachusetts Institute of Technology', 'MIT', now());
INSERT INTO university VALUES (2, 'Technical University of Munich', 'TUM', now());

INSERT INTO department VALUES (3, 'Architecture', 'ARCH', now());
INSERT INTO department VALUES (4, 'Computer Science', 'CS', now());
INSERT INTO department VALUES (5, 'Electrical Engineering', 'EE', now());

INSERT INTO applicant VALUES (25, 1, 5, '2368225', 'alba@fake.com', 'Jessica', 'Alba', '1998-11-15', '+43-1-40160-36560', now());
INSERT INTO applicant VALUES (73, 2, 3, '2350900', 'hello@trashmail.de', 'Jason', 'Statham', '1999-02-02', '0011223345567', now());
INSERT INTO applicant VALUES (22, 2, 5, '2350361', 'hanks@trashmail.de', 'Tom', 'Hanks', '2000-01-01', '123456789', now());
INSERT INTO applicant VALUES (19, 1, 4, '2350484', 'max@fake.com', 'Max', 'Mustermann', '1980-01-01', '1951230586', now());
INSERT INTO applicant VALUES (15, 1, 4, '2363225', 'seller@fake.com', 'Tom', 'Seller', '2002-06-15', '4135081192', now());
INSERT INTO applicant VALUES (28, 2, 5, '2350196', 'portman@trashmail.de', 'Natalie', 'Portman', '2000-05-05', '0032675543', now());

INSERT INTO applicant_message VALUES (1, 25, 'Application for Electrical Engineering', 'I am interested in the Electrical Engineering program.', null, now());
INSERT INTO applicant_message VALUES (2, 73, 'Application for Architecture', 'I am interested in the Architecture program.', null, now());
INSERT INTO applicant_message VALUES (3, 22, 'Application for Electrical Engineering', 'I am interested in the Electrical Engineering program.', null, now());
INSERT INTO applicant_message VALUES (4, 19, 'Application for Computer Science', 'I am interested in the Computer Science program.', null, now());
INSERT INTO applicant_message VALUES (5, 15, 'Application for Computer Science', 'I am interested in the Computer Science program.', null, now());
INSERT INTO applicant_message VALUES (6, 28, 'Application for Electrical Engineering', 'I am interested in the Electrical Engineering program.', null, now());

INSERT INTO applicant_message VALUES (7, 73, 'Application for Architecture', 'I aspire to design sustainable and innovative structures as an architect.', null, now());
INSERT INTO applicant_message VALUES (8, 22, 'Application for Electrical Engineering', 'I am drawn to Electrical Engineering for its potential to solve modern energy challenges.', null, now());
INSERT INTO applicant_message VALUES (9, 19, 'Application for Computer Science', 'I am fascinated by coding and software development and wish to pursue Computer Science.', null, now());
INSERT INTO applicant_message VALUES (10, 15, 'Application for Computer Science', 'My interest in algorithms and AI has motivated me to apply for Computer Science.', null, now());
INSERT INTO applicant_message VALUES (11, 25, 'Application for Electrical Engineering', 'Studying Electrical Engineering will allow me to innovate in the field of renewable energy.', null, now());
INSERT INTO applicant_message VALUES (12, 73, 'Application for Architecture', 'I am inspired by modern architecture and want to contribute to urban development.', null, now());
INSERT INTO applicant_message VALUES (13, 22, 'Application for Electrical Engineering', 'I am eager to learn about circuit design and electronics through Electrical Engineering.', null, now());
INSERT INTO applicant_message VALUES (14, 19, 'Application for Computer Science', 'I have a strong interest in building scalable applications, which is why I chose Computer Science.', null, now());
INSERT INTO applicant_message VALUES (15, 15, 'Application for Computer Science', 'I hope to explore cybersecurity and its applications through a Computer Science degree.', null, now());
INSERT INTO applicant_message VALUES (16, 25, 'Application for Electrical Engineering', 'I have always been passionate about Electrical Engineering and would like to contribute to the field.', null, now());
