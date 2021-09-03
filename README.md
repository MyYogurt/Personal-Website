# Personal-Website
Personal implementation of a Java backend webserver plus frontend HTML+CSS. You can visit it at http://pansomoisiaids.com

The backend is written in Java and is an implementation of the HttpServer class along with extra custom components.

## Security

The backend does not currently support HTTPS. However, it is not necessary. All contact field submissions are encrypted client-side with PGP. Thus, any information is encrypted before being sent. The PGP implementation is provided my [openpgp.js](https://openpgpjs.org/). Submission can then only be decrypted by me. Users can verify their information is encrypted by looking at the Javascript sources in their web browser.

In addition, to prevent spamming, client's IPs are added to a HashSet after a submission. Those IPs will be in the HashSet for a set amount of time. If an IP is in the set, they cannot make a submission.