INSERT INTO currency (code, full_name, sign)
VALUES ('AUD', 'Australian dollar', 'A$'),
       ('USD', 'US dollar', '$'),
       ('RUB', 'Russian ruble', '₽'),
       ('EUR', 'Euro', '€');

INSERT INTO exchange_rate (base_currency_id, target_currency_id, rate)
VALUES (2, 3, 90.59), (2, 4, 0.89);