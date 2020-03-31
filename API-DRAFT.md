API draft design document

All endpoints except `POST /user` and `POST /login` requires JWT authorization.

Some endpoints except authorization may require user to be
guide or admin.

A disabled user is considered to have role "none".

## User management

Endpoints implemented - see in Swagger.

## Support ticket management

`GET /ticket?all=false` - get support tickets

Role: any (extra conditions apply)

Request:
(none)

Response:
```json
{
    "tickets": [
        {
            "id": 1,
            "closed": false,
            "author": "user-1@example.com",
            "createDate": "2020-01-01"
        },
        {
            "id": 2,
            "closed": false,
            "author": "user-2@example.com",
            "createDate": "2020-01-01"
        }
    ]
}
```

Only admin can use `all=true`. If true return every ticket regardless of current user.

`POST /ticket` - create new support ticket as current user

Role: any (extra conditions apply)

Request:
```json
{
  "content": "ticket-text-content"
}
```

Response:
(none)

Content limited to 1000 characters.

`PATCH /ticket/{ticket-id}` - modify ticket

Role: any (extra conditions apply)

Request:
```json
{
  "content": "ticket-text-content",
  "closed": false
}
```

Both admins and users can close tickets. User can only close their own ticket.

Response:
(none)

User can only modify their own ticket

`GET /ticket/{ticket-id}` - get ticket details

Role: any (extra conditions apply)

Request:
(none)

Response:
```json
{
    "id": 1,
    "closed": false,
    "createDate": "2020-01-01",
    "comments": [
        {
          "content": "comment-text",
          "author": "user-1@example.com"
        },
        {
          "content": "comment-text-2",
          "author": "admin-1@example.com"
        }
    ]
}
```

User can only get their own ticket. Tickets sorted by created date.

`POST /ticket/{ticket-id}/comment` - add ticket comment

Role: any (extra conditions apply)

Request:
```json
{
    "comment": "comment-text"
}
```

Response:
(none) 

User can only add comment to their own ticket.

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
