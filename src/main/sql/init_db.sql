--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.8
-- Dumped by pg_dump version 9.5.8

-- Started on 2017-10-09 20:40:24 CEST

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
-- TOC entry 2220 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- TOC entry 563 (class 1247 OID 49619)
-- Name: order_status; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE order_status AS ENUM (
    'reviewed',
    'checked',
    'payed'
);


--
-- TOC entry 191 (class 1255 OID 49696)
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
-- TOC entry 185 (class 1259 OID 49642)
-- Name: categories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE categories (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    department character varying(100) NOT NULL,
    description character varying(300) NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 184 (class 1259 OID 49640)
-- Name: categories_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE categories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2221 (class 0 OID 0)
-- Dependencies: 184
-- Name: categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE categories_id_seq OWNED BY categories.id;


--
-- TOC entry 190 (class 1259 OID 49683)
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
-- TOC entry 183 (class 1259 OID 49625)
-- Name: orders; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE orders (
    user_id integer NOT NULL,
    id integer NOT NULL,
    billing_country character varying(100) NOT NULL,
    billing_city character varying(100) NOT NULL,
    billing_zip character varying(20) NOT NULL,
    billing_address character varying(255) NOT NULL,
    shipping_country character varying(100) NOT NULL,
    shipping_city character varying(100) NOT NULL,
    shipping_zip character varying(20) NOT NULL,
    shipping_address character varying(255) NOT NULL,
    status order_status,
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 189 (class 1259 OID 49665)
-- Name: products; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE products (
    id integer NOT NULL,
    category_id integer NOT NULL,
    supplier_id integer NOT NULL,
    price numeric(10,2) NOT NULL,
    currency character varying(3) NOT NULL,
    image_filename character varying(100),
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(300) NOT NULL
);


--
-- TOC entry 188 (class 1259 OID 49663)
-- Name: products_product_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE products_product_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2222 (class 0 OID 0)
-- Dependencies: 188
-- Name: products_product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE products_product_id_seq OWNED BY products.id;


--
-- TOC entry 187 (class 1259 OID 49655)
-- Name: suppliers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE suppliers (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(300) NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 186 (class 1259 OID 49653)
-- Name: suppliers_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE suppliers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2223 (class 0 OID 0)
-- Dependencies: 186
-- Name: suppliers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE suppliers_id_seq OWNED BY suppliers.id;


--
-- TOC entry 182 (class 1259 OID 49604)
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE users (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    phone_number character varying(50) NOT NULL,
    email character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    billing_country character varying(100) NOT NULL,
    billing_city character varying(100) NOT NULL,
    billing_zip character varying(20) NOT NULL,
    billing_address character varying(255) NOT NULL,
    shipping_country character varying(100) NOT NULL,
    shipping_city character varying(100) NOT NULL,
    shipping_zip character varying(20) NOT NULL,
    shipping_address character varying(255) NOT NULL,
    active integer DEFAULT 0,
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 181 (class 1259 OID 49602)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2224 (class 0 OID 0)
-- Dependencies: 181
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


--
-- TOC entry 2059 (class 2604 OID 49645)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY categories ALTER COLUMN id SET DEFAULT nextval('categories_id_seq'::regclass);


--
-- TOC entry 2065 (class 2604 OID 49668)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY products ALTER COLUMN id SET DEFAULT nextval('products_product_id_seq'::regclass);


--
-- TOC entry 2062 (class 2604 OID 49658)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY suppliers ALTER COLUMN id SET DEFAULT nextval('suppliers_id_seq'::regclass);


--
-- TOC entry 2053 (class 2604 OID 49607)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


--
-- TOC entry 2208 (class 0 OID 49642)
-- Dependencies: 185
-- Data for Name: categories; Type: TABLE DATA; Schema: public; Owner: -
--

COPY categories (id, name, department, description, created, modified) FROM stdin;
1	Famous Artifacts	Article	Tangible memories from popular musicians, athletes etc.	2017-10-09 19:37:59.356189	2017-10-09 19:37:59.356189
2	Historical	Article	Tangible memories from emperors, generals etc.	2017-10-09 19:37:59.356189	2017-10-09 19:37:59.356189
3	Movies	Entertainment	Vehicles, weapons and other famous objects from great movies.	2017-10-09 19:37:59.356189	2017-10-09 19:37:59.356189
\.


--
-- TOC entry 2225 (class 0 OID 0)
-- Dependencies: 184
-- Name: categories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('categories_id_seq', 3, true);


--
-- TOC entry 2213 (class 0 OID 49683)
-- Dependencies: 190
-- Data for Name: lineitems; Type: TABLE DATA; Schema: public; Owner: -
--

COPY lineitems (product_id, order_id, quantity, actual_price, currency) FROM stdin;
\.


--
-- TOC entry 2206 (class 0 OID 49625)
-- Dependencies: 183
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: -
--

COPY orders (user_id, id, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, status, created, modified) FROM stdin;
\.


--
-- TOC entry 2212 (class 0 OID 49665)
-- Dependencies: 189
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: -
--

COPY products (id, category_id, supplier_id, price, currency, image_filename, created, modified, name, description) FROM stdin;
1	3	5	49.90	USD	lightsaber.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Luke's Lightsaber	Fantastic price. Good ecosystem and controls. Helpful technical support.
2	3	2	20.00	USD	bud_pan.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Bud Spencer's pan	Old tool from az old friend. Old tool from az old friend.
3	3	13	25.00	USD	soap.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	The last soap from Fight Club	Be clean. Be a fighter. Be a weapon.
4	3	6	3000.00	USD	ring.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	The Ring	One Ring to rule them all, One Ring to find them, One Ring to bring them all and in the darkness bind them.
5	3	5	500.00	USD	hansolo.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Carbonite Han Solo	This cold piece of carbonite was Jabba's favorite knick-knackery.
6	3	1	200.00	USD	whiskeycup.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Jockey Ewing's whiskey glass	A glass cup from the famous oil tycoon.
7	3	5	89.00	USD	chewbaccabow.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Chewbacca's crossbow	Dependable weapon from a good guy.
8	3	7	2000.00	USD	terminator.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Terminator's endoskeleton	Your new personal coach and trainer
9	3	5	479.00	USD	atst.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	AT-ST	Good old vehicle from the dark side.
10	3	5	500.00	USD	vaderhelmet.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	The Helmet of Darth Vader	Black hat from the dark side.
11	3	5	400.00	USD	r2d2.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	R2D2	Faithful personal assistant. Faithful personal assistant.
12	3	5	1500.00	USD	falcon.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Millennium Falcon	Good vehicle instead of a dull car.
13	2	12	10000.00	USD	titanic.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Titanic Wreck	A sad wreck in the deep sea. A sad wreck in the deep sea. A sad wreck in the deep sea.
14	2	11	89.00	USD	dagger.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	The Dagger of the Killers of Iulius Caesar	This weapon was used by Brutus too. This weapon was used by Brutus too.
15	3	5	4000.00	USD	deathstar.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Death Star	Your new flat. Your new flat. Your new flat. Your new flat.
16	2	10	3000.00	USD	holyright.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	The Holy Right	The blissful hand of King Saint Stephen. The blissful hand of King Saint Stephen.
17	2	10	3000.00	USD	attila.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	The Horse of Attila the Hun	Original and entire skeleton,  it has every bones.
18	2	10	3000.00	USD	crown.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	The Holy Crown	Crown jewel from the ancient times of Hungary.
19	1	3	1230.00	USD	elvis.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Elvis Jacket	Elvis went on the stage in this jacket many times.
20	1	8	90.00	USD	trump.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Donald Trump's necktie	Trump wore this necktie when he discussed some problems with Kim Jong-un.
21	1	9	200.00	USD	gorbicz.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Anita Gorbicz's first ball	Gorbicz played her first handball match with this ball. She was only 6 years old.
22	1	9	170.00	USD	neymar.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Neymar's shoes	Neymar scored 4 goals against Mozambique in this special sport equipment.
23	1	9	800.00	USD	ali.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Mohamed Ali's boxing gloves	Ali won against a lot of opponents in this gloves.
24	1	9	100.00	USD	erdei.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Zsolt Erdei's boxing gloves	Erdei won against a lot of opponents in this gloves.
25	1	9	200.00	USD	federer.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Federer's tennis racket	Federer won his last US Open with this racket.
26	1	3	500.00	USD	lennon.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	The piano of John Lennon	Lennon played Imagine on this instrument.
27	1	9	820.00	USD	puskas.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	The shirt of Ferenc Puskas	Puskas scored 3 goals against England in this jersey.
28	2	4	3000.00	USD	monalisa.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Mona Lisa	Leonardo's original painting, it's a very exclusive offer.
29	1	9	330.00	USD	vettel.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Sebastian Vettel's helmet	Vettel won 4 Formula-1 GP with this helmet.
30	1	3	1000.00	USD	brianmay.jpg	2017-10-09 20:34:45.333209	2017-10-09 20:34:45.333209	Brian May's Red Special	A guitar made by the famous member of Queen, he played in this instrument for example We Will Rock You.
\.


--
-- TOC entry 2226 (class 0 OID 0)
-- Dependencies: 188
-- Name: products_product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('products_product_id_seq', 30, true);


--
-- TOC entry 2210 (class 0 OID 49655)
-- Dependencies: 187
-- Data for Name: suppliers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY suppliers (id, name, description, created, modified) FROM stdin;
1	CBS	 Television series	2017-10-09 20:01:13.424155	2017-10-09 20:01:13.424155
2	Columbia Pictures	 Movie making	2017-10-09 20:01:13.424155	2017-10-09 20:01:13.424155
3	EMI Music	 CD and LP publishing etc.	2017-10-09 20:01:13.424155	2017-10-09 20:01:13.424155
4	Louvre	 Museum in Paris, France	2017-10-09 20:01:13.424155	2017-10-09 20:01:13.424155
5	Lucasarts	 Movie and dream making	2017-10-09 20:01:13.424155	2017-10-09 20:01:13.424155
6	New Line Cinema	 Movie making	2017-10-09 20:01:13.424155	2017-10-09 20:01:13.424155
7	Orion Pictures	 Movie making, change the future	2017-10-09 20:01:13.424155	2017-10-09 20:01:13.424155
8	Pentagon	 The headquarters of the US Department of Defense	2017-10-09 20:01:13.424155	2017-10-09 20:01:13.424155
9	Sport Museum	 Sport equipment	2017-10-09 20:01:13.424155	2017-10-09 20:01:13.424155
10	State of Hungary	 Our beautiful homeland	2017-10-09 20:01:13.424155	2017-10-09 20:01:13.424155
11	Uffizi	 Museum in Italy	2017-10-09 20:01:13.424155	2017-10-09 20:01:13.424155
12	White Star Line	 Ship building	2017-10-09 20:01:13.424155	2017-10-09 20:01:13.424155
13	20th Century Fox	 Movie making	2017-10-09 20:01:13.424155	2017-10-09 20:01:13.424155
\.


--
-- TOC entry 2227 (class 0 OID 0)
-- Dependencies: 186
-- Name: suppliers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('suppliers_id_seq', 13, true);


--
-- TOC entry 2205 (class 0 OID 49604)
-- Dependencies: 182
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY users (id, name, phone_number, email, password, billing_country, billing_city, billing_zip, billing_address, shipping_country, shipping_city, shipping_zip, shipping_address, active, created, modified) FROM stdin;
\.


--
-- TOC entry 2228 (class 0 OID 0)
-- Dependencies: 181
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('users_id_seq', 1, false);


--
-- TOC entry 2075 (class 2606 OID 49652)
-- Name: categories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


--
-- TOC entry 2073 (class 2606 OID 49634)
-- Name: orders_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


--
-- TOC entry 2079 (class 2606 OID 49672)
-- Name: products_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);


--
-- TOC entry 2077 (class 2606 OID 49662)
-- Name: suppliers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY suppliers
    ADD CONSTRAINT suppliers_pkey PRIMARY KEY (id);


--
-- TOC entry 2069 (class 2606 OID 49617)
-- Name: users_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- TOC entry 2071 (class 2606 OID 49615)
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 2087 (class 2620 OID 49699)
-- Name: update_categories_modtime; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER update_categories_modtime BEFORE UPDATE ON categories FOR EACH ROW EXECUTE PROCEDURE update_modified_column();


--
-- TOC entry 2086 (class 2620 OID 49701)
-- Name: update_orders_modtime; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER update_orders_modtime BEFORE UPDATE ON orders FOR EACH ROW EXECUTE PROCEDURE update_modified_column();


--
-- TOC entry 2089 (class 2620 OID 49700)
-- Name: update_products_modtime; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER update_products_modtime BEFORE UPDATE ON products FOR EACH ROW EXECUTE PROCEDURE update_modified_column();


--
-- TOC entry 2088 (class 2620 OID 49697)
-- Name: update_suppliers_modtime; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER update_suppliers_modtime BEFORE UPDATE ON suppliers FOR EACH ROW EXECUTE PROCEDURE update_modified_column();


--
-- TOC entry 2085 (class 2620 OID 49698)
-- Name: update_users_modtime; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER update_users_modtime BEFORE UPDATE ON users FOR EACH ROW EXECUTE PROCEDURE update_modified_column();


--
-- TOC entry 2081 (class 2606 OID 49673)
-- Name: category_id_products_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY products
    ADD CONSTRAINT category_id_products_fk FOREIGN KEY (category_id) REFERENCES categories(id);


--
-- TOC entry 2083 (class 2606 OID 49686)
-- Name: order_id_lineitems_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY lineitems
    ADD CONSTRAINT order_id_lineitems_fk FOREIGN KEY (order_id) REFERENCES orders(id);


--
-- TOC entry 2084 (class 2606 OID 49691)
-- Name: product_id_products_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY lineitems
    ADD CONSTRAINT product_id_products_fk FOREIGN KEY (product_id) REFERENCES products(id);


--
-- TOC entry 2082 (class 2606 OID 49678)
-- Name: supplier_id_products_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY products
    ADD CONSTRAINT supplier_id_products_fk FOREIGN KEY (supplier_id) REFERENCES suppliers(id);


--
-- TOC entry 2080 (class 2606 OID 49635)
-- Name: user_id_orders_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT user_id_orders_fk FOREIGN KEY (user_id) REFERENCES users(id);


-- Completed on 2017-10-09 20:40:25 CEST

--
-- PostgreSQL database dump complete
--

