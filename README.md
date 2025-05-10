# Space Explorer


## 🚀 Project Overview

Space Explorer is an elegant Android application that brings the wonders of the cosmos to your fingertips. This app connects to NASA's APOD (Astronomy Picture of the Day) API to deliver breathtaking space imagery along with detailed scientific explanations, creating an immersive astronomical experience.

The app features a clean, space-themed interface designed with Material Design principles, smooth navigation between screens, and interactive features that allow users to explore and share fascinating celestial content.

## ✨ Features

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

## 🛠 Architecture & Technical Details

Space Explorer is built with modern Android development practices and follows the MVVM (Model-View-ViewModel) architecture pattern, ensuring a clean separation of concerns and maintainable codebase.

### Technical Specifications

- **Language**: 100% Kotlin
- **Minimum SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 34 (Android 14)
- **Architecture**: MVVM with Repository pattern
- **Libraries & Technologies**:
  - **UI Components**: Material Design, ConstraintLayout, RecyclerView, CardView
  - **Navigation**: Jetpack Navigation Component with safe args
  - **API Communication**: Retrofit2, OkHttp3, Gson
  - **Image Loading**: Glide with transitions and placeholders
  - **Asynchronous Tasks**: Kotlin Coroutines
  - **Dependency Injection**: Koin for lightweight DI
  - **Lifecycle Management**: Jetpack ViewModel and LiveData

### Project Structure

The project is organized into logical packages following the MVVM architecture:

```
com.example.spaceexplorer/
├── api/
│   ├── NasaApiService.kt      # API endpoint definitions
│   └── RetrofitClient.kt      # Network client configuration
├── models/
│   └── AstronomyPicture.kt    # Data model for NASA APOD response
├── ui/
│   ├── activities/
│   │   └── MainActivity.kt     # Main activity & navigation host
│   ├── adapters/
│   │   └── SpaceImageAdapter.kt # RecyclerView adapter
│   ├── fragments/
│   │   ├── SpaceImageListFragment.kt  # List view
│   │   └── SpaceImageDetailFragment.kt # Detail view
│   └── viewmodels/
│       └── SpaceViewModel.kt   # ViewModel for UI data
└── utils/
    ├── Constants.kt           # App-wide constants
    └── DateUtils.kt           # Date formatting utilities
```

## 💻 Implementation Highlights

### Data Fetching & Display
- **Efficient API Calls**: Uses Retrofit with coroutines for smooth, non-blocking network requests
- **Graceful Error Handling**: Provides informative feedback for network issues
- **Optimized Image Loading**: Implements disk caching and transition animations

### User Experience
- **Smooth Navigation**: Custom animations between list and detail screens
- **Responsive UI**: Adapts to different screen sizes and orientations
- **Loading States**: Clear visual feedback during content loading

### Clean Code Practices
- **Separation of Concerns**: Clear boundaries between data, logic, and presentation
- **Resource Management**: Proper localization of strings and dimensions
- **Memory Efficiency**: Proper lifecycle management and resource cleanup

## 📋 Requirements Met

This project successfully implements all the requirements specified for the assignment:

### Basic Requirements (Grade +1)
- ✅ Receives data from internet API (NASA's APOD API)
- ✅ Displays minimum 10 items (configurable up to 50)
- ✅ Parses data into class structure (AstronomyPicture data class)
- ✅ Shows data in RecyclerView with proper formatting
- ✅ Custom app styling (space-themed colors, proper icon)
- ✅ Custom layout with proper margins and padding

### Advanced Requirements (Grade +2)
- ✅ Uses fragments for navigation
- ✅ Detailed view when list item is clicked
- ✅ New screen with navigation for detailed data

### Bonus Points/Challenge
- ✅ Added images to both list and detailed view
- ✅ Added links to NASA source for each image
- ✅ Support for both portrait and landscape orientations

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox (2021.3.1) or newer
- Android SDK 34
- JDK 17

### Setup
1. Clone the repository:
```bash
git clone https://github.com/Aliipou/Space-Explorer.git
```

2. Open the project in Android Studio

3. Get a NASA API key from [NASA's API Portal](https://api.nasa.gov/)

4. Create a `keys.properties` file in the project root with:
```
NASA_API_KEY=your_api_key_here
```

5. Build and run the application

## 📄 API Usage

This app uses NASA's APOD (Astronomy Picture of the Day) API. To run the app, you'll need to:

1. Get an API key from [NASA's API portal](https://api.nasa.gov/)
2. Add your API key to the `keys.properties` file as described in the setup section

## 📄 License

This project is licensed for educational purposes only and is not intended for commercial use.

## 🙏 Acknowledgements

- [NASA Open APIs](https://api.nasa.gov/) for providing the astronomy data
- [Retrofit](https://github.com/square/retrofit) for API communication
- [Glide](https://github.com/bumptech/glide) for image loading
- [Koin](https://github.com/InsertKoinIO/koin) for dependency injection
- [Navigation Component](https://developer.android.com/guide/navigation) for app navigation
- [Android Jetpack](https://developer.android.com/jetpack) for modern Android development

---

<p align="center">
  Developed for Android Development Course
</p>
