SELECT a.id, d.code d_code, u.code u_code, a.app_id as applicant_id, a.email, a.first_name, a.last_name, a.birth_date,
 a.phone_number, a.creation_time
 FROM APPLICANT a, UNIVERSITY u, DEPARTMENT d WHERE a.u_id = u.id AND a.d_id = d.id
 ORDER BY a.id
