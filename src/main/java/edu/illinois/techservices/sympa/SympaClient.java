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

  public static SOAPElement getFirstElementByName(SOAPElement parent, String childName) {
    List<SOAPElement> elements = getElementsByName(parent, childName, true);
    return elements.get(0);
  }

  public static List<SOAPElement> getAllElementsByName(SOAPElement parent, String childName) {
    return getElementsByName(parent, childName, false);
  }

  /**
   * Recursively fetch the first SOAPElement that matches the given node name.
   * Meant for SOAP response parsing.
   * Searches nested elements before siblings.
   * 
   * Avoid calling this funciton directly. Use the above wrappers for getall vs getfirst.
   *
   * @param parent      Parent SOAPElement
   * @param childName   Node name to search for
   * @param callback    Callback function to fetch values from matching elements
   * @return            First matching SOAPElement or null
   */
  // =========== MASTER FUNCTION ============================
  //--------------------------------------------------------------
  public static List<SOAPElement> getElementsByName(SOAPElement parent, String childName, Boolean returnFirstMatch) {
    if (parent == null) {
      System.out.println("[WARN] Parent SOAPElement is null. Cannot search for child element.");
      return null;
    }
    List<SOAPElement> matchingElements = new ArrayList<>();
    Iterator<?> children = parent.getChildElements();
    while (children.hasNext()) {
      Object next = children.next();
      if (!(next instanceof SOAPElement)) {
        continue;
      }
      SOAPElement child = (SOAPElement) next;
      if (child.getNodeName().equals(childName)) {
        // System.out.println("[DEBUG] Found element name: " + child.getNodeName());
        // if (callback != null) callback.apply(child);
        // return child;
        matchingElements.add(child);
        if (returnFirstMatch) {
          return matchingElements;
        }
      }
      // Log SOAP faults and stop searching
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
        List<SOAPElement> nestedElements = getElementsByName(child, childName, returnFirstMatch);
        if (nestedElements != null) {
          matchingElements.addAll(nestedElements);
        }
        // if (found != null) return found;
      }
      else {
        // System.out.println("[DEBUG] No child node match for: '" + childName + "'. Found: '" + child.getNodeName() + "'. Continuing search laterally (siblings).");
        // this is in a while loop so its going to continue searching through the siblings
      }
    }

    if (matchingElements.size() == 0) {
      System.out.println("[WARN] Element not found with name: " + childName);
      return null;
    }
    return matchingElements;
  }

  /**
   * EXAMPLE Manual implementation.
   * You can use org.json if you do not anticipate large response bodies
   * and dont care about typing.
   * 
   * Builds a JSON object from a SOAPElement.
   * Returns a value if element has no children.
   * 
   * Note: If you only want to return strings and have solid typing,
   *   you can modify this function to call element.getValue() instead of 
   *   getXmlTypedValue(element) (see below)
   * 
   * @param element Element to convert to JSON. Anticipates xml typing attributes on value-only elements.
   * @return JSON-like Map, or string value if element has no children
   */
  public static Map<String, Object> buildTypedJsonFromXml(SOAPElement element) {
    if (element == null) {
      System.out.println("[WARN] Cannot build JSON object from empty element.");
      return null;
    }

    // Build nested object from child elements
    java.util.Map<String, Object> jsonObj = new java.util.HashMap<>();

    if(!ifElementChildren(element, (SOAPElement child) -> {
      Map<String, Object> childValue = buildTypedJsonFromXml(child);
      jsonObj.putAll(childValue);
      return null;
    })) {
      jsonObj.put(element.getNodeName(), getXmlTypedValue(element));
    }
    return jsonObj;
  }

  /**
   * If element has children, perform callback on each child.
   * (check because saaj does not make distinction between elements and text nodes
   *  and you wind up with lots of repeat logic)
   * 
   * @param element
   * @param callback
   * @return
   */
  public static Boolean ifElementChildren(SOAPElement element, Function<SOAPElement, Void> callback) {
    Iterator<?> children = element.getChildElements();
    while (children.hasNext()) {
      Object next = children.next();
      // validate whether it's actually an element (saaj does not make distinction between elements and text nodes)
      if (next instanceof SOAPElement) {
        SOAPElement child = (SOAPElement) next;
        // Run intended action
        if (callback != null) callback.apply(child);
      } else {
        return false;
      }
    } 
    return true;
  }

  // overload
  public static Boolean ifElementChildren(SOAPElement element) {
    return ifElementChildren(element, null);
  }

  // only parses booleans and integers if not strings
  public static Object getXmlTypedValue(SOAPElement element) {
    // validate element has a value
    if (element == null || "true".equals(element.getAttribute("xsi:nil"))) {
      return null;
    }
    // based on xsd
    String type = element.getAttribute("xsi:type");
    String value = element.getValue();
    if (type == null || type.isEmpty() || value == null) {
      return value;
    }
    /* 
     *  Example manual xml type casting.
     *  Can be extended to support more types.
     *  There are probably libraries out there that also look at
     *  the WSDL but the ones we tried were not compatible with the
     *  version of SOAP sympa uses
     */
    switch (type) {
      case "xsd:boolean":
        return Boolean.valueOf(value);
      case "xsd:int":
        try {
          return Integer.valueOf(value);
        } catch (NumberFormatException e) {
          return value;
        }
      case "xsd:string":
      default:
        return value;
    }
  }

  public static String buildJsonStringFromXml(SOAPElement element) {
    StringBuilder jsonLike = new StringBuilder("{");

    if(ifElementChildren(element, (SOAPElement child) -> {
      jsonLike.append(",");
      jsonLike.append("\"").append(child.getNodeName()).append("\":\"")
          .append(child.getValue()).append("\"");
      return null;
    })) {
      // remove first comma
      jsonLike.deleteCharAt(1);
      jsonLike.append("}");
      return jsonLike.toString();
    }
    else {
      jsonLike.append("}");
      return jsonLike.toString();
    }
  }

  /**
   * boolean to kill or continue
   * log warn when list element is not found or not a soapenc:Array
   * 
   * @param listElement
   * @param listElementName
   * @return true if element not null
   */
  public static Boolean validateListElementNotNull(SOAPElement listElement, String listElementName) {
    if (listElement == null) {
      System.out.println("[WARN] List element not found with name: " + listElementName);
      return false; // kill
    }
    if (!listElement.getAttribute("xsi:type").equals("soapenc:Array")) {
      System.out.println("[WARN] List element is not a soapenc:Array. You may have the wrong listElementName. Attempting to continue...");
      return true;
    }
    return true;

  }

  /**
   * Expects a soapenc:Array element.
   * (attribute typesxsi:type="soapenc:Array")
   * Use map for a json-like object
   * 
   * @param masterElement         Usually the response body
   * @param listParentName  eg, "return", as in <return>...</return>
   * @param listItemNames   eg, "item", as in <item>...</item>
   * @return
   */
  public static List<Map<String, Object>> getElementListAsMapList(SOAPElement masterElement, String listParentName, String listItemNames) {
    SOAPElement listParent = getFirstElementByName(masterElement, listParentName);

    if (!validateListElementNotNull(listParent, listParentName)) {
      return null;
    }

    List<SOAPElement> listItemsElements = getAllElementsByName(listParent, listItemNames);
    List<Map<String, Object>> listItemsMap = new ArrayList<>();
    for (SOAPElement listItemElement : listItemsElements) {
      Map<String, Object> outMap = buildTypedJsonFromXml(listItemElement);
      listItemsMap.add(outMap);
    }
    return listItemsMap;
  }

  /**
   * Fetch list of elements as a single string in JSON format.
   * Specify the list parent so that it can iterate from that point on.
   * 
   * @param masterElement   Usually the response body
   * @param listParentName  Node name of the parent element that contains the list (eg, "return")
   * @param listItemNames   Node name of list items to be retrieved (eg, "item")
   * @return
   */
  public static List<String> getElementListAsJsonStringList(SOAPElement masterElement, String listParentName, String listItemNames) {

    SOAPElement listParent = getFirstElementByName(masterElement, listParentName);
    if (!validateListElementNotNull(listParent, listParentName)) {
      return null;
    }

    List<String> jsonStringList = new ArrayList<>();

    List<SOAPElement> listItemsElements = getAllElementsByName(listParent, listItemNames);
    for (SOAPElement listItemElement : listItemsElements) {
      String jsonString = buildJsonStringFromXml(listItemElement);
      jsonStringList.add(jsonString);
    }

    return jsonStringList;
  }

  /**
   * DEV: ON HOLD OR SLATED FOR DELETE (is useful for when list items do not have
   * nodenames and only values, but i dont have a call to test on atm)
   * 
   * Fetch list of elements as strings (cast/untyped)
   * @param masterElement   Usually the response body
   * @param listItemNames   Node name of list items to be retrieved
   * @return
   */
  // public static ArrayList<String> getAllChildElementValuesAsStringArray(SOAPElement masterElement, String listItemNames) {
  //   SOAPElement listParent = getFirstElementByName(masterElement, listItemNames);
  //   ArrayList<String> listItems = new ArrayList<>();
  //     // String value = child.getValue();
  //     // if (value != null) {
  //     //     listItems.add(value);
  //     // }
  //   return listItems;
  // }

  // Overload, if expecting string return
  public static String getFirstChildElementValueByName(SOAPElement masterElement, String childName) {
    return (String) getFirstChildElementValueByName(masterElement, childName, false);
  }
  /**
   * Assumes child element contains a value. Does not validate whether element or text node
   * Returns an untyped value as string.
   *
   * @param masterElement Parent SOAPElement
   * @param childName Node name to search for
   * @return          String value or null
   */
  public static Object getFirstChildElementValueByName(SOAPElement masterElement, String childName, Boolean isTyped) {
    SOAPElement childElement = getFirstElementByName(masterElement, childName);
    // may be redundant
    if (childElement == null) {
      System.out.println("[WARN] List element not found with name: " + childName);
      return null; 
    }
    if (isTyped) {
      // Try to type cast
      return getXmlTypedValue(childElement);
    }
    return childElement.getValue();
  }

  /**
   * Tries to cast to type
   * @param masterElement
   * @param childName
   * @return
   */
  public static Object getTypedFirstChildElementValueByName(SOAPElement masterElement, String childName) {
    SOAPElement childElement = getFirstElementByName(masterElement, childName);
    if (childElement == null) {
      System.out.println("[WARN] List element not found with name: " + childName);
      return null; 
    }
    return getXmlTypedValue(childElement);
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
