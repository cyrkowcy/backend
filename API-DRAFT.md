API draft design document

All endpoints except `POST /user` and `POST /login` requires JWT authorization.

Some endpoints except authorization may require user to be
guide or admin.

A disabled user is considered to have role "none".

## Already implemented endpoints

See implemented endpoints in Swagger:

- User management
- Support ticket management
- Trip authoring, management and comments

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
