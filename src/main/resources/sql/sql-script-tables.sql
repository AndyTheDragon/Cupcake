-- This script was generated by the ERD tool in pgAdmin 4.
-- Please log an issue at https://github.com/pgadmin-org/pgadmin4/issues/new/choose if you find any bugs, including reproduction steps.
BEGIN;


CREATE TABLE IF NOT EXISTS public.users
(
    user_id serial NOT NULL,
    username character varying(64) NOT NULL,
    password character varying(64) NOT NULL,
    role character varying(12) NOT NULL,
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS public.orders
(
    order_id serial NOT NULL,
    name character varying(64) NOT NULL,
    date_placed date NOT NULL,
    date_paid date NULL,
    date_completed date NULL,
    status character varying(64) NOT NULL,
    user_id integer NOT NULL,
    CONSTRAINT pk PRIMARY KEY (order_id)
);

CREATE TABLE IF NOT EXISTS public.order_line
(
    order_line_id serial NOT NULL,
    quantity integer NOT NULL,
    price integer NOT NULL,
    order_id integer NOT NULL,
    CONSTRAINT pk PRIMARY KEY (order_line_id)
);

ALTER TABLE IF EXISTS public.orders
    ADD CONSTRAINT fk FOREIGN KEY (user_id)
    REFERENCES public.users (user_id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public.order_line
    ADD CONSTRAINT fk FOREIGN KEY (order_id)
    REFERENCES public.orders (order_id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;

END;