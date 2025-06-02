# Evaluation lab - Contiki-NG

## Group number: 23

## Group members

- Luca Lain 10726790
- Alessandro Mosconi 10681624
- Martino Piaggi 10686518

## Solution description

### Server

The server initializes by resetting the readings array to zero, activating the DAG root for routing, and establishing the UDP connection using predefined ports and a callback function. Upon receiving a UDP packet, the `udp_rx_callback()` function is invoked to process it.
The `isValid()` function determines if a client IP is already registered; if not, and the client limit hasn't been reached, it registers the new client IP in the clients array. `isValid()` returns true for valid senders and false otherwise. 
For valid senders, the received data (a **floating-point** reading) is incorporated into the readings array, and the updated average of these readings is computed and recorded.


### Client

The client initializes the readings array, sets up the UDP connection and enters a loop where it periodically reads the temperature.

- If the network node is reachable, the client sends the current temperature reading.
- If the network node is not reachable, the client batches the temperature readings locally.
- If the network node is reachable and it was previously disconnected, it computes and sends the average of the previously batched readings. Once the average is sent, the client resets the readings array and, after a timer, sends the current temperature to the server.