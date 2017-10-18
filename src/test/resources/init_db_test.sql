--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.8
-- Dumped by pg_dump version 9.5.8

-- Started on 2017-10-13 00:58:00 CEST

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
    user_id integer,
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
    modified timestamp without time zone DEFAULT now() NOT NULL,
    billing_name character varying(255),
    billing_phone character varying(50),
    billing_email character varying(255)
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
    category_id integer,
    supplier_id integer,
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

--
-- TOC entry 2235 (class 0 OID 0)
-- Dependencies: 181
-- Name: categories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--



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



--
-- TOC entry 2236 (class 0 OID 0)
-- Dependencies: 189
-- Name: orders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--



--
-- TOC entry 2237 (class 0 OID 0)
-- Dependencies: 190
-- Name: orders_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--



--
-- TOC entry 2215 (class 0 OID 50174)
-- Dependencies: 186
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: -
--

--
-- TOC entry 2238 (class 0 OID 0)
-- Dependencies: 185
-- Name: products_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--



--
-- TOC entry 2213 (class 0 OID 50164)
-- Dependencies: 184
-- Data for Name: suppliers; Type: TABLE DATA; Schema: public; Owner: -
--

--
-- TOC entry 2239 (class 0 OID 0)
-- Dependencies: 183
-- Name: suppliers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--



--
-- TOC entry 2217 (class 0 OID 50198)
-- Dependencies: 188
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2240 (class 0 OID 0)
-- Dependencies: 187
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--



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
    ADD CONSTRAINT lineitems_order_id_fk FOREIGN KEY (order_id) REFERENCES orders(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2089 (class 2606 OID 50235)
-- Name: lineitems_product_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY lineitems
    ADD CONSTRAINT lineitems_product_id_fk FOREIGN KEY (product_id) REFERENCES products(id) ON UPDATE CASCADE ON DELETE CASCADE ;


--
-- TOC entry 2088 (class 2606 OID 50227)
-- Name: orders_user_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_user_id_fk FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE SET NULL ;


--
-- TOC entry 2086 (class 2606 OID 50186)
-- Name: products_category_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY products
    ADD CONSTRAINT products_category_id_fk FOREIGN KEY (category_id) REFERENCES categories(id) ON UPDATE CASCADE ON DELETE SET NULL ;


--
-- TOC entry 2087 (class 2606 OID 50191)
-- Name: products_supplier_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY products
    ADD CONSTRAINT products_supplier_id_fk FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON UPDATE CASCADE ON DELETE SET NULL ;


-- Completed on 2017-10-13 00:58:01 CEST

--
-- PostgreSQL database dump complete
--

