package com.equipo3.SIGEVA.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/***
 * Controlador para mapear las peticiones del front end en el back end.
 * 
 * @author Equipo3
 *
 */
@Controller
public class ViewController {

	@RequestMapping(path={ "/home", "/crearCS", "/cnfgCupos", "/crearUsuarios", "/indicarDosisVacunas", "/usuariosSistema",
			"/solicitarCita", "/editarUsuario/:idUsuario", "/editarCS/:idCentroSalud", "/listarPacientes", "/login",
			"/misCitas", "/listarPacientesPorCentro" }, method = {RequestMethod.GET})
	public String index() {
		return "forward:/index.html";
	}
}
