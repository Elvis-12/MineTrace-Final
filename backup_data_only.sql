--
-- PostgreSQL database dump
--

-- Dumped from database version 17.2
-- Dumped by pg_dump version 17.2

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

--
-- Data for Name: audit_logs; Type: TABLE DATA; Schema: public; Owner: postgres
--

SET SESSION AUTHORIZATION DEFAULT;

ALTER TABLE public.audit_logs DISABLE TRIGGER ALL;

COPY public.audit_logs (id, "timestamp", details, action, entity_id, entity_type, ip_address, performed_by, user_email, user_name) FROM stdin;
1	2026-04-06 21:35:59.164683	User logged in	USER_LOGIN	1	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
2	2026-04-06 21:36:18.190029	User logged in	USER_LOGIN	1	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
3	2026-04-06 21:36:18.704716	Organization created: Test	ORGANIZATION_CREATED	2	Organization	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
4	2026-04-06 21:36:19.091083	Mine registered: Mine A	MINE_CREATED	1	Mine	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
5	2026-04-06 21:36:19.679521	Batch created: MT-2026-001	BATCH_CREATED	1	MineralBatch	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
6	2026-04-06 21:36:30.873286	User logged in	USER_LOGIN	1	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
7	2026-04-06 22:07:47.552727	User logged in	USER_LOGIN	1	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
8	2026-04-06 22:07:48.480853	Organization created: Del Test Org	ORGANIZATION_CREATED	3	Organization	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
9	2026-04-06 22:07:49.095345	Mine registered: Del Mine	MINE_CREATED	2	Mine	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
10	2026-04-06 22:07:49.675318	Batch created: MT-2026-002	BATCH_CREATED	2	MineralBatch	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
11	2026-04-06 22:07:51.590633	New user registered: del@test.com	USER_CREATED	2	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
12	2026-04-06 22:09:19.704524	User logged in	USER_LOGIN	1	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
13	2026-04-07 09:12:09.042107	User logged in	USER_LOGIN	1	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
14	2026-04-07 09:12:09.645497	Organization created: DelTestOrg	ORGANIZATION_CREATED	4	Organization	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
15	2026-04-07 09:12:09.900089	Mine registered: DelMine	MINE_CREATED	3	Mine	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
16	2026-04-07 09:12:10.22232	Batch created: MT-2026-003	BATCH_CREATED	3	MineralBatch	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
17	2026-04-07 09:12:10.824428	New user registered: deluser1775545930@test.com	USER_CREATED	3	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
18	2026-04-07 09:12:11.069534	Batch deleted	BATCH_DELETED	3	MineralBatch	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
19	2026-04-07 09:12:11.207846	Mine deleted	MINE_DELETED	3	Mine	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
20	2026-04-07 09:12:11.381799	Organization deleted	ORGANIZATION_DELETED	4	Organization	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
21	2026-04-07 09:12:11.877862	User permanently deleted	USER_DELETED	3	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
22	2026-04-07 10:46:10.416264	User logged in	USER_LOGIN	1	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
23	2026-04-07 10:52:12.171345	User logged in	USER_LOGIN	1	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
24	2026-04-07 10:52:47.016025	Batch deleted	BATCH_DELETED	1	MineralBatch	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
25	2026-04-07 10:52:52.149591	Batch deleted	BATCH_DELETED	2	MineralBatch	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
26	2026-04-07 10:54:40.085582	Mine active status toggled	MINE_STATUS_TOGGLED	1	Mine	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
27	2026-04-07 10:54:44.783887	Mine active status toggled	MINE_STATUS_TOGGLED	1	Mine	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
28	2026-04-07 12:54:45.091696	Batch created: MT-2026-001	BATCH_CREATED	4	MineralBatch	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
29	2026-04-07 22:05:13.133987	User logged in	USER_LOGIN	1	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
30	2026-04-07 22:06:01.923687	Batch updated: MT-2026-001	BATCH_UPDATED	4	MineralBatch	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
31	2026-04-07 22:07:23.102746	Movement DISPATCH recorded for batch MT-2026-001	MOVEMENT_RECORDED	1	Movement	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
32	2026-04-07 22:08:15.642039	Fraud analysis run on batch 4	BATCH_ANALYZED	4	MineralBatch	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
33	2026-04-07 22:09:23.002891	Verification at QR Scan Checkpoint - passed: true	VERIFICATION_RECORDED	1	Verification	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
34	2026-04-09 08:22:42.476786	User logged in	USER_LOGIN	1	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
35	2026-04-09 08:29:38.93851	Batch created: MT-2026-002	BATCH_CREATED	5	MineralBatch	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
36	2026-04-09 08:30:18.580766	Batch created: MT-2026-003	BATCH_CREATED	6	MineralBatch	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
37	2026-04-09 08:30:34.133037	Batch created: MT-2026-004	BATCH_CREATED	7	MineralBatch	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
38	2026-04-09 08:30:50.794075	Batch created: MT-2026-005	BATCH_CREATED	8	MineralBatch	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
39	2026-04-09 08:32:41.529901	Movement RECEIVE recorded for batch MT-2026-002	MOVEMENT_RECORDED	2	Movement	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
40	2026-04-09 08:33:30.206241	Movement STORAGE recorded for batch MT-2026-003	MOVEMENT_RECORDED	3	Movement	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
41	2026-04-09 08:34:46.280889	Movement SALE recorded for batch MT-2026-004	MOVEMENT_RECORDED	4	Movement	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
42	2026-04-09 08:35:41.341146	Movement DISPATCH recorded for batch MT-2026-005	MOVEMENT_RECORDED	5	Movement	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
43	2026-04-09 08:35:58.537147	Isolation Forest model training triggered	ML_MODEL_TRAIN	\N	FraudDetection	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
44	2026-04-09 08:36:10.022244	System-wide fraud analysis run on 5 batches	FRAUD_ANALYSIS_ALL	\N	MineralBatch	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
45	2026-04-09 15:03:50.920688	New user registered: mineofficer@minetrace.com	USER_CREATED	4	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
46	2026-04-09 15:04:35.102496	New user registered: inspector@minetrace.com	USER_CREATED	5	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
47	2026-04-09 15:05:21.398366	New user registered: supplyofficer@minetrace.com	USER_CREATED	6	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
48	2026-04-09 15:05:36.775125	User updated: mineofficer@minetrace.com	USER_UPDATED	4	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
49	2026-04-09 15:05:46.092102	User deactivated	USER_DEACTIVATED	2	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
50	2026-04-09 15:05:52.632054	User permanently deleted	USER_DELETED	2	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
51	2026-04-09 15:06:13.449485	User logged in	USER_LOGIN	4	User	0:0:0:0:0:0:0:1	Melo	mineofficer@minetrace.com	Melo
52	2026-04-09 15:07:09.837133	User logged in	USER_LOGIN	5	User	0:0:0:0:0:0:0:1	Hoodie	inspector@minetrace.com	Hoodie
53	2026-04-09 15:07:52.268308	User logged in	USER_LOGIN	6	User	0:0:0:0:0:0:0:1	Kemo	supplyofficer@minetrace.com	Kemo
54	2026-04-09 15:08:57.844023	Movement SALE recorded for batch MT-2026-001	MOVEMENT_RECORDED	6	Movement	0:0:0:0:0:0:0:1	Kemo	supplyofficer@minetrace.com	Kemo
55	2026-04-09 15:10:00.671767	Password changed	PASSWORD_CHANGED	\N	User	0:0:0:0:0:0:0:1	Kemo	supplyofficer@minetrace.com	Kemo
56	2026-04-09 15:10:25.397568	User logged in	USER_LOGIN	6	User	0:0:0:0:0:0:0:1	Kemo	supplyofficer@minetrace.com	Kemo
57	2026-04-09 15:10:56.890078	User logged in	USER_LOGIN	1	User	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
58	2026-04-09 15:14:42.694421	Fraud analysis run on batch 5	BATCH_ANALYZED	5	MineralBatch	0:0:0:0:0:0:0:1	System Admin	admin@minetrace.com	System Admin
59	2026-04-09 15:18:31.841755	User logged in	USER_LOGIN	1	User	192.168.1.72	System Admin	admin@minetrace.com	System Admin
60	2026-04-09 15:19:12.979001	Batch created: MT-2026-006	BATCH_CREATED	9	MineralBatch	192.168.1.72	System Admin	admin@minetrace.com	System Admin
61	2026-04-09 15:19:43.97377	User logged in	USER_LOGIN	5	User	192.168.1.72	Hoodie	inspector@minetrace.com	Hoodie
62	2026-04-09 15:27:00.107512	Fraud analysis run on batch 7	BATCH_ANALYZED	7	MineralBatch	192.168.1.73	System Admin	admin@minetrace.com	System Admin
63	2026-04-09 15:30:21.913269	Batch updated: MT-2026-001	BATCH_UPDATED	4	MineralBatch	192.168.1.73	System Admin	admin@minetrace.com	System Admin
64	2026-04-09 15:31:00.338697	Batch created: MT-2026-007	BATCH_CREATED	10	MineralBatch	192.168.1.73	System Admin	admin@minetrace.com	System Admin
65	2026-04-09 15:50:24.311422	Verification at QR Scan Checkpoint - passed: true	VERIFICATION_RECORDED	2	Verification	192.168.1.72	Hoodie	inspector@minetrace.com	Hoodie
66	2026-04-09 15:50:24.311422	Verification at QR Scan Checkpoint - passed: true	VERIFICATION_RECORDED	3	Verification	192.168.1.72	Hoodie	inspector@minetrace.com	Hoodie
67	2026-04-11 17:53:49.441463	User logged in	USER_LOGIN	1	User	192.168.1.73	System Admin	admin@minetrace.com	System Admin
68	2026-04-11 19:38:15.375271	Batch created: MT-2026-008	BATCH_CREATED	11	MineralBatch	192.168.1.73	System Admin	admin@minetrace.com	System Admin
69	2026-04-11 19:41:00.107535	Movement DISPATCH recorded for batch MT-2026-008	MOVEMENT_RECORDED	7	Movement	192.168.1.73	System Admin	admin@minetrace.com	System Admin
70	2026-04-11 19:41:53.499173	Movement DISPATCH recorded for batch MT-2026-008	MOVEMENT_RECORDED	8	Movement	192.168.1.73	System Admin	admin@minetrace.com	System Admin
71	2026-04-11 19:42:23.146548	Movement TRANSFER recorded for batch MT-2026-008	MOVEMENT_RECORDED	9	Movement	192.168.1.73	System Admin	admin@minetrace.com	System Admin
72	2026-04-11 19:43:01.99172	Movement STORAGE recorded for batch MT-2026-008	MOVEMENT_RECORDED	10	Movement	192.168.1.73	System Admin	admin@minetrace.com	System Admin
73	2026-04-11 19:43:35.554088	Movement RECEIVE recorded for batch MT-2026-008	MOVEMENT_RECORDED	11	Movement	192.168.1.73	System Admin	admin@minetrace.com	System Admin
74	2026-04-11 19:44:27.211756	Movement DISPATCH recorded for batch MT-2026-008	MOVEMENT_RECORDED	12	Movement	192.168.1.73	System Admin	admin@minetrace.com	System Admin
75	2026-04-11 19:44:48.731689	Fraud analysis run on batch 11	BATCH_ANALYZED	11	MineralBatch	192.168.1.73	System Admin	admin@minetrace.com	System Admin
76	2026-04-11 19:53:53.305756	Isolation Forest model training triggered	ML_MODEL_TRAIN	\N	FraudDetection	192.168.1.73	System Admin	admin@minetrace.com	System Admin
77	2026-04-11 19:53:54.43886	System-wide fraud analysis run on 8 batches	FRAUD_ANALYSIS_ALL	\N	MineralBatch	192.168.1.73	System Admin	admin@minetrace.com	System Admin
78	2026-04-11 19:54:13.37209	Fraud analysis run on batch 11	BATCH_ANALYZED	11	MineralBatch	192.168.1.73	System Admin	admin@minetrace.com	System Admin
79	2026-04-11 19:57:57.624555	Fraud analysis run on batch 11	BATCH_ANALYZED	11	MineralBatch	192.168.1.73	System Admin	admin@minetrace.com	System Admin
80	2026-04-11 20:07:13.624105	Fraud analysis run on batch 11	BATCH_ANALYZED	11	MineralBatch	192.168.1.73	System Admin	admin@minetrace.com	System Admin
81	2026-04-11 20:28:19.042207	Isolation Forest model training triggered	ML_MODEL_TRAIN	\N	FraudDetection	192.168.1.73	System Admin	admin@minetrace.com	System Admin
82	2026-04-11 20:28:26.249189	Isolation Forest model training triggered	ML_MODEL_TRAIN	\N	FraudDetection	192.168.1.73	System Admin	admin@minetrace.com	System Admin
83	2026-04-11 20:33:10.122962	Isolation Forest model training triggered	ML_MODEL_TRAIN	\N	FraudDetection	192.168.1.73	System Admin	admin@minetrace.com	System Admin
84	2026-04-11 20:33:11.355489	System-wide fraud analysis run on 8 batches	FRAUD_ANALYSIS_ALL	\N	MineralBatch	192.168.1.73	System Admin	admin@minetrace.com	System Admin
85	2026-04-11 20:36:03.619392	User logged in	USER_LOGIN	5	User	192.168.1.72	Hoodie	inspector@minetrace.com	Hoodie
86	2026-04-11 20:36:04.023886	Verification at QR Scan Checkpoint - passed: false	VERIFICATION_RECORDED	4	Verification	192.168.1.72	Hoodie	inspector@minetrace.com	Hoodie
87	2026-04-11 20:36:04.066738	Verification at QR Scan Checkpoint - passed: false	VERIFICATION_RECORDED	5	Verification	192.168.1.72	Hoodie	inspector@minetrace.com	Hoodie
88	2026-04-11 20:50:19.527223	Batch created: MT-2026-009	BATCH_CREATED	12	MineralBatch	192.168.1.73	System Admin	admin@minetrace.com	System Admin
89	2026-04-11 20:59:16.241409	Fraud analysis run on batch 6	BATCH_ANALYZED	6	MineralBatch	192.168.1.73	System Admin	admin@minetrace.com	System Admin
90	2026-04-19 19:03:21.016938	User logged in	USER_LOGIN	1	User	192.168.1.66	System Admin	admin@minetrace.com	System Admin
91	2026-04-19 19:21:02.888772	Mine registered: Testing mine	MINE_CREATED	4	Mine	192.168.1.66	System Admin	admin@minetrace.com	System Admin
92	2026-04-19 19:21:26.029743	Mine updated: Mine A	MINE_UPDATED	1	Mine	192.168.1.66	System Admin	admin@minetrace.com	System Admin
93	2026-04-19 19:26:41.439511	Batch created: MT-2026-010	BATCH_CREATED	13	MineralBatch	192.168.1.66	System Admin	admin@minetrace.com	System Admin
94	2026-04-19 19:33:19.702231	User logged in	USER_LOGIN	5	User	192.168.1.66	Hoodie	inspector@minetrace.com	Hoodie
95	2026-04-19 19:33:49.756704	Inspector approved batch: approved	BATCH_APPROVED	11	MineralBatch	192.168.1.66	Hoodie	inspector@minetrace.com	Hoodie
96	2026-04-20 22:29:01.193237	User logged in	USER_LOGIN	1	User	192.168.1.66	System Admin	admin@minetrace.com	System Admin
97	2026-04-20 22:30:19.533296	Fraud analysis run on batch 6	BATCH_ANALYZED	6	MineralBatch	192.168.1.66	System Admin	admin@minetrace.com	System Admin
98	2026-04-20 22:30:21.759844	Fraud analysis run on batch 6	BATCH_ANALYZED	6	MineralBatch	192.168.1.66	System Admin	admin@minetrace.com	System Admin
\.


