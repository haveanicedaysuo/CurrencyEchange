CREATE TABLE IF NOT EXISTS currency
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    code character varying(3)  NOT NULL,
    full_name character varying(255)  NOT NULL,
    sign character varying(10)  NOT NULL,
    CONSTRAINT currency_pkey PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS exchange_rate
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    base_currency_id bigint NOT NULL,
    target_currency_id bigint NOT NULL,
    rate numeric(12,6) NOT NULL,
    CONSTRAINT exchange_rates_pkey PRIMARY KEY (id),
    CONSTRAINT base_currency_fk FOREIGN KEY (base_currency_id)
        REFERENCES currency  (id) MATCH SIMPLE,

    CONSTRAINT target_currency_fk FOREIGN KEY (target_currency_id)
        REFERENCES currency (id) MATCH SIMPLE

);
