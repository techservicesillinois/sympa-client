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
SOAPElement soapElement = soapBody.addChildElement("which", "ns", "urn:sympasoap");

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
### Submit 'WHICH' service request

```java

SOAPMessage response = null;
SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
SOAPConnection soapConnection = soapConnectionFactory.createConnection();
response = soapConnection.call(soapMessage, "https://lists-dev.techservices.illinois.edu/sympasoap");
soapConnection.close();
```
This should build a request that looks like this:

```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:ns="urn:sympasoap" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" xmlns:targetNamespace="https://lists-dev.techservices.illinois.edu/lists/wsdl" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><SOAP-ENV:Header/><SOAP-ENV:Body><ns:subscribe><ns:list xsi:type="xsd:string">pbale@illinois.edu</ns:list></ns:subscribe></SOAP-ENV:Body></SOAP-ENV:Envelope>
```
response payload looks like this:

```xml
<?xml version="1.0" encoding="UTF-8"?><soap:Envelope soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"><soap:Body><soap:Fault><faultcode>soap:Server</faultcode><faultstring>Unknown list</faultstring><detail xsi:type="xsd:string">List pbale@illinois.edu unknown</detail></soap:Fault></soap:Body></soap:Envelope>
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
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:ns="urn:sympasoap" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" xmlns:targetNamespace="https://lists-dev.techservices.illinois.edu/lists/wsdl" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><SOAP-ENV:Header/><SOAP-ENV:Body><ns:review><ns:list xsi:type="xsd:string">scrumTeamB</ns:list></ns:review></SOAP-ENV:Body></SOAP-ENV:Envelope>
```

response payload looks like this:

```xml
<?xml version="1.0" encoding="UTF-8"?><soap:Envelope soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"><soap:Body><reviewResponse xmlns="urn:sympasoap"><return soapenc:arrayType="xsd:string[7]" xsi:type="soapenc:Array"><item xsi:type="xsd:string">aosar@illinois.edu</item><item xsi:type="xsd:string">pbale4@illinois.edu</item><item xsi:type="xsd:string">pbalex@illinois.edu</item><item xsi:type="xsd:string">pbaleyx@illinois.edu</item><item xsi:type="xsd:string">rstanton@illinois.edu</item><item xsi:type="xsd:string">scrumteamb@illinois.edu</item><item xsi:type="xsd:string">xyz123@gmail.com</item></return></reviewResponse></soap:Body></soap:Envelope>
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
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:ns="urn:sympasoap" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" xmlns:targetNamespace="https://lists-dev.techservices.illinois.edu/lists/wsdl" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><SOAP-ENV:Header/><SOAP-ENV:Body><ns:fullReview><ns:list xsi:type="xsd:string">scrumTeamB</ns:list></ns:fullReview></SOAP-ENV:Body></SOAP-ENV:Envelope>
```

response payload looks like this:

```xml
<?xml version="1.0" encoding="UTF-8"?><soap:Envelope soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"><soap:Body><fullReviewResponse xmlns="urn:sympasoap"><return soapenc:arrayType="xsd:anyType[8]" xsi:type="soapenc:Array"><item><email xsi:type="xsd:string">pbalex@illinois.edu</email><isSubscriber xsi:type="xsd:boolean">true</isSubscriber><isOwner xsi:type="xsd:boolean">false</isOwner><gecos xsi:type="xsd:string">true</gecos><isEditor xsi:type="xsd:boolean">false</isEditor></item><item><isOwner xsi:type="xsd:boolean">false</isOwner><isSubscriber xsi:type="xsd:boolean">true</isSubscriber><email xsi:type="xsd:string">rstanton@illinois.edu</email><isEditor xsi:type="xsd:boolean">false</isEditor><gecos xsi:type="xsd:string">true</gecos></item><item><isSubscriber xsi:type="xsd:boolean">true</isSubscriber><isOwner xsi:type="xsd:boolean">false</isOwner><email xsi:type="xsd:string">aosar@illinois.edu</email><isEditor xsi:type="xsd:boolean">false</isEditor><gecos xsi:type="xsd:string">true</gecos></item><item><isSubscriber xsi:type="xsd:boolean">true</isSubscriber><isOwner xsi:type="xsd:boolean">false</isOwner><email xsi:type="xsd:string">scrumteamb@illinois.edu</email><isEditor xsi:type="xsd:boolean">false</isEditor><gecos xsi:type="xsd:string" /></item><item><isEditor xsi:type="xsd:boolean">false</isEditor><gecos xsi:type="xsd:string">true</gecos><isOwner xsi:type="xsd:boolean">false</isOwner><isSubscriber xsi:type="xsd:boolean">true</isSubscriber><email xsi:type="xsd:string">pbale4@illinois.edu</email></item><item><gecos xsi:type="xsd:string">true</gecos><isEditor xsi:type="xsd:boolean">false</isEditor><email xsi:type="xsd:string">pbaleyx@illinois.edu</email><isOwner xsi:type="xsd:boolean">false</isOwner><isSubscriber xsi:type="xsd:boolean">true</isSubscriber></item><item><isEditor xsi:type="xsd:boolean">false</isEditor><gecos xsi:type="xsd:string">Prasanna</gecos><isOwner xsi:type="xsd:boolean">true</isOwner><isSubscriber xsi:type="xsd:boolean">false</isSubscriber><email xsi:type="xsd:string">pbale@illinois.edu</email></item><item><gecos xsi:type="xsd:string">true</gecos><isEditor xsi:type="xsd:boolean">false</isEditor><email xsi:type="xsd:string">xyz123@gmail.com</email><isOwner xsi:type="xsd:boolean">false</isOwner><isSubscriber xsi:type="xsd:boolean">true</isSubscriber></item></return></fullReviewResponse></soap:Body></soap:Envelope>
```

