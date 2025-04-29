package edu.illinois.techservices.sympa;

import javax.xml.namespace.QName;
import java.io.*;

import jakarta.xml.soap.*;


public class Review {

private static String sympaSoapUrl = "https://lists-dev.techservices.illinois.edu/sympasoap";

public static void review(String cookie) {
    try {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = addNamespaceDeclaration(soapPart);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("Content-Type", "text/xml"); 
        headers.addHeader("SOAPAction", "urn:sympasoap#review"); 
        headers.addHeader("Cookie", "sympa_session="+cookie);
        SOAPBody soapBody = envelope.getBody();

        SOAPElement soapElement = soapBody.addChildElement("review", "ns", "urn:sympasoap");

        SOAPElement param1 = soapElement.addChildElement("list", "ns");
        param1.addTextNode("testlist3");
        param1.addAttribute(new QName("xsi:type"), "xsd:string");


        soapMessage.saveChanges();
        printSOAPMessage(soapMessage);
        // Create a SOAP connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Send the SOAP message to the endpoint
        SOAPMessage review = soapConnection.call(soapMessage, sympaSoapUrl);
        System.out.println("\n Review Response : ");
        printSOAPMessage(review);


    } catch(Exception e) {
      e.printStackTrace();
    }
  }


    /**
   * Print the contents of soap message to the console.
   * @param message
   * @throws Exception
   */
  private static void printSOAPMessage(SOAPMessage message) throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    message.writeTo(out);
    System.out.println(new String(out.toByteArray()));

  }


  /**
   * Repetetion of namespacedeclaration.
   * @param soapPart
   * @return
   * @throws Exception
   */
  public static SOAPEnvelope addNamespaceDeclaration(SOAPPart soapPart) throws Exception {
    
    SOAPEnvelope envelope = soapPart.getEnvelope();

    envelope.addNamespaceDeclaration("ns", "urn:sympasoap");
    envelope.addNamespaceDeclaration("soapenc", "http://schema.xmlsoap.org/soap/encoding/");
    envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
    envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
    envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    envelope.addNamespaceDeclaration("targetNamespace", "https://lists-dev.techservices.illinois.edu/lists/wsdl");
    envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

    return envelope;
  }

}