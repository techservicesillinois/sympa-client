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

      MessageFactory messageFactory = MessageFactory.newInstance();
      SOAPMessage soapMessage = messageFactory.createMessage();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

      SOAPBody soapBody = envelope.getBody();

      SOAPElement soapElement = soapBody.addChildElement("authenticateAndRun", "ns", "urn:sympasoap");

      // Encode password in Base64
      String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());

      System.out.println("Base64 Encoded Password: " + encodedPassword);
      SOAPElement param1 = soapElement.addChildElement("email").addTextNode(email);
      param1.addAttribute(new QName("xsi:type"), "xsd:string");

      SOAPElement param2 = soapElement.addChildElement("cookie").addTextNode(cookie);
      param2.addAttribute(new QName("xsi:type"), "xsd:string");

      SOAPElement param3 = soapElement.addChildElement("service").addTextNode(service);
      param3.addAttribute(new QName("xsi:type"), "xsd:string");

      // ArrayOfString parameter
      SOAPElement param4 = soapElement.addChildElement("parameters", "ns");
      param4.addAttribute(new QName("xsi:type"), "SOAP-ENC:Array");
      param4.addAttribute(new QName("SOAP-ENC:arrayType"), "xsd:string[" + parameters.size() + "]");

      for (String param : parameters) {
        SOAPElement item = param4.addChildElement("item");
        item.addTextNode(param);
        item.addAttribute(new QName("xsi:type"), "xsd:string");
      }

      String auth = email + ":" + password;
      String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes("UTF-8"));
      MimeHeaders headers = soapMessage.getMimeHeaders();

      headers.addHeader("Authorization", encodedAuth);
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
      System.out.println("\n THE ERROR...\n");
      e.printStackTrace();
    }
  }

  public static void authenticateRemoteAppAndRun(String cookie) {

  }

  public static void casLogin(String cookie) {

  }

  public static void getUserEmailByCookie(String cookie) {

  }
}
