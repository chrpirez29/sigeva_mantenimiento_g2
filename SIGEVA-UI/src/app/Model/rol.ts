export interface Rol {
  id: string;
  nombre: string;
}

export class Rol {
  id: string;
  nombre: string;

  constructor(id: string, nombre: string) {

    this.id = id;
    this.nombre = nombre;
  }
}
