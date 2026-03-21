<div align="center">

[![Android](https://img.shields.io/badge/Android-API%2024+-3DDC84?style=flat&amp;logo=android)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-green?style=flat)](LICENSE)

# Space Explorer

**Android app for exploring NASA open data — APOD, Mars rover imagery, and astronomy photos.**

</div>

## Overview

Space Explorer is an Android application that makes NASA's public data accessible in a clean, navigable mobile UI. Browse the Astronomy Picture of the Day archive, explore Mars rover photos by sol and camera, and discover space imagery from across NASA's missions.

## Features

**Astronomy Picture of the Day**
Browse the full APOD archive. Each entry includes the high-resolution image, title, date, and NASA's explanation. Tap the image for full-screen view.

**Mars Rover Photos**
Photos from Curiosity, Opportunity, and Spirit. Filter by sol (Martian day), Earth date, or camera. Each rover has different cameras — the app shows which cameras are available.

**Material Design UI**
Space-themed color palette with Material Design components. Fragment-based navigation with smooth transitions.

**Offline Cache**
Images are cached locally. Previously viewed photos work without a connection.

## Quick Start

1. Clone the repository
2. Get a free NASA API key at [api.nasa.gov](https://api.nasa.gov/)
3. Add `NASA_API_KEY=your-key` to `local.properties`
4. Build with Android Studio or `./gradlew assembleDebug`

## License

MIT
