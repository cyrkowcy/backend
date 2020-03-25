API draft design document

All endpoints except `POST /user` and `POST /login` requires JWT authorization.

Some endpoints except authorization may require user to be
guide or admin.

A disabled user is considered to have role "none".

## User management

`GET /user` - get current user

Role: any

Request:
(none)

Response:
```json
{
    "firstName": "user",
    "lastName": "user",
    "password": "123456",
    "roles": ["user", "admin", "guide"],
    "disabled": false
}
```

`POST /user` - create new user

Role: none

Request:
```json
{
    "firstName": "user",
    "lastName": "user",
    "email": "user",
    "password": "123456"
}
```

Response:
```json
{
    "firstName": "user",
    "lastName": "user",
    "password": "123456",
    "roles": ["user"],
    "disabled": false
}
```

`PATCH /user/{username}` - modify user

Role: any (extra conditions apply)

Request:
```json
{
    "firstName": "user",
    "lastName": "user",
    "password": "123456",
    "roles": ["user", "admin", "guide"],
    "disabled": false
}
```
Response:
(none)

At least one field is required. Only admin can specify roles and disabled fields.
 
Non-admin user can only patch themselves. Admin can patch anyone.

`POST /login` - login

Role: none

Request:
```json
{
    "email": "user",
    "password": "123456"
}
```

Response:
```json
{
  "token": "jwt-token"
}
```

`GET /users` - get list of all users

Role: admin

Request  
(none)

Response
```json
[
  {
      "firstName": "user",
      "lastName": "user",
      "password": "123456",
      "roles": ["user", "admin", "guide"],
      "disabled": false
  },
  {
    "firstName": "user",
    "lastName": "user",
    "password": "123456",
    "roles": ["user", "admin", "guide"],
    "disabled": false
  },
  ...
]
```

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
            "createDate": "2020-01-01"
        },
        {
            "id": 2,
            "closed": false,
            "createDate": "2020-01-01"
        }
    ]
}
```

Only admin can use `all=true`. If true return every ticket regardless of current user.

`POST /ticket` - create new support ticket

Role: any

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
  "content": "ticket-text-content"
}
```

Response:
```json
{
    "id": 1,
    "closed": false,
    "createDate": "2020-01-01"
}
```

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
          "author": "user-1"
        },
        {
          "content": "comment-text-2",
          "author": "admin-1"
        }
    ]
}
```

User can only get their own ticket

`POST /ticket/{ticket-id}` - add ticket comment

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

`PATCH /ticket` - update ticket

Role: any (extra conditions apply)

Request:
```json
{
    "closed": false
}
```

Response:
(none)

Both admins and users can close tickets. User can only close their own ticket.

## Trip authoring

`GET /trip` - get guide added trips

Role: guide

Request:
(none)

Response:
```json
{
    "trips": [
        {
            "cost": 123,
            "guide": "guide-1",
            "description": 123,
            "peopleLimit": 10,
            "dateTrip": "2020-01-01",
            "active": true
        },
        {
            "cost": 123,
            "guide": "guide-1",
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

Role: guide

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
            "author": "user-1",
            "comment": "text"
        },
        {
            "author": "user-2",
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
