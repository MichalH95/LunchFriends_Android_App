var oracledb = require('oracledb');
var http = require('http');
var obj2xml = require('object-to-xml');
var libxmljs = require("libxmljs");

module.exports = {

    handleRequest: function (request, response, pool) {

        pool.getConnection(function(err, connection) {
            response.writeHead(200, {"Content-Type": "text/xml"});

            if (err) {
                console.error("Error getting connection: " + err.message);
                return;
            }

            if      ( /*GET*/ request.url.match('^/person/?$') ) {
                connection.execute(
                    `SELECT * FROM PERSON`,
                    function (err, result) {
                        if (err) {
                            endConnection(connection, response, err);
                            return;
                        }

                        var persons = parsePersons(result);
                        writeObjectArray(persons, response, "person", "persons");

                        doRelease(connection);
                    });
            }
            else if ( /*GET*/ request.url.match('^/person/[0-9]*$') ) {
                var pid = request.url.split("/")[2];
                connection.execute(
                    `SELECT * FROM PERSON WHERE PID = :pid`,
                    [pid],
                    function (err, result) {
                        if (err) {
                            endConnection(connection, response, err);
                            return;
                        }

                        var persons = parsePersons(result);
                        writeObjectArray(persons, response, "person", "persons");

                        doRelease(connection);
                    });
            }
            else if ( /*GET*/ request.url.match('^/lunch/?$') ) {
                connection.execute(
                    `SELECT LID, PLACEID, to_char(LDATE, 'yyyy-mm-dd hh24:mi:ss' ), PID FROM LUNCH`,
                    function (err, result) {
                        if (err) {
                            endConnection(connection, response, err);
                            return;
                        }

                        var lunches = parseLunches(result);
                        writeObjectArray(lunches, response, "lunch", "lunches");

                        doRelease(connection);
                    });
            }
            else if ( /*GET*/ request.url.match('^/lunch/[0-9]*$') ) {
                var lid = request.url.split("/")[2];
                connection.execute(
                    `SELECT LID, PLACEID, to_char(LDATE, 'yyyy-mm-dd hh24:mi:ss' ), PID FROM LUNCH WHERE LID = :lid`,
                    [lid],
                    function (err, result) {
                        if (err) {
                            endConnection(connection, response, err);
                            return;
                        }

                        var lunches = parseLunches(result);
                        writeObjectArray(lunches, response, "lunch", "lunches");

                        doRelease(connection);
                    });
            }
            else if ( /*GET*/ request.url.match('^/lunchdel/?$') ) {
                connection.execute(
                    `DELETE FROM Lunch WHERE LDATE < SYSDATE`,
                    function (err, result) {
                        if (err) {
                            endConnection(connection, response, err);
                            return;
                        }

                        connection.execute(
                            `commit`,
                            function (err, result) {
                                if (err) {
                                    endConnection(connection, response, err);
                                    return;
                                }

                                writeObject(null, response, "nic");

                                doRelease(connection);
                                console.log("successfully deleted old lunches");
                            });
                    });
            }
            else if ( /*GET*/ request.url.match('^/lunchperson/?$') ) {
                connection.execute(
                    `select p.PID,REALNAME,GENDER,AGE,USERID,PASSWD,IMAGEURI,EMAIL,PHONE,HOBBIES,LID,to_char(LDATE, 'yyyy-mm-dd hh24:mi:ss'),PLACEID from PERSON p join LUNCH l on p.PID = l.PID`,
                    function (err, result) {
                        if (err) {
                            endConnection(connection, response, err);
                            return;
                        }

                        var lunchPersons = parseLunchPersons(result);
                        writeObjectArray(lunchPersons, response, "lunchperson", "lunchpersons");

                        doRelease(connection);
                    });
            }
            else if ( /*POST*/ request.url.match('^/register/?$') ) {
                var body = '';
                request.on('data', function (data) {
                    body += data;

                    if ( body[body.length-1] === '>' ) {

                        var xmlDoc = libxmljs.parseXml(body);

                        var userid = xmlDoc.get('//userid').text();
                        var passwd = xmlDoc.get('//passwd').text();
                        var realname = xmlDoc.get('//realname').text();
                        var age = xmlDoc.get('//age').text();
                        var gender = xmlDoc.get('//gender').text();
                        var imageuri = xmlDoc.get('//imageuri').text();
                        var email = xmlDoc.get('//email').text();
                        var phone = xmlDoc.get('//phone').text();
                        var hobbies = xmlDoc.get('//hobbies').text();

                        connection.execute(
                            `INSERT INTO Person (REALNAME, GENDER, AGE, USERID, PASSWD, IMAGEURI, EMAIL, PHONE, HOBBIES)
                            VALUES ('` + realname + `', '` + gender + `', ` + age + `, '` + userid + `', '` + passwd + `', '` + imageuri + `', '` + email + `', '` + phone + `', '` + hobbies + `')`,
                            function (err, result) {
                                if (err) {
                                    if ( err.message.match("ORA-00001: unique constraint .* violated") !== null ) {
                                        writeObject(new RegisterResult(false, true), response, "registerresult");

                                        doRelease(connection);
                                        console.log("userid already exists");
                                    } else {
                                        endConnection(connection, response, err);
                                    }
                                    return;
                                }

                                connection.execute(
                                    `commit`,
                                    function (err, result) {
                                        if (err) {
                                            endConnection(connection, response, err);
                                            return;
                                        }

                                        writeObject(new RegisterResult(true, false), response, "registerresult");

                                        doRelease(connection);
                                        console.log("successfully registered");
                                    });
                            });
                    }
                });
            }
            else if ( /*POST*/ request.url.match('^/login/?$') ) {
                var body = '';
                request.on('data', function (data) {
                    body += data;

                    if ( body[body.length-1] === '>' ) {

                        var xmlDoc = libxmljs.parseXml(body);

                        var userid = xmlDoc.get('//userid').text();

                        connection.execute(
                            `SELECT PID, REALNAME, GENDER, AGE, USERID, PASSWD, IMAGEURI, HOBBIES FROM Person WHERE USERID = '` + userid + `'`,
                            function (err, result) {
                                if (err) {
                                    endConnection(connection, response, err);
                                    return;
                                }

                                if ( result.rows.length !== 1 ) {
                                    writeObject(new LoginResult(false, true, null), response, "loginresult");
                                    console.log("login username doesn't exist");
                                } else {
                                    var person = parsePersons(result)[0];
                                    writeObject(new LoginResult(true, false, person), response, "loginresult");
                                    console.log("successful login");
                                }

                                doRelease(connection);
                            });
                    }
                });
            }
            else if ( /*POST*/ request.url.match('^/postlunch/?$') ) {
                var body = '';
                request.on('data', function (data) {
                    body += data;

                    if ( body[body.length-1] === '>' ) {

                        var xmlDoc = libxmljs.parseXml(body);

                        var placeid = xmlDoc.get('//placeid').text();
                        var ldate = xmlDoc.get('//ldate').text();
                        var PID = xmlDoc.get('//PID').text();

                        connection.execute(
                            `INSERT INTO Lunch (PLACEID, LDATE, PID)
                            VALUES ('` + placeid + `', to_date('` + ldate + `', 'yyyy-mm-dd hh24:mi:ss'), ` + PID + `)`,
                            function (err, result) {
                                if (err) {
                                    endConnection(connection, response, err);
                                    return;
                                }

                                connection.execute(
                                    `commit`,
                                    function (err, result) {
                                        if (err) {
                                            endConnection(connection, response, err);
                                            return;
                                        }

                                        writeObject(new CreateLunchResult(true), response, "createlunchresult");

                                        doRelease(connection);
                                        console.log("successfully created lunch");
                                    });
                            });
                    }
                });
            }
            else if ( /*POST*/ request.url.match('^/getinvitation/?$') ) {
                var body = '';
                request.on('data', function (data) {
                    body += data;

                    if ( body[body.length-1] === '>' ) {

                        var xmlDoc = libxmljs.parseXml(body);

                        var PID = xmlDoc.get('//PID').text();

                        connection.execute(
                            `SELECT TARGETPID, PID, LID FROM Invitation WHERE TARGETPID = ` + PID,
                            function (err, result) {
                                if (err) {
                                    endConnection(connection, response, err);
                                    return;
                                }

                                var invitations = parseInvitations(result);
                                writeObjectArray(invitations, response, "invitation", "invitations");

                                doRelease(connection);
                                console.log("finished loading invitations");
                            });
                    }
                });
            }
            else if ( /*POST*/ request.url.match('^/postinvitation/?$') ) {
                var body = '';
                request.on('data', function (data) {
                    body += data;

                    if ( body[body.length-1] === '>' ) {

                        var xmlDoc = libxmljs.parseXml(body);

                        var targetPID = xmlDoc.get('//targetPID').text();
                        var PID = xmlDoc.get('//PID').text();
                        var LID = xmlDoc.get('//LID').text();

                        connection.execute(
                            `INSERT INTO Invitation (TARGETPID, PID, LID) VALUES (` + targetPID + `, ` + PID + `, `+ LID + `)`,
                            function (err, result) {
                                if (err) {
                                    endConnection(connection, response, err);
                                    return;
                                }

                                connection.execute(
                                    `commit`,
                                    function (err, result) {
                                        if (err) {
                                            endConnection(connection, response, err);
                                            return;
                                        }

                                        writeObject(null, response, "nic");

                                        doRelease(connection);
                                        console.log("successfully created invitation");
                                    });
                            });
                    }
                });
            }
            else if ( /*POST*/ request.url.match('^/invitationdel/?$') ) {
                var body = '';
                request.on('data', function (data) {
                    body += data;

                    if ( body[body.length-1] === '>' ) {

                        var xmlDoc = libxmljs.parseXml(body);

                        var targetPID = xmlDoc.get('//targetPID').text();
                        var PID = xmlDoc.get('//PID').text();
                        var LID = xmlDoc.get('//LID').text();

                        connection.execute(
                            `DELETE FROM Invitation WHERE TARGETPID = ` + targetPID + ` AND PID = ` + PID + ` AND LID = ` + LID,
                            function (err, result) {
                                if (err) {
                                    endConnection(connection, response, err);
                                    return;
                                }

                                connection.execute(
                                    `commit`,
                                    function (err, result) {
                                        if (err) {
                                            endConnection(connection, response, err);
                                            return;
                                        }

                                        connection.execute(
                                            `DELETE FROM Lunch WHERE LID = ` + LID,
                                            function (err, result) {
                                                if (err) {
                                                    endConnection(connection, response, err);
                                                    return;
                                                }

                                                connection.execute(
                                                    `commit`,
                                                    function (err, result) {
                                                        if (err) {
                                                            endConnection(connection, response, err);
                                                            return;
                                                        }

                                                        writeObject(null, response, "nic");

                                                        doRelease(connection);
                                                        console.log("successfully deleted lunch and invitation");
                                                    });
                                            });
                                    });
                            });
                    }
                });
            }
            else if ( /*POST*/ request.url.match('^/posthistory/?$') ) {
                var body = '';
                request.on('data', function (data) {
                    body += data;

                    if ( body[body.length-1] === '>' ) {

                        var xmlDoc = libxmljs.parseXml(body);

                        var targetPID = xmlDoc.get('//targetPID').text();
                        var PID = xmlDoc.get('//PID').text();
                        var placeId = xmlDoc.get('//placeid').text();
                        var ldate = xmlDoc.get('//ldate').text();

                        connection.execute(
                            `INSERT INTO History (TARGETPID, PID, PLACEID, LDATE) VALUES (` + targetPID + `, ` + PID + `, '` + placeId + `', to_date('` + ldate + `', 'yyyy-mm-dd hh24:mi:ss'))`,
                            function (err, result) {
                                if (err) {
                                    endConnection(connection, response, err);
                                    return;
                                }

                                connection.execute(
                                    `commit`,
                                    function (err, result) {
                                        if (err) {
                                            endConnection(connection, response, err);
                                            return;
                                        }

                                        writeObject(null, response, "nic");

                                        doRelease(connection);
                                        console.log("successfully created lunch history");
                                    });
                            });
                    }
                });
            }
            else if ( /*POST*/ request.url.match('^/gethistory/?$') ) {
                var body = '';
                request.on('data', function (data) {
                    body += data;

                    if ( body[body.length-1] === '>' ) {

                        var xmlDoc = libxmljs.parseXml(body);

                        var PID = xmlDoc.get('//PID').text();

                        connection.execute(
                            `SELECT p.PID,REALNAME,GENDER,AGE,USERID,PASSWD,IMAGEURI,EMAIL,PHONE,HOBBIES,0 LID,to_char(LDATE, 'yyyy-mm-dd hh24:mi:ss'),PLACEID FROM Person p, (SELECT PID,LDATE,PLACEID FROM History WHERE TARGETPID = ` + PID + `) h WHERE p.PID = h.PID`,
                            function (err, result) {
                                if (err) {
                                    endConnection(connection, response, err);
                                    return;
                                }

                                var histories = parseLunchPersons(result);

                                connection.execute(
                                    `SELECT p.PID,REALNAME,GENDER,AGE,USERID,PASSWD,IMAGEURI,EMAIL,PHONE,HOBBIES,0 LID,to_char(LDATE, 'yyyy-mm-dd hh24:mi:ss'),PLACEID FROM Person p, (SELECT TARGETPID,LDATE,PLACEID FROM History WHERE PID = ` + PID + `) h WHERE p.PID = h.TARGETPID`,
                                    function (err, result2) {
                                        if (err) {
                                            console.log("err");
                                            endConnection(connection, response, err);
                                            return;
                                        }
                                        var histories2 = parseLunchPersons(result2);

                                        histories = histories.concat(histories2);

                                        writeObjectArray(histories, response, "lunchperson", "lunchpersons");

                                        doRelease(connection);
                                        console.log("finished loading histories");
                                    });
                            });
                    }
                });
            }
            else {
                connection.execute(
                    ``,
                    function (err) {
                        response.writeHead(400);
                        response.end();
                        doRelease(connection);
                    });
            }
        });
    }

};

