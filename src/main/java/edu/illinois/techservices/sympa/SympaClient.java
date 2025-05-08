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


  public static void add(String cookie) {
    try {
      MessageFactory messageFactory = MessageFactory.newInstance();
      SOAPMessage soapMessage = messageFactory.createMessage();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      //String myNamespaceURI = "https://lists-dev.techservices.illinois.edu/lists/wsdl";
      SOAPEnvelope envelope = addNamespaceDeclaration(soapPart);

      MimeHeaders headers = soapMessage.getMimeHeaders();

      //headers.addHeader("Authorization", encodedAuth);
      headers.addHeader("Content-Type", "text/xml"); //application/soap+xml
      headers.addHeader("SOAPAction", "urn:sympasoap#add"); 
      headers.addHeader("Cookie", "sympa_session="+cookie);
      SOAPBody soapBody = envelope.getBody();
      
      SOAPElement soapElement = soapBody.addChildElement("add", "ns", "urn:sympasoap");

      SOAPElement param1 = soapElement.addChildElement("list", "ns");
      param1.addTextNode("pbalesamplelist");
      param1.addAttribute(new QName("xsi:type"), "xsd:string");

      SOAPElement param2 = soapElement.addChildElement("email", "ns");
      param2.addTextNode("rstanton@test.com");
      param2.addAttribute(new QName("xsi:type"), "xsd:string");

      SOAPElement param3 = soapElement.addChildElement("gecos", "ns");
      param3.addTextNode("Test user");
      param3.addAttribute(new QName("xsi:type"), "xsd:string");

      SOAPElement param4 = soapElement.addChildElement("quiet", "ns");
      param4.addTextNode("0");
      param4.addAttribute(new QName("xsi:type"), "xsd:boolean");
      //param4.setValue("true");

      soapMessage.saveChanges();

      /*
      System.out.println("Printing SOAP message?");
      System.out.println(soapMessage.toString());
      System.out.println("Done Printing SOAP message?");
      */

      // Send the SOAP message to the endpoint
      SOAPMessage add = callSympaAPI(soapMessage);

      System.out.println("\n add Response : ");
      printSOAPMessage(add);

    } catch(Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * 
   * @param cookie
   */
  public static void del(String cookie) {
    try {

      MessageFactory messageFactory = MessageFactory.newInstance();
      SOAPMessage soapMessage = messageFactory.createMessage();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      //String myNamespaceURI = "https://lists-dev.techservices.illinois.edu/lists/wsdl";
      SOAPEnvelope envelope = addNamespaceDeclaration(soapPart);

      MimeHeaders headers = soapMessage.getMimeHeaders();

      //headers.addHeader("Authorization", encodedAuth);
      headers.addHeader("Content-Type", "text/xml"); //application/soap+xml
      headers.addHeader("SOAPAction", "urn:sympasoap#del"); 
      headers.addHeader("Cookie", "sympa_session="+cookie);
      SOAPBody soapBody = envelope.getBody();
      
      SOAPElement soapElement = soapBody.addChildElement("del", "ns", "urn:sympasoap");

      System.out.println("Starting to add parameters");

      SOAPElement param1 = soapElement.addChildElement("list", "ns");
      param1.addTextNode("pbalesamplelist");
      param1.addAttribute(new QName("xsi:type"), "xsd:string");

      SOAPElement param2 = soapElement.addChildElement("email", "ns");
      param2.addTextNode("rstanton@test.com");
      param2.addAttribute(new QName("xsi:type"), "xsd:string");
      
      SOAPElement param3 = soapElement.addChildElement("quiet", "ns");
      param3.addTextNode("0");
      param3.addAttribute(new QName("xsi:type"), "xsd:boolean");
      soapMessage.saveChanges();

      System.out.println("Going to try to print soapMessage");
      System.out.println(soapMessage.toString());
      System.out.println("After trying to print soapMessage");

      System.out.println("\n  Soap Call for del ");
      printSOAPMessage(soapMessage);

      SOAPMessage del = callSympaAPI(soapMessage);

      System.out.println("\n del Response : ");
      printSOAPMessage(del);

    } catch(Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * 
   * @param cookie
   */
  public static void getComplexLists(String cookie) {
    try {

      MessageFactory messageFactory = MessageFactory.newInstance();
      SOAPMessage soapMessage = messageFactory.createMessage();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      //String myNamespaceURI = "https://lists-dev.techservices.illinois.edu/lists/wsdl";
      SOAPEnvelope envelope = addNamespaceDeclaration(soapPart);

      MimeHeaders headers = soapMessage.getMimeHeaders();

      //headers.addHeader("Authorization", encodedAuth);
      headers.addHeader("Content-Type", "text/xml"); //application/soap+xml
      headers.addHeader("SOAPAction", "urn:sympasoap#complexLists"); 
      headers.addHeader("Cookie", "sympa_session="+cookie);
      SOAPBody soapBody = envelope.getBody();
      
      SOAPElement soapElement = soapBody.addChildElement("complexLists", "ns", "urn:sympasoap");

      soapMessage.saveChanges();

      System.out.println("\n  Soap Call for complexLists ");
      
      SOAPMessage lists = callSympaAPI(soapMessage);

      System.out.println("\n complexLists Response : ");
      printSOAPMessage(lists);

    } catch(Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * 
   * @param cookie
   */
  public static void closeList(String cookie) {
    try {

      MessageFactory messageFactory = MessageFactory.newInstance();
      SOAPMessage soapMessage = messageFactory.createMessage();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      //String myNamespaceURI = "https://lists-dev.techservices.illinois.edu/lists/wsdl";
      SOAPEnvelope envelope = addNamespaceDeclaration(soapPart);

      MimeHeaders headers = soapMessage.getMimeHeaders();

      //headers.addHeader("Authorization", encodedAuth);
      headers.addHeader("Content-Type", "text/xml"); //application/soap+xml
      headers.addHeader("SOAPAction", "urn:sympasoap#closeList"); 
      headers.addHeader("Cookie", "sympa_session="+cookie);
      SOAPBody soapBody = envelope.getBody();
      
      SOAPElement soapElement = soapBody.addChildElement("closeList", "ns", "urn:sympasoap");
      
      SOAPElement param1 = soapElement.addChildElement("list", "ns");
      param1.addTextNode("rstanton_samplelist_1");
      param1.addAttribute(new QName("xsi:type"), "xsd:string");

      soapMessage.saveChanges();

      System.out.println("\n  Soap Call for closeList ");
      
      SOAPMessage closeList = callSympaAPI(soapMessage);

      System.out.println("\n closeList Response : ");
      printSOAPMessage(closeList);

    } catch(Exception e) {
      e.printStackTrace();
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
