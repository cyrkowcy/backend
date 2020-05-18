ALTER TABLE user_account
    ADD COLUMN create_date TIMESTAMPTZ(0) NOT NULL DEFAULT now()
