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
- User trip management
