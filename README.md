# Slide Desktop

Slide Desktop is the computer-side companion app for Slide, an old Android-to-desktop input system. It discovers an Android device running the Slide app, connects over Wi-Fi or USB, receives gesture packets, and turns them into real desktop mouse and keyboard actions.

Together with `slide-android`, this lets a phone or Samsung S Pen device behave like a lightweight pen tablet, touchpad, gesture pad, and keyboard helper for a desktop computer.

## What it does

- Discovers Android devices running Slide on the local network.
- Detects USB-connected Android devices through ADB.
- Lets the user connect to a discovered device from a small Swing UI.
- Receives serialized input command packets from the Android app.
- Converts those packets into desktop input with `java.awt.Robot`:
  - cursor movement
  - taps/clicks
  - click-and-drag
  - right click
  - scrolling
  - zooming with Ctrl + mouse wheel
  - keyboard key presses
  - cut/copy/paste shortcuts

## How it works

Slide is made of three sibling projects:

- `slide-android`: captures touch, S Pen, gestures, and keyboard events on Android.
- `slide-desktop`: this desktop receiver, written in Scala/Java with Swing.
- `SlideLauncher`: a small Windows launcher/updater that downloads and starts the desktop JAR.

The desktop app does not poll the phone for pointer state. Instead, the Android app sends command messages as the user interacts with the screen. The desktop app reads each message and immediately performs the corresponding OS-level input event.

## Connection modes

### Wi-Fi / LAN

1. The Android app broadcasts its IP, manufacturer, and model over UDP port `5000`.
2. `NetworkDeviceManager` listens for broadcasts in the background.
3. When a device is found, it appears in the Swing UI with a Wi-Fi icon.
4. When the user connects, `NetworkDeviceConnection` opens a TCP socket to the phone on port `8074`.
5. Incoming command packets are read with an `ObjectInputStream`.

### USB / ADB

1. `UsbDeviceManager` watches for ADB-visible devices.
2. The app initializes/starts ADB through `connections.usb.Adb`.
3. ADB forwarding maps a local desktop connection to the Android app's USB server port.
4. `UsbDeviceConnection` connects to `localhost` on the forwarded port and reads the same command stream.
5. USB mode uses Android-side TCP port `8072`.

## Input protocol

Messages are Java-serialized `Array[Short]` / `short[]` packets. The first value is usually a command id based on `10000`; additional values are parameters such as sensitivity, version, coordinates, or key codes.

`BaseDeviceConnection` handles the mapping from ids to desktop actions. Examples include:

- finger down / tap / double tap
- mouse down / mouse up
- long hold / right click
- relative movement
- absolute positioning configuration
- scroll and zoom directions
- keyboard key codes
- cut, copy, paste
- close connection

The minimum accepted Android protocol version is defined in `slide.Const` as `MIN_VERSION = 47`.

## Main code locations

- `src/main/scala/slide/Main.scala`
  - Entry point. Shows the Swing frame.

- `src/main/scala/gui/Frame.scala`
  - Main desktop UI.
  - Starts Wi-Fi and USB background scanners.
  - Displays discovered devices and starts the selected connection.

- `src/main/scala/connections/BaseDeviceConnection.scala`
  - Core command loop.
  - Reads packets and maps them to mouse/keyboard actions using `java.awt.Robot`.

- `src/main/scala/connections/network/`
  - UDP discovery and TCP connection logic for LAN mode.

- `src/main/scala/connections/usb/`
  - ADB initialization, device detection, port forwarding, and USB TCP connection logic.

- `src/main/java/enums/DeviceMessageType.java`
  - Protocol command ids used by the desktop receiver.

- `src/main/scala/slide/Settings.scala`
  - Runtime settings such as positioning mode, sensitivity, screen scaling, and scroll axis.

## Building

This is an old SBT/Scala project:

- Scala: `2.11.7`
- SBT project file: `build.sbt`
- Main class: `slide.Main`
- Key dependencies:
  - Android `ddmlib` for ADB/device support
  - Guava
  - MigLayout

Historical build/run commands:

```bash
sbt compile
sbt run
```

To package a runnable JAR, use the SBT packaging approach available in your local setup. The companion `SlideLauncher` expects a file named `Slide.jar`.

On modern systems, expect to update SBT/JDK settings or run this with an older Java/Scala toolchain.

## Running

1. Start the Android app from `slide-android` on a phone/tablet.
2. Launch this desktop app.
3. For Wi-Fi:
   - Put both devices on the same network.
   - Make sure firewall/VPN software does not block UDP `5000` or TCP `8074`.
4. For USB:
   - Enable Android Developer Options and USB debugging.
   - Connect the device with USB.
   - Make sure ADB can see the device.
5. Click the discovered device in the desktop UI.
6. Use the Android canvas as the pointer/control surface.

## Platform notes

- The input emulation uses `java.awt.Robot`, so behavior depends on desktop OS permissions and focus rules.
- ADB is required for USB mode.
- Network mode depends on local UDP broadcast, which some networks block.
- This code predates current desktop security models; macOS, Wayland Linux sessions, and locked-down environments may require extra accessibility/input permissions or may not work without changes.

## License

See `LICENSE`.
