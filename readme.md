
## INSTRUCTIONS FOR STARTING THE APPLICATION (with REST server and database)

Application can be run on an emulator or on a real device. The Android project is located in the folder
LunchFriends, which can be opened in Android Studio. The program can be run directly in Android Studio, or
after installing the .apk file in the root directory.

Oracle Express Edition can be used as a database
(https://www.oracle.com/database/technologies/xe-downloads.html),
or any other Oracle database to run initialization scripts from the database directory.
If a database other than Express Edition is used, it is necessary to modify the user credentials for the database in the file `REST_server/server.js` (lines 12, 13, 14).

To run the REST server, you need to have node.js installed in at least version 8 (https://nodejs.org/en/download/)
and required libraries. Libraries can be installed using the following commands:

    npm install oracledb
    npm install http
    npm install object-to-xml
    npm install libxmljs

The server is started with the command `node server.js` in the directory with the REST_server source files. After a proper start the program displays `Listening on port 3000` on the console.

After launching the application, use the following user data to log in:

    User ID: Adam5
    Password: Adam

