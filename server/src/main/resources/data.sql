INSERT INTO UserData (id, username, password) VALUES (1001, 'user1', 'pwd1');
INSERT INTO UserData (id, username, password) VALUES (1002, 'user2', 'pwd1');

INSERT INTO CaffFile (id, user_id) VALUES (2001, 1001);

INSERT INTO CaffComment (id, caff_id, created, content) VALUES (3001, 2001, '2005-12-31 10:32:34', 'das ist ein comment');