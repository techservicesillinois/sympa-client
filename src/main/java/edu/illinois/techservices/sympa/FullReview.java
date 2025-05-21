package edu.illinois.techservices.sympa;

import javax.xml.namespace.QName;

import javax.xml.soap.*;
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FullReview {

  private static String sympaSoapUrl = "https://lists-dev.techservices.illinois.edu/sympasoap";

  public static void fullreview(String cookie, String listName, String type) {
    try {
      List<String> emailList = new ArrayList<>();
      SOAPMessage soapMessage = SympaClient.createMessageFactoryInstance();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

      MimeHeaders headers = soapMessage.getMimeHeaders();
      headers.addHeader("Content-Type", "text/xml");
      headers.addHeader("SOAPAction", "urn:sympasoap#fullReview");
      headers.addHeader("Cookie", "sympa_session=" + cookie);
      SOAPBody soapBody = envelope.getBody();

      SOAPElement soapElement = soapBody.addChildElement("fullReview", "ns", "urn:sympasoap");

      System.out.println(listName);

      soapElement.addChildElement("list", "ns")
          .addTextNode(listName)
          .addAttribute(new QName("xsi:type"), "xsd:string");

      soapMessage.saveChanges();
      SympaClient.printSOAPMessage(soapMessage);
      // Create a SOAP connection
      SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
      SOAPConnection soapConnection = soapConnectionFactory.createConnection();

      // Send the SOAP message to the endpoint
      SOAPMessage fullreview = soapConnection.call(soapMessage, sympaSoapUrl);
      System.out.println("\n Full Review Response : ");
      SympaClient.printSOAPMessage(fullreview);

      emailList = permissionPrint(fullreview, type);
      System.out.println("List of " + type + ": " + emailList);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Prints the permissions of
   * 
   * @param soapMessage
   */
  public static List<String> permissionPrint(SOAPMessage soapMessage, String type) throws Exception {

    int count = 0;
    List<String> emailList = new ArrayList<>();

    SOAPBody responseBody = soapMessage.getSOAPBody();

    if (responseBody.hasFault()) {
      SOAPFault fault = responseBody.getFault();
      System.err.println("SOAP Fault: " + fault.getFaultString());

      return emailList;
    }

    NodeList returnNodes = responseBody.getElementsByTagName("return");
    if (returnNodes.getLength() == 0) {
      System.out.println("No <return> element found.");
    }

    if ("subscriber".equalsIgnoreCase(type)) {
      type = "isSubscriber";
    } else if ("owner".equalsIgnoreCase(type)) {
      type = "isOwner";
    } else {
      type = "isEditor";
    }

    Node returnNode = returnNodes.item(0);
    NodeList items = returnNode.getChildNodes();
    Boolean flag = false;
    for (int i = 0; i < items.getLength(); i++) {
      Node item = items.item(i);
      String email = "";
      if (item.getNodeType() == Node.ELEMENT_NODE && "item".equals(item.getLocalName())) {

        NodeList fields = item.getChildNodes();

        for (int j = 0; j < fields.getLength(); j++) {
          Node field = fields.item(j);
          String fieldValue = field.getTextContent() != null ? field.getTextContent().trim() : "";
          System.out.println("Field Name: " + field.getLocalName() + "; fieldValue: " + fieldValue);

          if (field.getNodeType() == Node.ELEMENT_NODE) {
            if ("email".equalsIgnoreCase(field.getLocalName())) {
              email = fieldValue;
            }
            if (type.equals(field.getLocalName()) && "true".equalsIgnoreCase(fieldValue)) {
              flag = true;
            }
            if (!"".equals(email) && email != null && flag) {
              emailList.add(email);

              // Reset the 'flag' and 'email' once we find the necessary type(i.e.,
              // editor/owner/subscriber) so the values that are
              // set are not repeated in the next iteration.
              flag = false;
              email = "";
            }
          }
        }
      }
    }
    return emailList;
  }
}
