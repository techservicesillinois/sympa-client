# sympa-client
List of functions to connect to Sympa server and access the services provided by it.

To Run (development):
```
mvn compile
mvn exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.Main -Dexec.args="getList"
```
This makes a call on `getList` API Call