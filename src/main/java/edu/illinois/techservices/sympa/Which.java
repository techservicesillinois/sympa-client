package edu.illinois.techservices.sympa;

import javax.xml.namespace.QName;
import jakarta.xml.soap.*;
import edu.illinois.techservices.sympa.SympaClient;

public class Which {
  private static String sympaSoapUrl = SympaClient.sympaSoapUrl;
  private static String cookie = SympaClient.sessionCookie;

  public static String which(String listName) {
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

      // System.out.println("\n  Calling Which ");
      SympaClient.callSympaAPI(soapMessage);

      System.out.println("\n  Which Response : ");
      SympaClient.printSOAPMessage(soapMessage);

      String listValue = SympaClient.getFirstChildElementValueByName(
        soapMessage.getSOAPBody(),
        "ns:list"
      );
      System.out.println("== Printing res value (which.ns:list): ==");
      System.out.println(listValue);

      return listValue;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String complexWhich(String listName) {
    try {
      SOAPMessage soapMessage = SympaClient.createMessageFactoryInstance();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

      MimeHeaders headers = soapMessage.getMimeHeaders();
      headers.addHeader("Content-Type", "text/xml");
      headers.addHeader("SOAPAction", "urn:sympasoap#complexwhich");
      headers.addHeader("Cookie", "sympa_session=" + cookie);

      SOAPBody soapBody = envelope.getBody();

      SOAPElement soapElement = soapBody.addChildElement("complexwhich", "ns", "urn:sympasoap");

      soapElement.addChildElement("list", "ns")
          .addTextNode(listName)
          .addAttribute(new QName("xsi:type"), "xsd:string");

      soapMessage.saveChanges();

      // System.out.println("\n  Calling Complex Which ");
      SympaClient.callSympaAPI(soapMessage);

      System.out.println("\n  Complex Which Response : ");
      SympaClient.printSOAPMessage(soapMessage);

      String listValue = SympaClient.getFirstChildElementValueByName(
        soapMessage.getSOAPBody(),
        "ns:list"
      );
      System.out.println("== Printing res value (complexwhich.ns:list): ==");
      System.out.println(listValue);

      return listValue;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