ALTER TABLE public.audit_logs ENABLE TRIGGER ALL;

--
-- Data for Name: organizations; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.organizations DISABLE TRIGGER ALL;

COPY public.organizations (created_at, id, address, email, name, phone) FROM stdin;
2026-04-06 21:24:50.780597	1	Kigali, Rwanda	info@minetrace.gov	MineTrace Authority	+250 788 000 000
2026-04-06 21:36:18.685658	2	\N	\N	Test	\N
2026-04-06 22:07:48.333548	3	\N	\N	Del Test Org	\N
\.


ALTER TABLE public.organizations ENABLE TRIGGER ALL;

--
-- Data for Name: mines; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.mines DISABLE TRIGGER ALL;

COPY public.mines (active, created_at, id, organization_id, district, license_number, location, name, province) FROM stdin;
t	2026-04-06 22:07:49.061214	2	3	\N	\N	X	Del Mine	\N
t	2026-04-19 19:21:02.865999	4	1	one	L12	Testing	Testing mine	Test
t	2026-04-06 21:36:19.054134	1	2	kicukiro	L13	Kigali	Mine A	Kigali
\.


ALTER TABLE public.mines ENABLE TRIGGER ALL;

--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.users DISABLE TRIGGER ALL;

COPY public.users (failed_login_attempts, created_at, id, organization_id, email, full_name, password_hash, role, status) FROM stdin;
0	2026-04-06 21:24:50.827476	1	1	admin@minetrace.com	System Admin	$2a$10$/k9dnNZQP4c1ZMGu1f9btuWVIyZwbLLRtlhd6esbNAj9pdJ520XvC	ADMIN	ACTIVE
0	2026-04-09 15:04:34.714694	5	2	inspector@minetrace.com	Hoodie	$2a$10$OcBem.AKz32X4KFeSEhV/erwFLfcv57mTYIJYoGF5bWMIDbobme96	INSPECTOR	ACTIVE
0	2026-04-09 15:03:50.228478	4	1	mineofficer@minetrace.com	Melo	$2a$10$OR7mEXLKUGTezp7NTnn4xe1kjlTGyd82nTY0AobbxsS2yEZb3Oj7G	MINE_OFFICER	ACTIVE
0	2026-04-09 15:05:21.144226	6	1	supplyofficer@minetrace.com	Kemo	$2a$10$3OEVac6ZnIoaw5hHV0Yct.U4Wbo3bggJ6dc5SRV7AHmhiIGukN0iS	SUPPLY_OFFICER	ACTIVE
\.


