# SOAP SERVICE USAGE

Note: Almost all the service calls require session cookie to establish the connection to sympa service. Please see JavaSOAP.md on how to grab a session cookie.

# Building SOAP Requests for Sympa Services

## Service - SUBSCRIBE
### Initialize the MessageFactory and SOAPMessage:
```java
MessageFactory messageFactory = MessageFactory.newInstance();
SOAPMessage soapMessage = messageFactory.createMessage();
SOAPPart soapPart = soapMessage.getSOAPPart();

SOAPEnvelope envelope = soapPart.getEnvelope();
envelope.addNamespaceDeclaration("ns", "urn:sympasoap");
envelope.addNamespaceDeclaration("soapenc", "http://schema.xmlsoap.org/soap/encoding/");
envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
envelope.addNamespaceDeclaration("targetNamespace", "https://lists-dev.techservices.illinois.edu/lists/wsdl");
envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

SOAPBody soapBody = envelope.getBody();
```
### Initialize/set SOAP envelope elements:
```java
// Set the API call for `which`
SOAPElement soapElement = soapBody.addChildElement("subscribe", "ns", "urn:sympasoap");

// Add list naem to the SOAP request
soapElement.addChildElement("list", "ns")
                    .addTextNode(listName)
                    .addAttribute(new QName("xsi:type"), "xsd:string");
MimeHeaders headers = soapMessage.getMimeHeaders();

headers.addHeader("Authorization", encodedAuth);
headers.addHeader("Content-Type", "text/xml"); 
headers.addHeader("SOAPAction", "urn:sympasoap#subscribe"); 
headers.addHeader("RequestMethod", "POST");

// Save the changes to the SOAP message
soapMessage.saveChanges();
```
### Submit 'SUBSCRIBE' service request

```java

SOAPMessage response = null;
SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
SOAPConnection soapConnection = soapConnectionFactory.createConnection();
response = soapConnection.call(soapMessage, "https://lists-dev.techservices.illinois.edu/sympasoap");
soapConnection.close();
```
This should build a request that looks like this:

```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
  xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:ns="urn:sympasoap" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
  xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:targetNamespace="https://lists-dev.techservices.illinois.edu/lists/wsdl"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
  <SOAP-ENV:Header/>
  <SOAP-ENV:Body>
    <ns:subscribe>
      <ns:list xsi:type="xsd:string">pbale@illinois.edu</ns:list>
    </ns:subscribe>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```
Successfull (200) response payload looks like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
  xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <soap:Body>
    <subscribeResponse xmlns="urn:sympasoap">
      <result xsi:type="xsd:boolean">true</result>
    </subscribeResponse>
  </soap:Body>
</soap:Envelope>
```

## Service - REVIEW
### Initialize the MessageFactory and SOAPMessage:
```java
MessageFactory messageFactory = MessageFactory.newInstance();
SOAPMessage soapMessage = messageFactory.createMessage();
SOAPPart soapPart = soapMessage.getSOAPPart();

SOAPEnvelope envelope = soapPart.getEnvelope();
envelope.addNamespaceDeclaration("ns", "urn:sympasoap");
envelope.addNamespaceDeclaration("soapenc", "http://schema.xmlsoap.org/soap/encoding/");
envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
envelope.addNamespaceDeclaration("targetNamespace", "https://lists-dev.techservices.illinois.edu/lists/wsdl");
envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

SOAPBody soapBody = envelope.getBody();
```
### Initialize/set SOAP envelope elements:
```java
// Set the API call for `review`
SOAPElement soapElement = soapBody.addChildElement("review", "ns", "urn:sympasoap");
soapElement.addChildElement("list", "ns")
          .addTextNode(listName)
          .addAttribute(new QName("xsi:type"), "xsd:string");

//Add required headers          
MimeHeaders headers = soapMessage.getMimeHeaders();

headers.addHeader("Authorization", encodedAuth);
headers.addHeader("Content-Type", "text/xml"); 
headers.addHeader("SOAPAction", "urn:sympasoap#subscribe"); 
headers.addHeader("RequestMethod", "POST");

soapMessage.saveChanges();
```
### Submit 'REVIEW' service request

```java

