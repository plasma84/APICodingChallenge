# Book Management API - Postman Testing Guide

This guide will help you test the Book Management API using Postman. The API provides JWT authentication and CRUD operations for books.

## 🚀 Getting Started

### 1. Start the Application
```bash
mvn spring-boot:run
```
The application will start on `http://localhost:8080`

### 2. Sample Data
The application automatically creates sample users and books on startup:

**Users:**
- **Admin:** username=`admin`, password=`admin123`
- **User:** username=`user`, password=`user123`

**Books:** 5 sample books are automatically created.

## 📋 API Endpoints

### Authentication Endpoints

#### 1. POST `/api/auth/login` - User Login
**Description:** Authenticate user and get JWT token

**Request:**
```json
{
    "username": "admin",
    "password": "admin123"
}
```

**Response:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "admin",
    "email": "admin@bookstore.com",
    "role": "ROLE_ADMIN"
}
```

#### 2. POST `/api/auth/signup` - User Registration
**Description:** Register a new user

**Request:**
```json
{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "password123",
    "role": "USER"
}
```

### Book Management Endpoints

#### 3. GET `/api/books` - Get All Books
**Description:** Retrieve all books (requires authentication)

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
```

#### 4. GET `/api/books/{isbn}` - Get Book by ISBN
**Description:** Retrieve a specific book by ISBN

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
```

#### 5. POST `/api/books` - Create New Book
**Description:** Create a new book (requires USER or ADMIN role)

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

**Request:**
```json
{
    "isbn": "9781234567890",
    "title": "Spring Boot in Action",
    "author": "Craig Walls",
    "publicationYear": 2020
}
```

#### 6. PUT `/api/books/{isbn}` - Update Book
**Description:** Update an existing book (requires ADMIN role)

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

**Request:**
```json
{
    "isbn": "9781234567890",
    "title": "Spring Boot in Action - Updated",
    "author": "Craig Walls",
    "publicationYear": 2021
}
```

#### 7. DELETE `/api/books/{isbn}` - Delete Book
**Description:** Delete a book (requires ADMIN role)

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
```

## 🔧 Postman Setup Instructions

### Step 1: Create a New Collection
1. Open Postman
2. Click "New" → "Collection"
3. Name it "Book Management API"

### Step 2: Set Collection Variables
1. Right-click your collection → "Edit"
2. Go to "Variables" tab
3. Add these variables:
   - `baseUrl`: `http://localhost:8080`
   - `token`: (leave empty, will be set dynamically)

### Step 3: Create Authentication Request

**Login Request:**
- Method: POST
- URL: `{{baseUrl}}/api/auth/login`
- Body (JSON):
```json
{
    "username": "admin",
    "password": "admin123"
}
```

**Tests Script (to auto-save token):**
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.collectionVariables.set("token", response.token);
    console.log("Token saved:", response.token);
}
```

### Step 4: Configure Authorization for Other Requests
For all book-related endpoints:
1. Go to "Authorization" tab
2. Type: "Bearer Token"
3. Token: `{{token}}`

## 🧪 Test Scenarios

### Scenario 1: Admin User Flow
1. **Login as admin**
   - POST `/api/auth/login` with admin credentials
   - Save the JWT token

2. **Get all books**
   - GET `/api/books`
   - Should return list of books

3. **Create a new book**
   - POST `/api/books` with book data
   - Should return 201 Created

4. **Update a book**
   - PUT `/api/books/{isbn}` with updated data
   - Should return 200 OK

5. **Delete a book**
   - DELETE `/api/books/{isbn}`
   - Should return 200 OK

### Scenario 2: Regular User Flow
1. **Login as user**
   - POST `/api/auth/login` with user credentials

2. **Get all books**
   - GET `/api/books`
   - Should work (200 OK)

3. **Create a new book**
   - POST `/api/books` with book data
   - Should work (201 Created)

4. **Try to update a book**
   - PUT `/api/books/{isbn}`
   - Should fail (403 Forbidden)

5. **Try to delete a book**
   - DELETE `/api/books/{isbn}`
   - Should fail (403 Forbidden)

### Scenario 3: Unauthorized Access
1. **Try to access books without token**
   - GET `/api/books` without Authorization header
   - Should fail (401 Unauthorized)

## 📄 Sample Test Data

### Books to Create:
```json
{
    "isbn": "9781617294945",
    "title": "Spring Boot in Action",
    "author": "Craig Walls",
    "publicationYear": 2015
}
```

```json
{
    "isbn": "9780132350884",
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "publicationYear": 2008
}
```

### Users to Register:
```json
{
    "username": "testuser",
    "email": "test@example.com",
    "password": "test123",
    "role": "USER"
}
```

## 🔍 Expected HTTP Status Codes

- **200 OK**: Successful GET, PUT operations
- **201 Created**: Successful POST operations
- **401 Unauthorized**: Missing or invalid JWT token
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **400 Bad Request**: Invalid request data

## 🚨 Troubleshooting

### Common Issues:

1. **401 Unauthorized**
   - Check if JWT token is included in Authorization header
   - Verify token format: `Bearer YOUR_TOKEN`

2. **403 Forbidden**
   - User role doesn't have permission for the operation
   - ADMIN role required for UPDATE/DELETE operations

3. **400 Bad Request**
   - Check JSON format
   - Verify required fields are included
   - ISBN should be unique for creation

4. **Connection Refused**
   - Ensure application is running on port 8080
   - Check if another service is using port 8080

### Debug Tips:
- Check application logs in the console
- Verify JWT token expiration (default: 24 hours)
- Use Postman Console to see detailed request/response logs

## 🎯 Quick Start Commands

Start the application:
```bash
cd "c:\Users\vsour\Desktop\coading"
mvn spring-boot:run
```

The application will be available at: `http://localhost:8080`

Happy testing! 🚀
