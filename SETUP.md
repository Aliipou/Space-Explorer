# Space Explorer - Setup Guide

## Prerequisites

1. **JDK 17** - Required for building the project
   - Download from: https://adoptium.net/temurin/releases/
   - Set `JAVA_HOME` environment variable to JDK installation path
   - Add `%JAVA_HOME%\bin` to PATH

2. **Android SDK** - Already configured at:
   ```
   C:\Users\admin\AppData\Local\Android\Sdk
   ```

3. **Android Studio** (Optional but recommended)
   - For full IDE support and debugging

## Quick Start

### Option 1: Using Android Studio
1. Open Android Studio
2. File → Open → Select `D:\spaceexplorer_project`
3. Let Gradle sync complete
4. Run/Debug the app

### Option 2: Command Line Build
```bash
# Set JAVA_HOME (adjust path to your JDK installation)
set JAVA_HOME=C:\path\to\jdk-17

# Clean and build
.\gradlew.bat clean assembleDebug

# Run unit tests
.\gradlew.bat test

# Run lint checks
.\gradlew.bat lint
```

## Configuration

### NASA API Key
1. Copy `keys.properties.template` to `keys.properties`
2. Get your free API key from: https://api.nasa.gov/
3. Replace `YOUR_NASA_API_KEY_HERE` with your actual key

Note: The app will use `DEMO_KEY` by default (rate limited)

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/spaceexplorer/
│   │   │   ├── api/              # Retrofit API services
│   │   │   ├── models/           # Data models
│   │   │   ├── ui/               # Activities, Fragments, ViewModels
│   │   │   ├── utils/            # Utility classes
│   │   │   └── SpaceExplorerApp.kt  # Application class
│   │   └── res/                  # Resources (layouts, strings, etc.)
│   └── test/                     # Unit tests
└── build.gradle                  # App-level build config
```

## Testing

### Run Unit Tests
```bash
.\gradlew.bat test
```

### Run Lint Checks
```bash
.\gradlew.bat lint
```

Report will be at: `app/build/reports/lint-results-debug.html`

## Building

### Debug Build
```bash
.\gradlew.bat assembleDebug
```
APK location: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build
```bash
.\gradlew.bat assembleRelease
```
Note: Requires signing configuration

## Troubleshooting

### JAVA_HOME not set
Ensure JDK 17 is installed and JAVA_HOME is properly configured:
```bash
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot
```

### Gradle sync issues
Try invalidating caches:
1. In Android Studio: File → Invalidate Caches / Restart
2. Or delete `.gradle` folder and rebuild

### API rate limits
If using DEMO_KEY, you're limited to:
- 30 requests per IP per hour
- 50 requests per IP per day

Get your own key at: https://api.nasa.gov/

## Architecture

- **MVVM** - Model-View-ViewModel pattern
- **Koin** - Dependency injection
- **Navigation Component** - Fragment navigation with Safe Args
- **Coroutines** - Asynchronous programming
- **LiveData** - Lifecycle-aware observable data
- **Retrofit** - REST API client
- **Glide** - Image loading and caching

## Key Features

- NASA Astronomy Picture of the Day (APOD)
- Random and recent picture browsing
- HD image viewing
- Video content support
- Share functionality
- Swipe to refresh
- Material Design 3 UI
