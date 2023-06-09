INSERT INTO ROLE (NAME) VALUES
    ('ROLE_AUTHENTICATED');
INSERT INTO ROLE (NAME) VALUES
    ('ROLE_ADMIN');

INSERT INTO USERS(EMAIL, PASSWORD, NAME, SURNAME,LAST_PASSWORD_RESET_DATE, PHONE_NUMBER, IS_CONFIRMED, PROVIDER) VALUES ('mail1@mail.com',
                                                                                      '$2a$10$IgPBrBNNOaCVhb4dGmEKLeMPndC09k30PbQq..kMghoDzZNYicVG6',
                                                                                      'Maja',
                                                                                      'Varga',
                                                                                      '2023-05-02',
                                                                                      '+381627834992', true, 'GOOGLE');
INSERT INTO USERS_PAST_PASSWORDS(EMAIL,PASSWORD) VALUES ('mail1@mail.com','$2a$10$IgPBrBNNOaCVhb4dGmEKLeMPndC09k30PbQq..kMghoDzZNYicVG6');
/*INSERT INTO USERS(EMAIL, PASSWORD, NAME, SURNAME, PHONE_NUMBER, IS_CONFIRMED) VALUES ('marko.milijanovic2001@gmail.com',
                                                                                      '$2a$10$IgPBrBNNOaCVhb4dGmEKLeMPndC09k30PbQq..kMghoDzZNYicVG6',
                                                                                      'Marko',
                                                                                      'Milijanovic',
                                                                                      '+381627834992', true);*/
INSERT INTO USERS(EMAIL, PASSWORD, NAME, SURNAME,LAST_PASSWORD_RESET_DATE, PHONE_NUMBER, IS_CONFIRMED, PROVIDER) VALUES ('mail2@mail.com',
                    '$2a$10$E3fnG2Z/pNYdQCuMOSYCn.UyTLW1zXfCwR.ds5j9IztyJ0TIjRyJG',
                    'Milan',
                    'Simić',
                    '2023-05-25',
                    '+381641183201', true, 'GOOGLE');

INSERT INTO USERS_PAST_PASSWORDS(EMAIL,PASSWORD) VALUES ('mail2@mail.com','$2a$10$E3fnG2Z/pNYdQCuMOSYCn.UyTLW1zXfCwR.ds5j9IztyJ0TIjRyJG');
INSERT INTO USER_ROLE (user_id, role_id) VALUES (1, 2);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (2, 1);

-- INSERT INTO CERTIFICATES(SERIAL_NUMBER,ISSUER_SERIAL_NUMBER,ORGANIZATION_DATA,
--                          IS_VALID,VALID_FROM,VALID_UNTIL,CERTIFICATE_TYPE, OWNER_EMAIL) VALUES
--                         ('123456',NULL,'ACS|FTN|SRB',TRUE,'2023-04-01','2026-04-01','ROOT','2001vuk@gmail.com');
--
-- INSERT INTO CERTIFICATES(SERIAL_NUMBER,ISSUER_SERIAL_NUMBER,ORGANIZATION_DATA,
--                          IS_VALID,VALID_FROM,VALID_UNTIL,CERTIFICATE_TYPE, OWNER_EMAIL) VALUES
--                         ('654321','123456','ACS|PMF|SRB',TRUE,'2023-04-02','2025-04-01','INTERMEDIATE','mail2@mail.com');
--
-- INSERT INTO CERTIFICATE_REQUESTS(ISSUER_SERIAL_NUMBER, REQUESTER_ID,CERTIFICATE_TYPE,DATE_REQUESTED,VALID_UNTIL,
--                                  ORGANIZATION_DATA,REQUEST_STATUS,DENIAL_REASON) VALUES
--                                 (NULL,1,'ROOT','2023-04-01','2026-04-01','ACS|FTN|SRB','ACCEPTED',NULL);
--
-- INSERT INTO CERTIFICATE_REQUESTS(ISSUER_SERIAL_NUMBER, REQUESTER_ID,CERTIFICATE_TYPE,DATE_REQUESTED,VALID_UNTIL,
--                                  ORGANIZATION_DATA,REQUEST_STATUS,DENIAL_REASON) VALUES
--                                  ('123456',2,'INTERMEDIATE','2023-04-02','2025-04-01','ACS|PMF|SRB','ACCEPTED',NULL);