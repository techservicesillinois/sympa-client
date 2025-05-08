package edu.illinois.techservices.sympa;

public class Main {
  
  public static void main(String[] args) {
    String sessionCookie = null;
    String input;
      try {
        input = args[0];
        System.out.println("input: " + input);
      } catch (ArrayIndexOutOfBoundsException e) {
        System.out.println("[ERROR] Please provide an argument for function call.");
        System.out.println("  Example args: getList, createList, getInfo");
        System.out.println("  Example Usage: -Dexec.args=\"getList\"");
        throw new IllegalArgumentException("[ERROR] Please provide an argument for function call. See logs for details.");
      }
      // TODO: Validate call before logging in (use enum or something similar?)
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
          case "add":
          {
            SympaClient.add(sessionCookie);
            break;
          }
          case "del":
          {
            SympaClient.del(sessionCookie);
            break;
          }
          case "getComplexLists": {
            SympaClient.getComplexLists(sessionCookie);
            break;
          }
          case "closeList": {
            SympaClient.closeList(sessionCookie);
            break;
          }
          default:
            System.out.println("Invalid API call. Please provide a valid function call.");
            System.out.println("Example args: getList, createList, getInfo");
            System.out.println("Example Usage: -Dexec.args=\"getList\"");
          }
        }
  }
}
