insert into categories (id, name)
values (1, 'Detective');

insert into books (id, title, author, isbn, price)
values (1, 'Sherlock Holmes', 'Arthur Ignatius Conan Doyle', '674-465-23', 65.99);

insert into books_categories (book_id, category_id)
values (1, 1);
