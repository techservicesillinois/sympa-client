# sympa-client
List of functions to connect to Sympa server and access the services provided by it.


# Local setup

### Prerequisites
- Java 8 or Java 11 or Java 17+
- Maven

## Dependencies
Download the listed libraries and Set classpath to required libraries to run as a standalone java program. 
Or add these libraries to pom file.

| Library                       | Version | Purpose                                                               |
|-------------------------------|---------|-----------------------------------------------------------------------|
| javax.xml.soap-api            | 1.4.0   | Manual SOAP message construction, for legacy SOAP services.           |
| jaxws-api                     | 2.3.1   | WSDL-based client & server generation.                                |
| jakarta.xml.soap-api          | 3.0.2   | Fine grain control over SOAP messages.                                |
| jakarta.jws-api               | 3.0.0   | Defines annotations for web services ex: @WebMethod, @WebParam.       |
| jakarta.xml.bind-api          | 4.0.0   | XML to Java binding.                                                  |
| jakarta.xml.ws-api            | 4.0.0   | To expose or consume SOAP services.                                   |
| jaxws-ri                      | 4.0.1   | Distribution bundle.                                                  |
| jaxws-rt                      | 4.0.2   | Engine to run web service. runtime + glue to binding (JAXB)           |
| jaxb-runtime                  | 4.0.5   | Marshalling & unmarshalling Java<->Xml                                |                           

## Java 8 Dependencies
Download the listed libraries and Set classpath to required libraries to run as a standalone java program. 
Or add these libraries to pom file.

| Library                       | Version | Purpose                                                               |
|-------------------------------|---------|-----------------------------------------------------------------------|
| javax.xml.soap-api            | 1.4.0   | Manual SOAP message construction, for legacy SOAP services.           |
| jaxws-api                     | 2.3.1   | WSDL-based client & server generation.                                |
| jaxws-ri                      | 4.0.1   | Distribution bundle.                                                  |
| jaxws-rt                      | 4.0.2   | Engine to run web service. runtime + glue to binding (JAXB)           |
| FastInfoset                   | 1.2.18  | Specifies binary encoding format for the XML Information Set          |
| jaxb-runtime                  | 4.0.5   | Marshalling & unmarshalling Java<->Xml                                |                           

### Maven Dependencies
```xml
<dependencies>
  <dependency>
    <groupId>javax.xml.soap</groupId>
    <artifactId>javax.xml.soap-api</artifactId>
    <version>1.4.0</version>
  </dependency>
  <dependency>
    <groupId>javax.xml.ws</groupId>
    <artifactId>jaxws-api</artifactId>
    <version>2.3.1</version>
  </dependency>
  <dependency>
      <groupId>jakarta.xml.soap</groupId>
      <artifactId>jakarta.xml.soap-api</artifactId>
      <version>3.0.2</version>
  </dependency>
  <dependency>
    <groupId>jakarta.jws</groupId>
    <artifactId>jakarta.jws-api</artifactId>
    <version>3.0.0</version>
  </dependency>
  <dependency>
    <groupId>jakarta.xml.bind</groupId>
    <artifactId>jakarta.xml.bind-api</artifactId>
    <version>4.0.0</version>
  </dependency>
  <dependency>
    <groupId>jakarta.xml.ws</groupId>
    <artifactId>jakarta.xml.ws-api</artifactId>
    <version>4.0.0</version>
  </dependency>
  <dependency>
    <groupId>com.sun.xml.ws</groupId>
    <artifactId>jaxws-ri</artifactId>
    <version>4.0.1</version>
    <scope>runtime</scope>
    <type>pom</type>
  </dependency>
  <dependency>
    <groupId>com.sun.xml.ws</groupId>
    <artifactId>jaxws-rt</artifactId>
    <version>4.0.2</version>
    <type>pom</type>
  </dependency>
  <dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <version>4.0.5</version>
    <type>pom</type>
  </dependency>
</dependencies>
```

