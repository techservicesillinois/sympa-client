# sympa-client
List of functions to connect to Sympa server and access the services provided by it.

To Run (development):
mvn clean install

## mvn executable command with params for each sympa service API Calls:

### getList
mvn exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.SympaMain -Dexec.args="getList"

### createList (All parameters are mandatory)
mvn exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.SympaMain -Dexec.args="createList scrumTeamB scrumTeamB discussion_list description template"
where createList - Name of the service
scrumTeamB - Name of the mailing list to create
scrumTeamB - subject name of the list
discussion_list - the subject of the list 
description - description of the list
template - the name of a template found in the create_list_templates directory for this Sympa service

### add with authenticateAndRun
mvn exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.SympaMain -Dexec.args="authenticateAndRun add scrumTeamB,pbale@illinois.edu,true,true"
where authenticateAndRun - Name of the service
add -  Name of the service
scrumTeamB,xxx@illinois.edu,true,true - Parameters passed to add a user to the list 

### del with authenticateAndRun
mvn exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.SympaMain -Dexec.args="authenticateAndRun appName appPwd USER_EMAIL=xxx@illinois.edu del scrumTeamB,pbale@illinois.edu,true"
where authenticateAndRun - Name of the service
appName - Name of the remote application from which user is authenticating
appPwd - application md5 password
vars (USER_EMAIL=xxx@illinois.edu) - A comma separated list of variables that can be set by the remote application, If you list USER_EMAIL in this parameter, 
  then the remote application can act as a user. Any other variable such as remote_host can be listed.
del - Name of the service
scrumTeamB,xxx@illinois.edu,true,true - Parameters passed to delete a user to the list 

### getUserEmailByCookie
mvn exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.SympaMain -Dexec.args="getUserEmailByCookie "
where getUserEmailByCookie - user who sent the request


