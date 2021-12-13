import {Component, OnInit} from '@angular/core';
import {JsonService} from '../Service/json.service';
import {ConfiguracionCupos} from '../Model/configuracion-cupos';

@Component({
  selector: 'app-configuracion-cupos',
  templateUrl: './configuracion-cupos.component.html',
  styleUrls: ['./configuracion-cupos.component.css'],
  providers: [JsonService],
})
export class ConfiguracionCuposComponent implements OnInit {

  duracionMinutos: number;
  numeroPacientes: number;
  duracionJornada: string;
  fecha: Date;
  pacientesVacunadosDia: number;
  fechaInicio: string;
  fechaCreada: boolean;
  aceptarEstadisticas: boolean;
  mensaje: string;
  solicitada: boolean;
  configuracionExistente: boolean;
  duracionJornadaHoras: number;
  duracionJornadaMinutos: number;
  mensajeControlDuracion: string;
  erroresHoras: boolean;
  erroresMinutos: boolean;



  constructor(private json: JsonService) {
    this.duracionMinutos = 0;
    this.numeroPacientes = 0;
    this.pacientesVacunadosDia = 0;
    this.duracionJornada = '';
    this.fechaInicio = '';
    this.fechaCreada = false;
    this.fecha = new Date();
    this.duracionJornadaHoras = 0;
    this.duracionJornadaMinutos = 0;
    this.aceptarEstadisticas = false;
    this.mensaje = '';
    this.solicitada = false;
    this.configuracionExistente = false;
    this.mensajeControlDuracion = '';
    this.erroresHoras = false;
    this.erroresMinutos = false;

  }

  ngOnInit(): void {
    this.json.getJson('cnfg/existConfCupos').subscribe((res: any) => {
      this.configuracionExistente = JSON.parse(res);
      if (this.configuracionExistente) {
        this.mostrarConfiguracion();
      }
    }, err => {
      this.mensaje = 'Ha ocurrido un error :( Vuelva a intentarlo más tarde'
      console.log(err);
    });
  }

  mostrarConfiguracion() {
    this.json.getJson('cnfg/getConfCupos').subscribe((res: any) => {
      let configuracionCupos: ConfiguracionCupos;
      configuracionCupos = JSON.parse(res);
      this.fechaInicio = configuracionCupos.fechaInicio;
      this.duracionMinutos = configuracionCupos.duracionMinutos;
      this.duracionJornadaHoras = configuracionCupos.duracionJornadaHoras;
      this.duracionJornadaMinutos = configuracionCupos.duracionJornadaMinutos;
      this.numeroPacientes = configuracionCupos.numeroPacientes;
      this.calcularHoraFin();

    }, err => {
      this.mensaje = 'Ha ocurrido un error :( Vuelva a intentarlo más tarde'
      console.log(err);
    });
  }


  calcularHoraFin() {
    if (this.duracionJornadaHoras <= 23 && this.duracionJornadaMinutos <= 59) {
      this.mensajeControlDuracion = '';
      if (this.fechaInicio != '') {
        this.fecha = new Date(this.fechaInicio);
        this.fecha.setHours(this.fecha.getHours() + this.duracionJornadaHoras);
        this.fecha.setMinutes(this.fecha.getMinutes() + this.duracionJornadaMinutos);
        this.fechaCreada = true;
      }
    } else {
      if (this.duracionJornadaHoras > 23) {
        this.erroresHoras = true;	
      }
      if (this.duracionJornadaMinutos > 59) {
        this.erroresMinutos = true;
      }
      this.mensajeControlDuracion = '';
      this.mensajeControlDuracion = '¡Valores incorrectos!';
    };
  }

  crearConfiguracionCupos() {
    let confCupo: ConfiguracionCupos = new ConfiguracionCupos(this.duracionMinutos, this.numeroPacientes, this.duracionJornadaHoras,
      this.duracionJornadaMinutos, this.fechaInicio)

    this.json.postJson('cnfg/crearConfCupos', confCupo).subscribe((res: any) => {
      this.mensaje = 'Configuración guardada correctamente!'
      this.configuracionExistente = true;
    }, err => {
      this.mensaje = 'Ha ocurrido un error :( Vuelva a intentarlo más tarde'
      console.log(err);
    });

    this.solicitada = true;
  }

  aceptarEstadisticasF() {
    this.aceptarEstadisticas = true;
  }

}
