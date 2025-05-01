package edu.illinois.techservices.sympa;

import javax.xml.namespace.QName;
import javax.xml.namespace.QName;
import java.io.*;
import java.util.*;

import jakarta.xml.soap.*;
import edu.illinois.techservices.sympa.SympaClient;

public class SympaLoginClient {
  static String email = System.getenv("SYMPA_EMAIL");
  static String password = System.getenv("SYMPA_PASSWORD");
  private static String sessionCookie = null;

  /**
   * 
   * @param cookie     - session cookie
   * @param service    - Name of the service for ex: (add, del) offeref by sympa
   *                   API.
   * @param parameters - Parameters used to perform the specific services. for
   *                   example, the parameters for service add:
   * 
   *                   del:
   *                   List<String> parameters = List.of(
   *                   "pbalesamplelist", // list name
   *                   "pbale@illinois.edu", // subscriber to delete
   *                   "false" // quiet flag (can be true/false)
   *                   );
   * 
   *                   add:
   *                   List<String> parameters = List.of(
   *                   "scrumTeamB",
   *                   "pbale@illinois.edu",
   *                   "true",
   *                   "true");
   */
  public static void authenticateAndRun(String cookie, String service, List<String> parameters) {
    try {

      SOAPMessage soapMessage = SympaClient.createMessageFactoryInstance();

      SOAPPart soapPart = soapMessage.getSOAPPart();
      SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

      SOAPBody soapBody = envelope.getBody();

      SOAPElement soapElement = soapBody.addChildElement("authenticateAndRun", "ns", "urn:sympasoap");

      soapElement.addChildElement("email")
          .addTextNode(email)
          .addAttribute(new QName("xsi:type"), "xsd:string");

      soapElement.addChildElement("cookie")
          .addTextNode(cookie)
          .addAttribute(new QName("xsi:type"), "xsd:string");

      soapElement.addChildElement("service")
          .addTextNode(service)
          .addAttribute(new QName("xsi:type"), "xsd:string");

      // Parameters to perform specific service like (add, del)
      SOAPElement items = soapElement.addChildElement("parameters", "ns");
      items.addAttribute(new QName("xsi:type"), "SOAP-ENC:Array");
      items.addAttribute(new QName("SOAP-ENC:arrayType"), "xsd:string[" + parameters.size() + "]");

      for (String param : parameters) {
        SOAPElement item = items.addChildElement("item");
        item.addTextNode(param);
        item.addAttribute(new QName("xsi:type"), "xsd:string");
      }

      MimeHeaders headers = soapMessage.getMimeHeaders();

      headers.addHeader("Content-Type", "text/xml"); // application/soap+xml
      headers.addHeader("SOAPAction", "urn:sympasoap#authenticateAndRun");
      headers.addHeader("RequestMethod", "POST");
      soapMessage.saveChanges();

      System.out.println("\n  SOAP AuthenticateAndRun Request for service " + service + ": \n");

      SympaClient.printSOAPMessage(soapMessage);
      System.out.println("\n");

      System.out.println("\n SoapConnection.call() : \n");

      SOAPMessage soapResponse = SympaClient.callSympaAPI(soapMessage);

      System.out.println("\n AuthenticateAndRun Response: \n");
      SympaClient.printSOAPMessage(soapResponse);

    } catch (Exception e) {
      System.out.println("\n Something is wrong...\n");
      e.printStackTrace();
    }
  }

