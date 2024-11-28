INSERT INTO APPLICANT_MESSAGE (
 id, year, a_id, app_year, u_code, d_code, subject, text_msg,
 read_time, creation_time, archiving_time)
VALUES (
  :?id, :?year, :?a_id, :?app_year, :?u_code, :?d_code, :?subject, :?text_msg,
  :?read_time, :?creation_time, :?archiving_time
)
