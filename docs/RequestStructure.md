# Sympa SOAP/HTTP API Data Structures and Authentication Handling

Examples of how to make a request and handle the response from the Sympa SOAP/HTTP API using Perl. This document outlines the data structures and authentication management logic for the Sympa SOAP/HTTP API.

**Please note:** The request headers displayed are **incomplete**. SOAP Lite will append more information before the final request goes out, which includes cookies. The headers shown should be sufficient as a guide for the final request.

## Connection Configuration details:
| Key Name | Description | Example Value |
|-----------|-------------|---------|
| `url` | The URL of the SOAP/HTTP API | `https://lists-dev.techservices.illinois.edu/sympasoap` |
| `default_ns` | The default namespace for the SOAP/HTTP API as defined in the wsdl file | `urn:sympasoap` |

Note: The wsdl file/url is not necessary to make a call with soap lite, but may be needed for other languages.

## Authentication Handling
You will make a call to `login()` to get an auth cookie. This cookie will be used for all subsequent calls.

Authentication Cookie: <mark>**`sympa_session=123456789`**</mark>

How cookies are initialized and used in perl with soap lite:

```
my $url = 'https://lists-dev.techservices.illinois.edu/sympasoap';
my $cookie_jar = HTTP::Cookies->new();
my $soap_client = SOAP::Lite
    ->proxy($url, cookie_jar => $cookie_jar)
    ->default_ns('urn:sympasoap');
```


## Making API Calls

You will call one of many RPC methods to interact with the Sympa SOAP/HTTP API. Here is an example of how to utilize the `login()` method to retrieve and handle the auth cookie. This will then be used to make a call to `lists()` to retrieve a list of email lists.

### RPC Call: `login(email, pw)`

**`$soap_client->login($email, $pw);`**

**Cookies returned are auto-set by the library** (SOAP Lite)
#### REQUEST

```
SOAP::Transport::HTTP::Client::send_receive: POST [https://lists-dev.techservices.illinois.edu/sympasoap](https://lists-dev.techservices.illinois.edu/sympasoap) HTTP/1.1
Accept: text/xml
Accept: multipart/*
Accept: application/soap
Content-Length: 530
Content-Type: text/xml; charset=utf-8
SOAPAction: "urn:sympasoap#login"

<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope 
    soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"
    xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <soap:Body>
        <login xmlns="urn:sympasoap">
            <c-gensym3 xsi:type="xsd:string">aosar@illinois.edu</c-gensym3>
            <c-gensym5 xsi:type="xsd:string">REDACTED-PW</c-gensym5>
        </login>
    </soap:Body>
</soap:Envelope>
```

#### RESPONSE
```
SOAP::Transport::HTTP::Client::send_receive: HTTP/1.1 200 OK
Connection: close
Date: Tue, 08 Apr 2025 21:05:30 GMT
Server: Apache/2.4.62 (Red Hat Enterprise Linux) OpenSSL/3.2.2
Content-Type: text/xml; charset=utf-8
Client-Date: Tue, 08 Apr 2025 21:05:30 GMT
Client-Peer: 130.126.157.22:443
Client-Response-Num: 1
Client-SSL-Cert-Issuer: /C=US/O=Let's Encrypt/CN=R10
Client-SSL-Cert-Subject: /CN=lists-dev.techservices.illinois.edu
Client-SSL-Cipher: TLS_AES_256_GCM_SHA384
Client-SSL-Socket-Class: IO::Socket::SSL
Client-SSL-Version: TLSv1_3
Client-SSL-Warning: Peer certificate not verified
Client-Transfer-Encoding: chunked
Set-Cookie2: sympa_session=123456789; domain=lists-dev.techservices.illinois.edu; path=/; max-age=600
SOAPServer: SOAP::Lite/Perl/1.27

<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope 
    soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"
    xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <soap:Body>
        <loginResponse xmlns="urn:sympasoap">
            <result xsi:type="xsd:string">123456789</result>
        </loginResponse>
    </soap:Body>
</soap:Envelope>
```

Response body data structure:
| Element Name* | Description | Example Value |
|-----------|-------------|---------|
| `loginResponse.result` | Session ID as string | `123456789` |

\* (dot = nested element)

### RPC Call: `lists()`

**`$soap_client->lists();`**

