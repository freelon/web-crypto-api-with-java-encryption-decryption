# Connection the Web Crypto API with a Java Backend - Encryption in the Backend

Since I couldn't find a working example for the use case
- a key pair is generated in the browser
- a Java backend encrypts something with the public key
- the frontend decrypts the text

here is a simple code example how to make this work.
I easily got this to work in python (see `server.py`) but not in Java.
The important part I was missing was: you have to specify the usage of `SHA-256` explicitly in the `AlgorithmParams` - while "RSA/ECB/OAEPWithSHA-256AndMGF1Padding" seems to specify the use of SHA256, it has to be provided again as the algorithm for the padding. In the default Java uses Sha-1 here.

Anyways, this was just quickly thrown together, so take it with a grain of salt before using it for something serious.
