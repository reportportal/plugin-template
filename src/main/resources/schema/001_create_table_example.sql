CREATE TABLE IF NOT EXISTS public.example
(
    id         BIGSERIAL               NOT NULL PRIMARY KEY,
    name       VARCHAR(256) UNIQUE     NOT NULL,
    created_at TIMESTAMP DEFAULT now() NOT NULL
);
