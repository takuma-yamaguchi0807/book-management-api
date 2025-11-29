CREATE TABLE books (
    id         BIGSERIAL PRIMARY KEY,
    title      TEXT NOT NULL,
    price      INTEGER NOT NULL,
    published  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE books_authors (
    author_id BIGINT NOT NULL REFERENCES authors(id),
    book_id   BIGINT NOT NULL REFERENCES books(id),
    PRIMARY KEY (author_id, book_id)
);

