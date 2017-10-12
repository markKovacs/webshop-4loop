--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.8
-- Dumped by pg_dump version 9.5.8

-- Started on 2017-10-12 11:15:19 CEST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 1 (class 3079 OID 12395)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2228 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- TOC entry 560 (class 1247 OID 50142)
-- Name: order_status; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE order_status AS ENUM (
    'reviewed',
    'checked',
    'paid'
);


--
-- TOC entry 193 (class 1255 OID 50245)
-- Name: update_modified_column(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION update_modified_column() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.modified = now();
    RETURN NEW;	
END;
$$;


SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 182 (class 1259 OID 50151)
-- Name: categories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE categories (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    department character varying(100),
    description character varying(300),
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 181 (class 1259 OID 50149)
-- Name: categories_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE categories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2229 (class 0 OID 0)
-- Dependencies: 181
-- Name: categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE categories_id_seq OWNED BY categories.id;


--
-- TOC entry 192 (class 1259 OID 50232)
-- Name: lineitems; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE lineitems (
    product_id integer NOT NULL,
    order_id integer NOT NULL,
    quantity integer NOT NULL,
    actual_price numeric(10,2) NOT NULL,
    currency character varying(3) NOT NULL
);


--
-- TOC entry 191 (class 1259 OID 50214)
-- Name: orders; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE orders (
    id integer NOT NULL,
    user_id integer NOT NULL,
    closed_date timestamp without time zone,
    status order_status,
    deleted smallint DEFAULT 0 NOT NULL,
    log_filename character varying(100),
    billing_country character varying(100),
    billing_city character varying(100),
    billing_zip character varying(20),
    billing_address character varying(255),
    shipping_country character varying(100),
    shipping_city character varying(100),
    shipping_zip character varying(20),
    shipping_address character varying(255),
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 189 (class 1259 OID 50210)
-- Name: orders_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE orders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2230 (class 0 OID 0)
-- Dependencies: 189
-- Name: orders_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE orders_id_seq OWNED BY orders.id;


--
-- TOC entry 190 (class 1259 OID 50212)
-- Name: orders_user_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE orders_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2231 (class 0 OID 0)
-- Dependencies: 190
-- Name: orders_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE orders_user_id_seq OWNED BY orders.user_id;


--
-- TOC entry 186 (class 1259 OID 50174)
-- Name: products; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE products (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(300),
    category_id integer NOT NULL,
    supplier_id integer NOT NULL,
    price numeric(10,2) NOT NULL,
    currency character varying(3) NOT NULL,
    image_filename character varying(100),
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    CONSTRAINT products_price_check CHECK ((price > (0)::numeric))
);


--
-- TOC entry 185 (class 1259 OID 50172)
-- Name: products_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2232 (class 0 OID 0)
-- Dependencies: 185
-- Name: products_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE products_id_seq OWNED BY products.id;


--
-- TOC entry 184 (class 1259 OID 50164)
-- Name: suppliers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE suppliers (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(300),
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 183 (class 1259 OID 50162)
-- Name: suppliers_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE suppliers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2233 (class 0 OID 0)
-- Dependencies: 183
-- Name: suppliers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE suppliers_id_seq OWNED BY suppliers.id;


--
-- TOC entry 188 (class 1259 OID 50198)
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE users (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    phone_number character varying(50),
    email character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    billing_country character varying(100),
    billing_city character varying(100),
    billing_zip character varying(20),
    billing_address character varying(255),
    shipping_country character varying(100),
    shipping_city character varying(100),
    shipping_zip character varying(20),
    shipping_address character varying(255),
    active integer DEFAULT 0,
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 187 (class 1259 OID 50196)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2234 (class 0 OID 0)
-- Dependencies: 187
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


--
-- TOC entry 2057 (class 2604 OID 50154)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY categories ALTER COLUMN id SET DEFAULT nextval('categories_id_seq'::regclass);


--
-- TOC entry 2071 (class 2604 OID 50217)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY orders ALTER COLUMN id SET DEFAULT nextval('orders_id_seq'::regclass);


--
-- TOC entry 2072 (class 2604 OID 50218)
-- Name: user_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY orders ALTER COLUMN user_id SET DEFAULT nextval('orders_user_id_seq'::regclass);


--
-- TOC entry 2063 (class 2604 OID 50177)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY products ALTER COLUMN id SET DEFAULT nextval('products_id_seq'::regclass);


--
-- TOC entry 2060 (class 2604 OID 50167)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY suppliers ALTER COLUMN id SET DEFAULT nextval('suppliers_id_seq'::regclass);


--
-- TOC entry 2067 (class 2604 OID 50201)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


--
-- TOC entry 2211 (class 0 OID 50151)
-- Dependencies: 182
-- Data for Name: categories; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO categories (id, name, department, description, created, modified) VALUES (1, 'Famous Artifacts', 'Article', 'Tangible memories from popular musicians, athletes etc.', '2017-10-09 19:37:59.356189', '2017-10-09 19:37:59.356189');
INSERT INTO categories (id, name, department, description, created, modified) VALUES (2, 'Historical', 'Article', 'Tangible memories from emperors, generals etc.', '2017-10-09 19:37:59.356189', '2017-10-09 19:37:59.356189');
INSERT INTO categories (id, name, department, description, created, modified) VALUES (3, 'Movies', 'Entertainment', 'Vehicles, weapons and other famous objects from great movies.', '2017-10-09 19:37:59.356189', '2017-10-09 19:37:59.356189');


--
-- TOC entry 2235 (class 0 OID 0)
-- Dependencies: 181
-- Name: categories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('categories_id_seq', 1, false);


--
-- TOC entry 2221 (class 0 OID 50232)
-- Dependencies: 192
-- Data for Name: lineitems; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2220 (class 0 OID 50214)
-- Dependencies: 191
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (21, 1, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 11:07:01.361802', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (14, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 10:59:08.649291', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (13, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 10:57:46.937634', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (12, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 10:57:36.737797', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (8, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 10:55:14.372658', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (1, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 10:47:51.146097', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (2, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 10:49:12.728404', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (3, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 10:49:59.625156', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (4, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 10:50:47.723015', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (5, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 10:51:55.312794', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (6, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 10:52:04.790464', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (7, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 10:52:46.289449', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (9, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 10:55:55.934394', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (10, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 10:56:16.67315', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (11, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 10:56:38.378919', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (15, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 11:04:22.802027', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (16, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 11:05:18.624698', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (17, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 11:05:20.44815', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (18, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 11:05:27.129695', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (19, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 11:06:18.326433', '2017-10-12 11:07:01.361802');
INSERT INTO orders (id, user_id, closed_date, status, deleted, log_filename, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, created, modified) VALUES (20, 1, NULL, NULL, 0, 'teszt_log21', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2017-10-12 11:06:21.730328', '2017-10-12 11:07:01.361802');


--
-- TOC entry 2236 (class 0 OID 0)
-- Dependencies: 189
-- Name: orders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('orders_id_seq', 21, true);


--
-- TOC entry 2237 (class 0 OID 0)
-- Dependencies: 190
-- Name: orders_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('orders_user_id_seq', 1, false);


--
-- TOC entry 2215 (class 0 OID 50174)
-- Dependencies: 186
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (1, 'Luke''s Lightsaber', 'Fantastic price. Good ecosystem and controls. Helpful technical support.', 3, 5, 49.90, 'USD', 'lightsaber.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (2, 'Bud Spencer''s pan', 'Old tool from az old friend. Old tool from az old friend.', 3, 2, 20.00, 'USD', 'bud_pan.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (3, 'The last soap from Fight Club', 'Be clean. Be a fighter. Be a weapon.', 3, 13, 25.00, 'USD', 'soap.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (4, 'The Ring', 'One Ring to rule them all, One Ring to find them, One Ring to bring them all and in the darkness bind them.', 3, 6, 3000.00, 'USD', 'ring.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (5, 'Carbonite Han Solo', 'This cold piece of carbonite was Jabba''s favorite knick-knackery.', 3, 5, 500.00, 'USD', 'hansolo.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (6, 'Jockey Ewing''s whiskey glass', 'A glass cup from the famous oil tycoon.', 3, 1, 200.00, 'USD', 'whiskeycup.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (7, 'Chewbacca''s crossbow', 'Dependable weapon from a good guy.', 3, 5, 89.00, 'USD', 'chewbaccabow.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (8, 'Terminator''s endoskeleton', 'Your new personal coach and trainer', 3, 7, 2000.00, 'USD', 'terminator.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (9, 'AT-ST', 'Good old vehicle from the dark side.', 3, 5, 479.00, 'USD', 'atst.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (10, 'The Helmet of Darth Vader', 'Black hat from the dark side.', 3, 5, 500.00, 'USD', 'vaderhelmet.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (11, 'R2D2', 'Faithful personal assistant. Faithful personal assistant.', 3, 5, 400.00, 'USD', 'r2d2.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (12, 'Millennium Falcon', 'Good vehicle instead of a dull car.', 3, 5, 1500.00, 'USD', 'falcon.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (13, 'Titanic Wreck', 'A sad wreck in the deep sea. A sad wreck in the deep sea. A sad wreck in the deep sea.', 2, 12, 10000.00, 'USD', 'titanic.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (14, 'The Dagger of the Killers of Iulius Caesar', 'This weapon was used by Brutus too. This weapon was used by Brutus too.', 2, 11, 89.00, 'USD', 'dagger.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (15, 'Death Star', 'Your new flat. Your new flat. Your new flat. Your new flat.', 3, 5, 4000.00, 'USD', 'deathstar.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (16, 'The Holy Right', 'The blissful hand of King Saint Stephen. The blissful hand of King Saint Stephen.', 2, 10, 3000.00, 'USD', 'holyright.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (17, 'The Horse of Attila the Hun', 'Original and entire skeleton,  it has every bones.', 2, 10, 3000.00, 'USD', 'attila.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (18, 'The Holy Crown', 'Crown jewel from the ancient times of Hungary.', 2, 10, 3000.00, 'USD', 'crown.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (19, 'Elvis Jacket', 'Elvis went on the stage in this jacket many times.', 1, 3, 1230.00, 'USD', 'elvis.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (20, 'Donald Trump''s necktie', 'Trump wore this necktie when he discussed some problems with Kim Jong-un.', 1, 8, 90.00, 'USD', 'trump.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (21, 'Anita Gorbicz''s first ball', 'Gorbicz played her first handball match with this ball. She was only 6 years old.', 1, 9, 200.00, 'USD', 'gorbicz.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (22, 'Neymar''s shoes', 'Neymar scored 4 goals against Mozambique in this special sport equipment.', 1, 9, 170.00, 'USD', 'neymar.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (23, 'Mohamed Ali''s boxing gloves', 'Ali won against a lot of opponents in this gloves.', 1, 9, 800.00, 'USD', 'ali.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (24, 'Zsolt Erdei''s boxing gloves', 'Erdei won against a lot of opponents in this gloves.', 1, 9, 100.00, 'USD', 'erdei.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (25, 'Federer''s tennis racket', 'Federer won his last US Open with this racket.', 1, 9, 200.00, 'USD', 'federer.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (26, 'The piano of John Lennon', 'Lennon played Imagine on this instrument.', 1, 3, 500.00, 'USD', 'lennon.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (27, 'The shirt of Ferenc Puskas', 'Puskas scored 3 goals against England in this jersey.', 1, 9, 820.00, 'USD', 'puskas.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (28, 'Mona Lisa', 'Leonardo''s original painting, it''s a very exclusive offer.', 2, 4, 3000.00, 'USD', 'monalisa.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (29, 'Sebastian Vettel''s helmet', 'Vettel won 4 Formula-1 GP with this helmet.', 1, 9, 330.00, 'USD', 'vettel.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');
INSERT INTO products (id, name, description, category_id, supplier_id, price, currency, image_filename, created, modified) VALUES (30, 'Brian May''s Red Special', 'A guitar made by the famous member of Queen, he played in this instrument for example We Will Rock You.', 1, 3, 1000.00, 'USD', 'brianmay.jpg', '2017-10-09 20:34:45.333209', '2017-10-09 20:34:45.333209');


--
-- TOC entry 2238 (class 0 OID 0)
-- Dependencies: 185
-- Name: products_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('products_id_seq', 1, false);


--
-- TOC entry 2213 (class 0 OID 50164)
-- Dependencies: 184
-- Data for Name: suppliers; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO suppliers (id, name, description, created, modified) VALUES (1, 'CBS', ' Television series', '2017-10-09 20:01:13.424155', '2017-10-09 20:01:13.424155');
INSERT INTO suppliers (id, name, description, created, modified) VALUES (2, 'Columbia Pictures', ' Movie making', '2017-10-09 20:01:13.424155', '2017-10-09 20:01:13.424155');
INSERT INTO suppliers (id, name, description, created, modified) VALUES (3, 'EMI Music', ' CD and LP publishing etc.', '2017-10-09 20:01:13.424155', '2017-10-09 20:01:13.424155');
INSERT INTO suppliers (id, name, description, created, modified) VALUES (4, 'Louvre', ' Museum in Paris, France', '2017-10-09 20:01:13.424155', '2017-10-09 20:01:13.424155');
INSERT INTO suppliers (id, name, description, created, modified) VALUES (5, 'Lucasarts', ' Movie and dream making', '2017-10-09 20:01:13.424155', '2017-10-09 20:01:13.424155');
INSERT INTO suppliers (id, name, description, created, modified) VALUES (6, 'New Line Cinema', ' Movie making', '2017-10-09 20:01:13.424155', '2017-10-09 20:01:13.424155');
INSERT INTO suppliers (id, name, description, created, modified) VALUES (7, 'Orion Pictures', ' Movie making, change the future', '2017-10-09 20:01:13.424155', '2017-10-09 20:01:13.424155');
INSERT INTO suppliers (id, name, description, created, modified) VALUES (8, 'Pentagon', ' The headquarters of the US Department of Defense', '2017-10-09 20:01:13.424155', '2017-10-09 20:01:13.424155');
INSERT INTO suppliers (id, name, description, created, modified) VALUES (9, 'Sport Museum', ' Sport equipment', '2017-10-09 20:01:13.424155', '2017-10-09 20:01:13.424155');
INSERT INTO suppliers (id, name, description, created, modified) VALUES (10, 'State of Hungary', ' Our beautiful homeland', '2017-10-09 20:01:13.424155', '2017-10-09 20:01:13.424155');
INSERT INTO suppliers (id, name, description, created, modified) VALUES (11, 'Uffizi', ' Museum in Italy', '2017-10-09 20:01:13.424155', '2017-10-09 20:01:13.424155');
INSERT INTO suppliers (id, name, description, created, modified) VALUES (12, 'White Star Line', ' Ship building', '2017-10-09 20:01:13.424155', '2017-10-09 20:01:13.424155');
INSERT INTO suppliers (id, name, description, created, modified) VALUES (13, '20th Century Fox', ' Movie making', '2017-10-09 20:01:13.424155', '2017-10-09 20:01:13.424155');


--
-- TOC entry 2239 (class 0 OID 0)
-- Dependencies: 183
-- Name: suppliers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('suppliers_id_seq', 1, false);


--
-- TOC entry 2217 (class 0 OID 50198)
-- Dependencies: 188
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO users (id, name, phone_number, email, password, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, active, created, modified) VALUES (1, 'TESZT Elek', NULL, 'fnejkfnaek@fjewijf.com', 'fmkfneqjf', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, '2017-10-12 10:40:20.283828', '2017-10-12 10:40:20.283828');


--
-- TOC entry 2240 (class 0 OID 0)
-- Dependencies: 187
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('users_id_seq', 1, true);


--
-- TOC entry 2077 (class 2606 OID 50161)
-- Name: categories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


--
-- TOC entry 2085 (class 2606 OID 50226)
-- Name: orders_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


--
-- TOC entry 2081 (class 2606 OID 50185)
-- Name: products_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);


--
-- TOC entry 2079 (class 2606 OID 50171)
-- Name: suppliers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY suppliers
    ADD CONSTRAINT suppliers_pkey PRIMARY KEY (id);


--
-- TOC entry 2083 (class 2606 OID 50209)
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 2091 (class 2620 OID 50246)
-- Name: update_categories_modtime; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER update_categories_modtime BEFORE UPDATE ON categories FOR EACH ROW EXECUTE PROCEDURE update_modified_column();


--
-- TOC entry 2095 (class 2620 OID 50247)
-- Name: update_orders_modtime; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER update_orders_modtime BEFORE UPDATE ON orders FOR EACH ROW EXECUTE PROCEDURE update_modified_column();


--
-- TOC entry 2093 (class 2620 OID 50248)
-- Name: update_products_modtime; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER update_products_modtime BEFORE UPDATE ON products FOR EACH ROW EXECUTE PROCEDURE update_modified_column();


--
-- TOC entry 2092 (class 2620 OID 50249)
-- Name: update_suppliers_modtime; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER update_suppliers_modtime BEFORE UPDATE ON suppliers FOR EACH ROW EXECUTE PROCEDURE update_modified_column();


--
-- TOC entry 2094 (class 2620 OID 50250)
-- Name: update_users_modtime; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER update_users_modtime BEFORE UPDATE ON users FOR EACH ROW EXECUTE PROCEDURE update_modified_column();


--
-- TOC entry 2090 (class 2606 OID 50240)
-- Name: lineitems_order_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY lineitems
    ADD CONSTRAINT lineitems_order_id_fk FOREIGN KEY (order_id) REFERENCES orders(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 2089 (class 2606 OID 50235)
-- Name: lineitems_product_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY lineitems
    ADD CONSTRAINT lineitems_product_id_fk FOREIGN KEY (product_id) REFERENCES products(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 2088 (class 2606 OID 50227)
-- Name: orders_user_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_user_id_fk FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 2086 (class 2606 OID 50186)
-- Name: products_category_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY products
    ADD CONSTRAINT products_category_id_fk FOREIGN KEY (category_id) REFERENCES categories(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 2087 (class 2606 OID 50191)
-- Name: products_supplier_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY products
    ADD CONSTRAINT products_supplier_id_fk FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON UPDATE CASCADE ON DELETE RESTRICT;


-- Completed on 2017-10-12 11:15:19 CEST

--
-- PostgreSQL database dump complete
--

