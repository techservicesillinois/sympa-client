# XML-Parsing Utility Functions for SOAP Responses

## Example usage:
Using `fullreview` SOAP response (a robust response to test with)

### getElementListAsMapList
Args: `masterElement`, `listParentName`, `listItemNames`

Returns: List of json-like maps (`List<Map<String, Object>>`)
```java
// Get json-like map list
List<Map<String, Object>> itemsJson = SympaClient.getElementListAsMapList(
  fullreview.getSOAPBody(),
  "return",
  "item"
);
System.out.println("itemsJson");
for (Map<String, Object> item : itemsJson) {
  // System.out.println(item.get("email"));
  System.out.println(item.toString());
}
```

### getElementListAsJsonStringList
Args: `masterElement`, `listParentName`, `listItemNames`

Returns: List of json-like strings (`List<String>`)
```java
// Get json-like string list
List<String> jsonStringList = SympaClient.getElementListAsJsonStringList(
  fullreview.getSOAPBody(),
  "return",
  "item"
);
System.out.println("jsonStringList");
for (String item : jsonStringList) {
  System.out.println(item);
}
```

### getFirstChildElementValueByName
Args: `masterElement`, `childName`

Returns: String value
```java
// Get untyped string value
String email = SympaClient.getFirstChildElementValueByName(
  fullreview.getSOAPBody(),
  "email"
);
System.out.println("email: " + email);
```

### getTypedFirstChildElementValueByName
Args: `masterElement`, `childName`

Returns: Typed value (currently oneof: String, Boolean, Integer)
```java
// Get typed value
Object isSubscriber = SympaClient.getTypedFirstChildElementValueByName(
  fullreview.getSOAPBody(),
  "isSubscriber"
);
System.out.println("isSubscriber: " + isSubscriber);
System.out.println("isSubscriber type: " + isSubscriber.getClass().getName());
```