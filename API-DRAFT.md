API draft design document

All endpoints except `POST /user` and `POST /login` requires JWT authorization.

Some endpoints except authorization may require user to be
guide or admin.

A disabled user is considered to have role "none".

## Already implemented endpoints

See implemented endpoints in Swagger:

- User management
- Support ticket management

## Trip authoring

`GET /trip` - get guide added trips (of current user)

Role: guide

Request:
(none)

Response:
```json
{
    "trips": [
        {
            "cost": 123,
            "guide": "guide-1@example.com",
            "description": 123,
            "peopleLimit": 10,
            "dateTrip": "2020-01-01",
            "active": true
        },
        {
            "cost": 123,
            "guide": "guide-1@example.com",
            "description": 123,
            "peopleLimit": 10,
            "dateTrip": "2020-01-01",
            "active": true
        }
    ]
}
```

`POST /trip` - create guide trip

Role: guide

Request:
```json
{
    "cost": 123,
    "description": 123,
    "peopleLimit": 10,
    "dateTrip": "2020-01-01",
    "active": true,
    "route": {
      "name": "route name",
      "points": [
        {
          "order": 1,
          "coordinates": "41°24'12.2\"N 2°10'26.5\"E"        
        },
        {
          "order": 2,
          "coordinates": "41°26'12.2\"N 2°15'26.5\"E"        
        }
      ] 
    }
}
```

Response:
(none)

Validate coordinates.

`PATCH /trip/{id}` - modify guide trip

Role: guide

Request:
```json
{
    "cost": 123,
    "description": 123,
    "peopleLimit": 10,
    "dateTrip": "2020-01-01",
    "active": true,
    "route": {
      "name": "route name",
      "points": [
        {
          "order": 1,
          "coordinates": "41°24'12.2\"N 2°10'26.5\"E"        
        },
        {
          "order": 2,
          "coordinates": "41°26'12.2\"N 2°15'26.5\"E"        
        }
      ] 
    }
}
```

Validate coordinates.

Response:
(none)


## Trip comments

`GET /trip/{trip-id}/comment/` - get trip comments

Role: any

Request:
(none)

Response:
```json
{
    "comments": [
        {
            "author": "user-1@example.com",
            "comment": "text"
        },
        {
            "author": "user-2@example.com",
            "comment": "text"
        }
    ]
}
```

`POST /trip/{trip-id}/comment/` - create trip comment

Role: any

Request:
```json
{
  "content": "comment-text"
}
```

Response:
(none)

`PATCH /trip/{trip-id}/comment/{comment-id}` - edit trip comment

Request:
```json
{
  "content": "comment-text",
  "deleted": false
}
```

Response:
(none)

At least one field is required. User can only edit their own comments.

Role: any (extra conditions apply)

## User trip management

`GET /user/trip` - get or search trips for user

TODO

`POST /user/trip` - join trip, handle payment

TODO

`PATCH /user/trip` - edit user trip.

TODO