SOAPMessage response = null;
SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
SOAPConnection soapConnection = soapConnectionFactory.createConnection();
response = soapConnection.call(soapMessage, "https://lists-dev.techservices.illinois.edu/sympasoap");
soapConnection.close();
```
This should build a request that looks like this:

```xml
<SOAP-ENV:Envelope
  xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
  xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:ns="urn:sympasoap"
  xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
  xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:targetNamespace="https://lists-dev.techservices.illinois.edu/lists/wsdl"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
  <SOAP-ENV:Header/>
  <SOAP-ENV:Body>
    <ns:review>
      <ns:list xsi:type="xsd:string">scrumTeamB</ns:list>
    </ns:review>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

response payload looks like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
  <soap:Envelope
    soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"
    xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <soap:Body>
      <reviewResponse xmlns="urn:sympasoap">
      <return soapenc:arrayType="xsd:string[7]" xsi:type="soapenc:Array">
        <item xsi:type="xsd:string">aosar@illinois.edu</item>
        <item xsi:type="xsd:string">pbale4@illinois.edu</item>
        <item xsi:type="xsd:string">pbalex@illinois.edu</item>
        <item xsi:type="xsd:string">pbaleyx@illinois.edu</item>
        <item xsi:type="xsd:string">rstanton@illinois.edu</item>
        <item xsi:type="xsd:string">scrumteamb@illinois.edu</item>
        <item xsi:type="xsd:string">xyz123@gmail.com</item>
      </return>
    </reviewResponse>
  </soap:Body>
</soap:Envelope>
```

## Service - FULLREVIEW
### Initialize the MessageFactory and SOAPMessage:
```java
MessageFactory messageFactory = MessageFactory.newInstance();
SOAPMessage soapMessage = messageFactory.createMessage();
SOAPPart soapPart = soapMessage.getSOAPPart();

SOAPEnvelope envelope = soapPart.getEnvelope();
envelope.addNamespaceDeclaration("ns", "urn:sympasoap");
envelope.addNamespaceDeclaration("soapenc", "http://schema.xmlsoap.org/soap/encoding/");
envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
envelope.addNamespaceDeclaration("targetNamespace", "https://lists-dev.techservices.illinois.edu/lists/wsdl");
envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

SOAPBody soapBody = envelope.getBody();
```
### Initialize/set SOAP envelope elements:
```java
// Set the API call for `review`
SOAPElement soapElement = soapBody.addChildElement("fullReview", "ns", "urn:sympasoap");
soapElement.addChildElement("list", "ns")
          .addTextNode(listName)
          .addAttribute(new QName("xsi:type"), "xsd:string");

//Add required headers          
MimeHeaders headers = soapMessage.getMimeHeaders();

headers.addHeader("Authorization", encodedAuth);
headers.addHeader("Content-Type", "text/xml"); 
headers.addHeader("SOAPAction", "urn:sympasoap#fullReview"); 
headers.addHeader("RequestMethod", "POST");

soapMessage.saveChanges();
```
### Submit 'FULLREVIEW' service request

```java
SOAPMessage response = null;
SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
SOAPConnection soapConnection = soapConnectionFactory.createConnection();
response = soapConnection.call(soapMessage, "https://lists-dev.techservices.illinois.edu/sympasoap");
soapConnection.close();
```
This should build a request that looks like this:

```xml
<SOAP-ENV:Envelope
  xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
  xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:ns="urn:sympasoap" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
  xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:targetNamespace="https://lists-dev.techservices.illinois.edu/lists/wsdl"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
    <ns:fullReview>
      <ns:list xsi:type="xsd:string">scrumTeamB</ns:list>
    </ns:fullReview>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

