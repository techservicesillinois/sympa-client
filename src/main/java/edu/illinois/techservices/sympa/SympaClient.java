package edu.illinois.techservices.sympa;

import javax.xml.namespace.QName;
import java.io.*;
import java.util.*;

import jakarta.xml.soap.*;
import io.github.cdimascio.dotenv.Dotenv;

public class SympaClient {
  /**
   * Dynamically load environment variable from either system env or .env file
   * @param key env var key
   * @return value
   */
  private static String loadEnvVar(String key) {
    return System.getenv(key) != null ? System.getenv(key) : Dotenv.load().get(key);
  }

  
  private static String sympaSoapUrl = "https://lists-dev.techservices.illinois.edu/sympasoap";
  private static String sessionCookie = null;
  static String email = loadEnvVar("SYMPA_EMAIL");
  static String password = loadEnvVar("SYMPA_PASSWORD");
  
  
  /**
   * Log in to sympa server and retrieve session cookie to pass it on to subsequent request.
   * @return
   */
  public static String loginSympa() {
    if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
      System.out.println("[ERROR] Email or password is not set. Please configure environment variables for SYMPA_EMAIL and SYMPA_PASSWORD");
      throw new IllegalArgumentException("Email or password is not set. Please configure environment variables for SYMPA_EMAIL and SYMPA_PASSWORD");
    }

    try {
      MessageFactory messageFactory = MessageFactory.newInstance();
      SOAPMessage soapMessage = messageFactory.createMessage();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      //String myNamespaceURI = "https://lists-dev.techservices.illinois.edu/lists/wsdl";
      SOAPEnvelope envelope = addNamespaceDeclaration(soapPart);

      SOAPBody soapBody = envelope.getBody();
      
      SOAPElement soapElement = soapBody.addChildElement("login", "ns", "urn:sympasoap");

      // Encode password in Base64
      String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());

      System.out.println("Base64 Encoded Password: " + encodedPassword);
      SOAPElement param1 = soapElement.addChildElement("email").addTextNode(email);
      param1.addAttribute(new QName("xsi:type"), "xsd:string");
     
      SOAPElement param2 = soapElement.addChildElement("password").addTextNode(password);
      param2.addAttribute(new QName("xsi:type"), "xsd:string");

