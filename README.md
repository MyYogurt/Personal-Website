# Personal-Website
Implementation of Apache web server serving static content with reverse proxy for Spring Boot microservice. You can visit it at https://panosmoisiadis.com

You can view the JavaDoc of the backend at https://panosmoisiadis.com/docs

## Security

Data is encrypted via TLS with certificate provided by Let's Encrypt.

All contact field messages are end-to-end encrypted with PGP via [OpenPGP.js](https://openpgpjs.org)

In addition, to prevent spamming, client's IPs are added to a HashSet after a submission. Those IPs will be in the HashSet for a set amount of time. If an IP is in the set, they cannot make a submission.