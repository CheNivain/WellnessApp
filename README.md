# Wellness App 🌱

A comprehensive Android wellness application built with Kotlin that helps users track their daily habits, journal their moods, stay hydrated, and monitor their overall wellbeing.

## Features

### 🎯 **Habit Tracking**
- Add, edit, and delete custom habits
- Track daily completion with an intuitive checkbox interface
- Visual progress indicator showing daily completion percentage
- Persistent data storage using SharedPreferences

### 😊 **Mood Journaling**
- Log moods using emoji-based selection (10 different mood levels)
- Add optional notes to mood entries
- View mood history in chronological order
- Visualize mood trends with interactive charts using MPAndroidChart
- Share mood summaries via implicit intents

### 💧 **Hydration Tracking**
- Track daily water intake with a simple tap interface
- Visual progress bar showing daily hydration goal (8 glasses)
- Customizable reminder notifications using WorkManager
- Adjustable reminder intervals (15, 30, 60, 90, 120 minutes)

### ⚙️ **Settings & Customization**
- User profile management
- Notification preferences
- Hydration reminder settings
- Secure logout functionality

### 🏠 **Home Screen Widget**
- Live widget showing today's habit completion percentage
- One-tap access to open the main app
- Real-time updates when habits are completed

### 📱 **Sensor Integration**
- **Shake Detection**: Shake your phone to quickly log your mood
- **Step Counter**: Track daily steps using device accelerometer
- Real-time sensor data processing

### 🔔 **Smart Notifications**
- WorkManager-powered hydration reminders
- Customizable notification intervals
- Persistent notifications that survive app restarts

## Technical Architecture

### **Data Persistence**
- **SharedPreferences**: All user data stored locally
- **Gson**: JSON serialization for complex data structures
- **PreferencesManager**: Centralized data management

### **UI/UX Design**
- **Material Design 3**: Modern, clean interface
- **Bottom Navigation**: Intuitive app navigation
- **Fragments**: Modular UI components
- **ViewPager2**: Smooth onboarding experience
- **RecyclerView**: Efficient list displays

### **Advanced Features**
- **MPAndroidChart**: Interactive mood trend visualizations
- **WorkManager**: Reliable background task scheduling
- **Sensor APIs**: Accelerometer integration for steps and shake detection
- **App Widgets**: Home screen integration
- **Implicit Intents**: Share functionality

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API level 25+ (Android 7.1)
- Kotlin 1.8.10+
- Gradle 7.4.2+

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/CheNivain/WellnessApp.git
   cd WellnessApp
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Choose "Open an existing Android Studio project"
   - Navigate to the cloned repository folder
   - Select the project and click "OK"

3. **Sync Dependencies**
   - Android Studio will automatically sync Gradle files
   - If needed, click "Sync Now" in the notification bar

4. **Build the Project**
   ```bash
   ./gradlew build
   ```

5. **Run the App**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio
   - Or use the command line:
   ```bash
   ./gradlew installDebug
   ```

## Project Structure

```
app/
├── src/main/java/com/example/wellnessapp/
│   ├── data/
│   │   ├── models/          # Data models (Habit, MoodEntry, User)
│   │   └── preferences/     # SharedPreferences management
│   ├── ui/
│   │   ├── auth/           # Login/authentication screens
│   │   ├── onboarding/     # App introduction screens
│   │   ├── habits/         # Habit tracking functionality
│   │   ├── mood/           # Mood journaling features
│   │   ├── hydration/      # Water intake tracking
│   │   └── settings/       # App settings and preferences
│   ├── sensors/            # Accelerometer and shake detection
│   ├── notification/       # WorkManager notification system
│   ├── widget/             # Home screen widget implementation
│   └── MainActivity.kt     # Main app coordinator
├── src/main/res/
│   ├── layout/             # XML layout files
│   ├── drawable/           # Icons and graphics
│   ├── values/             # Colors, strings, themes
│   ├── menu/               # Navigation menus
│   └── xml/                # Widget configurations
└── build.gradle           # Dependencies and build configuration
```

