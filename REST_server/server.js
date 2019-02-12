var oracledb = require('oracledb');
var http = require('http');
var obj2xml = require('object-to-xml');
var libxmljs = require("libxmljs");
var handler = require('./handleRequest');

var connPool = null;

function init() {
    oracledb.createPool(
        {
        user          : "hr",
        password      : "hr",
        connectString : "localhost:1521/XE"
        },
        function(err, pool) {
            connPool = pool;
            if (err) {
                console.error("createPool() error: " + err.message);
                return;
            }

            http.createServer(function(request, response) {
                    handler.handleRequest(request, response, pool);
                })
                .listen(3000);

            console.log("Listening on port 3000");
        }
    );
}

process
    .on('SIGTERM', function() {
        console.log("\nTerminating");
        if ( connPool != null ) {
            connPool.close(function (err) {
                if (err)
                    console.error("Error closing pool: " + err.message);
            });
        }
        process.exit(0);
    })
    .on('SIGINT', function() {
        console.log("\nTerminating");
        if ( connPool != null ) {
            connPool.close(function (err) {
                if (err)
                    console.error("Error closing pool: " + err.message);
            });
        }
        process.exit(0);
    });


init();
