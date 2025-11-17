# Space Explorer

## Project Overview

Space Explorer is an Android application that fetches and displays astronomy-related data from NASA's open APIs. The app features a clean, modern UI with a list view of space images and detailed information screens, along with multiple interactive features.

## Features

- **NASA API Integration**: Fetches astronomy pictures and data from NASA's APOD (Astronomy Picture of the Day) API
- **Modern UI**: Implements Material Design components with a space-themed color scheme
- **Fragment-Based Navigation**: Uses the Navigation Component for smooth transitions between screens
- **Responsive Design**: Works well in both portrait and landscape orientations
- **Rich Detail Views**: Displays comprehensive information about each astronomy picture
- **Interactive Elements**:
  - Share space images with friends
  - Open NASA website for more information
  - View videos when available
- **Image Loading**: Uses Glide for efficient image loading and caching
- **Pull-to-Refresh**: Update content with a simple pull gesture
- **Error Handling**: Graceful handling of network errors and loading states

## Project Structure

The project follows the MVVM (Model-View-ViewModel) architecture pattern and is organized into the following packages:

- **api**: Contains API service interfaces and network-related classes
- **models**: Data classes that represent the application's domain model
- **ui**: User interface components
  - **activities**: Main activity that hosts the navigation components
  - **adapters**: RecyclerView adapters for displaying data
  - **fragments**: UI fragments for different screens
  - **viewmodels**: ViewModels that manage UI-related data
- **utils**: Utility classes and helper functions

## Technical Specifications

- **Language**: Kotlin
- **Minimum SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 34 (Android 14)
- **Architecture**: MVVM (Model-View-ViewModel)
- **API Communication**: Retrofit, OkHttp
- **Image Loading**: Glide
- **UI Components**: RecyclerView, CardView, ConstraintLayout
- **Navigation**: Navigation Component (JetPack)
- **Concurrency**: Kotlin Coroutines
- **Dependency Injection**: Koin

## API Usage

This app uses NASA's APOD (Astronomy Picture of the Day) API. To run the app, you'll need to:

1. Get an API key from [NASA's API portal](https://api.nasa.gov/)
2. Add your API key to the `app/build.gradle` file in the `defaultConfig` section:

```groovy
buildConfigField "String", "NASA_API_KEY", "\"YOUR_API_KEY_HERE\""
```

## Installation

1. Clone the repository:
```
git clone https://github.com/Aliipou/space-explorer.git
```

2. Open the project in Android Studio.

3. Add your NASA API key as described in the API Usage section.

4. Build and run the app on your device or emulator.

## Project Requirements Met

This project meets all the requirements specified in the assignment:

### Grade +1 Requirements
- ✅ Receives data from internet API (NASA's APOD API)
- ✅ Displays minimum 10 items (configurable up to 50)
- ✅ Parses data into class structure (AstronomyPicture data class)
- ✅ Shows data in RecyclerView with proper formatting
- ✅ Custom app styling (space-themed colors, proper icon)
- ✅ Custom layout with proper margins and padding

### Grade +2 Requirements
- ✅ Uses fragments for navigation
- ✅ Detailed view when list item is clicked
- ✅ New screen with navigation for detailed data

### Bonus Points/Challenge
- ✅ Added images to both list and detailed view
- ✅ Added links to NASA source for each image
- ✅ Support for both portrait and landscape orientations


## License

This project is for educational purposes and is not intended for commercial use.

## Acknowledgements

- [NASA Open APIs](https://api.nasa.gov/) for providing the astronomy data
- [Retrofit](https://github.com/square/retrofit) for API communication
- [Glide](https://github.com/bumptech/glide) for image loading
- [Koin](https://github.com/InsertKoinIO/koin) for dependency injection
