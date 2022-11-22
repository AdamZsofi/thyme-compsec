INSERT INTO UserData (id, username, password, IsAdmin) VALUES (1001, 'user1', 'pwd1', true);
INSERT INTO UserData (id, username, password) VALUES (1002, 'user2', 'pwd1');

INSERT INTO CaffFile (id, user_id, path) VALUES (2001, 1001, '/pics/bla');
INSERT INTO CaffFile (id, user_id, path) VALUES (2002, 1001, 'c:\\pics\\bla');

INSERT INTO CaffComment (id, caff_id, created, author, content) VALUES (3001, 2001, '2005-12-31 10:32:34', 'user1', 'das ist ein comment');