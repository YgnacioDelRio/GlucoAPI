DROP DATABASE IF EXISTS gluco_api;
create database gluco_api;
use gluco_api;

create table usuario(
	idUsuario int not null primary key auto_increment,
    nombre varchar(40) not null,
    primerApellido varchar(40) not null,
    segundoApellido varchar(40),
    email varchar(50) not null
);

#insert into usuario values(0, "Arturo", "Del Rio", "Rayas", "aaa@gmail.com")
#select * from usuario

create table medida(
	idMedida int not null primary key auto_increment,
    idUsuario int not null,
    glucosa int not null, -- medida en mg/dL
    fechaHora datetime,
    contexto varchar(40),
    notas varchar(100),
    foreign key(idUsuario) references usuario(idUsuario)
);
