package edu.illinois.techservices.sympa;

import java.util.ArrayList;
import java.util.Map;

import javax.xml.namespace.QName;
import jakarta.xml.soap.*;
import edu.illinois.techservices.sympa.SympaClient;

public class Which {
  private static String sympaSoapUrl = SympaClient.sympaSoapUrl;
  private static String cookie = SympaClient.sessionCookie;

  public static String which() {
    try {
      SOAPMessage soapMessage = SympaClient.createMessageFactoryInstance();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

      MimeHeaders headers = soapMessage.getMimeHeaders();
      headers.addHeader("Content-Type", "text/xml");
      headers.addHeader("SOAPAction", "urn:sympasoap#which");
      headers.addHeader("Cookie", "sympa_session=" + cookie);

      SOAPBody soapBody = envelope.getBody();
      soapBody.addChildElement("which", "ns", "urn:sympasoap");
      soapMessage.saveChanges();

      // System.out.println("\n  Calling Which ");
      SOAPMessage resMsg = SympaClient.callSympaAPI(soapMessage);

      System.out.println("\n[DEBUG] Which Response : ");
      SympaClient.printSOAPMessage(resMsg);
      System.out.println("\n");
      String resAsStr = SympaClient.getFirstChildElementValueByName(
        resMsg.getSOAPBody(),
        "item"
      );
      System.out.println("[DEBUG] Parsed response:\n" + resAsStr);
      return resAsStr;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Map<String, Object> complexWhich() {
    try {
      SOAPMessage soapMessage = SympaClient.createMessageFactoryInstance();
      SOAPPart soapPart = soapMessage.getSOAPPart();
      SOAPEnvelope envelope = SympaClient.addNamespaceDeclaration(soapPart);

      MimeHeaders headers = soapMessage.getMimeHeaders();
      headers.addHeader("Content-Type", "text/xml");
      headers.addHeader("SOAPAction", "urn:sympasoap#complexWhich");
      headers.addHeader("Cookie", "sympa_session=" + cookie);

      SOAPBody soapBody = envelope.getBody();
      soapBody.addChildElement("complexWhich", "ns", "urn:sympasoap");
      soapMessage.saveChanges();

      // System.out.println("\n  Calling Complex Which ");
      SOAPMessage resMsg = SympaClient.callSympaAPI(soapMessage);

      System.out.println("\n[DEBUG] Complex Which Response : ");
      SympaClient.printSOAPMessage(resMsg);

      // Option 1: Print as json string (rename this function)
      // ArrayList<String> listItems = SympaClient.getElementListAsString(
      //   resMsg.getSOAPBody(),
      //   "return",
      //   "item"
      // );
      // if (listItems.size() == 0) {
      //   System.out.println("[WARN] No items found in which response");
      //   return null;
      //     stem.oSystem.out.println(listItems.get(0));

      // // Which returns a single item as semicolon-separated values
      // return listItems.get(0);

      // Option 2: Get json object and cast from there
      Map<String, Object> itemsJson = SympaClient.getElementListAsJson(
        resMsg.getSOAPBody(),
        "return",
        "item"
      );

      if (itemsJson.size() == 0) {
        System.out.println("[WARN] No items found in which response");
        return null;
      }
      System.out.println(itemsJson.get("isSubscriber"));

      // String isSub = ((Map<String, Object>)itemsJson.get("item")).get("isSubscriber").toString();
      // System.out.println("isSub: " + isSub);
      System.out.println("[DEBUG] Parsed response: \n" + itemsJson.toString() + "\n");
      return itemsJson;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void example() {
    Map<String, Object> itemsJson = complexWhich();
    System.out.println("[DEBUG] Response as JSON: \n" + itemsJson.toString());

    // Example fetch subscriber and owner status
    String isSub = itemsJson.get("isSubscriber").toString();
    System.out.println("isSubscriber: " + isSub);
    String isOwner = itemsJson.get("isOwner").toString();
    System.out.println("isOwner: " + isOwner);
  }
}
