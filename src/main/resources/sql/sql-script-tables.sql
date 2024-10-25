-- This script was generated by the ERD tool in pgAdmin 4.
-- Please log an issue at https://github.com/pgadmin-org/pgadmin4/issues/new/choose if you find any bugs, including reproduction steps.
BEGIN;

DROP TABLE IF EXISTS public.cupcake_flavours CASCADE;
DROP TABLE IF EXISTS public.order_lines CASCADE;
DROP TABLE IF EXISTS public.orders CASCADE;
DROP TABLE IF EXISTS public.users CASCADE;


CREATE TABLE IF NOT EXISTS public.users
(
    user_id serial NOT NULL,
    username character varying(64) NOT NULL UNIQUE,
    password character varying(64) NOT NULL,
    balance integer default 0,
    role character varying(12) NOT NULL,
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS public.orders
(
    order_id serial NOT NULL,
    name character varying(64) NOT NULL,
    date_placed date DEFAULT NOW(),
    date_paid date NULL,
    date_completed date NULL,
    status character varying(64) NOT NULL,
    user_id integer default NULL,
    PRIMARY KEY (order_id)
);

CREATE TABLE IF NOT EXISTS public.cupcake_flavours
(
    flavour_id serial NOT NULL,
    flavour_name character varying(64) NOT NULL,
    is_top_flavour boolean NOT NULL,
    is_bottom_flavour boolean NOT NULL,
    price int NOT NULL,
    PRIMARY KEY (flavour_id)
);

CREATE TABLE IF NOT EXISTS public.order_lines
(
    order_line_id serial NOT NULL,
    quantity integer NOT NULL,
    top_flavour integer NOT NULL,
    bottom_flavour integer NOT NULL,
    price integer NOT NULL,
    order_id integer NOT NULL,
    PRIMARY KEY (order_line_id)
);

ALTER TABLE IF EXISTS public.orders
    ADD CONSTRAINT fk FOREIGN KEY (user_id)
    REFERENCES public.users (user_id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public.order_lines
    ADD CONSTRAINT fk FOREIGN KEY (order_id)
        REFERENCES public.orders (order_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID;

ALTER TABLE IF EXISTS public.order_lines
    ADD CONSTRAINT fk_top FOREIGN KEY (top_flavour)
        REFERENCES public.cupcake_flavours (flavour_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID;

ALTER TABLE IF EXISTS public.order_lines
    ADD CONSTRAINT fk_bot FOREIGN KEY (bottom_flavour)
        REFERENCES public.cupcake_flavours (flavour_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID;

INSERT INTO cupcake_flavours (flavour_name, is_top_flavour, is_bottom_flavour, price) VALUES
                 ('Chocolate',true,true,500),
                 ('Vanilla',false,true,500),
                 ('Nutmeg',false,true,500),
                 ('Pistachio',false,true,600),
                 ('Almond', false,true, 700),
                 ('Blueberry', true,false,500),
                 ('Raspberry', true,false,500),
                 ('Crispy', true,false,600),
                 ('Strawberry', true,false,600),
                 ('Rum/Raisin', true,false,700),
                 ('Orange', true,false,800),
                 ('Lemon', true,false,800),
                 ('Blue cheese', true,false,900);


END;