package edu.illinois.techservices.sympa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import jakarta.xml.soap.*;

/*
 * Utility class for parsing SOAP responses.
 * 
 * This class is meant to be an EXAMPLE of how you might parse various SOAP responses.
 * It is not meant to be a complete or comprehensive library.
 * It is used throughout this project to simplify the code.
 * 
 * Parts of this class were implemented in lieu of libraries like org.json because they are less
 * memory-intensive and allow for typing and handling of various things like SOAP Faults.
 * 
 * The versions of SOAP-parsing libraries we tried were not fully compatible with the
 * version of SOAP sympa uses, and were thus incomplete and insufficient for our needs.
 * It is helpful to be able to handle and log SOAP Faults properly, so that you can
 * debug your code.
 * 
 */

public final class XmlParser {
  private XmlParser(){}

  // ============= FETCHING FUNCTIONS =============
  /**
   * Recursively fetch the first SOAPElement that matches the given node name.
   * Meant for SOAP response parsing.
   * Searches nested elements before siblings.
   * 
   * **Avoid calling this funciton directly.**
   * Use the below wrappers for getallelements vs getfirstelement
   *
   * @param parent      Parent SOAPElement
   * @param childName   Node name to search for
   * @param callback    Callback function to fetch values from ma tching elements
   * @return            First matching SOAPElement or null
   */
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
      else if (child.getChildElements().hasNext()) {
        // System.out.println("[DEBUG] No child node match for: '" + childName + "'. Found: '" + child.getNodeName() + "'. Continuing search nested (children).");
        // Recursive search on nested elements
        List<SOAPElement> nestedElements = getElementsByName(child, childName, returnFirstMatch);
        if (nestedElements != null) {
          matchingElements.addAll(nestedElements);
        }
      }
      else {
        // TODO: Implement prasannas logger stuff so we can set the log level and not
        // have to comment/uncomment lines like this
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

  public static SOAPElement getFirstElementByName(SOAPElement parent, String childName) {
    List<SOAPElement> elements = getElementsByName(parent, childName, true);
    return elements.get(0);
  }

  public static List<SOAPElement> getAllElementsByName(SOAPElement parent, String childName) {
    return getElementsByName(parent, childName, false);
  }

  // === Data Extraction ===
  // === Get elements as.... string, list of maps, list of strings ===

  /**
   * Fetch list of elements as a single string in JSON format.
   * Specify the list parent so that it can iterate from that point on.
   * 
   * @param masterElement  Usually the response body
   * @param listParentName Node name of the parent element that contains the list
   *                       (eg, "return")
   * @param listItemNames  Node name of list items to be retrieved (eg, "item")
   * @return
   */
  public static List<String> getElementListAsJsonStringList(SOAPElement masterElement, String listParentName,
      String listItemNames) {

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
   * Expects a soapenc:Array element.
   * (attribute typesxsi:type="soapenc:Array")
   * Use map for a json-like object
   * 
   * @param masterElement  Usually the response body
   * @param listParentName eg, "return", as in <return>...</return>
   * @param listItemNames  eg, "item", as in <item>...</item>
   * @return
   */
  public static List<Map<String, Object>> getElementListAsMapList(SOAPElement masterElement, String listParentName,
      String listItemNames) {
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

  // Overload, if expecting string return
  public static String getFirstChildElementValueByName(SOAPElement masterElement, String childName) {
    return (String) getFirstChildElementValueByName(masterElement, childName, false);
  }

  /**
   * Assumes child element contains a value. Does not validate whether element or
   * text node
   * Returns an untyped value as string.
   *
   * @param masterElement Parent SOAPElement
   * @param childName     Node name to search for
   * @return String value or null
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
   * 
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
   * DEV: ON HOLD OR SLATED FOR DELETE (is useful for when list items do not have
   * nodenames and only values, but i dont have a call to test on atm)
   * 
   * Fetch list of elements as strings (cast/untyped)
   * 
   * @param masterElement Usually the response body
   * @param listItemNames Node name of list items to be retrieved
   * @return
   */
  // public static ArrayList<String>
  // getAllChildElementValuesAsStringArray(SOAPElement masterElement, String
  // listItemNames) {
  // SOAPElement listParent = getFirstElementByName(masterElement, listItemNames);
  // ArrayList<String> listItems = new ArrayList<>();
  // // String value = child.getValue();
  // // if (value != null) {
  // // listItems.add(value);
  // // }
  // return listItems;
  // }

  // =========== VALIDATION FUNCTIONS ===============

  // overload
  public static Boolean ifElementChildren(SOAPElement element) {
    return ifElementChildren(element, null);
  }
  /**
   * If element has children, perform callback on each child.
   * (check because saaj does not make distinction between elements and text nodes
   *  and you wind up with lots of repeat logic)
   * 
   * The callback is meant to be used when you need to fetch values or other
   * metadata from the child elements, but you don't want to re-parse the xml.
   * You can do this by instantiating an object before you call ifElementChildren
   * and then calling the object's methods within the callback. This will successfully
   * mutate the object's state; there is no scoping issue.
   * 
   * @param element
   * @param callback (SOAPElement child) The action you'd like to perform on each child
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

  /**
   * Return boolean when element not null
   * May be used to determine whether to stop/kill or continue in outside logic
   * log warnings when list element is not found or not a soapenc:Array
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
      System.out.println(
          "[WARN] List element is not a soapenc:Array. You may have the wrong listElementName. Attempting to continue...");
      return true;
    }
    return true;
  }


  // =========== DATA PARSING FUNCTIONS ===============

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
     * Example manual xml type casting.
     * Can be extended to support more types.
     * There are libraries out there that also look at the WSDL
     * but the ones we tried were not compatible with the version
     * of SOAP sympa uses
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
  /**
   * Example manual implementation of XML parsing for SOAP response bodies.
   * 
   * You can use org.json to parse XLM if you do not anticipate large response
   * bodies and dont care about typing.
   * 
   * Builds a JSON-like object from a SOAPElement.
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
  
  // Build a JSON-formatteds string from a SOAPElement.
  public static String buildJsonStringFromXml(SOAPElement element) {
    StringBuilder jsonLike = new StringBuilder("{");

    if (ifElementChildren(element, (SOAPElement child) -> {
      jsonLike.append(",");
      jsonLike.append("\"").append(child.getNodeName()).append("\":\"")
          .append(child.getValue()).append("\"");
      return null;
    })) {
      // remove first comma
      jsonLike.deleteCharAt(1);
      jsonLike.append("}");
      return jsonLike.toString();
    } else {
      jsonLike.append("}");
      return jsonLike.toString();
    }
  }
}
