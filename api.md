## api.md

# BCID API Documentation

## Base URL

```
http://localhost:8080/api/
```

## Authentication

Include the API key in headers:

```
Authorization: Bearer YOUR_API_KEY
```

## Endpoints

### 1. Create Identifier

**POST** `/identifiers`

```json
{
  "metadata": {
    "title": "Sample Dataset",
    "description": "Description",
    "creator": "John Doe",
    "institution": "Sample University"
  }
}
```

### 2. Get Identifier

**GET** `/identifiers/{id}`

### 3. Update Identifier

**PUT** `/identifiers/{id}`

```json
{
  "metadata": { "title": "Updated Title" }
}
```

### 4. Delete Identifier

**DELETE** `/identifiers/{id}`

### 5. List Identifiers

**GET** `/identifiers`

For more details, visit [API Documentation](api.md).


