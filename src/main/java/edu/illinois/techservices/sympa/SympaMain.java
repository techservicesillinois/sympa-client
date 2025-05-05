package edu.illinois.techservices.sympa;

public class Main {

  public static void main(String[] args) {
    String sessionCookie = null;
    String input = null;
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
      switch (input) {

        case "getList": {
          SympaClient.getLists(sessionCookie);
          break;
        }
        case "createList": {
          String listName = null;
          if (args.length > 1) {
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
            System.out
                .println("Please Provide service(for example: add/del) and parameters required to perform add/del");
            System.exit(0);
          }
        }
        case "authenticateRemoteAppAndRun": {
          SympaLoginClient.authenticateRemoteAppAndRun();
          break;
        }
        case "getUserEmailByCookie": {
          SympaLoginClient.getUserEmailByCookie(sessionCookie);
          break;
        }
        case "review": {
          String listName = null;
          if (args.length > 0) {
            listName = args[1];
          }
          Review.review(sessionCookie, listName);
          break;
        }
        case "subscribe": {
          String listName = null;
          if (args.length > 0) {
            listName = args[1];
          }
          Subscribe.subscribe(sessionCookie, listName);
          break;
        }
        case "fullreview": {
          FullReview.fullreview(sessionCookie);
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
