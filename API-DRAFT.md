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

## User trip management

`GET /user/trip` - get or search trips for user

TODO

`POST /user/trip` - join trip, handle payment

TODO

`PATCH /user/trip` - edit user trip.

TODO
