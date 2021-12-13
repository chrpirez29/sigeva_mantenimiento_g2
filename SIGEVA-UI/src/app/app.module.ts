import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {routing, appRoutingProviders} from "./app.routing";


import {AppComponent} from './app.component';

import {HomeComponent} from './home/home.component';
import {FormularioCentroSaludComponent} from './formulario-centro-salud/formulario-centro-salud.component';

import {ConfiguracionCuposComponent} from './configuracion-cupos/configuracion-cupos.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {CrearUsuariosComponent} from './crear-usuarios/crear-usuarios.component';
import {IndicarDosisVacunasComponent} from './indicar-dosis-vacunas/indicar-dosis-vacunas.component';
import {UsuariosSistemaComponent} from './usuarios-sistema/usuarios-sistema.component';
import {UsuarioComponent} from './usuario/usuario.component';
import {UsuarioCentroComponent} from './usuario-centro/usuario-centro.component';
import {SolicitarCitaComponent} from './solicitar-cita/solicitar-cita.component';
import {CentroSaludComponent} from './centro-salud/centro-salud.component';
import {CentrosSaludSistemaComponent} from './centros-salud-sistema/centros-salud-sistema.component';
import {EditarUsuarioComponent} from './editar-usuario/editar-usuario.component';
import {VentanaEmergenteComponent} from './ventana-emergente/ventana-emergente.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatButtonModule} from "@angular/material/button";
import {MatDialogModule} from "@angular/material/dialog";
import {ListadoPacientesComponent} from './listado-pacientes/listado-pacientes.component';
import {ListadoPacientesPorCentroComponent} from './listado-pacientes-por-centro/listado-pacientes-por-centro.component';
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatNativeDateModule, MatOptionModule} from "@angular/material/core";
import {MatFormFieldModule} from "@angular/material/form-field";
import {ModificacionCentroSaludComponent} from './modificacion-centro-salud/modificacion-centro-salud.component';
import {LoginComponent} from './login/login.component';
import {BarraNavegacionComponent} from './barra-navegacion/barra-navegacion.component';
import {MatInputModule} from "@angular/material/input";
import {MatMenuModule} from "@angular/material/menu";
import {ContenedorCitasComponent} from './contenedor-citas/contenedor-citas.component';
import {ContenedorCitasPersonalComponent} from './contenedor-citasPersonal/contenedor-citasPersonal.component';
import {MatRadioModule} from "@angular/material/radio";
import {CitaEditarComponent} from './cita-editar/cita-editar.component';
import {CitaEditarPersonalComponent} from './cita-editarPersonal/cita-editarPersonal.component';
import {MatSelectModule} from "@angular/material/select";
import {MatIconModule} from "@angular/material/icon";
import {MensajeConfiguracionComponent} from './mensaje-configuracion/mensaje-configuracion.component';
import { CitaPedirComponent } from './cita-pedir/cita-pedir.component';
import {VistaPersonalComponent} from './vista-personal/vista-personal.component';



@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    FormularioCentroSaludComponent,
    ConfiguracionCuposComponent,
    CrearUsuariosComponent,
    IndicarDosisVacunasComponent,
    UsuariosSistemaComponent,
    UsuarioComponent,
	UsuarioCentroComponent,
    SolicitarCitaComponent,
    CentroSaludComponent,
    CentrosSaludSistemaComponent,
    EditarUsuarioComponent,
    ListadoPacientesComponent,
	ListadoPacientesPorCentroComponent,
    EditarUsuarioComponent,
    VentanaEmergenteComponent,
    ModificacionCentroSaludComponent,
    LoginComponent,
    BarraNavegacionComponent,
    ContenedorCitasComponent,
    CitaEditarComponent,
    MensajeConfiguracionComponent,
    CitaPedirComponent,
	VistaPersonalComponent,
	ContenedorCitasPersonalComponent,
	CitaEditarPersonalComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    routing,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MatButtonModule,
    MatDialogModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatFormFieldModule,
    MatInputModule,
    MatMenuModule,
    MatRadioModule,
    MatOptionModule,
    MatSelectModule,
    MatIconModule
  ],
  providers: [appRoutingProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
