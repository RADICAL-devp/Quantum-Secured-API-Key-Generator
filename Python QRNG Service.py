from flask import Flask, jsonify, request
import requests

app = Flask(__name__)

def generate_random_bits(num_bits):
    """
    Generates real quantum random bits from ANU Quantum RNG API.
    """
    url = f"https://qrng.anu.edu.au/API/jsonI.php?length={num_bits}&type=uint8"
    response = requests.get(url).json()

    if not response.get("success"):
        raise Exception("Failed to fetch quantum randomness from ANU")

    bits = ""
    for number in response["data"]:
        bits += format(number, "08b")  # Convert byte to 8 binary bits

    return bits[:num_bits]  # Ensure exact length


@app.route('/qrng', methods=['GET'])
def get_quantum_random_bits():
    try:
        num_bits = int(request.args.get('bits', 256))
        if not 1 <= num_bits <= 4096:
            return jsonify({"error": "Invalid bit count. Must be between 1 and 4096."}), 400

        random_bits = generate_random_bits(num_bits)

        # Return JSON, required by Java DTO
        return jsonify({"random_bits": random_bits})

    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0", port=5000)
