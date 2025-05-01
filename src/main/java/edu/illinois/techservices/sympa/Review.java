package edu.illinois.techservices.sympa;

import javax.xml.namespace.QName;
import java.io.*;

import jakarta.xml.soap.*;


public class Review {

private static String sympaSoapUrl = "https://lists-dev.techservices.illinois.edu/sympasoap";

public static void review(String cookie, String listName) {
    try {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("Content-Type", "text/xml"); 
        headers.addHeader("SOAPAction", "urn:sympasoap#review"); 
        headers.addHeader("Cookie", "sympa_session="+cookie);
        SOAPBody soapBody = envelope.getBody();

        SOAPElement soapElement = soapBody.addChildElement("review", "ns", "urn:sympasoap");

        soapElement.addChildElement("list", "ns")
          .addTextNode(listName)
          .addAttribute(new QName("xsi:type"), "xsd:string");

        soapMessage.saveChanges();
        SympaClient.printSOAPMessage(soapMessage);
        // Create a SOAP connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Send the SOAP message to the endpoint
        SOAPMessage review = soapConnection.call(soapMessage, sympaSoapUrl);
        System.out.println("\n Review Response : ");
        SympaClient.printSOAPMessage(review);


    } catch(Exception e) {
      e.printStackTrace();
    }
  }

}