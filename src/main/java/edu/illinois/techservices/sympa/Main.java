package edu.illinois.techservices.sympa;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPMessage;


public class Main {
  
  public static void main(String[] args) {
    String sessionCookie = null;
    try {
      String input = args[0];
      System.out.println("input: " + input);
      sessionCookie = SympaClient.loginSympa();
      
      if (sessionCookie != null) {

        switch(input) {
          case "getList": {
            SympaClient.getLists(sessionCookie);
            break;
          }
          case "createList": {
            //SympaClient.createList(sessionCookie);
            break;
          }
          case "getInfo":
          {
            SympaClient.getInfo(sessionCookie);
            break;
          }
          case "review":
          {
            //Review.review(sessionCookie);
            break;
          }
          case "subscribe":
          {
            String listName = null;
            if (args.length > 0) {
              listName = args[1];
            }
            Subscribe.subscribe(sessionCookie, listName);
            break;
          }
          case "fullreview":
          {
            String listName = null;
            if (args.length > 0) {
              listName = args[1];
              InputStream xmldoc = new ByteArrayInputStream(listName.getBytes());
              SOAPMessage request = MessageFactory.newInstance().createMessage(null, xmldoc);
              System.out.println(request);


            }
            FullReview.fullreview(sessionCookie, listName);
            break;
          }
          default:
            System.out.println("wrong input");
        }
       
      }
      
    } catch(Exception e) {
      e.printStackTrace();
    }
    
    
     
  }
}
