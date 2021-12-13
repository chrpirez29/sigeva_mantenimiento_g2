package com.equipo3.SIGEVA;

import java.util.Date;
import java.util.UUID;

import com.equipo3.SIGEVA.controller.CentroController;
import com.equipo3.SIGEVA.dto.*;
import com.equipo3.SIGEVA.model.Cupo;

import com.equipo3.SIGEVA.utils.Utilidades;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.equipo3.SIGEVA.controller.UsuarioController;
import com.equipo3.SIGEVA.dao.CupoDao;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EliminarCentroSaludTest {

	@Autowired
	private UsuarioController usuarioController;

	@Autowired
	private CentroController centroController;

	@Autowired
	private Utilidades utilidades;

	static CentroSaludDTO centroSaludDTO;
	
	static AdministradorDTO administradorDTO;
	
	static SanitarioDTO sanitarioDTO;
	
	static PacienteDTO pacienteDTO;
	
	@Autowired
	private CupoDao cupoDao;

	@Test
	void eliminacionCorrectaTest() {
		centroSaludDTO = new CentroSaludDTO();
		centroSaludDTO.setNombreCentro(UUID.randomUUID().toString());
		centroController.crearCentroSalud(centroSaludDTO);
		try {
			centroController.borrarCentroSalud(centroSaludDTO);
		}catch(Exception e){
			assertNull(e);
		}
	}

	@Test
	void eliminacionCentroSaludDuplicadoTest() {
		centroSaludDTO = new CentroSaludDTO();
		centroSaludDTO.setNombreCentro(UUID.randomUUID().toString());
		centroController.crearCentroSalud(centroSaludDTO);
		try {
			centroController.borrarCentroSalud(centroSaludDTO);
			centroController.borrarCentroSalud(centroSaludDTO);
		}catch(Exception e){
			assertNotNull(e);
			utilidades.eliminarCentro(centroSaludDTO.getId());
		}
	}

	@Test
	void eliminacionCentroSaludConPacienteTest() {
		centroSaludDTO = new CentroSaludDTO();
		centroSaludDTO.setNombreCentro(UUID.randomUUID().toString());
		centroController.crearCentroSalud(centroSaludDTO);
		
		sanitarioDTO = new SanitarioDTO();
		sanitarioDTO.setUsername(UUID.randomUUID().toString());
		sanitarioDTO.setCentroSalud(centroSaludDTO);
		sanitarioDTO.setRol(utilidades.getRolByNombre("Sanitario"));
		
		pacienteDTO = new PacienteDTO();
		pacienteDTO.setUsername(UUID.randomUUID().toString());
		pacienteDTO.setCentroSalud(centroSaludDTO);
		pacienteDTO.setRol(utilidades.getRolByNombre("Paciente"));
		
		usuarioController.crearUsuarioPaciente(pacienteDTO);
		usuarioController.crearUsuarioSanitario(sanitarioDTO);
		try {
			centroController.borrarCentroSalud(centroSaludDTO);
		}catch(Exception e){
			assertNotNull(e);
			utilidades.eliminarUsuario(sanitarioDTO.getUsername());
			utilidades.eliminarUsuario(pacienteDTO.getUsername());
			utilidades.eliminarCentro(centroSaludDTO.getId());
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	void eliminacionCentroSaludConCitasTest() {
		centroSaludDTO = new CentroSaludDTO();
		centroSaludDTO.setNombreCentro(UUID.randomUUID().toString());
		centroController.crearCentroSalud(centroSaludDTO);
		
		Date fecha = new Date(120,0,1);
		Cupo cupo = new Cupo();
		cupo.setFechaYHoraInicio(fecha);
		cupo.setUuidCentroSalud(centroSaludDTO.getId());
		cupo.setTamanoActual(1);
		cupoDao.save(cupo);
	
		try {
			centroController.borrarCentroSalud(centroSaludDTO);
		}catch(Exception e){
			assertNotNull(e);
			utilidades.eliminarCentro(centroSaludDTO.getId());
		}
	}
}
