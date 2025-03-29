# Super Layout Plugin for JetBrains IDEs

A plugin for JetBrains IDEs that allows you to save and restore window layouts with advanced features.

## Features

- **Save and Restore Layouts**: Save your current window layout (positions, sizes, etc.) with a custom name and restore it later.
- **Keyboard Shortcuts**: Assign keyboard shortcuts to quickly switch between layouts.
- **Action Triggers**: Automatically activate layouts when specific IDE actions are performed (e.g., when debugging stops).
- **Centralized Management**: Manage all your layouts from a single settings page.

## Installation

1. Download the plugin from the JetBrains Marketplace or install it directly from the IDE:
   - Go to Settings/Preferences > Plugins
   - Click "Browse repositories..."
   - Search for "Super Layout"
   - Click "Install"

2. Restart the IDE to activate the plugin.

## Usage

### Saving a Layout

1. Arrange your windows, tool panels, and editor tabs as desired.
2. Go to the main menu and select "Super Layout" > "Save Current Layout".
3. Enter a name for your layout and click "OK".

### Applying a Layout

There are several ways to apply a saved layout:

1. **From the menu**: Go to "Super Layout" > "Manage Layouts", select a layout, and click "Apply".
2. **Using a keyboard shortcut**: If you've assigned a shortcut to a layout, press the shortcut keys.
3. **Automatically**: If you've configured a layout to be triggered by specific actions, it will be applied automatically when those actions occur.

### Managing Layouts

1. Go to "Super Layout" > "Manage Layouts" or open Settings/Preferences > Tools > Super Layout.
2. From here you can:
   - Apply a layout
   - Delete a layout
   - Configure a layout (assign shortcuts and trigger actions)

### Configuring a Layout

1. Select a layout in the Manage Layouts dialog or Settings page.
2. Click "Configure".
3. In the configuration dialog:
   - To set a keyboard shortcut, click "Record Shortcut" and press the desired key combination.
   - To add trigger actions, select actions from the available list and click the right arrow button.
   - To remove trigger actions, select actions from the selected list and click the left arrow button.
   - Click "OK" to save your changes.

## Examples

### Example 1: Coding Layout

Create a layout optimized for coding with:
- Project panel visible on the left
- Structure panel visible on the right
- Terminal panel minimized at the bottom

### Example 2: Debugging Layout

Create a layout optimized for debugging with:
- Debug panel visible at the bottom
- Variables and watches panels visible
- Console panel visible

### Example 3: Automatic Layout Switching

Configure your layouts to switch automatically:
- Set the "Coding Layout" to activate when you stop debugging
- Set the "Debugging Layout" to activate when you start debugging

## Building from Source

### Prerequisites

- **Java Development Kit (JDK) 21**
  - This project is configured to use Java 21
  - Using an older JDK version will cause build errors

### Installing Java

Before building the plugin, you need to install JDK 21:

#### On Ubuntu/Debian:
```
sudo apt update
sudo apt install openjdk-21-jdk
```

#### On Windows:
1. Download JDK 21 from [Oracle's website](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) or [AdoptOpenJDK](https://adoptium.net/)
2. Run the installer and follow the instructions
3. Set the JAVA_HOME environment variable to point to your JDK installation

#### On macOS:
```
brew install openjdk@21
```

### Building the Plugin

1. Clone the repository:
   ```
   git clone https://github.com/daniel-kuon/super-layout.git
   cd super-layout
   ```

2. Build the plugin using Gradle:
   ```
   ./gradlew buildPlugin
   ```

   On Windows:
   ```
   gradlew.bat buildPlugin
   ```

3. The built plugin will be available in the `build/distributions` directory.

### Troubleshooting

#### Error: "Unsupported class file major version"

This error occurs when you're using a different JDK version than what the project requires. To fix this:

1. Install JDK 21 (if not already installed)
2. Set JAVA_HOME to point to JDK 21:

   On Linux/macOS:
   ```
   export JAVA_HOME=/path/to/jdk21
   export PATH=$JAVA_HOME/bin:$PATH
   ```

   On Windows:
   ```
   set JAVA_HOME=C:\path\to\jdk21
   set PATH=%JAVA_HOME%\bin;%PATH%
   ```

3. Verify your Java version:
   ```
   java -version
   ```

4. Try building again:
   ```
   ./gradlew buildPlugin
   ```

#### Error: "ClassNotFoundException: org.gradle.wrapper.GradleWrapperMain"

This error occurs when the Gradle wrapper JAR file is missing. To fix this:

1. If you have Gradle installed globally, you can regenerate the wrapper:
   ```
   gradle wrapper --gradle-version 8.5
   ```

2. If you don't have Gradle installed, you can manually download the wrapper JAR:

   On Linux/macOS:
   ```
   mkdir -p gradle/wrapper
   curl -o gradle/wrapper/gradle-wrapper.jar https://github.com/gradle/gradle/raw/v8.5.0/gradle/wrapper/gradle-wrapper.jar
   ```

   On Windows (using PowerShell):
   ```
   New-Item -ItemType Directory -Force -Path gradle\wrapper
   Invoke-WebRequest -Uri https://github.com/gradle/gradle/raw/v8.5.0/gradle/wrapper/gradle-wrapper.jar -OutFile gradle\wrapper\gradle-wrapper.jar
   ```

3. Make sure the wrapper script is executable (Linux/macOS only):
   ```
   chmod +x gradlew
   ```

4. Try building again:
   ```
   ./gradlew buildPlugin
   ```

## Support

If you encounter any issues or have feature requests, please submit them to the [issue tracker](https://github.com/daniel-kuon/super-layout/issues).

## License

This plugin is available under the MIT License. See the LICENSE file for more information.
