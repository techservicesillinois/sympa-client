package edu.illinois.techservices.sympa;

import javax.xml.namespace.QName;
import java.io.*;
import java.util.*;

import jakarta.xml.soap.*;
import io.github.cdimascio.dotenv.Dotenv;

public class SympaClient {

  private static String sympaSoapUrl = loadEnvVar("SYMPA_URL");
  private static String sessionCookie = null;
  static String email = loadEnvVar("SYMPA_EMAIL");
  static String password = loadEnvVar("SYMPA_PASSWORD");

  /**
   * Dynamically load environment variable from either system env or .env file
   * 
   * @param key env var key
   * @return value
   */
  private static String loadEnvVar(String key) {
    return System.getenv(key) != null ? System.getenv(key) : Dotenv.load().get(key);
  }

  /**
   * Log in to sympa server and retrieve session cookie to pass it on to
   * subsequent request.
   * 
   * @return
   */
  public static String loginSympa() {
    if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
      System.out.println(
          "[ERROR] Email or password is not set. Please configure environment variables for SYMPA_EMAIL and SYMPA_PASSWORD");
      throw new IllegalArgumentException(
          "Email or password is not set. Please configure environment variables for SYMPA_EMAIL and SYMPA_PASSWORD");
    }

    try {

      SOAPMessage soapMessage = createMessageFactoryInstance();

      SOAPPart soapPart = soapMessage.getSOAPPart();
      SOAPEnvelope envelope = addNamespaceDeclaration(soapPart);

      SOAPBody soapBody = envelope.getBody();

      SOAPElement soapElement = soapBody.addChildElement("login", "ns", "urn:sympasoap");

      soapElement.addChildElement("email")
          .addTextNode(email)
          .addAttribute(new QName("xsi:type"), "xsd:string");

      soapElement.addChildElement("password")
          .addTextNode(password)
          .addAttribute(new QName("xsi:type"), "xsd:string");

      MimeHeaders headers = soapMessage.getMimeHeaders();
      headers.addHeader("Content-Type", "text/xml"); // application/soap+xml
      headers.addHeader("SOAPAction", "urn:sympasoap#login");
      headers.addHeader("RequestMethod", "POST");
      soapMessage.saveChanges();

      SOAPMessage soapResponse = callSympaAPI(soapMessage);

      System.out.println("\n Login Response: \n");
      printSOAPMessage(soapResponse);

      sessionCookie = grabSessionCookie(soapResponse);
    } catch (Exception e) {
      System.out.println("\n THE ERROR...\n");
      e.printStackTrace();
    }
    return sessionCookie;
  }

  /**
   * Provides description informations about a given list.
   * 
   * @param cookie
   */
  public static void getInfo(String cookie) {
    try {

      SOAPMessage soapMessage = createMessageFactoryInstance();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      SOAPEnvelope envelope = addNamespaceDeclaration(soapPart);

      MimeHeaders headers = soapMessage.getMimeHeaders();

      headers.addHeader("Content-Type", "text/xml"); // application/soap+xml
      headers.addHeader("SOAPAction", "urn:sympasoap#info");
      headers.addHeader("cookie", "sympa_session=" + cookie);
      SOAPBody soapBody = envelope.getBody();

      SOAPElement soapElement = soapBody.addChildElement("info", "ns", "urn:sympasoap");

      soapMessage.saveChanges();

      // Send the SOAP message to the endpoint
      SOAPMessage info = callSympaAPI(soapMessage);
      printSOAPMessage(info);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Get a list of available lists.
   * 
   * @param cookie
   */
  public static void getLists(String cookie) {
    try {

      SOAPMessage soapMessage = createMessageFactoryInstance();
      SOAPPart soapPart = soapMessage.getSOAPPart();

      SOAPEnvelope envelope = addNamespaceDeclaration(soapPart);

      MimeHeaders headers = soapMessage.getMimeHeaders();

      headers.addHeader("Content-Type", "text/xml"); // application/soap+xml
      headers.addHeader("SOAPAction", "urn:sympasoap#lists");
      headers.addHeader("Cookie", "sympa_session=" + cookie);
      SOAPBody soapBody = envelope.getBody();

      SOAPElement soapElement = soapBody.addChildElement("lists", "ns", "urn:sympasoap");

      soapMessage.saveChanges();

      System.out.println("\n  Soap Call for Lists ");

      SOAPMessage lists = callSympaAPI(soapMessage);

      System.out.println("\n Lists Response : ");
      printSOAPMessage(lists);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 
   * @param cookie
   * @param listName
   */
  public static void createList(String cookie, String[] arguments) {
    try {
      SOAPMessage soapMessage = createMessageFactoryInstance();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      SOAPEnvelope envelope = addNamespaceDeclaration(soapPart);

      MimeHeaders headers = soapMessage.getMimeHeaders();

      headers.addHeader("Content-Type", "text/xml"); // application/soap+xml
      headers.addHeader("SOAPAction", "urn:sympasoap#createList");
      headers.addHeader("Cookie", "sympa_session=" + cookie);
      SOAPBody soapBody = envelope.getBody();

      SOAPElement soapElement = soapBody.addChildElement("createList", "ns", "urn:sympasoap");

      soapElement.addChildElement("list", "ns")
          .addTextNode(arguments[1])
          .addAttribute(new QName("xsi:type"), "xsd:string");

      soapElement.addChildElement("subject", "ns")
          .addTextNode(arguments[2])
          .addAttribute(new QName("xsi:type"), "xsd:string");

      soapElement.addChildElement("template", "ns")
          .addTextNode(arguments[3])
          .addAttribute(new QName("xsi:type"), "xsd:string");

      soapElement.addChildElement("description", "ns")
          .addTextNode(arguments[4])
          .addAttribute(new QName("xsi:type"), "xsd:string");

      soapElement.addChildElement("topic", "ns")
          .addTextNode(arguments[5])
          .addAttribute(new QName("xsi:type"), "xsd:string");

      soapMessage.saveChanges();

      // Send the SOAP message to the endpoint
      SOAPMessage createlist = callSympaAPI(soapMessage);

      System.out.println("\n createList Response : ");
      printSOAPMessage(createlist);

    } catch (Exception e) {

    }
  }

  /**
   * Print the contents of soap message to the console.
   * 
   * @param message
   * @throws Exception
   */
  public static void printSOAPMessage(SOAPMessage message) throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    message.writeTo(out);
    System.out.println(new String(out.toByteArray()));

  }

  /**
   * Retrieve the session cookie from the sympa response returned from the server.
   * 
   * @param soapMessage
   */
  public static String grabSessionCookie(SOAPMessage soapMessage) throws Exception {
    String sessionCookie = null;
    SOAPBody responseBody = soapMessage.getSOAPBody();
    Iterator<?> iterator = responseBody.getChildElements();
    while (iterator.hasNext()) {
      SOAPElement element = (SOAPElement) iterator.next();
      if (element.hasAttributes()) {
        Iterator<?> iterator1 = element.getChildElements();
        if (iterator1.hasNext()) {
          SOAPElement element1 = (SOAPElement) iterator1.next();
          System.out.println("Element: " + element1.getNodeName() + "======  value: " + element1.getValue());
          if (element1.getValue() != null) {
            sessionCookie = element1.getValue();
            break;
          }
        }
      }
    }
    return sessionCookie;
  }

  /**
   * Repetetion of namespacedeclaration.
   * 
   * @param soapPart
   * @return
   * @throws Exception
   */
  public static SOAPEnvelope addNamespaceDeclaration(SOAPPart soapPart) throws Exception {

    SOAPEnvelope envelope = soapPart.getEnvelope();

    envelope.addNamespaceDeclaration("ns", "urn:sympasoap");
    envelope.addNamespaceDeclaration("soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
    envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
    envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
    envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    envelope.addNamespaceDeclaration("targetNamespace", "https://lists-dev.techservices.illinois.edu/lists/wsdl");
    envelope.addNamespaceDeclaration("SOAP-ENC", "http://schemas.xmlsoap.org/soap/encoding/");
    envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

    envelope.addNamespaceDeclaration("SOAP-ENC", "http://schemas.xmlsoap.org/soap/encoding/");

    return envelope;
  }

  /**
   * 
   * @param soapMessage
   * @return
   */
  public static SOAPMessage callSympaAPI(SOAPMessage soapMessage) {
    SOAPMessage response = null;
    try {
      // Create a SOAP connection
      SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
      SOAPConnection soapConnection = soapConnectionFactory.createConnection();

      // Send the SOAP message to the endpoint
      response = soapConnection.call(soapMessage, sympaSoapUrl);

      soapConnection.close();
    } catch (Exception e) {
      System.out.println("Something went wrong!!");
    }
    return response;
  }

  /**
   * Separate SOAP MessageFactory Instance creation to its own method to avoid
   * duplicacy of code.
   * 
   * @return
   */
  public static SOAPMessage createMessageFactoryInstance() throws Exception {

    MessageFactory messageFactory = MessageFactory.newInstance();

    return messageFactory.createMessage();
  }
}
