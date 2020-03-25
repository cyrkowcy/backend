CREATE TABLE user_account(
    id_user_account SERIAL PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    disabled BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE role (
    id_role SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE role_user_account (
    id_role_user_account SERIAL PRIMARY KEY,
    user_account_id INT NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY (user_account_id) REFERENCES user_account(id_user_account),
    FOREIGN KEY (role_id) REFERENCES role(id_role),
    UNIQUE (id_role_user_account, user_account_id, role_id)
);

CREATE TABLE ticket (
    id_ticket SERIAL PRIMARY KEY,
    user_account_id INT NOT NULL,
    content TEXT NOT NULL,
    closed BOOLEAN NOT NULL DEFAULT FALSE,
    create_date TIMESTAMPTZ(0) NOT NULL DEFAULT now(),
    FOREIGN KEY (user_account_id) REFERENCES user_account(id_user_account)
);

CREATE TABLE ticket_comment (
    id_ticket_comment SERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    user_account_id INT NOT NULL,
    FOREIGN KEY (user_account_id) REFERENCES user_account(id_user_account)
);

CREATE TABLE route (
    id_route SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE point (
    id_point SERIAL PRIMARY KEY,
    order_position INT NOT NULL,
    coordinates TEXT NOT NULL,
    route_id INT NOT NULL,
    FOREIGN KEY (route_id) REFERENCES route(id_route)
);

CREATE TABLE trip (
    id_trip SERIAL PRIMARY KEY,
    user_account_id INT NOT NULL,
    route_id INT NOT NULL,
    cost TEXT NOT NULL,
    description TEXT NOT NULL,
    people_limit INT NOT NULL,
    date_trip TIMESTAMPTZ(0) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (user_account_id) REFERENCES user_account(id_user_account),
    FOREIGN KEY (route_id) REFERENCES route(id_route)
);

CREATE TABLE promotion (
    id_promotion SERIAL PRIMARY KEY,
    trip_id INT NOT NULL,
    start_period TIMESTAMPTZ(0) NOT NULL,
    end_period TIMESTAMPTZ(0) NOT NULL,
    percent TEXT NOT NULL,
    promotion_code TEXT NOT NULL,
    disposable BOOLEAN NOT NULL,
    FOREIGN KEY (trip_id) REFERENCES trip(id_trip)
);

CREATE TABLE payment_method (
    id_payment_method SERIAL PRIMARY KEY,
    psp_name TEXT NOT NULL,
    token TEXT NOT NULL,
    expired BOOLEAN NOT NULL DEFAULT FALSE,
    user_account_id INT NOT NULL,
    FOREIGN KEY (user_account_id) REFERENCES user_account(id_user_account)
);

CREATE TABLE trip_comment (
    id_comment_user_account SERIAL PRIMARY KEY,
    user_account_id INT NOT NULL,
    trip_id INT NOT NULL ,
    content TEXT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_account_id) REFERENCES user_account(id_user_account),
    FOREIGN KEY (trip_id) REFERENCES trip(id_trip)
);

CREATE TABLE trip_user_account (
    id_trip_user_account SERIAL PRIMARY KEY,
    trip_id INT NOT NULL,
    user_account_id INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (trip_id) REFERENCES trip(id_trip),
    FOREIGN KEY (user_account_id) REFERENCES user_account(id_user_account)
);

CREATE TABLE payment (
    id_payment SERIAL PRIMARY KEY,
    trip_user_account_id INT NOT NULL,
    paid BOOLEAN DEFAULT FALSE NOT NULL,
    FOREIGN KEY (trip_user_account_id) REFERENCES trip_user_account(id_trip_user_account)
);

CREATE TABLE payment_history (
    id_payment_history SERIAL PRIMARY KEY,
    payment_id INT NOT NULL,
    price TEXT NOT NULL,
    payment_method_id INT NOT NULL,
    FOREIGN KEY (payment_id) REFERENCES payment(id_payment),
    FOREIGN KEY (payment_method_id) REFERENCES payment_method(id_payment_method)
);
