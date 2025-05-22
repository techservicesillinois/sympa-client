# XML-Parsing Utility Functions for SOAP Responses

## About XmlParser

`XMLParser` is a utility class for parsing SOAP XML responses. It was designed to handle common response patterns by Sympa's SOAP API, but can be used for other SOAP APIs.

This is meant to be an example of how you might parse the various responses.
It is not meant to be a complete or comprehensive XML-parsing library. It is meant to be lightweight, for the purpose of simplifying redundant and lengthy code.

For more advanced XML processing, consider using dedicated libraries. See [Design Notes](#design-notes) section for more information on design choices.

## Use cases

- Extracting a list of items as a list of maps or JSON strings.
- Fetching a single value by element name.
- Getting typed values from elements.


### Quick Example Usage
Using Sympa's `fullreview` API SOAP response (a robust response to test with)

#### getElementListAsMapList

```java
// Get json-like map list
List<Map<String, Object>> itemsJson = XmlParser.getElementListAsMapList(
  fullreview.getSOAPBody(), // masterElement
  "return",                 // listParentName
  "item"                    // listItemNames
);
System.out.println("itemsJson");
for (Map<String, Object> item : itemsJson) {
  // System.out.println(item.get("email"));
  System.out.println(item.toString());
}
```

#### getElementListAsJsonStringList

```java
// Get json-like string list
List<String> jsonStringList = XmlParser.getElementListAsJsonStringList(
  fullreview.getSOAPBody(), // masterElement
  "return",                 // listParentName
  "item"                    // listItemNames
);
System.out.println("jsonStringList");
for (String item : jsonStringList) {
  System.out.println(item);
}
```

#### getFirstChildElementValueByName
```java
// Get untyped string value
String email = XmlParser.getFirstChildElementValueByName(
  fullreview.getSOAPBody(), // masterElement
  "email"                   // childName
);
System.out.println("email: " + email);
```

#### getTypedFirstChildElementValueByName
```java
// Get typed value
Object isSubscriber = XmlParser.getTypedFirstChildElementValueByName(
  fullreview.getSOAPBody(), // masterElement
  "isSubscriber"            // childName
);
System.out.println("isSubscriber: " + isSubscriber);
System.out.println("isSubscriber type: " + isSubscriber.getClass().getName());
```

--------

## Key Features

- **Recursive element search**: Find elements by name, either the first match or all matches, traversing nested structures.
- **Data extraction**: Retrieve element values as strings or attempt to cast them to appropriate Java types (e.g., `Boolean`, `Integer`). Designed for extensibility to other types as needed.
- **List handling**: Convert lists of XML elements into Java `List<Map<String, Object>>` or `List<String>` in JSON-like format.
- **SOAP Fault handling**: Detect and log SOAP faults encountered during parsing.
- **Minimal dependencies**: Avoids heavy libraries for memory efficiency and compatibility with Sympa's SOAP implementation.


## Design Notes
- This was designed to be less memory-intensive than other libraries like org.json.
- Modern SOAP-parsing libraries tried were not fully compatible with the version of SOAP Sympa uses, and led to poor or incomplete handling of requests and responses.
- Handles and logs SOAP Faults in order to debug appropriately. (Existing solutions tried did not fit this need)
- **Error handling**: These methods generally do not throw exceptions, but will log warnings if elements are not found or if the expected structure is not present. If you would prefer an error-first approach, you can modify this file as such for your own uses.
- **Naming convention**: Args with the convention `masterElement` will indicate that there may be further checks or parsing of a parent element. This can occur most often when parsing lists, especially where child elements will not all have the same node name, and where search would be more complex otherwise.

------

## Methods

### 1. Element Search
Return full element object

#### `getElementsByName(SOAPElement parent, String childName, Boolean returnFirstMatch)`
- **Description**: Recursively searches for elements with the specified name within the given parent element. Can return either the first match or all matches.
- **Note**: This method is not meant to be called directly. Use one of the two methods below.
- **Returns**: `List<SOAPElement>`, empty, or `null` if not found or on SOAP fault

#### `getFirstElementByName(SOAPElement parent, String childName)`
- **Description**: Returns the first `SOAPElement` matching the given name.
- **Returns**: `SOAPElement` or `null`

#### `getAllElementsByName(SOAPElement parent, String childName)`
- **Description**: Returns all `SOAPElement` instances matching the given name.
- **Returns**: `List<SOAPElement>`

------

### 2. Data Extraction: Element Value Retrieval
Return parsed data values

Common parameters:

| Parameter | Description | Example |
| --- | --- | --- |
| `masterElement` | Usually the response body | `SOAPElement.getSOAPBody()` |
| `listParentName` | Node name of the parent element that contains the list. This ensures we are fetching the correct list. This is also necessary for parsing lists where child elements will not all have the same node name. | "return" |
| `listItemNames` | Node name of list items to be retrieved | "item" |


#### `getElementListAsJsonStringList(SOAPElement masterElement, String listParentName, String listItemNames)`
- **Description**: Extracts a list of elements and converts each to a JSON-formatted `String`.
- **Returns**: `List<String>`
- **Example output**:
```
  [{"email":"test@example.com","isSubscriber":true},{"email":"test2@example.com","isSubscriber":false}]
```

#### `getElementListAsMapList(SOAPElement masterElement, String listParentName, String listItemNames)`
- **Description**: Extracts a list of elements (e.g., `<item>`) under a parent (e.g., `<return>`) and converts each to a `Map<String, Object>` representing a JSON-like structure, with type casting.
- **Note**: Expects parent to be a soapenc:Array element.
- **Returns**: `List<Map<String, Object>>`

#### `getFirstChildElementValueByName(SOAPElement masterElement, String childName)`
- **Description**: Retrieves the value of the first child element with the specified name as a `String`.
- **Note**: Assumes child element contains a value. Does not validate whether element or text node.
- **Returns**: `String` or `null`

#### `getTypedFirstChildElementValueByName(SOAPElement masterElement, String childName)`
- **Description**: Retrieves the value of the first child element with the specified name, attempting to cast it to the appropriate Java type based on XML schema type attributes.
- **Returns**: `Object` (may be `Boolean`, `Integer`, or `String`).


---

### 3. Data Parsing and Typing
Helpers to the above.

#### `getXmlTypedValue(SOAPElement element)`
- **Description**: Attempts to cast the value of an element to a Java type based on its `xsi:type` attribute (supports `xsd:boolean`, `xsd:int`, and `xsd:string`).
- **Returns**: `Object` (typed value or `String`)

#### `buildTypedJsonFromXml(SOAPElement element)`
- **Description**: Recursively builds a JSON-like `Map<String, Object>` from the given element, with type casting for leaf values.
- **Returns**: `Map<String, Object>`

#### `buildJsonStringFromXml(SOAPElement element)`
- **Description**: Builds a JSON-formatted `String` from the given element and its children.
- **Returns**: `String`

---

### 5. Validation and Utility
Note: You should **not** need to use these methods unless modifying the library.

#### `ifElementChildren(SOAPElement element, Function<SOAPElement, Void> callback)`
- **Description**: Iterates over child elements, applying a callback if provided. Returns `true` if children exist.
- **Why this was added**: SAAJ does not make distinction between elements and text nodes, so it is not simple to check.
- **Returns**: `Boolean`

The `callback` is meant to be used when you need to fetch values or other
metadata from the child elements, but you don't want to re-parse the xml.
You can do this by instantiating an object before you call ifElementChildren
and then calling the object's methods within the callback. This will successfully
mutate the object's state; there is no scoping issue.

Example usage:
```java
List<String> myList = new ArrayList<>();

ifElementChildren(element, (SOAPElement child) -> {
  myList.add(child.getValue());
  return null;
})
```

You can use the output of `ifElementChildren` to determine what to do next. In the XmlParser file, it is wrapped in an `if` check.

#### `validateListElementNotNull(SOAPElement listElement, String listElementName)`
- **Description**: Checks if the given element is not `null` and logs warnings if not found or not a SOAP array.
- **Returns**: `Boolean`

---




## Error Handling

- Log warnings if elements are not found or if the expected structure is not present.
- Detects and logs SOAP faults, halting further parsing in such cases.

## Limitations

- Only basic XML schema types are supported for type casting.
- Not a general-purpose XML-to-JSON converter. Tailored for Sympa SOAP response patterns.
- Minimal error recovery, primarily logs and returns `null` or empty results on error.
