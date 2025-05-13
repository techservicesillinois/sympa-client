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
// Set the API call for `login`
SOAPElement soapElement = soapBody.addChildElement("which", "ns", "urn:sympasoap");

// Add email and password parameters as tags to the SOAP request
soapElement.addChildElement("list", "ns")
                    .addTextNode(listName)
                    .addAttribute(new QName("xsi:type"), "xsd:string");
MimeHeaders headers = soapMessage.getMimeHeaders();

headers.addHeader("Authorization", encodedAuth);
headers.addHeader("Content-Type", "text/xml"); //application/soap+xml
headers.addHeader("SOAPAction", "urn:sympasoap#subscribe"); 
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