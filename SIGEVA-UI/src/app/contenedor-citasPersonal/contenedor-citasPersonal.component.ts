import { Component, OnInit } from '@angular/core';
import { JsonService } from "../Service/json.service";
import { TokenService } from "../Service/token.service";
import { HttpParams } from "@angular/common/http";
import { CitaConObjetos } from "../Model/cita-con-objetos";

@Component({
	selector: 'app-contenedor-citasPersonal',
	templateUrl: './contenedor-citasPersonal.component.html',
	styleUrls: ['./contenedor-citasPersonal.component.css']
})
export class ContenedorCitasPersonalComponent implements OnInit {

	citas: CitaConObjetos[] = [];
	idUsuario: string;
	msgNoCitas = "";

	constructor(private json: JsonService, private tokenService: TokenService) {
		this.idUsuario = String(this.getId());
	}
	
	ngOnInit(): void {
	}

	getCitas(id:string) {

		let params = new HttpParams({
			fromObject: {
				idPaciente: String(id)
			}
		});
		this.json.getJsonPJ('/cita/obtenerCitasFuturasDelPaciente', params).subscribe(
			data => {
				this.citas = data;
				if (this.citas.length == 0) {
					this.msgNoCitas = "No hay citas asignadas";
				}
			}
		);
	}

	getId(): any {
		this.json.getJson('/tokenUsuario/getId').subscribe(
			id => {
				this.getCitas(id)
			}
		);
		;
	}
}
