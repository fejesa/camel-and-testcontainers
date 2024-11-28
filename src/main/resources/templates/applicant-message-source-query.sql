SELECT m.id, m.a_id, a.app_id, d.code d_code, u.code u_code, m.subject, m.text_msg,
       m.read_time, m.creation_time
FROM APPLICANT_MESSAGE m, APPLICANT a, UNIVERSITY u, DEPARTMENT d
WHERE a.id = m.a_id AND a.u_id = u.id AND a.d_id = d.id
ORDER BY m.id
LIMIT 10000 OFFSET 0
