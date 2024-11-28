CREATE TABLE university (
    name character varying(255),
    code character varying(255),
    creation_time timestamp without time zone,
    archiving_time timestamp without time zone
);

CREATE TABLE department (
    name character varying(255),
    code character varying(255),
    creation_time timestamp without time zone,
    archiving_time timestamp without time zone
);

CREATE TABLE applicant (
    id bigint,
    year bigint,
    applicant_id character varying(7),
    u_code character varying(255),
    d_code character varying(255),
    email character varying(255),
    first_name character varying(50),
    last_name character varying(50),
    birth_date date,
    phone_number character varying(25),
    creation_time timestamp without time zone,
    archiving_time timestamp without time zone
);

CREATE TABLE applicant_message (
    id bigint,
    year bigint,
    a_id bigint,
    subject character varying(255),
    text_msg character varying(7000),
    read_time timestamp without time zone,
    creation_time timestamp without time zone,
    archiving_time timestamp without time zone
);

INSERT INTO university VALUES ('Massachusetts Institute of Technology', 'MIT', now(), now());
INSERT INTO university VALUES ('Technical University of Munich', 'TUM', now(), now());

INSERT INTO department VALUES ('Architecture', 'ARCH', now(), now());
INSERT INTO department VALUES ('Computer Science', 'CS', now(), now());
INSERT INTO department VALUES ('Electrical Engineering', 'EE', now(), now());
