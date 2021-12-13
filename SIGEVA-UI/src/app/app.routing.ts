import {ModuleWithProviders} from "@angular/core";
import {Routes, RouterModule} from "@angular/router";

import {HomeComponent} from "./home/home.component";
import {FormularioCentroSaludComponent} from "./formulario-centro-salud/formulario-centro-salud.component";
import {ConfiguracionCuposComponent} from "./configuracion-cupos/configuracion-cupos.component";
import {CrearUsuariosComponent} from "./crear-usuarios/crear-usuarios.component";
import {IndicarDosisVacunasComponent} from "./indicar-dosis-vacunas/indicar-dosis-vacunas.component"
import {UsuariosSistemaComponent} from './usuarios-sistema/usuarios-sistema.component';
import {SolicitarCitaComponent} from './solicitar-cita/solicitar-cita.component';
import {CentrosSaludSistemaComponent} from './centros-salud-sistema/centros-salud-sistema.component';
import {EditarUsuarioComponent} from "./editar-usuario/editar-usuario.component";
import {ModificacionCentroSaludComponent} from "./modificacion-centro-salud/modificacion-centro-salud.component";
import {ListadoPacientesPorCentroComponent} from "./listado-pacientes-por-centro/listado-pacientes-por-centro.component";
import {ListadoPacientesComponent} from "./listado-pacientes/listado-pacientes.component";
import {LoginComponent} from "./login/login.component";
import {FuncionalidadesGuardService as guard} from "./guards/funcionalidades-guard.service";
import {ContenedorCitasComponent} from "./contenedor-citas/contenedor-citas.component";
import {ContenedorCitasPersonalComponent} from "./contenedor-citasPersonal/contenedor-citasPersonal.component";
import {VistaPersonalComponent} from "./vista-personal/vista-personal.component";


const appRoutes: Routes = [
  {
    path: '', component: HomeComponent
  },
  {
    path: 'home', component: HomeComponent
  },
  {
    path: 'crearCS', component: FormularioCentroSaludComponent,
    canActivate: [guard], data: {expectedRol: ['Administrador', 'SuperAdmin']}
  },
  {
    path: 'cnfgCupos', component: ConfiguracionCuposComponent,
    canActivate: [guard], data: {expectedRol: ['Administrador', 'SuperAdmin']}
  },
  {
    path: 'crearUsuarios', component: CrearUsuariosComponent,
    canActivate: [guard], data: {expectedRol: ['Administrador', 'SuperAdmin']}
  },
  {
    path: 'indicarDosisVacunas', component: IndicarDosisVacunasComponent,
    canActivate: [guard], data: {expectedRol: ['Administrador', 'SuperAdmin']}
  },
  {
    path: 'usuariosSistema', component: UsuariosSistemaComponent,
    canActivate: [guard], data: {expectedRol: ['Administrador', 'SuperAdmin']}
  },
  {
    path: 'solicitarCita', component: SolicitarCitaComponent,
    canActivate: [guard], data: {expectedRol: ['Paciente', 'SuperAdmin']}
  },
  {
    path: 'centrosSistema', component: CentrosSaludSistemaComponent,
    canActivate: [guard], data: {expectedRol: ['Administrador', 'SuperAdmin']}
  },
  {
    path: 'editarUsuario/:idUsuario', component: EditarUsuarioComponent,
    canActivate: [guard], data: {expectedRol: ['Administrador', 'SuperAdmin']}
  },
  {
    path: 'editarCS/:idCentroSalud', component: ModificacionCentroSaludComponent,
    canActivate: [guard], data: {expectedRol: ['Administrador', 'SuperAdmin']}
  },
  {
    path: 'listarPacientes', component: ListadoPacientesComponent,
    canActivate: [guard], data: {expectedRol: ['Sanitario', 'SuperAdmin']}
  },
  {
    path: 'listarPacientesPorCentro', component: ListadoPacientesPorCentroComponent,
    canActivate: [guard], data: {expectedRol: ['PersonalDeCitas', 'SuperAdmin']}
  },
  {path: 'login', component: LoginComponent},

  {
    path: 'misCitas', component: ContenedorCitasComponent,
    canActivate: [guard], data: {expectedRol: ['Paciente', 'SuperAdmin']}
  },
{
    path: 'vistaPersonal', component: VistaPersonalComponent,
    canActivate: [guard], data: {expectedRol: ['PersonalDeCitas', 'SuperAdmin']}
  },
 {
    path: 'modificarCitas', component: ContenedorCitasPersonalComponent,
    canActivate: [guard], data: {expectedRol: ['PersonalDeCitas', 'SuperAdmin']}
  },

  {path: '**', redirectTo: '', pathMatch: 'full'}
];

export const appRoutingProviders: any[] = [];
export const routing: ModuleWithProviders<any> = RouterModule.forRoot(appRoutes);
