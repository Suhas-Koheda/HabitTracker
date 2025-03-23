# Habit Tracker

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)
- [Ktor GitHub page](https://github.com/ktorio/ktor)
- The [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). You'll need to [request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up) to join.

## Features

Here's a list of features included in this project:

| Name                                                                   | Description                                                                        |
| ------------------------------------------------------------------------|------------------------------------------------------------------------------------ |
| [Koin](https://start.ktor.io/p/koin)                                   | Provides dependency injection                                                      |
| [Routing](https://start.ktor.io/p/routing)                             | Provides a structured routing DSL                                                  |
| [kotlinx.serialization](https://start.ktor.io/p/kotlinx-serialization) | Handles JSON serialization using kotlinx.serialization library                     |
| [Content Negotiation](https://start.ktor.io/p/content-negotiation)     | Provides automatic content conversion according to Content-Type and Accept headers |
| [Postgres](https://start.ktor.io/p/postgres)                           | Adds Postgres database to your application                                         |
| [Exposed](https://start.ktor.io/p/exposed)                             | Adds Exposed database to your application                                          |
| [CORS](https://start.ktor.io/p/cors)                                   | Enables Cross-Origin Resource Sharing (CORS)                                       |
| [Authentication](https://start.ktor.io/p/auth)                         | Provides extension point for handling the Authorization header                     |
| [Authentication JWT](https://start.ktor.io/p/auth-jwt)                 | Handles JSON Web Token (JWT) bearer authentication scheme                          |

## Building & Running

To build or run the project, use one of the following tasks:

| Task                          | Description                                                          |
| -------------------------------|---------------------------------------------------------------------- |
| `./gradlew test`              | Run the tests                                                        |
| `./gradlew build`             | Build everything                                                     |
| `buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `buildImage`                  | Build the docker image to use with the fat JAR                       |
| `publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `run`                         | Run the server                                                       |
| `runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```
---

## **Authentication Routes**

### **1. Register a New User**
- **Route**: `POST /auth/register`
- **Purpose**: Registers a new user in the system.
- **Request Body**:
    - `name` (String): The full name of the user.
    - `email` (String): The email address of the user (must be unique).
    - `password` (String): The password for the user (stored in plain text in this example, which is not recommended for production).
- **Response**:
    - `201 Created`: User registered successfully.
    - `400 Bad Request`: If the request is invalid or an error occurs (e.g., duplicate email).
- **How it works**:
    - The request body is deserialized into a `UserRegisterRequest` object.
    - The `UserRespository` creates a new user in the database with the provided details.
    - If successful, a `201 Created` response is returned.

---

### **2. Login a User**
- **Route**: `POST /auth/login`
- **Purpose**: Authenticates a user and generates a JWT (JSON Web Token) for session management.
- **Request Body**:
    - `email` (String): The email address of the user.
    - `password` (String): The password of the user.
- **Response**:
    - `200 OK`: Returns a JWT and the user's ID.
        - Response Body: `{ "jwt": "<JWT>", "userId": "<userId>" }`
    - `401 Unauthorized`: If the email or password is incorrect.
    - `400 Bad Request`: If the request is invalid or an error occurs.
- **How it works**:
    - The request body is deserialized into a `UserLoginRequest` object.
    - The `UserRespository` fetches the user by email and compares the plain-text password (not recommended for production; use hashing).
    - If the credentials are valid, a JWT is generated using the `generateJwt` function.
    - The JWT is stored in the database for the user and returned in the response.

---

### **3. Logout a User**
- **Route**: `POST /auth/logout`
- **Purpose**: Logs out a user by clearing their JWT.
- **Request Body**:
    - `email` (String): The email address of the user.
- **Response**:
    - `200 OK`: Logout successful.
    - `404 Not Found`: If the user is not found.
    - `400 Bad Request`: If the request is invalid or an error occurs.
- **How it works**:
    - The request body is deserialized into a `UserRequest` object.
    - The `UserRespository` fetches the user by email and clears their JWT in the database.
    - If successful, a `200 OK` response is returned.

---

### **4. Validate a JWT**
- **Route**: `POST /auth/validate`
- **Purpose**: Validates a JWT to ensure it is authentic and not expired.
- **Request Body**:
    - `jwt` (String): The JWT to validate.
- **Response**:
    - `200 OK`: Returns whether the JWT is valid and the user ID.
        - Response Body: `{ "valid": true, "userId": "<userId>" }` or `{ "valid": false, "message": "Invalid JWT" }`
    - `400 Bad Request`: If the request is invalid or an error occurs.
- **How it works**:
    - The request body is deserialized into a map containing the JWT.
    - The `validateJwt` function verifies the JWT using the secret key and issuer.
    - If the JWT is valid, the user ID is extracted from the JWT and returned.

---

## **Habit Routes**

### **1. Get All Habits for a User**
- **Route**: `GET /habits/user/{userId}`
- **Purpose**: Retrieves all habits associated with a specific user.
- **Path Parameter**:
    - `userId` (Int): The ID of the user.
- **Response**:
    - `200 OK`: Returns a list of habits.
        - Response Body: `List<HabitDetailsResponse>`
    - `400 Bad Request`: If the user ID is invalid.
    - `500 Internal Server Error`: If an error occurs while fetching habits.
- **How it works**:
    - The `userId` is extracted from the path parameters.
    - The `HabitRepository` fetches all habits for the user from the database.
    - The habits are returned as a list of `HabitDetailsResponse` objects.

---

### **2. Get a Specific Habit by ID**
- **Route**: `GET /habits/{habitId}`
- **Purpose**: Retrieves details of a specific habit by its ID.
- **Path Parameter**:
    - `habitId` (Int): The ID of the habit.
- **Response**:
    - `200 OK`: Returns the habit details.
        - Response Body: `HabitDetailsResponse`
    - `404 Not Found`: If the habit is not found.
    - `400 Bad Request`: If the habit ID is invalid.
    - `500 Internal Server Error`: If an error occurs while fetching the habit.
- **How it works**:
    - The `habitId` is extracted from the path parameters.
    - The `HabitRepository` fetches the habit by its ID from the database.
    - If the habit exists, it is returned as a `HabitDetailsResponse` object.

---

### **3. Register a New Habit**
- **Route**: `POST /habits`
- **Purpose**: Registers a new habit for a user.
- **Request Body**:
    - `name` (String): The name of the habit.
    - `description` (String): A description of the habit.
    - `targetDays` (Int): The target number of days to follow the habit.
    - `userId` (Int): The ID of the user creating the habit.
- **Response**:
    - `201 Created`: Returns the ID of the newly created habit.
        - Response Body: `{ "habitId": <habitId> }`
    - `400 Bad Request`: If the request is invalid or an error occurs.
- **How it works**:
    - The request body is deserialized into a `HabitRegisterRequest` object.
    - The `HabitRepository` inserts the habit into the database.
    - If successful, the ID of the new habit is returned.

---

### **4. Update a Habit**
- **Route**: `PUT /habits/{habitId}`
- **Purpose**: Updates an existing habit.
- **Path Parameter**:
    - `habitId` (Int): The ID of the habit to update.
- **Request Body**:
    - `name` (String): The updated name of the habit.
    - `description` (String): The updated description of the habit.
    - `targetDays` (Int): The updated target number of days.
    - `userId` (Int): The ID of the user updating the habit.
- **Response**:
    - `200 OK`: Habit updated successfully.
    - `404 Not Found`: If the habit is not found.
    - `400 Bad Request`: If the request is invalid or an error occurs.
- **How it works**:
    - The `habitId` is extracted from the path parameters.
    - The request body is deserialized into a `HabitRegisterRequest` object.
    - The `HabitRepository` updates the habit in the database.
    - If successful, a `200 OK` response is returned.

---

### **5. Delete a Habit**
- **Route**: `DELETE /habits/{habitId}`
- **Purpose**: Deletes a habit by its ID.
- **Path Parameter**:
    - `habitId` (Int): The ID of the habit to delete.
- **Response**:
    - `200 OK`: Habit deleted successfully.
    - `404 Not Found`: If the habit is not found.
    - `400 Bad Request`: If the habit ID is invalid.
    - `500 Internal Server Error`: If an error occurs while deleting the habit.
- **How it works**:
    - The `habitId` is extracted from the path parameters.
    - The `HabitRepository` deletes the habit from the database.
    - If successful, a `200 OK` response is returned.

---

## **Utility Functions**

### **1. Generate JWT**
- **Function**: `generateJwt(userId: Int, email: String): String`
- **Purpose**: Generates a JWT for a user.
- **Parameters**:
    - `userId` (Int): The ID of the user.
    - `email` (String): The email of the user.
- **Returns**: A JWT string.
- **How it works**:
    - Uses the HMAC256 algorithm with a secret key.
    - Sets the issuer, subject (user ID), and claims (email).
    - Sets an expiration time of 1 week.

---

### **2. Validate JWT**
- **Function**: `validateJwt(token: String): DecodedJWT?`
- **Purpose**: Validates a JWT.
- **Parameters**:
    - `token` (String): The JWT to validate.
- **Returns**: A `DecodedJWT` object if valid, otherwise `null`.
- **How it works**:
    - Uses the HMAC256 algorithm with the same secret key used to generate the JWT.
    - Verifies the token's issuer and signature.
    - Returns the decoded JWT if valid, otherwise `null`.

---

## **Notes**
1. **Security Considerations**:
    - Passwords are stored in plain text, which is not secure. Use a hashing algorithm like bcrypt for production.
    - JWTs should be signed with a strong secret key and stored securely.
2. **Error Handling**:
    - All routes include error handling to return appropriate HTTP status codes and error messages.
3. **Database Schema**:
    - The `Users` and `Habits` tables are created automatically using Exposed's `SchemaUtils.create`.