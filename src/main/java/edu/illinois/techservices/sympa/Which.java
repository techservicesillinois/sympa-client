package edu.illinois.techservices.sympa;

import javax.xml.namespace.QName;
import jakarta.xml.soap.*;
import edu.illinois.techservices.sympa.SympaClient;

public class Which {
  private static String sympaSoapUrl = SympaClient.sympaSoapUrl;
  private static String cookie = SympaClient.sessionCookie;

  public static void which(String listName) {
    try {
      SOAPMessage soapMessage = SympaClient.createMessageFactoryInstance();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

      MimeHeaders headers = soapMessage.getMimeHeaders();
      headers.addHeader("Content-Type", "text/xml");
      headers.addHeader("SOAPAction", "urn:sympasoap#which");
      headers.addHeader("Cookie", "sympa_session=" + cookie);

      SOAPBody soapBody = envelope.getBody();

      SOAPElement soapElement = soapBody.addChildElement("which", "ns", "urn:sympasoap");

      soapElement.addChildElement("list", "ns")
          .addTextNode(listName)
          .addAttribute(new QName("xsi:type"), "xsd:string");

      soapMessage.saveChanges();

      System.out.println("\n  Calling Which ");
      SympaClient.callSympaAPI(soapMessage);

      System.out.println("\n  Which Response : ");
      SympaClient.printSOAPMessage(soapMessage);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
