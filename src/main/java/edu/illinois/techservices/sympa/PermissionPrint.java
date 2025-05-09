package edu.illinois.techservices.sympa;


import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jakarta.xml.soap.*;



public class PermissionPrint {

      /**
   * Prints the permissions of 
   * @param soapMessage
   */
public static String permissionPrint(SOAPMessage soapMessage) throws Exception { 

    try {
        SOAPBody responseBody = soapMessage.getSOAPBody();
        Iterator<?> iterator = responseBody.getChildElements();
        while (iterator.hasNext()) {
            SOAPElement element = (SOAPElement) iterator.next();

            System.out.println(element.getTagName());
            System.out.println(element);
        }


        
        /*DOMSource source = new DOMSource(responseBody);
        StringWriter stringResult = new StringWriter();
        TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
        String message = stringResult.toString();
        System.out.println(message);

        Iterator attributes = responseBody.getAllAttributesAsQNames();
        System.out.println(attributes.toString());*/



        //Iterator<Node> nodes = responseBody.getChildElements();
        //while(nodes.hasNext()){
        //    System.out.println(nodes.next().toString());
        //}


        //System.out.println(nodes.next().toString());
        

        }  catch(Exception e) {

}
    return null;

}
}