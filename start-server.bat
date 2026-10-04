@echo off
echo Starting CampusGig Server...
start http://localhost:8080/CampusGig/browse-gigs.html
"C:\Program Files\Apache Software Foundation\Tomcat 10.1\bin\catalina.bat" run
