/* Prueba inicial */
create table emp(id number(10), name varchar2(40), age number(2));

INSERT INTO emp (id, name, age) VALUES (47, 'sergio', 69);

SHOW TABLES;

SELECT * FROM emp;

COMMIT;



/*****************************************************************************/
/* FUNCIÓN RESET                                                             */
/*****************************************************************************/
/* Borra las tablas */
DROP TABLE stock  CASCADE CONSTRAINTS;
DROP TABLE pedido CASCADE CONSTRAINTS;
DROP TABLE detalle_pedido;



/* Crea las tablas */
CREATE TABLE stock (
    cproducto INT GENERATED AS IDENTITY,
    cantidad  INT,
    PRIMARY KEY (cproducto)
);

CREATE TABLE pedido (
    cpedido      INT GENERATED AS IDENTITY,
    ccliente     INT,
    fecha_pedido DATE DEFAULT SYSDATE,
    PRIMARY KEY (cpedido)
);

CREATE TABLE detalle_pedido (
    cpedido   INT REFERENCES pedido(cpedido),
    cproducto INT REFERENCES stock (cproducto),
    cantidad  INT,
    PRIMARY KEY (cpedido, cproducto)
);



/* Inserta el valor en stock */
INSERT INTO stock (cantidad) VALUES (10);

/* Inserta valores predefinidos */
/*
INSERT INTO stock VALUES (0, 16);
INSERT INTO stock VALUES (1, 11);
INSERT INTO stock VALUES (2, 11);
INSERT INTO stock VALUES (3, 15);
INSERT INTO stock VALUES (4, 17);
INSERT INTO stock VALUES (5, 18);
INSERT INTO stock VALUES (6, 17);
INSERT INTO stock VALUES (7, 11);
INSERT INTO stock VALUES (8, 10);
INSERT INTO stock VALUES (9, 19);
*/


/*****************************************************************************/
/* CREAR PEDIDO                                                              */
/*****************************************************************************/

/* Crea el pedido */
INSERT INTO pedido (ccliente) VALUES (47);
INSERT INTO pedido (ccliente) VALUES (69);
INSERT INTO pedido (ccliente) VALUES (420);

/* Obtener el código del pedido */
SELECT cpedido FROM pedido WHERE (ccliente=47);

































