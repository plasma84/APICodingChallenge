# Book Management API

A RESTful API built with Spring Boot for managing a collection of books with JWT authentication.

## Features

- **CRUD Operations**: Create, Read, Update, and Delete books
- **JWT Authentication**: Secure API endpoints with JWT tokens
- **Role-based Access Control**: Different permissions for USER and ADMIN roles
- **Input Validation**: Comprehensive validation for all input data
- **Exception Handling**: Global exception handling with meaningful error messages
- **H2 Database**: In-memory database for development and testing
- **Unit Tests**: Comprehensive test coverage

## Technologies Used

- **Spring Boot 3.2.0**
- **Spring Security 6**
- **Spring Data JPA**
- **JWT (JSON Web Tokens)**
- **H2 Database**
- **Maven**
- **JUnit 5**
- **Mockito**

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd book-management-api
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register a new user |
| POST | `/api/auth/signin` | Login and get JWT token |

### Book Management Endpoints

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/books` | Get all books | Authenticated |
| GET | `/api/books/{isbn}` | Get book by ISBN | Authenticated |
| POST | `/api/books` | Create a new book | USER or ADMIN |
| PUT | `/api/books/{isbn}` | Update a book | ADMIN |
| DELETE | `/api/books/{isbn}` | Delete a book | ADMIN |

## Request/Response Examples

### User Registration
```json
POST /api/auth/signup
{
    "username": "johndoe",
    "password": "password123",
    "email": "john@example.com",
    "role": "USER"
}
```

### User Login
```json
POST /api/auth/signin
{
    "username": "johndoe",
    "password": "password123"
}
```

**Response:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "username": "johndoe",
    "email": "john@example.com",
    "role": "ROLE_USER"
}
```

### Create a Book
```json
POST /api/books
Authorization: Bearer <jwt-token>
{
    "isbn": "1234567890",
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "publicationYear": 1925
}
```

### Get All Books
```json
GET /api/books
Authorization: Bearer <jwt-token>
```

**Response:**
```json
[
    {
        "isbn": "1234567890",
        "title": "The Great Gatsby",
        "author": "F. Scott Fitzgerald",
        "publicationYear": 1925
    }
]
```

## Data Validation

### Book Entity Validation
- **ISBN**: Required, must be 10 or 13 digits
- **Title**: Required, max 255 characters
- **Author**: Required, max 255 characters
- **Publication Year**: Required, between 1000 and 2030

### User Entity Validation
- **Username**: Required, 3-50 characters, unique
- **Password**: Required, minimum 6 characters
- **Email**: Required, valid email format, unique

## Security

- JWT tokens are required for all book management operations
- Passwords are encrypted using BCrypt
- Role-based access control:
  - **USER**: Can view and create books
  - **ADMIN**: Full CRUD operations

## Database

The application uses H2 in-memory database. You can access the H2 console at:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:bookstore`
- Username: `sa`
- Password: `password`

## Testing

Run the tests using Maven:

```bash
# Run all tests
mvn test

# Run only unit tests
mvn test -Dtest="*Test"

# Run only integration tests
mvn test -Dtest="*IntegrationTest"
```

### Test Coverage

- **Service Layer Tests**: Unit tests for business logic
- **Controller Layer Tests**: Web layer tests with MockMvc
- **Integration Tests**: End-to-end testing with real database
- **Security Tests**: Authentication and authorization testing

## Testing with Postman

### 1. Register a User
```
POST http://localhost:8080/api/auth/signup
Content-Type: application/json

{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "role": "USER"
}
```

### 2. Login
```
POST http://localhost:8080/api/auth/signin
Content-Type: application/json

{
    "username": "testuser",
    "password": "password123"
}
```

Copy the `token` from the response.

### 3. Create a Book
```
POST http://localhost:8080/api/books
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
    "isbn": "9780743273565",
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "publicationYear": 1925
}
```

### 4. Get All Books
```
GET http://localhost:8080/api/books
Authorization: Bearer <your-jwt-token>
```

### 5. Get Book by ISBN
```
GET http://localhost:8080/api/books/9780743273565
Authorization: Bearer <your-jwt-token>
```

### 6. Update a Book (Admin only)
```
PUT http://localhost:8080/api/books/9780743273565
Authorization: Bearer <admin-jwt-token>
Content-Type: application/json

{
    "isbn": "9780743273565",
    "title": "The Great Gatsby - Updated",
    "author": "F. Scott Fitzgerald",
    "publicationYear": 1925
}
```

### 7. Delete a Book (Admin only)
```
DELETE http://localhost:8080/api/books/9780743273565
Authorization: Bearer <admin-jwt-token>
```

## Error Handling

The API provides meaningful error responses:

- **400 Bad Request**: Validation errors
- **401 Unauthorized**: Missing or invalid JWT token
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **409 Conflict**: Duplicate resource (e.g., ISBN already exists)
- **500 Internal Server Error**: Unexpected errors

## Configuration

Key configuration properties in `application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:h2:mem:bookstore
spring.h2.console.enabled=true

# JWT Configuration
jwt.secret=mySecretKey12345678901234567890123456789012345678901234567890
jwt.expiration=86400000

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
