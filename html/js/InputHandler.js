async function sendInput() {
    const userName = document.getElementById("userName").value;
    const returnEmail = document.getElementById("returnEmail").value;
    const userMessage = document.getElementById("userMessage").value;
    const message = "NAME: " + userName + "\n\nRETURN EMAIL: " + returnEmail + "\n\nMESSAGE: " + userMessage;

    const publicKeyArmored = `-----BEGIN PGP PUBLIC KEY BLOCK-----
Version: OpenPGP.js v4.10.8
Comment: https://openpgpjs.org

xsBNBGAld6IBCACmgEXsKCkaeu8cV77Cjw6HxZ0uE89azB0rwfTWYCzEw2cg
Gzo1NEjrzxjM3Rt75bC9b88kSjv3/0bdfU7V3Pb4fdgHbyF037xPy22f1W21
SxflxHGG40LbCPVal88a9+TDx7neG5ny0qQQdDrEthJIE4KLtjBCjlfvJuSq
mhuWo6DZhYhPJe9zwA/E/twFju0PqLA24FdtYIEcnCBsyY8jlgL7ye/G4Weg
fYv2mqe6go2PRINL7SXHu3o3uNJX2LLMW2T+GlGSGPoa1ltKr8A0Iy7FHByk
lndULzxKVWK5N4DNP53lWzHl1B1i/WiExSOvyfs0hgLI61i/BMC8ZiBNABEB
AAHNK3Bhbm9zbW9pc2lhZGlzQHBtLm1lIDxwYW5vc21vaXNpYWRpc0BwbS5t
ZT7CwI0EEAEIACAFAmAld6IGCwkHCAMCBBUICgIEFgIBAAIZAQIbAwIeAQAh
CRAX5x0jmgx/2hYhBCQsI3xOgL1soz7uVBfnHSOaDH/ajRgIAIL35HvQJP3M
Et2DYLymDLP3DBrgcWlhGBOq4IVfqr4z6MeUjLAzrszjySfqzgvSsMiapYmp
Df3hyOLo6ufADgDuBAEAU3Pqcy1w9OyU9wioHifJ7YKxA5VUuZS47JKjL55q
GFMDpIhsIQqjYPIcG3weRmM1FNa7HYRwxKKEXQgkCXOlgP5jXFaCLzwwd6Nr
B31lkjxIIMaIgY0biaxavKhp6poEKmei2tQslwUC8eD0Ui60+kpZssYSE8yl
g+fshAqhrkn0soeXaQKaf2ZWeabOtwhFGdTTCCV5xbPaEPW8KKJAy6WUDAdB
5REwUlInWO2GH38fjB5LhoOaFTVqDAvOwE0EYCV3ogEIANMhqkva17uAx/zW
hx3B32govbP208j3sUvB1m4aREdkHB97Kj2xo8iqfAWt6RvfPpy4g9LkoMZA
PdleHX8N0WVffuRYT3h0ye/D5AECbYIxnfCkHlbstFzfHyXjaTRExIyG3WXg
kfbMcQAJmhFZpGFaMrCFMSmgBCfqnVZLV99IrTgiFyj55x/BCJHP2NYCs8C7
tNL6HT39CIeDCXe8OYO0A8sw+hsmQZJOKmhU2zKS0aFNt9MW2tawHZ4g/jtY
xx/npKB8M+AtcbANSMeSd19GrJGL8mLlTs3oOQNA8iRLq6rrV0wy1E3zmAEy
mzZIup77nKb8R2rnd0UWlf+nmnUAEQEAAcLAdgQYAQgACQUCYCV3ogIbDAAh
CRAX5x0jmgx/2hYhBCQsI3xOgL1soz7uVBfnHSOaDH/a9AYH/isaVkRmT1uq
CBPr4Yve1vPaToFJAotzJ+bvqcMxv4UD+ldm0wVB/ZNgNqhpf65v1WgSecVZ
D4jDUpA8kBDnKViospCp/1iFoVwlbQYvRcQZjuQ0Wn8L8JLIdbsxjAJJBJ7i
aB6Zl3dvRCDRa/z+KzMSZaV2ckxCu4yAOP/Uj4srDQN+tA+GCxxuyfNeIt11
gRnCA5r2mqIyNEDuvlcItV5LYv1K0MDp/dOQxCvYS2cR+quR+YK6iTbST9ad
JFXgqhvUzf/KWXZGmGXhV9Lb/f4LgSPC5pRc7JOmdWCf06otm/kvDAZOjNBu
3zv4UGGDox7W2QtrkjQU06BokVGrLas=
=KKL1
-----END PGP PUBLIC KEY BLOCK-----`;

    const publicKey = await openpgp.readKey({armoredKey: publicKeyArmored});

    const encrypted = await openpgp.encrypt({
        message: await openpgp.createMessage({text: message}),
        encryptionKeys: publicKey
    });

    const url = "http://panosmoisiadis.com/formsubmission/";
    const request = new XMLHttpRequest();
    request.open("POST", url);
    request.setRequestHeader("Accept", "text/plain");
    request.setRequestHeader("Content-Type", "text/plain");

    request.onreadystatechange = function () {
        if (request.readyState === 4) {
            if (request.status === 200) {
                alert("Message sent successfully.");
                document.getElementById("userName").value = "";
                document.getElementById("returnEmail").value = "";
                document.getElementById("userMessage").value = "";
            } else if (request.status === 429) {
                alert("Too many messages sent recently. Only one submission is permitted per minute. Please wait and try again later.");
            } else {
                alert("Error sending message.");
            }
            console.log(request.responseText);
        }
    };

    request.send(encrypted);
}