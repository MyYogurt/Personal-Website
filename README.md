# Personal-Website
Personal implementation of a Java backend webserver plus frontend HTML+CSS. You can visit it at http://panosmoisiadis.com

The backend is written in Java and is an implementation of the [HttpServer class](https://docs.oracle.com/en/java/javase/16/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpServer.html) along with extra custom components.

You can view the JavaDoc of the backend at http://panosmoisiadis.com/docs

## Security

The backend does not currently support HTTPS. However, it is not necessary. All contact field submissions are encrypted client-side with PGP. Thus, any information is encrypted before being sent. The PGP implementation is provided my [openpgp.js](https://openpgpjs.org/). Submission can then only be decrypted by me. Users can verify their information is encrypted by looking at the Javascript sources in their web browser.

In addition, to prevent spamming, client's IPs are added to a HashSet after a submission. Those IPs will be in the HashSet for a set amount of time. If an IP is in the set, they cannot make a submission.