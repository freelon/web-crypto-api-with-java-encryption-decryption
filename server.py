from flask import Flask, request, jsonify, send_from_directory
from cryptography.hazmat.primitives.asymmetric import rsa, padding
from cryptography.hazmat.primitives import serialization, hashes
import base64

app = Flask(__name__)

SECRET_DATA = b"Super Secret Message"


@app.route('/')
def serve_page():
    return send_from_directory('', 'index.html')


@app.route('/encrypt', methods=['POST'])
def encrypt():
    try:
        public_key_pem = request.json.get('publicKey').encode()
        public_key = serialization.load_pem_public_key(public_key_pem)

        encrypted = public_key.encrypt(
            SECRET_DATA,
            padding.OAEP(
                mgf=padding.MGF1(algorithm=hashes.SHA256()),
                algorithm=hashes.SHA256(),
                label=None
            )
        )

        return jsonify({'encryptedData': base64.b64encode(encrypted).decode()})
    except Exception as e:
        return jsonify({'error': str(e)}), 400


if __name__ == '__main__':
    app.run(debug=True)
