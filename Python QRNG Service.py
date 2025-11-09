from flask import Flask, jsonify, request
from qiskit import QuantumCircuit
# We use the Aer simulator for efficient classical simulation of a quantum circuit
from qiskit_aer import AerSimulator

app = Flask(__name__)

def generate_random_bits(num_bits):
    """
    Generates a string of random bits using a Qiskit quantum simulator.
    """
    # Create a quantum circuit with 'num_bits' qubits and 'num_bits' classical bits
    circuit = QuantumCircuit(num_bits, num_bits)

    # Apply a Hadamard gate (H) to each qubit
    # This puts each qubit into a superposition of |0> and |1>
    # When measured, it will have a 50/50 chance of being 0 or 1.
    for i in range(num_bits):
        circuit.h(i)

    # Measure all qubits and store the results in the classical bits
    circuit.measure(range(num_bits), range(num_bits))

    # Use the Qiskit Aer simulator
    simulator = AerSimulator()

    # Run the circuit on the simulator. We only need 1 "shot" (run)
    # to get one string of random numbers.
    job = simulator.run(circuit, shots=1)
    result = job.result()

    # Get the counts. For 1 shot, this will be a dict with one entry,
    # e.g., {'0110101...': 1}
    counts = result.get_counts(circuit)

    # Extract the bit string (the key of the dictionary)
    # The keys are returned in reverse order by Qiskit, so we reverse it
    # to match the qubit order (q0 -> first bit).
    bit_string = list(counts.keys())[0][::-1]
    
    return bit_string

@app.route('/qrng', methods=['GET'])
def get_quantum_random_bits():
    """
    API endpoint to get a string of quantum random bits.
    Accepts a 'bits' query parameter. Defaults to 256.
    """
    try:
        # Get the number of bits from query param, default to 256
        num_bits = int(request.args.get('bits', 256))
        if num_bits <= 0 or num_bits > 1024:
            return jsonify({"error": "Invalid bit count. Must be between 1 and 1024."}), 400

        print(f"Received request for {num_bits} random bits...")
        
        # Generate the bits
        random_bits = generate_random_bits(num_bits)
        
        print(f"Generated {len(random_bits)} bits.")

        # Return the bits as JSON
        return jsonify({"random_bits": random_bits})
    
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    # Run the Flask app on port 5000
    app.run(debug=True, port=5000)