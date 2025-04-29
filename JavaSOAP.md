# Java SOAP Usage with Sympa API

Sympa uses an outdated SOAP version which utilizes rpc calls. This means we must manually build the SOAP request to ensure correct data structure. This makes this process a little more verbose than it might otherwise be with a more modern SOAP library.

## Building the SOAP Request

Example for usage with the `login` API call.

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
// Set the API call for `login`
SOAPElement soapElement = soapBody.addChildElement("login", "ns", "urn:sympasoap");

// Add email and password parameters as tags to the SOAP request
soapElement
  .addChildElement("email")
  .addTextNode(email)
  .addAttribute(new QName("xsi:type"), "xsd:string");

soapElement
  .addChildElement("password")
  .addTextNode(password)
  .addAttribute(new QName("xsi:type"), "xsd:string");


// Format credentials
String auth = email + ":" + password;
String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes("UTF-8"));

// Add headers
MimeHeaders headers = soapMessage.getMimeHeaders();

headers.addHeader("Authorization", encodedAuth);
headers.addHeader("Content-Type", "text/xml"); //application/soap+xml
headers.addHeader("SOAPAction", "urn:sympasoap#login"); 
headers.addHeader("RequestMethod", "POST");

// Save the changes to the SOAP message
soapMessage.saveChanges();
```

### Submit request

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
    xmlns:ns="urn:sympasoap"
    xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
    xmlns:soapenc="http://schema.xmlsoap.org/soap/encoding/"
    xmlns:targetNamespace="https://lists-dev.techservices.illinois.edu/lists/wsdl"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <ns:login>
            <email xsi:type="xsd:string">test@email.com</email>
            <password xsi:type="xsd:string">S3cretP4ssw0rd</password>
        </ns:login>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

And a response like this:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope 
    soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" 
    xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" 
    xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" 
    xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <soap:Body>
        <loginResponse xmlns="urn:sympasoap">
            <result xsi:type="xsd:string">72901752050742</result>
        </loginResponse>
    </soap:Body>
</soap:Envelope>
```

# [WIP]
### Grab session cookie

```java
// todo: parse from response header if possible, not manual parse from body
```

Debugger code (to include, or to not include)

```java
System.out.println("\n My First SOAP LOGIN Request: \n");
printSOAPMessage(soapMessage);
System.out.println("\n");
System.out.println("\n SoapConnection.call() : \n");
# ... send request
System.out.println("\n Login Response: \n");
printSOAPMessage(soapResponse);
```


