from flask import Flask, request, make_response
from qiskit import QuantumCircuit
from qiskit_aer import AerSimulator

app = Flask(__name__)

def generate_random_bits(num_bits):
    circuit = QuantumCircuit(num_bits, num_bits)
    for i in range(num_bits):
        circuit.h(i)
    circuit.measure(range(num_bits), range(num_bits))

    simulator = AerSimulator()
    job = simulator.run(circuit, shots=1)
    result = job.result()

    counts = result.get_counts(circuit)
    bit_string = list(counts.keys())[0][::-1]  # Reverse to natural order
    return bit_string

@app.route('/qrng', methods=['GET'])
def get_quantum_random_bits():
    try:
        num_bits = int(request.args.get('bits', 256))
        if not 1 <= num_bits <= 1024:
            return "Invalid bit count. Must be 1–1024 bits.", 400

        random_bits = generate_random_bits(num_bits)

        # Return raw bits as plain text
        response = make_response(random_bits)
        response.headers["Content-Type"] = "text/plain"
        response.headers["Access-Control-Allow-Origin"] = "*"   # Allow Java calls
        return response

    except Exception as e:
        return make_response(f"QRNG error: {str(e)}", 500)

if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0", port=5000)
