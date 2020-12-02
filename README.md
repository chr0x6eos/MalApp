# MalApp
## Purpose
This repository contains the source code of the malware "MalApp". It was created during a software engineering project and is purely for educational purpose.

## www-server
The 'innocent' calculator application downloads the malware from the www-server. The url of the www-server will be placed in the [strings.xml](https://github.com/posseggs/MalApp/blob/master/Calculator/app/src/main/res/values/strings.xml) file.
The www-server is rather simple to implement: First of goto /var/www/html.
Copy the [download.php](https://github.com/posseggs/MalApp/blob/master/www-server/download.php) file to /var/www/html. Then create a data folder and place the malware in this folder.
The server will be started by following command: `service apache2 start`.
