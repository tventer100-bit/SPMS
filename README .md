# Smart Pantry Management System

## App Name

**Smart Pantry Management System (SPMS)**

## Description

Smart Pantry Management System is an Android application for managing pantry ingredients and finding recipes based on the ingredients currently available.

The app allows users to:

- Add ingredients to a pantry.
- Store quantity, measurement unit, and an optional expiry date.
- Edit or delete pantry ingredients.
- View pantry items in an alphabetically ordered list.
- See recipes that can be prepared with the ingredients currently available.
- See "Almost There" recipes that are missing one or two ingredients.
- Open a recipe to view its ingredients and preparation instructions.
- Configure expiry-alert and default-unit preferences.

The application is written in Java and uses Android activities, `RecyclerView`, Material components, SQLite, and `SharedPreferences`.

## Database Choice

### SQLite

The application uses **SQLite** as its local database through Android's `SQLiteOpenHelper`.

The database is stored locally as:

```text
smart_pantry_management.db
```

### Why SQLite was chosen

SQLite is suitable for this application because the pantry and recipe data are structured and relational:

- Pantry ingredients are stored as records with a name, quantity, unit, and expiry date.
- Recipes are stored separately from their ingredients.
- Recipe ingredients are linked to recipes using a foreign key.
- The app needs CRUD operations for pantry items.
- Recipe suggestions require querying stored pantry and recipe data.
- The application is designed to work with local data, so a local database avoids requiring a separate database server.

The implementation also enables SQLite foreign-key constraints. The `recipe_ingredients` table references the `recipes` table with `ON DELETE CASCADE`, so deleting a recipe also removes its related ingredient records.

### Database structure

The database contains three tables:

#### `pantry`

Stores the user's pantry ingredients.

| Column | Type |
|---|---|
| `pantry_ID` | INTEGER PRIMARY KEY AUTOINCREMENT |
| `name` | TEXT |
| `quantity` | REAL |
| `unit` | TEXT |
| `expiry` | TEXT |

#### `recipes`

Stores recipe names and instructions.

| Column | Type |
|---|---|
| `recipes_ID` | INTEGER PRIMARY KEY AUTOINCREMENT |
| `name` | TEXT |
| `instructions` | TEXT |

#### `recipe_ingredients`

Stores the ingredients required by each recipe.

| Column | Type |
|---|---|
| `RI_ID` | INTEGER PRIMARY KEY AUTOINCREMENT |
| `recipe_ID` | INTEGER, foreign key to `recipes(recipes_ID)` |
| `name` | TEXT |
| `quantity` | REAL |
| `unit` | TEXT |

The database is created by `DatabaseHelper`. Initial recipes are seeded automatically when the database is first created.

## Setup

The supplied project source shows an Android Studio project written in Java, but it does not include the project's Gradle files or exact Android SDK/dependency versions. Therefore, use the versions specified by the complete project if they are available.

### Requirements

- Android Studio.
- An Android SDK installation compatible with the project's Gradle configuration.
- An Android emulator or a compatible physical Android device.
- The complete project source, including its Gradle/build files.

### Installation

1. Clone or download the repository.
2. Open the project in **Android Studio**.
3. Allow Android Studio to sync the Gradle project.
4. Install any Android SDK components requested by Android Studio.
5. Connect an Android device with USB debugging enabled, or create/start an Android emulator.
6. Select the application run configuration in Android Studio.
7. Select the target device.
8. Click **Run**.

On first launch, `DatabaseHelper` creates the local SQLite database and its tables, then inserts the application's initial recipes.

## Running the App

After the application is installed:

1. Open **Smart Pantry Management System**.
2. The **Pantry** screen is displayed.
3. Tap the add button to create a pantry item.
4. Enter an ingredient name and a positive quantity.
5. Select the measurement unit.
6. Optionally select an expiry date.
7. Save the ingredient.

### Pantry Management

Existing pantry items can be edited or deleted from the Pantry screen.

Pantry entries are validated before they are saved:

- Ingredient name cannot be empty.
- Quantity cannot be empty.
- Quantity must be a valid number.
- Quantity must be greater than zero.

### Recipe Suggestions

Open the **Suggestions** screen to view recipes.

The app provides:

- **Available Recipes**: recipes for which all required ingredients and quantities are available.
- **Almost There Recipes**: recipes missing exactly one or two ingredients.

Recipe ingredient names are compared case-insensitively, with simple singular/plural matching. Quantities are also compared using the application's quantity-normalisation logic.

Select a recipe to view its ingredients and preparation instructions.

### Settings

The **Settings** screen allows the user to:

- Enable or disable expiry alerts.
- Select a default measurement unit.

These preferences are stored using Android `SharedPreferences`.

## Project Structure

The main Java components are:

| Component | Purpose |
|---|---|
| `MainActivity` | Pantry screen and pantry navigation |
| `AddEditPantryActivity` | Add and edit pantry ingredients |
| `SuggestedRecipeActivity` | Recipe suggestions |
| `RecipeDetailActivity` | Recipe details |
| `SettingsActivity` | User preferences |
| `DatabaseHelper` | SQLite database creation and data operations |
| `PantryAdapter` | Displays pantry items in a `RecyclerView` |
| `RecipeAdapter` | Displays recipe suggestions |
| `pantryItem` | Pantry item model |
| `Recipe` | Recipe model |
| `RecipeIngredient` | Recipe ingredient model |

The Java package used by the application is:

```text
com.example.spms
```

## Notes

- The database is local to the Android application.
- The initial recipe data is seeded automatically by `DatabaseHelper`.
- The current database version is `1`.
- The supplied source does not provide a license, so no license is specified here.
- The supplied source does not provide exact Gradle, SDK, or dependency versions; those should be taken from the complete Android Studio project configuration.