      //param2.addTextNode(Base64.getEncoder().encodeToString(password.getBytes()));
      String auth = email + ":" + password;
      String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes("UTF-8"));
      MimeHeaders headers = soapMessage.getMimeHeaders();

      headers.addHeader("Authorization", encodedAuth);
      headers.addHeader("Content-Type", "text/xml"); //application/soap+xml
      headers.addHeader("SOAPAction", "urn:sympasoap#login"); 
      headers.addHeader("RequestMethod", "POST");
      soapMessage.saveChanges();

      System.out.println("\n My First SOAP LOGIN Request: \n");
      printSOAPMessage(soapMessage);
      System.out.println("\n");
      //response = sendSOAPRequest(soapMessage);

      System.out.println("\n SoapConnection.call() : \n");

      SOAPMessage soapResponse = callSympaAPI(soapMessage);

      System.out.println("\n Login Response: \n");
      printSOAPMessage(soapResponse);

      sessionCookie = grabSessionCookie(soapResponse);
      // Close the connection
      //soapConnection.close();
    } catch(Exception e) {
        System.out.println("\n THE ERROR...\n");
        e.printStackTrace();
    }
    return sessionCookie;
  }

  /**
   * 
   * @param cookie
   */
  public static void getInfo(String cookie) {
    try {

      MessageFactory messageFactory = MessageFactory.newInstance();
      SOAPMessage soapMessage = messageFactory.createMessage();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      //String myNamespaceURI = "https://lists-dev.techservices.illinois.edu/lists/wsdl";
      SOAPEnvelope envelope = addNamespaceDeclaration(soapPart);

      MimeHeaders headers = soapMessage.getMimeHeaders();

      //headers.addHeader("Authorization", encodedAuth);
      headers.addHeader("Content-Type", "text/xml"); //application/soap+xml
      headers.addHeader("SOAPAction", "urn:sympasoap#info"); 
      headers.addHeader("cookie", "sympa_session="+cookie);
      SOAPBody soapBody = envelope.getBody();
      
      SOAPElement soapElement = soapBody.addChildElement("info", "ns", "urn:sympasoap");

      soapMessage.saveChanges();

      // Send the SOAP message to the endpoint
      SOAPMessage info = callSympaAPI(soapMessage);
      printSOAPMessage(info);

    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 
   * @param cookie
   */
  public static void getLists(String cookie) {
    try {

      MessageFactory messageFactory = MessageFactory.newInstance();
      SOAPMessage soapMessage = messageFactory.createMessage();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      //String myNamespaceURI = "https://lists-dev.techservices.illinois.edu/lists/wsdl";
      SOAPEnvelope envelope = addNamespaceDeclaration(soapPart);

      MimeHeaders headers = soapMessage.getMimeHeaders();

      //headers.addHeader("Authorization", encodedAuth);
      headers.addHeader("Content-Type", "text/xml"); //application/soap+xml
      headers.addHeader("SOAPAction", "urn:sympasoap#lists"); 
      headers.addHeader("Cookie", "sympa_session="+cookie);
      SOAPBody soapBody = envelope.getBody();
      
      SOAPElement soapElement = soapBody.addChildElement("lists", "ns", "urn:sympasoap");

      /*SOAPElement param1 = soapElement.addChildElement("topic", "ns");
      param1.addTextNode("science");
      param1.addAttribute(new QName("xsi:type"), "xsd:string");

      SOAPElement param2 = soapElement.addChildElement("subtopic", "ns");
      param2.addTextNode("physics");
      param2.addAttribute(new QName("xsi:type"), "xsd:string");*/
      
      soapMessage.saveChanges();

      System.out.println("\n  Soap Call for Lists ");
      
      SOAPMessage lists = callSympaAPI(soapMessage);

      System.out.println("\n Lists Response : ");
      printSOAPMessage(lists);

    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static void createList(String cookie) {
    try {
      MessageFactory messageFactory = MessageFactory.newInstance();
      SOAPMessage soapMessage = messageFactory.createMessage();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      //String myNamespaceURI = "https://lists-dev.techservices.illinois.edu/lists/wsdl";
      SOAPEnvelope envelope = addNamespaceDeclaration(soapPart);

      MimeHeaders headers = soapMessage.getMimeHeaders();

      //headers.addHeader("Authorization", encodedAuth);
      headers.addHeader("Content-Type", "text/xml"); //application/soap+xml
      headers.addHeader("SOAPAction", "urn:sympasoap#createList"); 
      headers.addHeader("Cookie", "sympa_session="+cookie);
      SOAPBody soapBody = envelope.getBody();
      
      SOAPElement soapElement = soapBody.addChildElement("createList", "ns", "urn:sympasoap");

      SOAPElement param1 = soapElement.addChildElement("list", "ns");
      param1.addTextNode("pbalesamplelist");
      param1.addAttribute(new QName("xsi:type"), "xsd:string");

      SOAPElement param2 = soapElement.addChildElement("subject", "ns");
      param2.addTextNode("pbalesamplelist");
      param2.addAttribute(new QName("xsi:type"), "xsd:string");

      SOAPElement param3 = soapElement.addChildElement("template", "ns");
      param3.addTextNode("discussion_list");
      param3.addAttribute(new QName("xsi:type"), "xsd:string");

      SOAPElement param4 = soapElement.addChildElement("description", "ns");
      param4.addTextNode("sample list created for testing.");
      param4.addAttribute(new QName("xsi:type"), "xsd:string");

      SOAPElement param5 = soapElement.addChildElement("topic", "ns");
      param5.addTextNode("technology,computing,innovation");
      param5.addAttribute(new QName("xsi:type"), "xsd:string");

      soapMessage.saveChanges();

      // Send the SOAP message to the endpoint
      SOAPMessage createlist = callSympaAPI(soapMessage);

      System.out.println("\n createList Response : ");
      printSOAPMessage(createlist);

    } catch(Exception e) {

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
   * Retrieve the session cookie from the sympa response returned from the server.
   * @param soapMessage
   */
  public static String grabSessionCookie(SOAPMessage soapMessage) throws Exception {
    String sessionCookie = null;
    SOAPBody responseBody = soapMessage.getSOAPBody();
    Iterator<?> iterator = responseBody.getChildElements();
    while (iterator.hasNext()) {
      SOAPElement element = (SOAPElement) iterator.next();
      if(element.hasAttributes()) {
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

  /**
   * 
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
}