  /**
   * Allow a trusted remote application to perform operations on behalf of a user
   * without requiring that user's cookie or password. In other words, impersonate
   * a user securely from a backend system
   * For example: A web app or script wants to call which, del or add on behalf of
   * a user without logging them in through the UI.
   * 
   * @param cookie
   */
  public static void authenticateRemoteAppAndRun() {
    try {
      SOAPMessage soapMessage = SympaClient.createMessageFactoryInstance();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

      // Encode password in Base64
      String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
      SOAPBody soapBody = envelope.getBody();

      SOAPElement soapElement = soapBody.addChildElement("authenticateRemoteAppAndRun", "ns", "urn:sympasoap");

      System.out.println("Base64 Encoded Password: " + encodedPassword);

      soapElement.addChildElement("appname")
          .addTextNode("sampleremoteApp")
          .addAttribute(new QName("xsi:type"), "xsd:string");

      soapElement.addChildElement("apppassword")
          .addTextNode("12345")
          .addAttribute(new QName("xsi:type"), "xsd:string");

      soapElement.addChildElement("vars")
          .addTextNode("USER_EMAIL=pbale@illinois.edu")
          .addAttribute(new QName("xsi:type"), "xsd:string");

      soapElement.addChildElement("service")
          .addTextNode("add")
          .addAttribute(new QName("xsi:type"), "xsd:string");

      // List<String> p = new ArrayList<>();
      String[] p = {
          "scrumTeamB",
          "pbale@xyz.edu",
          "true",
          "true" };

      System.out.println("parameters size: " + p);
      // ArrayOfString parameter

      SOAPElement element = soapElement.addChildElement("parameters", "ns");
      element.addAttribute(new QName("SOAP-ENC:arrayType", "arrayType",
          "SOAP-ENC"), "xsd:string[" + p.length + "]");
      element.addAttribute(new QName("xsi:type"), "SOAP-ENC:Array");

      if (p != null && p.length > 0) {
        for (String param : p) {
          SOAPElement item = element.addChildElement("item");
          item.addTextNode(param);
          item.addAttribute(new QName("xsi:type"), "xsd:string");
        }
      }

      MimeHeaders headers = soapMessage.getMimeHeaders();
      headers.addHeader("Content-Type", "text/xml"); // application/soap+xml
      headers.addHeader("SOAPAction", "urn:sympasoap#authenticateRemoteAppAndRun");
      headers.addHeader("RequestMethod", "POST");
      soapMessage.saveChanges();

      SympaClient.printSOAPMessage(soapMessage);
      System.out.println("\n");

      System.out.println("\n SoapConnection.call() : \n");

      SOAPMessage soapResponse = SympaClient.callSympaAPI(soapMessage);

      System.out.println("\n AuthenticateAndRun Response: \n");
      SympaClient.printSOAPMessage(soapResponse);

    } catch (Exception e) {
      System.out.println("\n THE ERROR...\n");
      e.printStackTrace();
    }
  }

  public static void casLogin(String cookie) {

  }

  /**
   * Return the user who sent the specific authorized session cookie to sympa
   * server.
   * 
   * @param cookie
   */
  public static void getUserEmailByCookie(String cookie) {
    try {

      SOAPMessage soapMessage = SympaClient.createMessageFactoryInstance();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

      SOAPBody soapBody = envelope.getBody();

      SOAPElement soapElement = soapBody.addChildElement("getUserEmailByCookie", "ns", "urn:sympasoap");

      soapElement.addChildElement("cookie")
          .addTextNode(cookie)
          .addAttribute(new QName("xsi:type"), "xsd:string");

      MimeHeaders headers = soapMessage.getMimeHeaders();
      headers.addHeader("Content-Type", "text/xml"); // application/soap+xml
      headers.addHeader("SOAPAction", "urn:sympasoap#getUserEmailByCookie");
      headers.addHeader("RequestMethod", "POST");

      soapMessage.saveChanges();

      SympaClient.printSOAPMessage(soapMessage);
      System.out.println("\n");

      System.out.println("\n SoapConnection.call() : \n");

      SOAPMessage soapResponse = SympaClient.callSympaAPI(soapMessage);

      System.out.println("\n getUserEmailByCookie Response: \n");
      SympaClient.printSOAPMessage(soapResponse);

    } catch (Exception e) {
      System.out.println("\n THE ERROR...\n");
      e.printStackTrace();
    }
  }
}