## Key Dependencies

```gradle
// Core Android libraries
implementation 'androidx.core:core-ktx:1.10.1'
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.9.0'

// UI Components
implementation 'androidx.fragment:fragment-ktx:1.6.1'
implementation 'androidx.viewpager2:viewpager2:1.0.0'
implementation 'androidx.recyclerview:recyclerview:1.3.1'

// Background Tasks
implementation 'androidx.work:work-runtime-ktx:2.8.1'

// Data Visualization
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'

// Data Serialization
implementation 'com.google.code.gson:gson:2.10.1'
```

## User Guide

### First Launch
1. **Create Account**: Enter a username and password (stored locally)
2. **Onboarding**: Swipe through 4 introduction screens
3. **Main Dashboard**: Start tracking habits, mood, and hydration

### Adding Habits
1. Navigate to "Habits" tab
2. Tap the "Add Habit" button
3. Enter habit name and optional description
4. Tap "Save" to create the habit

### Logging Mood
1. Go to "Mood" tab
2. Tap the floating action button (+)
3. Select an emoji that represents your mood
4. Optionally add a note
5. Tap "Save" to log the mood

### Quick Mood Logging
- **Shake your phone** anywhere in the app to instantly open the mood logging dialog

### Hydration Tracking
1. Open "Hydration" tab
2. Tap "Drink Water" each time you drink a glass
3. Monitor your progress toward the daily goal (8 glasses)
4. Use "Reset Today" to start over if needed

### Setting Up Notifications
1. Go to "Settings" tab
2. Toggle "Enable Hydration Reminders"
3. Tap "Change Interval" to adjust reminder frequency
4. Notifications will appear based on your settings

### Adding Home Widget
1. Long-press on your home screen
2. Select "Widgets"
3. Find "Wellness App - Daily Progress"
4. Drag to your home screen
5. The widget shows real-time habit completion

## Permissions

The app requests the following permissions:
- **VIBRATE**: For haptic feedback
- **SCHEDULE_EXACT_ALARM**: For precise hydration reminders
- **RECEIVE_BOOT_COMPLETED**: To restore reminders after device restart
- **WAKE_LOCK**: For background notification processing

## Data Privacy

- All data is stored locally on your device using SharedPreferences
- No data is transmitted to external servers
- User data includes: habits, mood entries, preferences, and usage statistics
- Data persists until the app is uninstalled

## Troubleshooting

### Common Issues

**Build Errors**
- Ensure Android SDK is properly installed
- Sync project with Gradle files
- Clear cache: Build → Clean Project → Rebuild Project

**Widget Not Updating**
- Remove and re-add the widget
- Check if the app has background execution permissions
- Restart your device if needed

**Notifications Not Working**
- Check notification permissions in device settings
- Ensure the app is not in battery optimization mode
- Verify reminder settings in the app

**Shake Detection Not Working**
- Ensure device has accelerometer sensor
- Try shaking more vigorously
- Check if sensor permissions are granted

## Contributing

This app was developed as a complete wellness solution. For improvements or bug reports:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## Future Enhancements

- **Cloud Sync**: Backup data to cloud storage
- **Advanced Analytics**: Weekly/monthly reports
- **Social Features**: Share progress with friends
- **Meditation Timer**: Guided meditation sessions
- **Sleep Tracking**: Monitor sleep patterns
- **Goal Setting**: Custom habit goals and streaks

## License

This project is open source and available under the [MIT License](LICENSE).

## Credits

- **MPAndroidChart**: Chart visualization library
- **Material Design**: UI/UX design system
- **WorkManager**: Background task management
- **Gson**: JSON parsing library

---

**Built with ❤️ for wellness and healthy living**

Version 1.0 | Last Updated: December 2024