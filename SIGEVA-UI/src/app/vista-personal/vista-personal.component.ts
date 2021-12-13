import {Component, OnInit,} from '@angular/core';
import {CentroSalud} from "../Model/centro-salud";
import {JsonService} from '../Service/json.service';
import {HttpParams} from "@angular/common/http";
import {CitaConObjetos} from "../Model/cita-con-objetos";
import {Paciente} from "../Model/paciente";
import {Rol} from "../Model/rol";
import {Vacuna} from "../Model/vacuna";
import {TokenService} from "../Service/token.service";


@Component({
  selector: 'app-vista-personal',
  templateUrl: './vista-personal.component.html',
  styleUrls: ['./vista-personal.component.css']
})

export class VistaPersonalComponent implements OnInit{
  paciente: Paciente;
  mensaje: string;
  mensajeError: string;
  citas: CitaConObjetos[];
  solicitada: boolean;
  idUsuario: string;
  msgNoCitas = "";

  constructor(private json: JsonService, private token: TokenService) {
    this.paciente = new Paciente(new Rol("1", "Paciente"), new CentroSalud("direccion", "nombre", 1, new Vacuna("vacuna", 3, 15), ""), "vasilesan", "", "", "",
      "", "", "", "", 0);
    this.citas = [];
    this.solicitada = false;
    this.mensaje = "SOLICITAR CITA";
    this.mensajeError = "";

	this.idUsuario = "";
  }

  ngOnInit(): void {
  }

  solicitarCita(id:string) {
    let params = new HttpParams({
      fromObject: {
        uuidPaciente: String(id)
      }
    });
    this.json.getJsonPJ("cita/buscarYAsignarCitas", params).subscribe(
      result => {
        this.citas = JSON.parse(JSON.stringify(result));
        this.solicitada = true;
        this.mensajeError = "";
        if (this.citas.length == 1 && this.citas[0].dosis == 1){
          this.mensajeError = "No se puede asignar la segunda cita por superar el límite de fecha. Contacte con el administrador.";
        }
      }, error => {
        this.mensajeError = error.error.message;
      });
  }
	getId(): any {
		this.json.getJson('/tokenUsuario/getId').subscribe(
			id => {
				this.solicitarCita(id)
			}
		);
		;
	}


}