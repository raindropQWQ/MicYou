# Desktop Network Server Layer

## OVERVIEW
Desktop network layer: TCP server (Ktor sockets), Bluetooth bridges (BlueCove/BlueZ), protocol handling.

## WHERE TO LOOK
| File | Role |
|------|------|
| NetworkServer.kt | TCP/Bluetooth server lifecycle, accept loop |
| ConnectionHandler.kt | Protocol framing, message dispatch |
| LinuxBlueZServer.kt | Linux Bluetooth via BlueZ D-Bus |

## CONVENTIONS
- TCP: Ktor sockets with coroutine-based I/O
- Bluetooth: BlueCove (Windows/macOS), BlueZ (Linux)
- Clear separation: NetworkServer = transport, ConnectionHandler = protocol
- Logging via project Logger abstraction
- Resource safety: close sockets/streams in finally blocks

## ANTI-PATTERNS
- Do not block I/O on non-network threads
- Never hard-code addresses/ports - use settings
- Do not leak sockets or streams
- Do not ignore Bluetooth capability limitations per OS
- Never swallow errors in accept loops