function doRelease(connection) {
    connection.close(function(err) {
        if (err) {
            console.error(err.message);
        }
    });
}

function endConnection(connection, response, err) {
    console.error("Error executing: " + err.message);
    response.end();
    doRelease(connection);
}

function parsePersons(result) {
    var persons = new Array(result.rows.length);

    for (var i = 0; i < persons.length; i++) {
        persons[i] = new Person(result.rows[i][0], result.rows[i][1], result.rows[i][2], result.rows[i][3],
            result.rows[i][4], result.rows[i][5], result.rows[i][6], result.rows[i][7], result.rows[i][8], result.rows[i][9]);
    }
    return persons;
}

function parseLunches(result) {
    var lunches = new Array(result.rows.length);

    for (var i = 0; i < lunches.length; i++) {
        lunches[i] = new Lunch(result.rows[i][0], result.rows[i][1], result.rows[i][2], result.rows[i][3]);
    }
    return lunches;
}

function parseLunchPersons(result) {
    var lunchPersons = new Array(result.rows.length);

    for (var i = 0; i < lunchPersons.length; i++) {
        lunchPersons[i] = new LunchPerson(result.rows[i][0], result.rows[i][1], result.rows[i][2], result.rows[i][3], result.rows[i][4],
            result.rows[i][5], result.rows[i][6], result.rows[i][7], result.rows[i][8], result.rows[i][9], result.rows[i][10], result.rows[i][11], result.rows[i][12]);
    }
    return lunchPersons;
}

