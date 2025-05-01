package edu.illinois.techservices.sympa;

import javax.xml.namespace.QName;
import java.io.*;

import jakarta.xml.soap.*;


public class Subscribe {

private static String sympaSoapUrl = "https://lists-dev.techservices.illinois.edu/sympasoap";

public static void subscribe(String cookie) {
    try {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("Content-Type", "text/xml"); 
        headers.addHeader("SOAPAction", "urn:sympasoap#subscribe"); 
        headers.addHeader("Cookie", "sympa_session="+cookie);
        SOAPBody soapBody = envelope.getBody();

        SOAPElement soapElement = soapBody.addChildElement("subscribe", "ns", "urn:sympasoap");

        soapElement.addChildElement("list", "ns").addTextNode("testlist3").addAttribute(new QName("xsi:type"), "xsd:string");

    
        soapMessage.saveChanges();
        SympaClient.printSOAPMessage(soapMessage);
        // Create a SOAP connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Send the SOAP message to the endpoint
        SOAPMessage subscribe = soapConnection.call(soapMessage, sympaSoapUrl);
        System.out.println("\n Subscribe Response : ");
        SympaClient.printSOAPMessage(subscribe);
    
    
    }catch(Exception e){
        e.printStackTrace();
    }

}

}