response payload looks like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope
  soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
  xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <soap:Body>
  	<fullReviewResponse xmlns="urn:sympasoap">
  		<return soapenc:arrayType="xsd:anyType[8]" xsi:type="soapenc:Array">
  			<item>
  				<isOwner xsi:type="xsd:boolean">false</isOwner>
  				<isSubscriber xsi:type="xsd:boolean">true</isSubscriber>
  				<email xsi:type="xsd:string">xyz123@gmail.com</email>
  				<isEditor xsi:type="xsd:boolean">false</isEditor>
  				<gecos xsi:type="xsd:string">true</gecos>
  			</item>
  			<item>
  				<isEditor xsi:type="xsd:boolean">false</isEditor>
  				<gecos xsi:type="xsd:string" />
  				<isSubscriber xsi:type="xsd:boolean">true</isSubscriber>
  				<isOwner xsi:type="xsd:boolean">false</isOwner>
  				<email xsi:type="xsd:string">scrumteamb@illinois.edu</email>
  			</item>
  			<item>
  				<isSubscriber xsi:type="xsd:boolean">true</isSubscriber>
  				<isOwner xsi:type="xsd:boolean">false</isOwner>
  				<email xsi:type="xsd:string">were4@illinois.edu</email>
  				<isEditor xsi:type="xsd:boolean">false</isEditor>
  				<gecos xsi:type="xsd:string">true</gecos>
  			</item>
  				
  			<item>
  				<email xsi:type="xsd:string">zzz@illinois.edu</email>
  				<isOwner xsi:type="xsd:boolean">true</isOwner>
  				<isSubscriber xsi:type="xsd:boolean">false</isSubscriber>
  				<gecos xsi:type="xsd:string">true</gecos>
  				<isEditor xsi:type="xsd:boolean">false</isEditor>
  			</item>
  			<item>
  				<isOwner xsi:type="xsd:boolean">false</isOwner>
  				<isSubscriber xsi:type="xsd:boolean">true</isSubscriber>
  				<email xsi:type="xsd:string">xnj@illinois.edu</email>
  				<isEditor xsi:type="xsd:boolean">false</isEditor>
  				<gecos xsi:type="xsd:string">true</gecos>
  			</item>
  			<item>
  				<isSubscriber xsi:type="xsd:boolean">true</isSubscriber>
  				<isOwner xsi:type="xsd:boolean">false</isOwner>
  				<email xsi:type="xsd:string">abc@illinois.edu</email>
  				<isEditor xsi:type="xsd:boolean">false</isEditor>
  				<gecos xsi:type="xsd:string">true</gecos>
  			</item>
  			<item>
  				<email xsi:type="xsd:string">a123@illinois.edu</email>
  				<isOwner xsi:type="xsd:boolean">false</isOwner>
  				<isSubscriber xsi:type="xsd:boolean">true</isSubscriber>
  				<gecos xsi:type="xsd:string">true</gecos>
  				<isEditor xsi:type="xsd:boolean">false</isEditor>
  			</item>
  			<item>
  				<isOwner xsi:type="xsd:boolean">false</isOwner>
  				<isSubscriber xsi:type="xsd:boolean">true</isSubscriber>
  				<email xsi:type="xsd:string">frt5@illinois.edu</email>
  				<isEditor xsi:type="xsd:boolean">false</isEditor>
  				<gecos xsi:type="xsd:string">true</gecos>
  			</item>
  		</return>
    </fullReviewResponse>
  </soap:Body>
</soap:Envelope>
```

## Service - AUTHENTICATEANDRUN
### Initialize the MessageFactory and SOAPMessage:
```java
MessageFactory messageFactory = MessageFactory.newInstance();
SOAPMessage soapMessage = messageFactory.createMessage();
SOAPPart soapPart = soapMessage.getSOAPPart();

SOAPEnvelope envelope = soapPart.getEnvelope();
envelope.addNamespaceDeclaration("ns", "urn:sympasoap");
envelope.addNamespaceDeclaration("soapenc", "http://schema.xmlsoap.org/soap/encoding/");
envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
envelope.addNamespaceDeclaration("targetNamespace", "https://lists-dev.techservices.illinois.edu/lists/wsdl");
envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

SOAPBody soapBody = envelope.getBody();
```
### Initialize/set SOAP envelope elements:
```java
// Set the API call for `authenticateAndRun`
SOAPElement soapElement = soapBody.addChildElement("authenticateAndRun", "ns", "urn:sympasoap");
soapElement.addChildElement("email")
          .addTextNode("xyz@illinois.edu")
          .addAttribute(new QName("xsi:type"), "xsd:string");

