package com.equipo3.SIGEVA;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.equipo3.SIGEVA.dto.RolDTO;
import com.equipo3.SIGEVA.dto.VacunaDTO;
import com.equipo3.SIGEVA.utils.Utilidades;

@SpringBootTest
public class CrearRolesYVacunasTest {

	@Autowired
	private Utilidades utilidades;

	static VacunaDTO vacunaDTO;
	static RolDTO rolDTOAdministrador;
	static RolDTO rolDTOPaciente;
	static RolDTO rolDTOSanitario;
	static RolDTO rolDTOPersonal;

	@BeforeAll
	public static void crearVacuna() {
		vacunaDTO = new VacunaDTO();
		vacunaDTO.setNumDosis(2);
		vacunaDTO.setNombre("Pfizer");
		vacunaDTO.setDiasEntreDosis(21);
	}

	@BeforeAll
	public static void crearRol() {
		rolDTOAdministrador = new RolDTO();
		rolDTOPaciente = new RolDTO();
		rolDTOSanitario = new RolDTO();
		rolDTOPersonal= new RolDTO();
	}

	@Test
	void addVacuna() {
		utilidades.addVacuna(vacunaDTO);
		utilidades.getVacunaById(vacunaDTO.getId());
		assertEquals(vacunaDTO.toString(), utilidades.getVacunaById(vacunaDTO.getId()).toString());
		utilidades.eliminarVacuna(vacunaDTO.getId());
	}

	@Test
	void getVacunaById() {
		vacunaDTO.setId(UUID.randomUUID().toString());
		utilidades.addVacuna(vacunaDTO);
		assertNotNull(utilidades.getVacunaById(vacunaDTO.getId()));
		utilidades.eliminarVacuna(vacunaDTO.getId());
	}

	@Test
	void addRol() {
		rolDTOAdministrador.setNombre("Administrador");
		rolDTOPaciente.setNombre("Paciente");
		rolDTOSanitario.setNombre("Sanitario");
		rolDTOPersonal.setNombre("Personal");
		

		utilidades.crearRol(rolDTOAdministrador);
		utilidades.crearRol(rolDTOPaciente);
		utilidades.crearRol(rolDTOSanitario);
		utilidades.crearRol(rolDTOPersonal);

		utilidades.eliminarRol(rolDTOAdministrador.getId());
		utilidades.eliminarRol(rolDTOPaciente.getId());
		utilidades.eliminarRol(rolDTOSanitario.getId());
		utilidades.eliminarRol(rolDTOPersonal.getId());

		assertTrue(true);
	}
}
