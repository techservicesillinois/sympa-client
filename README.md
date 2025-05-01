# sympa-client
List of functions to connect to Sympa server and access the services provided by it.

<<<<<<< HEAD
To Run (development):
```
mvn compile
mvn exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.Main -Dexec.args="getList"
```
This makes a call on `getList` API Call
=======
## mvn executable command with params for each sympa service:

### getList
mvn exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.SympaMain -Dexec.args=getList

### createList
mvn exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.SympaMain -Dexec.args="createList scrumTeamB"
where createList - Name of the service
scrumTeamB - Name of the list to create

### add with authenticateAndRun
mvn exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.SympaMain -Dexec.args="authenticateAndRun add scrumTeamB,pbalex@illinois.edu,true,true"
where authenticateAndRun - Name of the service
add -  Name of the service
scrumTeamB,pbale@illinois.edu,true,true - Parameters passed to add a user to the list 

### del with authenticateRemoteAppAndRun
mvn exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.SympaMain -Dexec.args="authenticateRemoteAppAndRun del scrumTeamB,pbale@illinois.edu,true"
where authenticateAndRun - Name of the service
del - Name of the service
scrumTeamB,pbale@illinois.edu,true,true - Parameters passed to delete a user to the list 

### getUserEmailByCookie
mvn exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.SympaMain -Dexec.args="getUserEmailByCookie "
where getUserEmailByCookie - user who sent the request


>>>>>>> f32ae30 (Document  passing attribute values to getList, createList, authenticateAndRun for add and del.)
