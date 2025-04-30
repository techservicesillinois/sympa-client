package edu.illinois.techservices.sympa;

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
            SympaClient.createList(sessionCookie);
            break;
          }
          case "getInfo":
          {
            SympaClient.getInfo(sessionCookie);
            break;
          }
          case "review":
          {
            Review.review(sessionCookie);
            break;
          }
          case "subscribe":
          {
            Subscribe.subscribe(sessionCookie);
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
