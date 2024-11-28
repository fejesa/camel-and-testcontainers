INSERT INTO APPLICANT (
 id, year, applicant_id, u_code, d_code, email, first_name, last_name, birth_date,
 phone_number, creation_time, archiving_time)
VALUES (
 :?id, :?year, :?applicant_id, :?u_code, :?d_code,
 :?email, :?first_name, :?last_name, :?birth_date,
 :?phone_number, :?creation_time, :?archiving_time
)