function parseInvitations(result) {
    var invitations = new Array(result.rows.length);

    for (var i = 0; i < invitations.length; i++) {
        invitations[i] = new Invitation(result.rows[i][0], result.rows[i][1], result.rows[i][2]);
    }
    return invitations;
}

function parseHistories(result) {
    var histories = new Array(result.rows.length);

    for (var i = 0; i < histories.length; i++) {
        histories[i] = new History(result.rows[i][0], result.rows[i][1], result.rows[i][2], result.rows[i][3]);
    }
    return histories;
}

function writeObject(object, response, string) {
    response.write("<" + string + ">\n");
    response.write(obj2xml(object));
    response.write("</" + string + ">\n");
    response.end();
}

function writeObjectArray(array, response, string, strings) {
    response.write("<" + strings + ">\n");
    for (var i = 0; i < array.length; i++) {
        response.write("<" + string + ">\n");
        response.write(obj2xml(array[i]));
        response.write("</" + string + ">\n");
    }
    response.write("</" + strings + ">");
    response.end();
}

class Person {
    constructor(PID, realname, gender, age, userid, passwd, imageuri, email, phone, hobbies) {
        this.PID = PID;
        this.realname = realname;
        this.gender = gender;
        this.age = age;
        this.userid = userid;
        this.passwd = passwd;
        this.imageuri = imageuri;
        this.email = email;
        this.phone = phone;
        this.hobbies = hobbies;
    }
}