### Java 8 Maven Dependencies
```xml
<dependencies>
  <dependency>
    <groupId>javax.xml.soap</groupId>
    <artifactId>javax.xml.soap-api</artifactId>
    <version>1.4.0</version>
  </dependency>
  <dependency>
    <groupId>javax.xml.ws</groupId>
    <artifactId>jaxws-api</artifactId>
    <version>2.3.1</version>
  </dependency>
  <dependency>
    <groupId>com.sun.xml.ws</groupId>
    <artifactId>jaxws-ri</artifactId>
    <version>4.0.1</version>
    <scope>runtime</scope>
    <type>pom</type>
  </dependency>
  <dependency>
    <groupId>com.sun.xml.ws</groupId>
    <artifactId>jaxws-rt</artifactId>
    <version>4.0.2</version>
    <type>pom</type>
  </dependency>
  <dependency>
    <groupId>com.sun.xml.fastinfoset</groupId>
    <artifactId>FastInfoset</artifactId>
    <version>1.2.18</version>
  </dependency>
  <dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <version>4.0.5</version>
    <type>pom</type>
  </dependency>
</dependencies>
```



### Using Maven:
```bash
mvn clean install
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.javaclass
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL} 
                  -Dexec.args=parameters"
```

### mvn executable command with params for each sympa service API Calls:

#### info
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.Info 
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL} 
                  -Dexec.args="listName"
```
example usage:
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.info 
                  -Dsympa.email=xxx@email.com 
                  -Dsympa.password=changeme 
                  -Dsympa.url=changeme 
                  -Dexec.args="xxx"
```

* listName - name of the list

#### getList
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.GetList 
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL} 
```
example usage: 
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.GetList
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL}  
```

#### createList (All parameters are mandatory)
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.CreateList
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL}
                  -Dexec.args="listName subject template description topics"
```
example usage:
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.CreateList
                  -Dsympa.email=xxx@email.com 
                  -Dsympa.password=changeme 
                  -Dsympa.url=changeme
                  -Dexec.args="scrumTeamB scrumTeamB discussion_list description template"
```

* list - scrumTeamB, Name of the mailing list to create
* subject - scrumTeamB - subject name of the list
* template - discussion_list, the subject of the list 
* description - description of the list
* topics - the name of a template found in the create_list_templates directory for this Sympa service

#### add/del with authenticateAndRun
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.AuthenticateAndRun 
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL} 
                  -Dexec.args="service service-parameters"
```
example usage:
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.AuthenticateAndRun 
                  -Dsympa.email=xxx@email.com 
                  -Dsympa.password=changeme 
                  -Dsympa.url=changeme
                  -Dexec.args="add scrumTeamB,pbale@illinois.edu,true,true"
```

* service - add/del -  Name of the service 
* service-parameters for add - scrumTeamB,xxx@illinois.edu,true,true - Parameters passed to add a user to the list 
* service-parameters for del - scrumTeamB,xxx@illinois.edu,true - Parameters passed to del a user to the list 

#### add/del with authenticateRemoteAppAndRun
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.AuthenticateRemoteAppAndRun 
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL} 
                  -Dexec.args="appName appPwd vars service service-parameters"
```
example usage:
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.AuthenticateRemoteAppAndRun 
                  -Dsympa.email=xxx@email.com 
                  -Dsympa.password=changeme 
                  -Dsympa.url=changeme
                  -Dexec.args="appName appPwd USER_EMAIL=xxx@illinois.edu 
                  del scrumTeamB,xxx@illinois.edu,true"
```

* appName - Name of the remote application from which user is authenticating
* appPwd - application md5 password
* vars (USER_EMAIL=xxx@illinois.edu) - A comma separated list of variables that can be set by the remote application, If you list USER_EMAIL in this parameter, then the remote application can act as a user. Any other variable such as remote_host can be listed.
* service - del, Name of the service
* service-parameters for add - scrumTeamB,xxx@illinois.edu,true,true - Parameters passed to delete a user to the list 
* service-parameters for del - scrumTeamB,xxx@illinois.edu,true - Parameters passed to delete a user to the list 

#### getUserEmailByCookie
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.GetUserEmailByCookie
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL} 
```
example usage:
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.GetUserEmailByCookie 
                  -Dsympa.email=xxx@email.com 
                  -Dsympa.password=changeme 
                  -Dsympa.url=changeme
```

