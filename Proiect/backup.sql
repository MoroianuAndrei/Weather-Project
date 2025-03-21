--
-- PostgreSQL database dump
--

-- Dumped from database version 17.0
-- Dumped by pg_dump version 17.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: app_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_user (
    id integer NOT NULL,
    username character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    CONSTRAINT app_user_email_check CHECK (((email)::text ~* '^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$'::text))
);


ALTER TABLE public.app_user OWNER TO postgres;

--
-- Name: app_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.app_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.app_user_id_seq OWNER TO postgres;

--
-- Name: app_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.app_user_id_seq OWNED BY public.app_user.id;


--
-- Name: app_users_roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_users_roles (
    app_user_id integer NOT NULL,
    role_id integer NOT NULL
);


ALTER TABLE public.app_users_roles OWNER TO postgres;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.hibernate_sequence OWNER TO postgres;

--
-- Name: locations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.locations (
    id_loc integer NOT NULL,
    city character varying(255),
    latitude double precision,
    longitude double precision
);


ALTER TABLE public.locations OWNER TO postgres;

--
-- Name: locations_id_loc_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.locations_id_loc_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.locations_id_loc_seq OWNER TO postgres;

--
-- Name: locations_id_loc_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.locations_id_loc_seq OWNED BY public.locations.id_loc;


--
-- Name: role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.role (
    id integer NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE public.role OWNER TO postgres;

--
-- Name: role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.role_id_seq OWNER TO postgres;

--
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.role_id_seq OWNED BY public.role.id;


--
-- Name: user_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_sequence OWNER TO postgres;

--
-- Name: weather; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.weather (
    id_weat integer NOT NULL,
    id_loc integer,
    date date,
    temperature character varying(255),
    condition character varying(255)
);


ALTER TABLE public.weather OWNER TO postgres;

--
-- Name: weather_id_weat_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.weather_id_weat_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.weather_id_weat_seq OWNER TO postgres;

--
-- Name: weather_id_weat_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.weather_id_weat_seq OWNED BY public.weather.id_weat;


--
-- Name: app_user id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user ALTER COLUMN id SET DEFAULT nextval('public.app_user_id_seq'::regclass);


--
-- Name: locations id_loc; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.locations ALTER COLUMN id_loc SET DEFAULT nextval('public.locations_id_loc_seq'::regclass);


--
-- Name: role id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role ALTER COLUMN id SET DEFAULT nextval('public.role_id_seq'::regclass);


--
-- Name: weather id_weat; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.weather ALTER COLUMN id_weat SET DEFAULT nextval('public.weather_id_weat_seq'::regclass);


--
-- Data for Name: app_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app_user (id, username, email, password) FROM stdin;
1	ContNou	contnou@mail.com	ccc2819c8100bf66e9131e495664eb81e3fa416a51bd2482660f029f360efa31
2	regele	rege@mail.ro	167b2d23bfcadbc8679c6cbada28f22a75ae0cc07a6f11ed8ca112d1bfad88ee
3	Andrei	andrei@mail.ro	b9352494463399aa6a44ed5e39425b8a0bc39b2fc3d0184ba2583ce9bc4e4c1d
4	Andrei	andreimoro@mail.ro	cd4cf8891267a6a180f2912d26a57acae95afea57b1c6f031112449a95e0c302
10	Moro	moroianu@mail.ro	531992b27d92fca9a3a0ba449b82319b801e59d5d93eabb4ea6bf5d8b021833b
11	IncercareContNou	incercarecontnou@mail.ro	9bdd811da0cbaa13bca2c70910fcd6e5051a1e358a4c758a5c8cf68ea205849c
12	Haz	haz@mail.ro	f1e70f654e6cf6e2d6cd34875f063d344850b9ae7da6c15616ba1af8c49da91b
14	a	a@m.ro	ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb
\.


--
-- Data for Name: app_users_roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app_users_roles (app_user_id, role_id) FROM stdin;
4	1
3	2
2	1
1	1
10	1
11	1
12	1
14	1
\.


--
-- Data for Name: locations; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.locations (id_loc, city, latitude, longitude) FROM stdin;
1	Brasov	40.73	25.3
2	Braila	29.3	70.3
3	Bucharest	20.99	10.9
4	Predeal	50.27	27.5
5	Constanta	10	20
6	Cluj	82	79
8	Galati	5	10
10	Bacau	20.9	90.2
11	Piatra Neamt	20.89	80.99
12	Unga Bunga	10	10
13	Hatz	40	40
\.


--
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.role (id, name) FROM stdin;
1	USER
2	ADMIN
\.


--
-- Data for Name: weather; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.weather (id_weat, id_loc, date, temperature, condition) FROM stdin;
24	10	2025-01-11	17	Cloudy
22	10	2025-01-09	14	Snow
27	11	2025-01-11	15	Cloudy
26	11	2025-01-10	14	Cloudy
25	11	2025-01-09	12	Snow
23	10	2025-01-10	16	Snow
1	1	2025-01-09	20	Cloudy
2	1	2025-01-10	22	Cloudy
3	1	2025-01-11	22	Sunny
4	2	2025-01-09	25	Sunny
5	2	2025-01-10	24	Cloudy
6	2	2025-01-11	27	Rain
7	3	2025-01-09	30	Sunny
8	3	2025-01-10	32	Sunny
9	3	2025-01-11	35	Cloudy
10	4	2025-01-09	10	Snow
11	4	2025-01-10	12	Snow
12	4	2025-01-11	20	Cloudy
16	6	2025-01-09	20	Cloudy
17	6	2025-01-10	24	Sunny
18	6	2025-01-11	26	Sunny
15	5	2025-01-11	35	Sunny
14	5	2025-01-10	32	Sunny
13	5	2025-01-09	30	Cloudy
21	8	2025-01-11	24	Rain
20	8	2025-01-10	22	Cloudy
19	8	2025-01-09	20	Cloudy
\.


--
-- Name: app_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.app_user_id_seq', 14, true);


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.hibernate_sequence', 1, false);


--
-- Name: locations_id_loc_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.locations_id_loc_seq', 13, true);


--
-- Name: role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.role_id_seq', 5, true);


--
-- Name: user_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_sequence', 22, true);


--
-- Name: weather_id_weat_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.weather_id_weat_seq', 17, true);


--
-- Name: app_user app_user_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_email_key UNIQUE (email);


--
-- Name: app_user app_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_pkey PRIMARY KEY (id);


--
-- Name: app_users_roles app_users_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_users_roles
    ADD CONSTRAINT app_users_roles_pkey PRIMARY KEY (app_user_id, role_id);


--
-- Name: locations locations_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.locations
    ADD CONSTRAINT locations_pkey PRIMARY KEY (id_loc);


--
-- Name: role role_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_name_key UNIQUE (name);


--
-- Name: role role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- Name: weather weather_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.weather
    ADD CONSTRAINT weather_pkey PRIMARY KEY (id_weat);


--
-- Name: app_users_roles app_users_roles_app_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_users_roles
    ADD CONSTRAINT app_users_roles_app_user_id_fkey FOREIGN KEY (app_user_id) REFERENCES public.app_user(id) ON DELETE CASCADE;


--
-- Name: app_users_roles app_users_roles_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_users_roles
    ADD CONSTRAINT app_users_roles_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.role(id) ON DELETE CASCADE;


--
-- Name: weather weather_id_loc_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.weather
    ADD CONSTRAINT weather_id_loc_fkey FOREIGN KEY (id_loc) REFERENCES public.locations(id_loc);


--
-- PostgreSQL database dump complete
--

