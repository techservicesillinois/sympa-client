# SOAP/HTTP API Example Usage


### **RPC Call: `fullReview(listname)`**
**Use case:** Get information about subscribers, owners, and editors in a list

Perl Example:
```
my $res = $soap_client->fullReview("scrumteamb");
```
**Note:** pass cookie `sympa_session` ID from `login()`. See [SympaRequestStructure.md](SympaRequestStructure.md) for details on cookie management.

Response

<!-- [todo: make consistent with other page?] -->

XML Body `fullReviewResponse.return` contains list of users `item` with attributes:
- **`isSubscriber`** - boolean
- **`isOwner`** - boolean
- **`isEditor`** - boolean
- **`email`** - string
- **`gecos`** - string

Raw response:
```
<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope 
    soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"
    xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <soap:Body>
        <fullReviewResponse xmlns="urn:sympasoap">
            <return soapenc:arrayType="xsd:anyType[5]" xsi:type="soapenc:Array">
                <item>
                    <isOwner xsi:type="xsd:boolean">true</isOwner>
                    <isSubscriber xsi:type="xsd:boolean">false</isSubscriber>
                    <email xsi:type="xsd:string">pbale@illinois.edu</email>
                    <isEditor xsi:type="xsd:boolean">false</isEditor>
                    <gecos xsi:type="xsd:string">Prasanna</gecos>
                </item>
                <item>
                    <isSubscriber xsi:type="xsd:boolean">true</isSubscriber>
                    <isOwner xsi:type="xsd:boolean">false</isOwner>
                    <gecos xsi:type="xsd:string">true</gecos>
                    <email xsi:type="xsd:string">aosar@illinois.edu</email>
                    <isEditor xsi:type="xsd:boolean">false</isEditor>
                </item>
                <item>
                    <gecos xsi:type="xsd:string">true</gecos>
                    <email xsi:type="xsd:string">xyz123@gmail.com</email>
                    <isEditor xsi:type="xsd:boolean">false</isEditor>
                    <isSubscriber xsi:type="xsd:boolean">true</isSubscriber>
                    <isOwner xsi:type="xsd:boolean">false</isOwner>
                </item>
                <item>
                    <isSubscriber xsi:type="xsd:boolean">true</isSubscriber>
                    <isOwner xsi:type="xsd:boolean">false</isOwner>
                    <gecos xsi:type="xsd:string">true</gecos>
                    <email xsi:type="xsd:string">rstanton@illinois.edu</email>
                    <isEditor xsi:type="xsd:boolean">false</isEditor>
                </item>
                <item>
                    <isEditor xsi:type="xsd:boolean">false</isEditor>
                    <email xsi:type="xsd:string">scrumteamb@illinois.edu</email>
                    <gecos xsi:type="xsd:string"/>
                    <isOwner xsi:type="xsd:boolean">false</isOwner>
                    <isSubscriber xsi:type="xsd:boolean">true</isSubscriber>
                </item>
            </return>
        </fullReviewResponse>
    </soap:Body>
</soap:Envelope>
```



