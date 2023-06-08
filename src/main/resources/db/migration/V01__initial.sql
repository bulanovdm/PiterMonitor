CREATE TABLE book
(
    title VARCHAR PRIMARY KEY,
    link  VARCHAR
);

CREATE TABLE price
(
    id         VARCHAR PRIMARY KEY,
    variation  VARCHAR,
    price      VARCHAR,
    book_title VARCHAR REFERENCES book (title)
);