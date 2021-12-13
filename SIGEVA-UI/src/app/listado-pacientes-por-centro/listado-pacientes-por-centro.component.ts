import { Component, OnInit } from '@angular/core';
import { JsonService } from '../Service/json.service';
import { HttpParams } from "@angular/common/http";
import { Rol } from '../Model/rol';
import { UsuarioConObjetos } from "../Model/Usuario-con-objetos";
import {TokenService} from "../Service/token.service";
import {Router} from "@angular/router";

@Component({
	selector: 'app-listado-pacientes',
	templateUrl: './listado-pacientes-por-centro.component.html',
	styleUrls: ['./listado-pacientes-por-centro.component.css']
})
export class ListadoPacientesPorCentroComponent implements OnInit {
	usuarios: UsuarioConObjetos[];
	existeConfiguracion = false;
	roles: Rol[];
	rolMostrado: string;
	idUsuario : string | null;

	constructor(private json: JsonService,private tokenService: TokenService, private router: Router) {
		this.usuarios = [];
		this.roles = [];
		this.rolMostrado = "Paciente";
		this.idUsuario = "";
	}


ngOnInit(): void {
	if (this.tokenService.getToken() == null) {
      this.router.navigate(['/login']);
    } else {
	  this.cargarRoles();
      this.cargarUsuarios();
    }
}

cargarRoles() {
	this.json.getJson("user/getRoles").subscribe(
		(res: any) => {
			this.roles = JSON.parse(res);
		},
		error => {
			console.log(error);
		}
	);
}

cambiarRolaId() {
	if (this.rolMostrado != "Todos") {
		let self = this;
		this.roles.forEach(function(rol: Rol) {
			if (self.rolMostrado === rol.nombre) {
				self.rolMostrado = rol.id;
			}
		})
	}
}

cargarUsuarios() {
    this.cambiarRolaId();
	this.getIdUsuario();
    if (this.idUsuario == null) {
      this.idUsuario = "";
    }

	let params = new HttpParams({
      fromObject: {
        rol: this.rolMostrado,
		idUsuario: this.idUsuario
      }
    });
	this.json.getJsonP("user/getUsuariosByPacienteAndCentroSalud",params).subscribe(
		result => {
			this.usuarios = JSON.parse(result);
		}, error => {
			console.log(error);
		});
}

  private getIdUsuario() {
    if (this.idUsuario == null) {
      this.idUsuario = "";
    }

	this.idUsuario = this.tokenService.getIdUsuario();

  }
}
