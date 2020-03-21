CREATE TABLE IF NOT EXISTS user_account(
    id_user_account SERIAL PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    disabled BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE TABLE IF NOT EXISTS comment_category (
    id_comment_category SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS role (
    id_role SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS role_user_account (
    id_role_user_account SERIAL PRIMARY KEY,
    user_account_id INT NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY (user_account_id) REFERENCES user_account(id_user_account),
    FOREIGN KEY (role_id) REFERENCES role(id_role)
);

CREATE TABLE IF NOT EXISTS ticket_category (
    id_ticket_category SERIAL PRIMARY KEY,
    name text NOT NULL
);

CREATE TABLE IF NOT EXISTS ticket (
    id_ticket SERIAL PRIMARY KEY,
    user_account_id INT NOT NULL,
    ticket_category_id INT NOT NULL,
    content text NOT NULL,
    ended BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_account_id) REFERENCES user_account(id_user_account),
    FOREIGN KEY (ticket_category_id) REFERENCES ticket_category(id_ticket_category)
);

CREATE TABLE IF NOT EXISTS payment_method (
    id_payment_method SERIAL PRIMARY KEY,
    psp_name text NOT NULL,
    token text NOT NULL,
    expired BOOLEAN NOT NULL DEFAULT FALSE,
    user_account_id INT NOT NULL,
    FOREIGN KEY (user_account_id) REFERENCES user_account(id_user_account)
);

CREATE TABLE IF NOT EXISTS point (
    id_point SERIAL PRIMARY KEY,
    coordinates text NOT NULL
);

CREATE TABLE IF NOT EXISTS route (
    id_route SERIAL PRIMARY KEY,
    name text NOT NULL,
    point_id INT NOT NULL,
    FOREIGN KEY (point_id) REFERENCES point(id_point)
);

CREATE TABLE IF NOT EXISTS trip (
    id_trip SERIAL PRIMARY KEY,
    user_account_id INT NOT NULL,
    route_id INT NOT NULL,
    cost text NOT NULL,
    description text NOT NULL,
    people_limit INT NOT NULL,
    date_trip TIMESTAMPTZ(0) NOT NULL,
    FOREIGN KEY (user_account_id) REFERENCES user_account(id_user_account),
    FOREIGN KEY (route_id) REFERENCES route(id_route)
);

CREATE TABLE IF NOT EXISTS promotion (
    id_promotion SERIAL PRIMARY KEY,
    trip_id INT NOT NULL,
    start_period TIMESTAMPTZ(0) NOT NULL,
    end_period TIMESTAMPTZ(0) NOT NULL,
    percent text NOT NULL,
    promotion_code text NOT NULL,
    disposable BOOLEAN NOT NULL,
    FOREIGN KEY (trip_id) REFERENCES trip(id_trip)
);

CREATE TABLE IF NOT EXISTS comment_user_account (
    id_comment_user_account SERIAL PRIMARY KEY,
    comment_category_id INT NOT NULL,
    user_account_id INT NOT NULL,
    trip_id INT NOT NULL ,
    content TEXT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (comment_category_id) REFERENCES comment_category(id_comment_category),
    FOREIGN KEY (user_account_id) REFERENCES user_account(id_user_account),
    FOREIGN KEY (trip_id) REFERENCES trip(id_trip)
);

CREATE TABLE IF not exists trip_user_account (
    id_trip_user_account SERIAL PRIMARY KEY,
    trip_id INT NOT NULL,
    user_account_id INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (trip_id) REFERENCES trip(id_trip),
    FOREIGN KEY (user_account_id) REFERENCES user_account(id_user_account)
);

CREATE TABLE IF NOT EXISTS payment (
    id_payment SERIAL PRIMARY KEY,
    trip_user_account_id INT NOT NULL,
    paid BOOLEAN DEFAULT FALSE NOT NULL,
    FOREIGN KEY (trip_user_account_id) REFERENCES trip_user_account(id_trip_user_account)
);

CREATE TABLE IF NOT EXISTS payment_history (
    id_payment_history SERIAL PRIMARY KEY,
    payment_id INT NOT NULL,
    price TEXT NOT NULL,
    payment_method_id INT NOT NULL,
    FOREIGN KEY (payment_id) REFERENCES payment(id_payment),
    FOREIGN KEY (payment_method_id) REFERENCES payment_method(id_payment_method)
);
