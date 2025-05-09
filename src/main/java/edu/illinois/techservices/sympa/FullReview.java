package edu.illinois.techservices.sympa;

import javax.xml.namespace.QName;

import jakarta.xml.soap.*;


public class FullReview {

private static String sympaSoapUrl = "https://lists-dev.techservices.illinois.edu/sympasoap";

public static void fullreview(String cookie, String listName) {
    try {

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("Content-Type", "text/xml"); 
        headers.addHeader("SOAPAction", "urn:sympasoap#fullReview"); 
        headers.addHeader("Cookie", "sympa_session="+cookie);
        SOAPBody soapBody = envelope.getBody();

        SOAPElement soapElement = soapBody.addChildElement("fullReview", "ns", "urn:sympasoap");

        System.out.println(listName);

        soapElement.addChildElement("list", "ns")
          .addTextNode(listName)
          .addAttribute(new QName("xsi:type"), "xsd:string");

        soapMessage.saveChanges();
        SympaClient.printSOAPMessage(soapMessage);
        // Create a SOAP connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Send the SOAP message to the endpoint
        SOAPMessage fullreview = soapConnection.call(soapMessage, sympaSoapUrl);
        System.out.println("\n Full Review Response : ");
        SympaClient.printSOAPMessage(fullreview);

        
        PermissionPrint.permissionPrint(fullreview);



    } catch(Exception e) {
      e.printStackTrace();
    }
  }
    
}