class Lunch {
    constructor(LID, placeid, ldate, PID) {
        this.LID = LID;
        this.placeid = placeid;
        this.ldate = ldate;
        this.PID = PID;
    }
}

class LunchPerson {
    constructor(PID, realname, gender, age, userid, passwd, imageuri, email, phone, hobbies, LID, ldate, placeid) {
        this.PID = PID;
        this.realname = realname;
        this.gender = gender;
        this.age = age;
        this.userid = userid;
        this.passwd = passwd;
        this.imageuri = imageuri;
        this.email = email;
        this.phone = phone;
        this.hobbies = hobbies;
        this.LID = LID;
        this.ldate = ldate;
        this.placeid = placeid;
    }
}

class RegisterResult {
    constructor(success, useridUniqueError) {
        this.success = success;
        this.useridUniqueError = useridUniqueError;
    }
}

class LoginResult {
    constructor(success, noUsernameError, person) {
        this.success = success;
        this.noUsernameError = noUsernameError;
        this.person = person;
    }
}

class CreateLunchResult {
    constructor(success) {
        this.success = success;
    }
}

class Invitation {
    constructor(targetPID, PID, LID) {
        this.targetPID = targetPID;
        this.PID = PID;
        this.LID = LID;
    }
}

class History {
    constructor(targetPID, PID, placeid, ldate) {
        this.targetPID = targetPID;
        this.PID = PID;
        this.placeid = placeid;
        this.ldate = ldate;
    }
}