soapElement.addChildElement("cookie")
          .addTextNode(cookie)
          .addAttribute(new QName("xsi:type"), "xsd:string");

soapElement.addChildElement("service")
          .addTextNode("add")  //Can be add/del
          .addAttribute(new QName("xsi:type"), "xsd:string");

// Parameters to perform specific service like (add, del)
SOAPElement items = soapElement.addChildElement("parameters", "ns");
items.addAttribute(new QName("xsi:type"), "SOAP-ENC:Array");
items.addAttribute(new QName("SOAP-ENC:arrayType"), "xsd:string[" + parameters.size() + "]");
parameters = {"scrumTeamB","abc@illinois.edu",true,true}
for (String param : parameters) {
  SOAPElement item = items.addChildElement("item");
  item.addTextNode(param);
  item.addAttribute(new QName("xsi:type"), "xsd:string");
}

//Add required headers          
MimeHeaders headers = soapMessage.getMimeHeaders();

headers.addHeader("Authorization", encodedAuth);
headers.addHeader("Content-Type", "text/xml"); 
headers.addHeader("SOAPAction", "urn:sympasoap#authenticateAndRun"); 
headers.addHeader("RequestMethod", "POST");

soapMessage.saveChanges();
```
### Submit 'AUTHENTICATEANDRUN' service request

```java
SOAPMessage response = null;
SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
SOAPConnection soapConnection = soapConnectionFactory.createConnection();
response = soapConnection.call(soapMessage, "https://lists-dev.techservices.illinois.edu/sympasoap");
soapConnection.close();
```
This should build a request that looks like this:

```xml
<SOAP-ENV:Envelope
  xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
  xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:ns="urn:sympasoap" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
  xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:targetNamespace="https://lists-dev.techservices.illinois.edu/lists/wsdl"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
    <ns:authenticateAndRun>
      <email xsi:type="xsd:string">yyy@illinois.edu</email>
      <cookie xsi:type="xsd:string">63372725802375</cookie>
      <service xsi:type="xsd:string">add</service>
        <ns:parameters SOAP-ENC:arrayType="xsd:string[4]" xsi:type="SOAP-ENC:Array">
          <item xsi:type="xsd:string">scrumTeamB</item>
          <item xsi:type="xsd:string">xxx@illinois.edu</item>
          <item xsi:type="xsd:string">true</item>
          <item xsi:type="xsd:string">true</item>
        </ns:parameters>
      </ns:authenticateAndRun>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

response payload looks like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope
  soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
  xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <soap:Body>
  	<authenticateAndRunResponse xmlns="urn:sympasoap">
      <result xsi:type="xsd:boolean">true</result>
    </authenticateAndRunResponse>
  </soap:Body>
</soap:Envelope>
```


## Service - getList
### Initialize the MessageFactory and SOAPMessage:
```java
MessageFactory messageFactory = MessageFactory.newInstance();
SOAPMessage soapMessage = messageFactory.createMessage();
SOAPPart soapPart = soapMessage.getSOAPPart();

SOAPEnvelope envelope = soapPart.getEnvelope();
envelope.addNamespaceDeclaration("ns", "urn:sympasoap");
envelope.addNamespaceDeclaration("soapenc", "http://schema.xmlsoap.org/soap/encoding/");
envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
envelope.addNamespaceDeclaration("targetNamespace", "https://lists-dev.techservices.illinois.edu/lists/wsdl");
envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

SOAPBody soapBody = envelope.getBody();
```
### Initialize/set SOAP envelope elements:
```java
// Set the API call for `review`
SOAPElement soapElement = soapBody.addChildElement("lists", "ns", "urn:sympasoap");

//Add required headers          
MimeHeaders headers = soapMessage.getMimeHeaders();

headers.addHeader("Authorization", encodedAuth);
headers.addHeader("Content-Type", "text/xml"); 
headers.addHeader("SOAPAction", "urn:sympasoap#lists"); 
headers.addHeader("RequestMethod", "POST");

