package com.equipo3.SIGEVA;

import java.util.Date;
import java.util.UUID;

import com.equipo3.SIGEVA.controller.CentroController;
import com.equipo3.SIGEVA.dto.*;
import com.equipo3.SIGEVA.utils.Utilidades;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.equipo3.SIGEVA.controller.UsuarioController;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CrearUsuariosTest {

	@Autowired
	private UsuarioController usuarioController;

	@Autowired
	private CentroController centroController;

	@Autowired
	private Utilidades utilidades;

	static AdministradorDTO administradorDTO;
	static SanitarioDTO sanitarioDTO;
	static PacienteDTO pacienteDTO;
	static CentroSaludDTO centroSaludDTO;

	@BeforeAll
	static void crearCentroSalud() {
		centroSaludDTO = new CentroSaludDTO();
		centroSaludDTO.setNombreCentro(UUID.randomUUID().toString());
		centroSaludDTO.setDireccion(UUID.randomUUID().toString());
		centroSaludDTO.setNumVacunasDisponibles((int) (Math.random() * 1000));
	}

	@BeforeAll
	static void crearAdministrador() {
		administradorDTO = new AdministradorDTO();
		administradorDTO.setRol(new RolDTO());
		administradorDTO.setCentroSalud(new CentroSaludDTO(UUID.randomUUID().toString(), "dirección", (int)Math.random()*100));
		administradorDTO.setUsername(UUID.randomUUID().toString());
		administradorDTO.setCorreo("micorreo@correo.com");
		administradorDTO.setHashPassword("sdfsdf");
		administradorDTO.setDni("99999999Q");
		administradorDTO.setNombre("Juan");
		administradorDTO.setApellidos("Perez");
		administradorDTO.setFechaNacimiento(new Date());
		administradorDTO.setImagen("912imagen");
	}

	@BeforeAll
	static void crearSanitario() {
		sanitarioDTO = new SanitarioDTO();
		sanitarioDTO.setRol(new RolDTO());
		sanitarioDTO.setCentroSalud(new CentroSaludDTO(UUID.randomUUID().toString(), "dirección", (int)Math.random()*100));
		sanitarioDTO.setUsername(UUID.randomUUID().toString());
		sanitarioDTO.setCorreo("micorreo@correo.com");
		sanitarioDTO.setHashPassword("sdfsdf");
		sanitarioDTO.setDni("99999999Q");
		sanitarioDTO.setNombre("Juan");
		sanitarioDTO.setApellidos("Perez");
		sanitarioDTO.setFechaNacimiento(new Date());
		sanitarioDTO.setImagen("912imagen");
	}

	@BeforeAll
	static void crearPaciente() {
		pacienteDTO = new PacienteDTO();
		pacienteDTO.setRol(new RolDTO());
		pacienteDTO.setCentroSalud(new CentroSaludDTO(UUID.randomUUID().toString(), "dirección", (int)Math.random()*100));
		pacienteDTO.setUsername(UUID.randomUUID().toString());
		pacienteDTO.setCorreo("micorreo@correo.com");
		pacienteDTO.setHashPassword("sdfsdf");
		pacienteDTO.setDni("99999999Q");
		pacienteDTO.setNombre("Juan");
		pacienteDTO.setApellidos("Perez");
		pacienteDTO.setFechaNacimiento(new Date());
		pacienteDTO.setImagen("912imagen");
	}

	
	@Test
	void insercionAdministradorDuplicado() {
		try{
			String uuid = UUID.randomUUID().toString();
			administradorDTO.setUsername(uuid);
			administradorDTO.setRol(utilidades.getRolByNombre("Administrador"));
			centroSaludDTO.setNombreCentro(UUID.randomUUID().toString());
			centroController.crearCentroSalud(centroSaludDTO);
			administradorDTO.setCentroSalud((centroSaludDTO));

			usuarioController.crearUsuarioAdministrador(administradorDTO);
			usuarioController.crearUsuarioAdministrador(administradorDTO);
		} catch (Exception e){
			utilidades.eliminarUsuario(administradorDTO.getUsername());
			utilidades.eliminarCentro(administradorDTO.getCentroSalud().getId());
			assertNotNull(e);
		}
	}


	@Test
	void insercionCorrectaSanitario() {
		sanitarioDTO.setRol(utilidades.getRolByNombre("Sanitario"));
		centroSaludDTO.setNombreCentro(UUID.randomUUID().toString());
		centroController.crearCentroSalud(centroSaludDTO);
		sanitarioDTO.setCentroSalud((centroSaludDTO));

		usuarioController.crearUsuarioSanitario(sanitarioDTO);

		assertEquals(usuarioController.getUsuarioById(sanitarioDTO.getIdUsuario()).toString(), sanitarioDTO.toString());

		utilidades.eliminarUsuario(sanitarioDTO.getUsername());
		utilidades.eliminarCentro(administradorDTO.getCentroSalud().getId());
	}

	@Test
	void insercionSanitarioDuplicado(){
		try{
			String uuid = UUID.randomUUID().toString();
			sanitarioDTO.setUsername(uuid);
			sanitarioDTO.setRol(utilidades.getRolByNombre("Sanitario"));
			centroSaludDTO.setNombreCentro(UUID.randomUUID().toString());
			centroController.crearCentroSalud(centroSaludDTO);
			sanitarioDTO.setCentroSalud((centroSaludDTO));

			usuarioController.crearUsuarioSanitario(sanitarioDTO);
			usuarioController.crearUsuarioSanitario(sanitarioDTO);
		} catch (Exception e){
			utilidades.eliminarUsuario(sanitarioDTO.getUsername());
			utilidades.eliminarCentro(administradorDTO.getCentroSalud().getId());
			assertNotNull(e);
		}
	}

	@Test
	void insercionCorrectaPaciente() {
		pacienteDTO.setRol(utilidades.getRolByNombre("Paciente"));
		centroSaludDTO.setNombreCentro(UUID.randomUUID().toString());
		centroController.crearCentroSalud(centroSaludDTO);
		pacienteDTO.setCentroSalud((centroSaludDTO));

		usuarioController.crearUsuarioPaciente(pacienteDTO);

		assertEquals(usuarioController.getPaciente(pacienteDTO.getIdUsuario()).toString(), pacienteDTO.toString());

		utilidades.eliminarUsuario(pacienteDTO.getUsername());
		utilidades.eliminarCentro(administradorDTO.getCentroSalud().getId());
	}

	@Test
	void insercionPacienteDuplicado() {
		try {
			String uuid = UUID.randomUUID().toString();
			pacienteDTO.setUsername(uuid);
			pacienteDTO.setRol(utilidades.getRolByNombre("Paciente"));
			centroSaludDTO.setNombreCentro(UUID.randomUUID().toString());
			centroController.crearCentroSalud(centroSaludDTO);
			pacienteDTO.setCentroSalud((centroSaludDTO));

			usuarioController.crearUsuarioPaciente(pacienteDTO);
			usuarioController.crearUsuarioPaciente(pacienteDTO);
		} catch (Exception e){
			utilidades.eliminarUsuario(pacienteDTO.getUsername());
			utilidades.eliminarCentro(administradorDTO.getCentroSalud().getId());
			assertNotNull(e);
		}
	}
}
