-- https://docs.spring.io/spring-boot/docs/2.1.0.M1/reference/html/howto-database-initialization.html#howto-initialize-a-database-using-spring-jdbc
-- 'Spring Boot can automatically create the schema (DDL scripts) of your DataSource and initialize it (DML scripts). It loads SQL from the standard root classpath locations: schema.sql and data.sql'

DROP TABLE IF EXISTS MST_PRODUCT;

CREATE TABLE MST_PRODUCT
(
    product_name character varying(255),
    price integer,
    category1 character varying(64),
    category2 character varying(64),
    PRIMARY KEY (product_name)
);


DROP TABLE IF EXISTS MST_OPERATION_DATE;

CREATE TABLE MST_OPERATION_DATE
(
    operation_date date,
    update_date timestamp(6) without time zone
);


-- Table: TRN_SALES

DROP TABLE IF EXISTS TRN_SALES;

CREATE TABLE TRN_SALES
(
    product_name character varying(255) NOT NULL,
    price integer,
    category1 character varying(64),
    category2 character varying(64),
    sales_date date
);

