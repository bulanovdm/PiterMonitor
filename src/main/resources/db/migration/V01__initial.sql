CREATE TABLE book
(
    id    VARCHAR(26) PRIMARY KEY,
    title VARCHAR(128),
    link  VARCHAR(1024)
);

CREATE TABLE price
(
    id        VARCHAR(26) PRIMARY KEY,
    variation VARCHAR(36),
    price     VARCHAR(24),
    book_id   VARCHAR(26) REFERENCES book (id)
);

CREATE TABLE chat
(
    id         VARCHAR(26) PRIMARY KEY,
    chat_id    BIGINT,
    subscribed INT
);