soapMessage.saveChanges();
```
### Submit 'LISTS' service request

```java
SOAPMessage response = null;
SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
SOAPConnection soapConnection = soapConnectionFactory.createConnection();
response = soapConnection.call(soapMessage, "https://lists-dev.techservices.illinois.edu/sympasoap");
soapConnection.close();
```
This should build a request that looks like this:

```xml
<SOAP-ENV:Envelope
  xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
  xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:ns="urn:sympasoap" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
  xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:targetNamespace="https://lists-dev.techservices.illinois.edu/lists/wsdl"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
      <ns:lists/>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

response payload looks like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope
  soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
  xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <soap:Body>
  	<listsResponse xmlns="urn:sympasoap">
      <listInfo soapenc:arrayType="xsd:string[11]" xsi:type="soapenc:Array">
        <item xsi:type="xsd:string">
          homepage=https://lists-dev.techservices.illinois.edu/lists/info/samplelist1;
          listAddress=samplelist1@lists-dev.techservices.illinois.edu;subject=samplelist1
        </item>
        <item xsi:type="xsd:string">
          homepage=https://lists-dev.techservices.illinois.edu/lists/info/samplelist2;
          listAddress=samplelist2@lists-dev.techservices.illinois.edu;subject=samplelist2
        </item>
        <item xsi:type="xsd:string">
          homepage=https://lists-dev.techservices.illinois.edu/lists/info/samplelist3;
          listAddress=samplelist3@lists-dev.techservices.illinois.edu;subject=samplelist3
        </item>
        <item xsi:type="xsd:string">
          homepage=https://lists-dev.techservices.illinois.edu/lists/info/samplelist4;
          listAddress=samplelist4@lists-dev.techservices.illinois.edu;subject=samplelist4
        </item>
      </listInfo>
    </listsResponse>
  </soap:Body>
</soap:Envelope>
```

## Service - CREATELIST
### Initialize the MessageFactory and SOAPMessage:
```java
MessageFactory messageFactory = MessageFactory.newInstance();
SOAPMessage soapMessage = messageFactory.createMessage();
SOAPPart soapPart = soapMessage.getSOAPPart();

SOAPEnvelope envelope = soapPart.getEnvelope();
envelope.addNamespaceDeclaration("ns", "urn:sympasoap");
envelope.addNamespaceDeclaration("soapenc", "http://schema.xmlsoap.org/soap/encoding/");
envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
envelope.addNamespaceDeclaration("targetNamespace", "https://lists-dev.techservices.illinois.edu/lists/wsdl");
envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

SOAPBody soapBody = envelope.getBody();
```
### Initialize/set SOAP envelope elements:
```java
// Set the API call for `review`
SOAPElement soapElement = soapBody.addChildElement("createlist", "ns", "urn:sympasoap");

//Add required headers          
MimeHeaders headers = soapMessage.getMimeHeaders();

headers.addHeader("Authorization", encodedAuth);
headers.addHeader("Content-Type", "text/xml"); 
headers.addHeader("SOAPAction", "urn:sympasoap#createlist"); 
headers.addHeader("RequestMethod", "POST");

soapMessage.saveChanges();
```
### Submit 'CREATELIST' service request

```java
SOAPMessage response = null;
SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
SOAPConnection soapConnection = soapConnectionFactory.createConnection();
response = soapConnection.call(soapMessage, "https://lists-dev.techservices.illinois.edu/sympasoap");
soapConnection.close();
```
This should build a request that looks like this:

```xml
<SOAP-ENV:Envelope
  xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
  xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:ns="urn:sympasoap" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
  xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:targetNamespace="https://lists-dev.techservices.illinois.edu/lists/wsdl"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
      <ns:createList>
        <ns:list xsi:type="xsd:string">sampleList1</ns:list>
        <ns:subject xsi:type="xsd:string">subject</ns:subject>
        <ns:template xsi:type="xsd:string">discussion_list</ns:template>
        <ns:description xsi:type="xsd:string">description</ns:description>
        <ns:topic xsi:type="xsd:string">template</ns:topic>
      </ns:createList>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

response payload looks like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope
  soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
  xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <soap:Body>
  	<createListResponse xmlns="urn:sympasoap">
      <result xsi:type="xsd:boolean">true</result>
    </createListResponse>
  </soap:Body>
</soap:Envelope>
```

