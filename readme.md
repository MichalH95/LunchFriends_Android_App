###NÁVOD NA SPUŠTĚNÍ APLIKACE (s REST serverem a databází)

Aplikaci lze spustit na emulátoru nebo na reálném zařízení. Android projekt se nachází ve složce
LunchFriends, kterou lze otevřít v Android Studiu. Program lze spustit rovnou v Android Studiu, nebo
po instalaci .apk souboru v kořenovém adresáři.

Jako databázi lze použít Oracle Express Edition
(http://www.oracle.com/technetwork/database/database-technologies/express-edition/downloads/index.html),
nebo libovolnou jinou Oracle databázi, na které se spustí inicializační skripty z adresáře database.
Pokud je použita jiná databáze než Express Edition, je potřeba upravit přihlašovací údaje do databáze v souboru
REST_server/server.js (řádky 12, 13, 14).

Pro spuštění REST serveru je potřeba mít nainstalované node.js ve verzi alespoň 8 (https://nodejs.org/en/download/)
a potřebné knihovny. Knihovny lze nainstalovat pomocí následujících příkazů:

		npm install oracledb
		npm install http
		npm install object-to-xml
		npm install libxmljs

Server se spouští příkazem "node server.js" v adresáři se zdrojovými soubory REST_server. Po správném startu
program vypíše na konzoli "Listening on port 3000".


Po startu aplikace lze jako přihlašovací údaje použít tyto údaje:

		Uživ. ID:  Adam5
		Heslo:     Adam