ALTER TABLE public.users ENABLE TRIGGER ALL;

--
-- Data for Name: batches; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.batches DISABLE TRIGGER ALL;

COPY public.batches (anomaly_score, duplicate, handover, initial_weight, license, route, weight, created_at, created_by, extraction_date, id, mine_id, batch_code, mineral_type, override_note, risk_level, status, duplicate_code, future_extraction, weight_loss, inspected_at, inspector_approved, inspector_note, inspected_by) FROM stdin;
0.4421	f	f	34	f	f	f	2026-04-09 08:30:18.564037	1	2026-04-09 00:00:00	6	2	MT-2026-003	Wolframite	\N	MEDIUM	IN_STORAGE	f	f	f	\N	\N	\N	\N
0.3695	f	f	23	f	f	f	2026-04-09 08:30:50.794075	1	2026-04-08 00:00:00	8	2	MT-2026-005	Tin	\N	MEDIUM	IN_TRANSIT	f	f	f	\N	\N	\N	\N
0.28	f	f	12	f	f	f	2026-04-09 08:29:38.905041	1	2026-04-09 00:00:00	5	1	MT-2026-002	Cassiterite	\N	LOW	REGISTERED	f	f	f	\N	\N	\N	\N
0.3032	f	f	78	f	f	f	2026-04-09 15:19:12.967809	1	2026-04-09 00:00:00	9	1	MT-2026-006	Gold	\N	LOW	REGISTERED	f	f	f	\N	\N	\N	\N
0.2805	f	f	45	f	f	f	2026-04-09 08:30:34.133037	1	2026-04-09 00:00:00	7	1	MT-2026-004	Gold	\N	LOW	SOLD	f	f	f	\N	\N	\N	\N
0.4438	f	f	13	f	f	f	2026-04-07 12:54:44.932508	1	2026-04-07 00:00:00	4	2	MT-2026-001	Coltan	\N	MEDIUM	SOLD	f	f	f	\N	\N	\N	\N
0.3445	f	f	344	f	f	f	2026-04-09 15:31:00.324798	1	2026-04-09 00:00:00	10	2	MT-2026-007	Cassiterite	\N	LOW	REGISTERED	f	f	f	\N	\N	\N	\N
0	f	f	100	f	f	f	2026-04-11 20:50:19.306011	1	2026-04-11 00:00:00	12	1	MT-2026-009	Coltan	\N	UNKNOWN	REGISTERED	f	f	f	\N	\N	\N	\N
0	f	f	890	f	f	f	2026-04-19 19:26:41.422644	1	2026-04-19 00:00:00	13	4	MT-2026-010	Lithium	\N	UNKNOWN	REGISTERED	f	f	f	\N	\N	\N	\N
0.6311	t	f	100000	f	f	t	2026-04-11 19:38:15.0425	1	2026-04-17 00:00:00	11	2	MT-2026-008	Lithium	\N	HIGH	FLAGGED	f	t	t	2026-04-19 19:33:49.631291	t	approved	5
\.


