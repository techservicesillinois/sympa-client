package edu.illinois.techservices.sympa;

import javax.xml.namespace.QName;
import java.io.*;
import java.util.*;
import java.util.function.Function;

import jakarta.xml.soap.*;
import io.github.cdimascio.dotenv.Dotenv;

public class SympaClient {

  static String sympaSoapUrl = loadEnvVar("SYMPA_URL");
  static String sessionCookie = null;
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

      System.out.println("\n[DEBUG] Login Response: \n");
      printSOAPMessage(soapResponse);

      sessionCookie = grabSessionCookie(soapResponse);

    } catch (Exception e) {
      System.out.println("\n[ERROR] Main call failed. See stack trace for details.");
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
    if (message == null) {
      System.out.println("[WARN] SOAP message body is empty. Cannot print SOAP message.");
      return;
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    message.writeTo(out);
    System.out.println(new String(out.toByteArray()));
  }

  // overload wrapper for optional callback
  public static SOAPElement getChildElementByName(SOAPElement parent, String childName) {
    return getElementByName(parent, childName, null);
  }

  /**
   * Recursively fetch the first SOAPElement that matches the given node name.
   *
   * @param parent      Parent SOAPElement
   * @param childName   Node name to search for
   * @return            First matching SOAPElement or null
   */
  public static SOAPElement getElementByName(SOAPElement parent, String childName, Function<SOAPElement, Void> callback) {
    if (parent == null) {
      System.out.println("[WARN] Parent SOAPElement is null. Cannot search for child element.");
      return null;
    }
    Iterator<?> children = parent.getChildElements();
    while (children.hasNext()) {
      Object next = children.next();
      if (!(next instanceof SOAPElement)) {
        continue;
      }
      SOAPElement child = (SOAPElement) next;
      if (child.getNodeName().equals(childName)) {
        // System.out.println("[DEBUG] Found element name: " + child.getNodeName());
        if (callback != null) callback.apply(child);
        return child;
      }
      else if (child.getNodeName().equals("soap:Fault")) {
        String faultCode = getFirstChildElementValueByName(child, "faultcode");
        // System.out.println("[DEBUG] child " + child.getNodeName());
        String faultString = getFirstChildElementValueByName(child, "faultstring");
        System.out.println("[ERROR] SOAP fault\n\t Fault code: " + faultCode + "\n\t Fault string: " + faultString);
        return null;
      }
      // else if (children.hasNext()) {
      //   // System.out.println("[DEBUG] child " + child.getNodeName());
      //   System.out.println("[DEBUG] No child node match for: '" + childName + "'. Found: '" + child.getNodeName() + "'. Has next element.Continuing search laterally.");
      //   return getElementByName(child, childName, callback);
      // }
      else if (child.getChildElements().hasNext()) {
        // System.out.println("[DEBUG] No child node match for: '" + childName + "'. Found: '" + child.getNodeName() + "'. Continuing search nested (children).");
          // Recursive search on nested elements
        SOAPElement found = getElementByName(child, childName, callback);
        if (found != null) return found;
      }
      else {
        // System.out.println("[DEBUG] No child node match for: '" + childName + "'. Found: '" + child.getNodeName() + "'. Continuing search laterally (siblings).");
        // this is in a loop so its going to continue searching through the siblings
      }
    }

    // Continue search depth-wise

    System.out.println("[WARN] Element not found with name: " + childName);
    return null;
  }

  public static Object buildJsonFromXml(SOAPElement element) {
    if (element == null) {
      System.out.println("[WARN] Element is null. Cannot build JSON object.");
      return null;
    }

    if (!element.getChildElements().hasNext()) {
      return element.getValue();
    }

    String xsiNil = element.getAttribute("xsi:nil");
    if ("true".equals(xsiNil)) {
      return null;
    }

    boolean hasElementChildren = false;
    Iterator<?> childrenCheck = element.getChildElements();
    while (childrenCheck.hasNext()) {
      if (childrenCheck.next() instanceof SOAPElement) {
        hasElementChildren = true;
        break;
      }
    }

    if (!hasElementChildren) {
      String type = element.getAttribute("xsi:type");
      String value = element.getValue();
      if (type == null || type.isEmpty() || value == null) {
        return value;
      }
      switch (type) {
        case "xsd:boolean":
          return Boolean.valueOf(value);
        case "xsd:int":
          try {
            return Integer.valueOf(value);
          } catch (NumberFormatException e) {
            return value; // fallback to string if parsing fails
          }
        case "xsd:string":
        default:
          return value;
      }
    }

    // Build nested object from child elements
    java.util.Map<String, Object> jsonObj = new java.util.HashMap<>();
    Iterator<?> children = element.getChildElements();

    while (children.hasNext()) {
      Object next = children.next();
      if (next instanceof SOAPElement) {
        SOAPElement child = (SOAPElement) next;
        String nodeName = child.getNodeName();
        Object childValue = buildJsonFromXml(child);
        jsonObj.put(nodeName, childValue);
      }
    }

    return jsonObj;
  }

  public static String buildJsonStringFromXml(SOAPElement element) {
    StringBuilder jsonLike = new StringBuilder("{");
    Iterator<?> children = element.getChildElements();
    boolean first = true;
    while (children.hasNext()) {
      Object next = children.next();
      if (next instanceof SOAPElement) {
        SOAPElement child = (SOAPElement) next;
        if (!first) {
          jsonLike.append(",");
        }
        jsonLike.append("\"").append(child.getNodeName()).append("\":\"")
            .append(child.getValue()).append("\"");
        first = false;
      }
    }
    jsonLike.append("}");
    return jsonLike.toString();
  }

  @SuppressWarnings("unchecked") // This is being checked best it can, unfortunately since buildJsonFromXml is recuresive it needs to return generics for now
  public static Map<String, Object> getElementListAsJson(SOAPElement masterElement, String listParentName, String listItemNames) {
    Map<String, Object> listItems = new HashMap<>();
    Function<SOAPElement, Void> callback = (SOAPElement child) -> {
      listItems.put(child.getNodeName(), buildJsonFromXml(child));
      return null;
    };

    // getElementByName(masterElement, listItemNames, callback);
    SOAPElement listParent = getElementByName(masterElement, listParentName, null);
    if (listParent == null) {
      System.out.println("[WARN] List parent element not found with name: " + listParentName);
      return null;
    }
    getElementByName(listParent, listItemNames, callback);

    try {
      Object item = listItems.get(listItemNames);
      if (item instanceof Map) {
        return (Map<String, Object>) item;
      }
      return listItems;
    } catch (ClassCastException e) {
      System.out.println("[ERROR] Failed to cast item to Map<String,Object>: " + e.getMessage());
      return null;
    }
  }

  /**
   * Fetch list of elements as strings from a specified parent element.
   * @param masterElement   Usually the response body
   * @param listParentName  Node name of the parent element that contains the list
   * @param listItemNames   Node name of list items to be retrieved
   * @return
   */
  public static ArrayList<String> getElementListAsString(SOAPElement masterElement, String listParentName, String listItemNames) {
    ArrayList<String> listItems = new ArrayList<>();
    Function<SOAPElement, Void> callback = (SOAPElement child) -> {
      // validate child type
      if (child.getAttribute("xsi:type").equals("xsd:string")) {
        listItems.add(child.getValue());
      }
      else if (child.getChildElements().hasNext()) {
        String jsonString = buildJsonStringFromXml(child);
        listItems.add(jsonString);
      }
      else {
        System.out.println("[WARN] Child element is not a string or has no children. Skipping.");
      }
      return null;
    };

    // Ensures that we are getting the correct list from the specified parent element name
    SOAPElement listParent = getElementByName(masterElement, listParentName, null);
    if (listParent == null) {
      System.out.println("[WARN] List parent element not found with name: " + listParentName);
      return null;
    }
    getElementByName(listParent, listItemNames, callback);

    // This could be called if we wanted implicit getListByItemName without specifying a parent
    // getChildElementByName(masterElement, listItemNames, callback);
    // if (listParent == null) {
    //   System.out.println("[WARN] List parent element not found with name: " + listParentName);
    //   return null;
    // }

    return listItems;
    // return null;
  }

  /**
   * Fetch list of elements as strings, indiscriminately.
   * @param masterElement   Usually the response body
   * @param listItemNames   Node name of list items to be retrieved
   * @return
   */
  public static ArrayList<String> getAllElementValues(SOAPElement masterElement, String listItemNames) {
    ArrayList<String> listItems = new ArrayList<>();
    Function<SOAPElement, Void> callback = (SOAPElement child) -> {
      // validate child type
      // if (child.getAttribute("xsi:type").equals("xsd:string")) {
        listItems.add(child.getValue());
      // }
      return null;
    };
    getElementByName(masterElement, listItemNames, callback);
    return listItems;
  }
    
  /**
   * Recursively fetch the text value of the first descendant element with the given
   * node name.
   *
   * @param parent    Parent SOAPElement
   * @param childName Node name to search for
   * @return          String value or null
   */
  public static String getFirstChildElementValueByName(SOAPElement parent, String childName) {
    final String[] value = new String[1];
    Function<SOAPElement, Void> callback = (SOAPElement child) -> {
      value[0] = child.getValue();
      // break out of the loop

      return null;
    };

    getElementByName(parent, childName, callback);

    return value[0];
  }

  /**
   * Retrieve the session cookie from the sympa response returned from the server.
   * 
   * @param soapMessage
   */
  public static String grabSessionCookie(SOAPMessage soapMessage) throws Exception {
    if (soapMessage == null) {
      System.out.println("[ERROR] SOAP message body is empty. Cannot grab session cookie.");
      return null;
    }
    String sessionCookie = null;
    SOAPBody responseBody = soapMessage.getSOAPBody();

    sessionCookie = getFirstChildElementValueByName(responseBody, "result");
    System.out.println("[DEBUG] Session cookie: " + sessionCookie);
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
