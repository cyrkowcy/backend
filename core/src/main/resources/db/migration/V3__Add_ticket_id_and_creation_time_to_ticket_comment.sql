ALTER TABLE ticket_comment
    ADD COLUMN ticket_id   INT,
    ADD COLUMN create_date TIMESTAMPTZ(0) NOT NULL DEFAULT now()