ALTER TABLE public.batches ENABLE TRIGGER ALL;

--
-- Data for Name: mineral_batches; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.mineral_batches DISABLE TRIGGER ALL;

COPY public.mineral_batches (id, batch_number, created_at, mineral_type, qr_code_path, quantity, status, mine_id) FROM stdin;
\.


ALTER TABLE public.mineral_batches ENABLE TRIGGER ALL;

--
-- Data for Name: fraud_analyses; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.fraud_analyses DISABLE TRIGGER ALL;

COPY public.fraud_analyses (id, analyzed_at, findings, risk_level, batch_id) FROM stdin;
\.


ALTER TABLE public.fraud_analyses ENABLE TRIGGER ALL;

--
-- Data for Name: movement_events; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.movement_events DISABLE TRIGGER ALL;

COPY public.movement_events (id, event_time, from_location, to_location, type, batch_id) FROM stdin;
\.


ALTER TABLE public.movement_events ENABLE TRIGGER ALL;

--
-- Data for Name: movements; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.movements DISABLE TRIGGER ALL;

COPY public.movements (weight, batch_id, id, "timestamp", driver_name, event_type, from_location, notes, recorded_by, to_location, vehicle) FROM stdin;
23	4	1	2026-04-07 22:07:22.888047	Kalisa	DISPATCH	Kicukiro	easy	System Admin	Musanze	ABC-123
34	5	2	2026-04-09 08:32:41.498413	Naz	RECEIVE	kigali	jj	System Admin	ruhango	qwe
34	6	3	2026-04-09 08:33:30.147352	Laro	STORAGE	ndaru	df	System Admin	bumbogo	jcx
43	7	4	2026-04-09 08:34:46.263031	Raf	SALE	Kigali	xhc	System Admin	Nairobi	er
23	8	5	2026-04-09 08:35:41.326724	Manzi	DISPATCH	Gisenyi	kj	System Admin	Gahanga	jd
34	4	6	2026-04-09 15:08:57.7573	wele	SALE	Kigali	done	Kemo	new york	ABC-123
2000	11	7	2026-04-11 19:40:59.757802		DISPATCH	Warehouse A	nsd	System Admin	Warehouse B	
400	11	8	2026-04-11 19:41:53.465436	Tom	DISPATCH	mine site A	js	System Admin	mine site B	ASD-12
10	11	9	2026-04-11 19:42:23.102815	js	TRANSFER	kigali	sd	System Admin	byumba	jsd
19	11	10	2026-04-11 19:43:01.909028	js	STORAGE	Jali	nx	System Admin	Doma	asj
1290	11	11	2026-04-11 19:43:35.455285	mnx	RECEIVE	kaj	mxn	System Admin	nx	ks
2000	11	12	2026-04-11 19:44:27.14496		DISPATCH	Warehouse A		System Admin	Warehouse B	
\.


