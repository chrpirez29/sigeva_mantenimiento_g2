import { Component, Input } from '@angular/core';
import { UsuarioConObjetos } from "../Model/Usuario-con-objetos";
import { CentroSalud } from "../Model/centro-salud";
import { Rol } from "../Model/rol";
import { MatDialog } from "@angular/material/dialog";
import { JsonService } from "../Service/json.service";
import { Vacuna } from "../Model/vacuna";
import { HttpParams } from '@angular/common/http';


@Component({
	selector: 'app-usuariocentro',
	templateUrl: './usuario-centro.component.html',
	styleUrls: ['./usuario-centro.component.css']
})

export class UsuarioCentroComponent {
	@Input() usuario: UsuarioConObjetos;
	@Input() existeConfiguracion: boolean = false;
	message: string = "";
	usuarioEliminado = false;
	errorMessage: string = "";

	constructor(private json: JsonService, public dialog: MatDialog) {
		this.usuario = new UsuarioConObjetos(new Rol("", ""), new CentroSalud("direccion", "nombre", 1, new Vacuna("vacuna", 3, 15), ""), "", "", "", "", "", "", "",
			"");
	}

	cogerId() {
		if(this.usuario.idUsuario==null){
			this.usuario.idUsuario="";
		}
		let params = new HttpParams({
			fromObject: {
				'idUsuario': this.usuario.idUsuario,
			}
		});

		this.json.postJson("tokenUsuario/cogerId", params).subscribe(
			result => {
				if (result === null) {
					this.errorMessage = "";
					this.message = ""
				}
				setTimeout('document.location.reload()', 2000);
			}, err => {
				this.errorMessage = err.error.message;
				console.log(err);
			});

	}

}
