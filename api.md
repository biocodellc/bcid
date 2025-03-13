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

## Examples


display the root service options
```
curl "https://bcid.geome-db.org/" 


Authenticate (returns AUTH_TOKEN)
```
curl -X POST "https://bcid.geome-db.org/oAuth2/token" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -H "Accept: application/json" \
     -d "grant_type=client_credentials" \
     -d "client_id=CLIENT_ID" \
     -d "client_secret=CLIENT_SECRET"
```


Display available endpoints:
```
curl -X OPTIONS "https://bcid.geome-db.org/" \
    -H "Authorization: Bearer AUTH_TOKEN"