ALTER TABLE public.movements ENABLE TRIGGER ALL;

--
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.notifications DISABLE TRIGGER ALL;

COPY public.notifications (read, id, "timestamp", user_id, message, related_entity_id, related_entity_type, title, type) FROM stdin;
f	1	2026-04-06 21:36:19.68856	1	Batch MT-2026-001 (Gold) has been registered.	1	MineralBatch	New Batch Registered	SUCCESS
f	2	2026-04-06 22:07:49.680332	1	Batch MT-2026-002 (Gold) has been registered.	2	MineralBatch	New Batch Registered	SUCCESS
f	3	2026-04-07 09:12:10.258686	1	Batch MT-2026-003 (Gold) has been registered.	3	MineralBatch	New Batch Registered	SUCCESS
f	5	2026-04-07 12:54:45.13022	1	Batch MT-2026-001 (Coltan) has been registered.	4	MineralBatch	New Batch Registered	SUCCESS
f	7	2026-04-07 22:07:23.165881	1	Batch MT-2026-001 dispatched from Kicukiro to Musanze	1	Movement	Batch Dispatched	BATCH_DISPATCHED
f	9	2026-04-09 08:29:38.956804	1	Batch MT-2026-002 (Cassiterite) has been registered.	5	MineralBatch	New Batch Registered	SUCCESS
f	11	2026-04-09 08:30:18.580766	1	Batch MT-2026-003 (Wolframite) has been registered.	6	MineralBatch	New Batch Registered	SUCCESS
f	13	2026-04-09 08:30:34.149603	1	Batch MT-2026-004 (Gold) has been registered.	7	MineralBatch	New Batch Registered	SUCCESS
f	15	2026-04-09 08:30:50.810987	1	Batch MT-2026-005 (Tin) has been registered.	8	MineralBatch	New Batch Registered	SUCCESS
f	17	2026-04-09 08:35:41.358603	1	Batch MT-2026-005 dispatched from Gisenyi to Gahanga	5	Movement	Batch Dispatched	BATCH_DISPATCHED
f	19	2026-04-09 08:36:10.040225	1	System-wide fraud analysis completed. 5 batches analyzed.	\N	MineralBatch	Fraud Analysis Complete	ALERT
f	21	2026-04-09 15:19:13.007211	1	Batch MT-2026-006 (Gold) has been registered.	9	MineralBatch	New Batch Registered	SUCCESS
f	22	2026-04-09 15:19:13.0256	5	Batch MT-2026-006 (Gold) has been registered.	9	MineralBatch	New Batch Registered	SUCCESS
f	23	2026-04-09 15:19:13.046838	4	Batch MT-2026-006 (Gold) has been registered.	9	MineralBatch	New Batch Registered	SUCCESS
f	24	2026-04-09 15:19:13.064459	6	Batch MT-2026-006 (Gold) has been registered.	9	MineralBatch	New Batch Registered	SUCCESS
f	25	2026-04-09 15:31:00.371834	1	Batch MT-2026-007 (Cassiterite) has been registered.	10	MineralBatch	New Batch Registered	SUCCESS
f	26	2026-04-09 15:31:00.406377	5	Batch MT-2026-007 (Cassiterite) has been registered.	10	MineralBatch	New Batch Registered	SUCCESS
f	27	2026-04-09 15:31:00.418551	4	Batch MT-2026-007 (Cassiterite) has been registered.	10	MineralBatch	New Batch Registered	SUCCESS
f	28	2026-04-09 15:31:00.423	6	Batch MT-2026-007 (Cassiterite) has been registered.	10	MineralBatch	New Batch Registered	SUCCESS
f	29	2026-04-11 19:38:15.52576	1	Batch MT-2026-008 (Lithium) has been registered.	11	MineralBatch	New Batch Registered	SUCCESS
f	30	2026-04-11 19:38:15.560027	5	Batch MT-2026-008 (Lithium) has been registered.	11	MineralBatch	New Batch Registered	SUCCESS
f	31	2026-04-11 19:38:15.593294	4	Batch MT-2026-008 (Lithium) has been registered.	11	MineralBatch	New Batch Registered	SUCCESS
f	32	2026-04-11 19:38:15.642515	6	Batch MT-2026-008 (Lithium) has been registered.	11	MineralBatch	New Batch Registered	SUCCESS
f	33	2026-04-11 19:41:00.174319	1	Batch MT-2026-008 dispatched from Warehouse A to Warehouse B	7	Movement	Batch Dispatched	BATCH_DISPATCHED
f	34	2026-04-11 19:41:00.207438	5	Batch MT-2026-008 dispatched from Warehouse A to Warehouse B	7	Movement	Batch Dispatched	BATCH_DISPATCHED
f	35	2026-04-11 19:41:00.240673	4	Batch MT-2026-008 dispatched from Warehouse A to Warehouse B	7	Movement	Batch Dispatched	BATCH_DISPATCHED
f	36	2026-04-11 19:41:00.289987	6	Batch MT-2026-008 dispatched from Warehouse A to Warehouse B	7	Movement	Batch Dispatched	BATCH_DISPATCHED
f	37	2026-04-11 19:41:53.532235	1	Batch MT-2026-008 dispatched from mine site A to mine site B	8	Movement	Batch Dispatched	BATCH_DISPATCHED
f	38	2026-04-11 19:41:53.532235	5	Batch MT-2026-008 dispatched from mine site A to mine site B	8	Movement	Batch Dispatched	BATCH_DISPATCHED
f	39	2026-04-11 19:41:53.548849	4	Batch MT-2026-008 dispatched from mine site A to mine site B	8	Movement	Batch Dispatched	BATCH_DISPATCHED
f	40	2026-04-11 19:41:53.583707	6	Batch MT-2026-008 dispatched from mine site A to mine site B	8	Movement	Batch Dispatched	BATCH_DISPATCHED
f	41	2026-04-11 19:44:27.243242	1	Batch MT-2026-008 dispatched from Warehouse A to Warehouse B	12	Movement	Batch Dispatched	BATCH_DISPATCHED
f	42	2026-04-11 19:44:27.278315	5	Batch MT-2026-008 dispatched from Warehouse A to Warehouse B	12	Movement	Batch Dispatched	BATCH_DISPATCHED
f	43	2026-04-11 19:44:27.312078	4	Batch MT-2026-008 dispatched from Warehouse A to Warehouse B	12	Movement	Batch Dispatched	BATCH_DISPATCHED
f	44	2026-04-11 19:44:27.328284	6	Batch MT-2026-008 dispatched from Warehouse A to Warehouse B	12	Movement	Batch Dispatched	BATCH_DISPATCHED
f	45	2026-04-11 19:53:54.455536	1	System-wide fraud analysis completed. 8 batches analyzed.	\N	MineralBatch	Fraud Analysis Complete	ALERT
f	46	2026-04-11 19:53:54.472077	5	System-wide fraud analysis completed. 8 batches analyzed.	\N	MineralBatch	Fraud Analysis Complete	ALERT
f	47	2026-04-11 19:53:54.472077	4	System-wide fraud analysis completed. 8 batches analyzed.	\N	MineralBatch	Fraud Analysis Complete	ALERT
f	48	2026-04-11 19:53:54.489821	6	System-wide fraud analysis completed. 8 batches analyzed.	\N	MineralBatch	Fraud Analysis Complete	ALERT
f	49	2026-04-11 20:33:11.388936	1	System-wide fraud analysis completed. 8 batches analyzed.	\N	MineralBatch	Fraud Analysis Complete	ALERT
f	50	2026-04-11 20:33:11.422961	5	System-wide fraud analysis completed. 8 batches analyzed.	\N	MineralBatch	Fraud Analysis Complete	ALERT
f	51	2026-04-11 20:33:11.454186	4	System-wide fraud analysis completed. 8 batches analyzed.	\N	MineralBatch	Fraud Analysis Complete	ALERT
f	52	2026-04-11 20:33:11.489171	6	System-wide fraud analysis completed. 8 batches analyzed.	\N	MineralBatch	Fraud Analysis Complete	ALERT
f	53	2026-04-11 20:36:04.087305	1	Batch verification failed at checkpoint: QR Scan Checkpoint	4	Verification	Verification Failed	VERIFICATION_FAILED
f	55	2026-04-11 20:36:04.126704	1	Batch verification failed at checkpoint: QR Scan Checkpoint	5	Verification	Verification Failed	VERIFICATION_FAILED
f	54	2026-04-11 20:36:04.118743	5	Batch verification failed at checkpoint: QR Scan Checkpoint	4	Verification	Verification Failed	VERIFICATION_FAILED
f	56	2026-04-11 20:36:04.161569	5	Batch verification failed at checkpoint: QR Scan Checkpoint	5	Verification	Verification Failed	VERIFICATION_FAILED
f	57	2026-04-11 20:36:04.166973	4	Batch verification failed at checkpoint: QR Scan Checkpoint	4	Verification	Verification Failed	VERIFICATION_FAILED
f	59	2026-04-11 20:36:04.216633	6	Batch verification failed at checkpoint: QR Scan Checkpoint	4	Verification	Verification Failed	VERIFICATION_FAILED
f	58	2026-04-11 20:36:04.21524	4	Batch verification failed at checkpoint: QR Scan Checkpoint	5	Verification	Verification Failed	VERIFICATION_FAILED
f	60	2026-04-11 20:36:04.356609	6	Batch verification failed at checkpoint: QR Scan Checkpoint	5	Verification	Verification Failed	VERIFICATION_FAILED
f	61	2026-04-11 20:50:19.612222	1	Batch MT-2026-009 (Coltan) has been registered.	12	MineralBatch	New Batch Registered	SUCCESS
f	62	2026-04-11 20:50:19.655087	5	Batch MT-2026-009 (Coltan) has been registered.	12	MineralBatch	New Batch Registered	SUCCESS
f	63	2026-04-11 20:50:19.686829	4	Batch MT-2026-009 (Coltan) has been registered.	12	MineralBatch	New Batch Registered	SUCCESS
f	64	2026-04-11 20:50:19.702899	6	Batch MT-2026-009 (Coltan) has been registered.	12	MineralBatch	New Batch Registered	SUCCESS
\.


