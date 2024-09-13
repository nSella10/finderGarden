# Garden Finder
Final Course Project for Android Application Development course, in Afeka - the academic college of Engineering in Tel Aviv.

# Overview
A comprehensive Android application for finding, adding, and managing gardens. The app displays a map with markers representing gardens, allows users to search for specific gardens, filter gardens based on amenities and distance, and maintain a list of favorite gardens. Additionally, users can add, delete gardens, and manage their profile with Firebase authentication.


# how it looks like:
### Login Screen
<img src="https://github.com/user-attachments/assets/d60f0917-f8ba-4422-8302-32c6f1df42be" alt="loginActivity" height="600" width="400" />.
### Main Screen
<img src="https://github.com/user-attachments/assets/631669e7-49db-4e73-a8b5-c3f2fa7b4410" alt="mainActivity" height="600" width="400" />.
### Add Garden Screen
<img src="https://github.com/user-attachments/assets/1d732687-14aa-4a49-866d-9b79dfa9ad37" alt="addGardenActivity" height="600" width="400" />.
### List Garden Screen
<img src="https://github.com/user-attachments/assets/781f528d-1988-4c9b-9f67-c20f50165b0a" alt="listActivity" height="600" width="400" />.
### Filter Garden Screen
<img src="https://github.com/user-attachments/assets/e55558e4-dc52-4c3e-8f95-09aae23917f5" alt="filterActivity" height="600" width="400" />.
### Garden Detail Screen
<img src="https://github.com/user-attachments/assets/8ce568be-d54b-4ebd-81c1-62b304e60cc7" alt="detailGardenActivity" height = 600  width="400" />.
### Favorite Garden Screen
<img src="https://github.com/user-attachments/assets/fed9e443-91b8-4fcb-bee3-be305f374d09" alt="favoriteActivity" height="600" width="400" />.






## Features
-   **Map View**: Displays gardens on a Google Map with custom markers.
-   **Search Functionality**: Search for gardens by name and navigate to the selected garden on the map.
-   **Add Gardens**: Users can add new gardens with a name, description, facilities, and coordinates.
-   **Delete Gardens**: Users can delete any garden from the database after selecting it from a list and confirming their choice.
-   **Filter Gardens**: Filter gardens based on various amenities (benches, kiosks, etc.) and proximity.
-   **Favorite Gardens**: Mark gardens as favorites and view them in a separate list.
-   **Real-time Location Updates**: Fetch the user's location in real-time and update the map accordingly.
-   **Firebase Authentication**: Sign in and sign out functionality.
-   **Firebase Realtime Database**: Store garden information and user data (such as favorites) in a Firebase database.

## Usage

-   **Map View**: When you first open the app, the map will display the gardens stored in the Firebase Realtime Database. Custom markers are used to represent each garden.
-   **Find Your Location**: Click the Find My Location button to zoom in on your current location.
-   **Search for a Garden**: Use the search bar to find a garden by name. The map will zoom in on the selected garden.
-   **Add a Garden**: Click on the Add Garden button to add a new garden to the database.
-   **Delete a Garden**: From the list view, click the _Delete Garden_ button, select the garden you want to remove, and confirm deletion. The garden will be deleted from Firebase and removed from the map.
-   **Favorites**: Mark gardens as favorites by clicking the favorite button in the list or map view. View all favorite gardens by navigating to the Saves screen.
-   **Filters**: Apply filters to view gardens that meet specific criteria (e.g., distance, amenities, etc.)


## Contributing

Contributions are welcome! If you'd like to contribute, please fork the repository and use a feature branch. Pull requests are warmly welcome.
