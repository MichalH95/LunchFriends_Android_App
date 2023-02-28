## Android application for bachelor thesis at the Czech Technical University in Prague

This is an Android application that lets you meet new people you can go on lunch with.

The application uses Oracle database and REST server written in node.js. It allows to log in with Google and Facebook accounts. Uses Google Places API to work with restaurants where people can go on lunch. Screenshots can be seen in the file `Presentation.pdf` (presentation is in czech language).

You can notice UI design and graphical display are not my strong points, but I mainly focused on designing the code and making the app work properly without any errors/crashes at all. I also focused on avoiding memory leaks, which are a common problem in Android applications. Avoiding memory leaks can be done through:

1. using SingleInstance pattern (class which inherits Application class) instead of Singleton pattern which should be avoided in Android apps,

2. using TaskFragment instead of creating AsyncTask as inner class which is a very common coding pattern but generates a memory leak when the Activity destroys before the task is finished.

### INSTRUCTIONS FOR STARTING THE APPLICATION (with REST server and database)

It is necessary to create a new Google Places API key since I can't share mine due to security reasons. The key should be inserted in `LunchFriends/app/src/main/AndroidManifest.xml` on line 33 and in `LunchFriends/app/src/main/java/huzevka/lunchfriends/activity/FriendResultsActivity.java` on line 593.

Application can be run on an emulator or on a real device. The Android project is located in the folder
LunchFriends, which can be opened in Android Studio. The program can be run directly in Android Studio, or
after installing the .apk file in the root directory.

Oracle Express Edition can be used as a database
(https://www.oracle.com/database/technologies/xe-downloads.html),
or any other Oracle database to run initialization scripts from the database directory.
If a database other than Express Edition is used, it is necessary to modify the user credentials for the database in the file `REST_server/server.js`, lines 12, 13, 14.

To run the REST server, you need to have node.js installed in at least version 8 (https://nodejs.org/en/download/)
and required libraries. Libraries can be installed using the following commands:

    npm install oracledb
    npm install http
    npm install object-to-xml
    npm install libxmljs

The server is started with the command `node server.js` in the directory with the REST_server source files. After a proper start the program displays `Listening on port 3000` on the console.

After launching the application, the following user data can be used to log in:

    User ID: Adam5
    Password: Adam