ALTER TABLE public.notifications ENABLE TRIGGER ALL;

--
-- Data for Name: verification_logs; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.verification_logs DISABLE TRIGGER ALL;

COPY public.verification_logs (id, verification_result, verified_at, verifier, batch_id) FROM stdin;
\.


ALTER TABLE public.verification_logs ENABLE TRIGGER ALL;

--
-- Data for Name: verifications; Type: TABLE DATA; Schema: public; Owner: postgres
--

ALTER TABLE public.verifications DISABLE TRIGGER ALL;

COPY public.verifications (passed, batch_id, id, "timestamp", checkpoint, remarks, verified_by) FROM stdin;
t	4	1	2026-04-07 22:09:22.893639	QR Scan Checkpoint	\N	System Admin
t	6	2	2026-04-09 15:50:24.248779	QR Scan Checkpoint	\N	Hoodie
t	6	3	2026-04-09 15:50:24.248779	QR Scan Checkpoint	\N	Hoodie
f	11	4	2026-04-11 20:36:03.972662	QR Scan Checkpoint	\N	Hoodie
f	11	5	2026-04-11 20:36:03.972662	QR Scan Checkpoint	\N	Hoodie
\.


ALTER TABLE public.verifications ENABLE TRIGGER ALL;

--
-- Name: audit_logs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.audit_logs_id_seq', 98, true);


--
-- Name: batches_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.batches_id_seq', 13, true);


--
-- Name: fraud_analyses_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.fraud_analyses_id_seq', 1, false);


--
-- Name: mineral_batches_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mineral_batches_id_seq', 1, false);


--
-- Name: mines_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mines_id_seq', 4, true);


--
-- Name: movement_events_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.movement_events_id_seq', 1, false);


--
-- Name: movements_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.movements_id_seq', 12, true);


--
-- Name: notifications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.notifications_id_seq', 64, true);


--
-- Name: organizations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.organizations_id_seq', 4, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 6, true);


--
-- Name: verification_logs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.verification_logs_id_seq', 1, false);


--
-- Name: verifications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.verifications_id_seq', 5, true);


--
-- PostgreSQL database dump complete
--

