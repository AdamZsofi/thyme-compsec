INSERT INTO UserData (id, username, password, IsAdmin) VALUES (1001, 'anne', 'password', true);
INSERT INTO UserData (id, username, password) VALUES (1002, 'bob', 'secure');

INSERT INTO CaffFile (id, file_name, user_id, path) VALUES (2001, 'testfile1', 1001, 'caffs/2001');
INSERT INTO CaffFile (id, file_name, user_id, path) VALUES (2002, 'testfile2', 1001, 'caffs/2002');
INSERT INTO CaffFile (id, file_name, user_id, path) VALUES (2003, 'testfile3', 1002, 'caffs/2003');

INSERT INTO CaffComment (id, caff_id, author, content) VALUES (3001, 2001, 'anne', 'very good');
INSERT INTO CaffComment (id, caff_id, author, content) VALUES (3002, 2002, 'anne', 'even better');
INSERT INTO CaffComment (id, caff_id, author, content) VALUES (3003, 2001, 'bob', 'das ist no good');