package edu.illinois.techservices.sympa;

import edu.illinois.techservices.sympa.SympaLoginClient;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class SympaMain {

  public static void main(String[] args) {
    String sessionCookie = null;
    try {

      String input = args[0];
      System.out.println("input: " + input);
      sessionCookie = SympaClient.loginSympa();

      if (sessionCookie != null) {

        switch (input) {
          case "getList": {
            SympaClient.getLists(sessionCookie);
            break;
          }
          case "createList": {
            String listName = null;
            if (args.length > 0) {
              listName = args[1];
            }
            SympaClient.createList(sessionCookie, listName);
            break;
          }
          case "getInfo": {
            SympaClient.getInfo(sessionCookie);
            break;
          }
          case "authenticateAndRun": {
            // SympaLoginClient.authenticateAndRun(sessionCookie, "del");
            String service = null;
            List<String> serviceParameters = new ArrayList<>();

            if (args.length == 3) {
              String parameters = null;
              service = args[1];
              parameters = args[2];
              serviceParameters.addAll(Arrays.asList(parameters.split(",")));
              System.out.println("service: " + service);
              System.out.println("serviceParameters: " + serviceParameters.size());
              System.out.println("serviceParameters: " + serviceParameters);

              SympaLoginClient.authenticateAndRun(sessionCookie, service, serviceParameters);
              break;
            } else {
              System.out.println("Please Provide all the attributes");
              System.exit(0);
            }
          }
          default:
            System.out.println("wrong input");
        }

      }

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
