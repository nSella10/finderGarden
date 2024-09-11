# Overview
A comprehensive Android application for finding, adding, and managing gardens. The app displays a map with markers representing gardens, allows users to search for specific gardens, filter gardens based on amenities and distance, and maintain a list of favorite gardens. Additionally, users can add new gardens and manage their profile with Firebase authentication.






## Features
-   **Map View**: Displays gardens on a Google Map with custom markers.
-   **Search Functionality**: Search for gardens by name and navigate to the selected garden on the map.
-   **Add Gardens**: Users can add new gardens with a name, description, facilities, and coordinates.
-   **Filter Gardens**: Filter gardens based on various amenities (benches, kiosks, etc.) and proximity.
-   **Favorite Gardens**: Mark gardens as favorites and view them in a separate list.
-   **Real-time Location Updates**: Fetch the user's location in real-time and update the map accordingly.
-   **Firebase Authentication**: Sign in and sign out functionality.
-   **Firebase Realtime Database**: Store garden information and user data (such as favorites) in a Firebase database.

## Usage

1.  **Map View**: When you first open the app, the map will display the gardens stored in the Firebase Realtime Database. Custom markers are used to represent each garden.
2.  **Find Your Location**: Click the **Find My Location** button to zoom in on your current location.
3.  **Search for a Garden**: Use the search bar to find a garden by name. The map will zoom in on the selected garden.
4.  **Add a Garden**: Click on the **Add Garden** button to add a new garden to the database.
5.  **Favorites**: Mark gardens as favorites by clicking the favorite button in the list or map view. View all favorite gardens by navigating to the **Saves** screen.
6.  **Filters**: Apply filters to view gardens that meet specific criteria (e.g., distance, amenities, etc.).


## Contributing

Contributions are welcome! If you'd like to contribute, please fork the repository and use a feature branch. Pull requests are warmly welcome.