<mark>Utilizes cookie **`sympa_session=123456789`**</mark>
#### REQUEST
```
SOAP::Transport::HTTP::Client::send_receive: POST [https://lists-dev.techservices.illinois.edu/sympasoap](https://lists-dev.techservices.illinois.edu/sympasoap) HTTP/1.1
Accept: text/xml
Accept: multipart/*
Accept: application/soap
Content-Length: 413
Content-Type: text/xml; charset=utf-8
SOAPAction: "urn:sympasoap#lists"

<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope 
    soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"
    xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <soap:Body>
        <lists xmlns="urn:sympasoap" xsi:nil="true" />
    </soap:Body>
</soap:Envelope>
```

Note: The **cookie** is appended by soap lite and is **not displayed** in the above output

#### RESPONSE
```
SOAP::Transport::HTTP::Client::send_receive: HTTP/1.1 200 OK
Connection: close

Date: Tue, 08 Apr 2025 21:05:30 GMT
Server: Apache/2.4.62 (Red Hat Enterprise Linux) OpenSSL/3.2.2
Content-Type: text/xml; charset=utf-8
Client-Date: Tue, 08 Apr 2025 21:05:30 GMT
Client-Peer: 130.126.157.22:443
Client-Response-Num: 1
Client-SSL-Cert-Issuer: /C=US/O=Let's Encrypt/CN=R10
Client-SSL-Cert-Subject: /CN=lists-dev.techservices.illinois.edu
Client-SSL-Cipher: TLS_AES_256_GCM_SHA384
Client-SSL-Socket-Class: IO::Socket::SSL
Client-SSL-Version: TLSv1_3
Client-SSL-Warning: Peer certificate not verified
Client-Transfer-Encoding: chunked
Set-Cookie2: sympa_session=123456789; domain=lists-dev.techservices.illinois.edu; path=/; max-age=600
SOAPServer: SOAP::Lite/Perl/1.27

<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope 
    soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"
    xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <soap:Body>
        <listsResponse xmlns="urn:sympasoap">
            <listInfo soapenc:arrayType="xsd:string[5]" xsi:type="soapenc:Array">
                <item xsi:type="xsd:string">
                    homepage=https://lists-dev.techservices.illinois.edu/lists/info/somelist;
                    listAddress=somelist@lists-dev.techservices.illinois.edu;
                    subject=somelist
                </item>
                <item xsi:type="xsd:string">
                    homepage=https://lists-dev.techservices.illinois.edu/lists/info/testing;
                    listAddress=testing@lists-dev.techservices.illinois.edu;
                    subject=Test
                </item>
                <item xsi:type="xsd:string">
                    homepage=https://lists-dev.techservices.illinois.edu/lists/info/testlist-one;
                    listAddress=testlist-one@lists-dev.techservices.illinois.edu;
                    subject=one
                </item>
                <item xsi:type="xsd:string">
                    homepage=https://lists-dev.techservices.illinois.edu/lists/info/testlist2;
                    listAddress=testlist2@lists-dev.techservices.illinois.edu;
                    subject=test2
                </item>
                <item xsi:type="xsd:string">
                    homepage=https://lists-dev.techservices.illinois.edu/lists/info/testlist3;
                    listAddress=testlist3@lists-dev.techservices.illinois.edu;
                    subject=test3
                </item>
            </listInfo>
        </listsResponse>
    </soap:Body>
</soap:Envelope>
```

**[TODO: FIX/CHANGE FORMATTING, update other data structure summaries to match]**

Response body data structure:
| Element Name* | Description | Example Value |
|-----------|-------------|---------|
| `listsResponse.listInfo` | List of email lists: <br />Array of objects (xml `item`) with keys: `homepage`, `listAddress`, `subject`) | `<item xsi:type="xsd:string">`<br />`    homepage=https://lists-dev.techservices.illinois.edu/lists/info/testlist3;`<br />`    listAddress=testlist3@lists-dev.techservices.illinois.edu;` <br />`    subject=test3` <br />`</item>`

<!-- `[ { "homepage":"https://lists-dev.techservices.illinois.edu/lists/info/somelist", "listAddress":"somelist@lists-dev.techservices.illinois.edu", "subject":"somelist" }]`** | -->

**Formatted List of Email Lists as JSON:**
```
[
    {
        "homepage":"https://lists-dev.techservices.illinois.edu/lists/info/somelist",
        "listAddress":"somelist@lists-dev.techservices.illinois.edu",
        "subject":"somelist"
    },
    {
        "homepage":"https://lists-dev.techservices.illinois.edu/lists/info/testlist3",
        "listAddress":"testlist3@lists-dev.techservices.illinois.edu",
        "subject":"test3"
    }
]
``` |

\* (dot = nested element)
