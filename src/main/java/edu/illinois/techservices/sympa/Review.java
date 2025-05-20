package edu.illinois.techservices.sympa;

import javax.xml.namespace.QName;
import java.io.*;

import jakarta.xml.soap.*;
import edu.illinois.techservices.sympa.SympaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Review {
  private static final Logger logger = LoggerFactory.getLogger(Review.class);

  public static void review(String cookie, String listName) {
    try {
      SOAPMessage soapMessage = SympaClient.createMessageFactoryInstance();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

      MimeHeaders headers = soapMessage.getMimeHeaders();
      headers.addHeader("Content-Type", "text/xml");
      headers.addHeader("SOAPAction", "urn:sympasoap#review");
      headers.addHeader("Cookie", "sympa_session=" + cookie);
      SOAPBody soapBody = envelope.getBody();

      SOAPElement soapElement = soapBody.addChildElement("review", "ns", "urn:sympasoap");

      soapElement.addChildElement("list", "ns")
          .addTextNode(listName)
          .addAttribute(new QName("xsi:type"), "xsd:string");

      soapMessage.saveChanges();
      logger.debug(SympaClient.printSOAPMessage(soapMessage));

      SOAPMessage review = SympaClient.callSympaAPI(soapMessage);

      SympaClient.printFormattedSOAPMessage(review);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}