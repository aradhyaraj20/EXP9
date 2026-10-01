# Android Data Persistence Demo (EXP 9)

An Android application demonstrating **relational database management with SQLite** (`SQLiteOpenHelper`) and **key-value storage with SharedPreferences**.

---

## 📱 Features

1. **Relational Database Management (SQLite)**:
   - Utilizes `SQLiteOpenHelper` to create and maintain `UserDatabase.db` with a `users` table (`id`, `username`, `password`).
   - Persists all registered user accounts permanently across app restarts and multiple logins/logouts.

2. **Key-Value Data Persistence (`SharedPreferences`)**:
   - Stores saved credentials locally via `SharedPreferences`.
   - Supports "Remember Me" functionality to autofill input fields on app launch.

3. **AutoComplete Database Suggestions**:
   - Built with `MaterialAutoCompleteTextView` on the Username field.
   - Dynamically fetches distinct usernames from the SQLite database as you type.
   - Selecting a suggestion automatically populates the corresponding stored password.

4. **Two-Page Navigation Architecture**:
   - **Page 1 (Login & Registration Screen - `MainActivity`)**:
     - Material 3 input fields for Username and Password.
     - Checkbox for "Remember Me" / SharedPreferences autofill.
     - Login & Register buttons that validate and persist credentials into SQLite.
   - **Page 2 (Dashboard & Database Records Screen - `HomeActivity`)**:
     - Displays a welcome message for the active session.
     - Shows a live list of all user records stored in the SQLite database.
     - **Top-Right Corner Logout Button**: Clears the session and returns to Page 1.

5. **Session Logout & Credentials Clearing**:
   - Logging out from Page 2 resets the login input fields on Page 1 while preserving all stored records in SQLite.

---

## 🛠 Project Architecture & Key Files

```text
app/src/main/
├── java/com/example/exp9/
│   ├── DatabaseHelper.kt   # SQLiteOpenHelper implementation managing SQLite CRUD operations
│   ├── MainActivity.kt     # Page 1: Login/Registration, SharedPreferences, & AutoComplete
│   └── HomeActivity.kt     # Page 2: Dashboard, SQLite records display, & Top-corner Logout
└── res/layout/
    ├── activity_main.xml   # Material 3 UI layout for Page 1
    └── activity_home.xml   # Dashboard & SQLite view layout for Page 2 with Logout button
```

### Core Classes

- **`DatabaseHelper.kt`**:
  - `insertUser(username, password)`: Inserts or updates user records in SQLite.
  - `getAllUsers()`: Retrieves formatted strings of all records stored in SQLite.
  - `getAllUsernames()`: Queries distinct usernames for AutoComplete suggestions.
  - `getPasswordForUser(username)`: Fetches password for a given username.

- **`MainActivity.kt`**:
  - Configures `MaterialAutoCompleteTextView` with an `ArrayAdapter` backed by `getAllUsernames()`.
  - Handles login/register actions and navigates to `HomeActivity` via `Intent`.
  - Resets input fields when returning from a logout action.

- **`HomeActivity.kt`**:
  - Displays logged-in user welcome message and SQLite records list.
  - Contains the top-corner Logout button that finishes Page 2 and sends an explicit logout intent to `MainActivity`.

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Jellyfish / Koala or newer
- **JDK**: Java 11 or later
- **Android SDK**: `compileSdk = 37`, `minSdk = 24`

### Building & Running

1. **Clone or Open Project**:
   Open the root directory in Android Studio.

2. **Sync Gradle**:
   Click **Sync Project with Gradle Files** or run:
   ```bash
   ./gradlew sync
   ```

3. **Build Debug APK**:
   ```bash
   ./gradlew app:assembleDebug
   ```

4. **Run on Emulator or Device**:
   Select an Android virtual device (API 24+) and click **Run 'app'** (`Shift + F10`).

---

## 🧪 Testing the Workflow

1. **Register User**:
   - Enter a username (e.g. `john_doe`) and password (e.g. `pass123`) on Page 1.
   - Tap **Register User in SQLite**.
2. **View Dashboard (Page 2)**:
   - App redirects to Page 2 showing "Welcome, john_doe!" and displays the user in the SQLite database list.
3. **Logout**:
   - Tap **Logout** in the top-right corner.
   - App returns to Page 1 with login fields cleared.
4. **AutoComplete Suggestions**:
   - Type `j` into the Username box.
   - Select `john_doe` from the dropdown suggestions -> Password `pass123` is automatically filled into the Password box.
   


## images
![Screenshot 2026-10-01 104400.png](images/Screenshot%202026-10-01%20104400.png)
![Screenshot 2026-10-01 104435.png](images/Screenshot%202026-10-01%20104435.png)
![Screenshot 2026-10-01 104444.png](images/Screenshot%202026-10-01%20104444.png)
![Screenshot 2026-10-01 104500.png](images/Screenshot%202026-10-01%20104500.png)
![Screenshot 2026-10-01 104515.png](images/Screenshot%202026-10-01%20104515.png)
![Screenshot 2026-10-01 104531.png](images/Screenshot%202026-10-01%20104531.png)
![Screenshot 2026-10-01 104540.png](images/Screenshot%202026-10-01%20104540.png)