#### subscribe
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.Subscribe
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL}
                  -Dexec.args="list"
```
example usage:
```bash
mvn -q exec:java  -Dexec.mainClass=edu.illinois.techservices.sympa.Subscribe
                  -Dsympa.email=xxx@email.com 
                  -Dsympa.password=changeme 
                  -Dsympa.url=changeme
                  -Dexec.args="scrumTeamB"
```

* list - scrumTeamB, Name of the list to be subscribed to  

#### review
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.Review
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL}
                  -Dexec.args="list"
```
example usage:
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.Review
                  -Dsympa.email=xxx@email.com 
                  -Dsympa.password=changeme 
                  -Dsympa.url=changeme
                  -Dexec.args="scrumTeamB"
```

* list - scrumTeamB, Name of the list

#### fullReview
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.FullReviewBusinessCase
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL}
                  -Dexec.args="list type"
```
example usage:
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.FullReviewBusinessCase
                  -Dsympa.email=xxx@email.com 
                  -Dsympa.password=changeme 
                  -Dsympa.url=changeme  
                  -Dexec.args="scrumTeamB subscriber"
```

* list - scrumTeamB, Name of the list
* type - permission type to enumerate through the list. values can be (subscriber, owner, editor)

#### add
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.AddUserToList
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL}
                  -Dexec.args="list user gecos quiet"
```
example usage:
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.AddUserToList
                  -Dsympa.email=xxx@email.com 
                  -Dsympa.password=changeme 
                  -Dsympa.url=changeme
                  -Dexec.args="scrumTeamB pbale@illinois.edu true true"
```

* list - list name to which the user is added
* user - user email to be added to list
* gecos - a boolean
* quiet - boolean

#### del
```bash
mvn -q exec:java  -Dexec.mainClass=edu.illinois.techservices.sympa.DelUserFromList
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL}
                  -Dexec.args="parameters"
```
example usage:
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.DelUserFromList
                  -Dsympa.email=xxx@email.com 
                  -Dsympa.password=changeme 
                  -Dsympa.url=changeme
                  -Dexec.args="scrumTeamB pbale@illinois.edu true"
```

* list - list name from which the user to delete
* user - user email to be deleted from the list
* gecos - a boolean

#### closeList
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.CloseList 
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL}
                  -Dexec.args="list"
```
example usage:
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.CloseList 
                  -Dsympa.email=xxx@email.com 
                  -Dsympa.password=changeme 
                  -Dsympa.url=changeme
                  -Dexec.args="scrumTeamB"
```

* list - scrumTeamB, Name of the list to be closed. 

#### complexList
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.ComplexList
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL}
                  -Dexec.args="list"
```
example usage:
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.ComplexList
                  -Dsympa.email=xxx@email.com 
                  -Dsympa.password=changeme 
                  -Dsympa.url=changeme
                  -Dexec.args="listName"
```

#### which
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.Which
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL}
```
example usage:
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.Which
                  -Dsympa.email=xxx@email.com 
                  -Dsympa.password=changeme 
                  -Dsympa.url=changeme
```

#### complexwhich
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.ComlexWhich
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL}
```
example usage:
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.ComplexWhich
                  -Dsympa.email=xxx@email.com 
                  -Dsympa.password=changeme 
                  -Dsympa.url=changeme
```


#### AmI
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.AmI
                  -Dsympa.email=${SYMPA_EMAIL} 
                  -Dsympa.password=${SYMPA_PASSWORD} 
                  -Dsympa.url=${SYMPA_URL}
                  -Dexec.args="list function user"

```
example usage:
```bash
mvn -q exec:java -Dexec.mainClass=edu.illinois.techservices.sympa.AmI
                  -Dsympa.email=xxx@email.com 
                  -Dsympa.password=changeme 
                  -Dsympa.url=changeme
                  -Dexec.args="scrumTeamB subscriber xxx@email.edu"

```

* list - scrumTeamB, Name of the list to be closed. 
* function - subscriber/owner/editor 
* user - user email 
