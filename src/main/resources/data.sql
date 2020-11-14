-- https://docs.spring.io/spring-boot/docs/2.1.0.M1/reference/html/howto-database-initialization.html#howto-initialize-a-database-using-spring-jdbc
-- 'Spring Boot can automatically create the schema (DDL scripts) of your DataSource and initialize it (DML scripts). It loads SQL from the standard root classpath locations: schema.sql and data.sql'

TRUNCATE TABLE MST_PRODUCT;
INSERT INTO MST_PRODUCT(product_name, price, category1, category2) VALUES ('５円チョコ', '5', 'food','candy');
INSERT INTO MST_PRODUCT(product_name, price, category1, category2) VALUES ('テロルチョコ', '10', 'food','candy');
INSERT INTO MST_PRODUCT(product_name, price, category1, category2) VALUES ('ドッキリチョコ', '30', 'food','candy');
INSERT INTO MST_PRODUCT(product_name, price, category1, category2) VALUES ('週刊マンガ', '170', 'book', 'magazine');
INSERT INTO MST_PRODUCT(product_name, price, category1, category2) VALUES ('米', '2000', 'food','staple');


TRUNCATE TABLE  MST_OPERATION_DATE;
INSERT INTO MST_OPERATION_DATE(operation_date) VALUES ('2020-07-01');

TRUNCATE TABLE  TRN_SALES;

