package com.equipo3.SIGEVA;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.equipo3.SIGEVA.controller.CentroController;
import com.equipo3.SIGEVA.controller.UsuarioController;
import com.equipo3.SIGEVA.dto.CentroSaludDTO;
import com.equipo3.SIGEVA.dto.PacienteDTO;
import com.equipo3.SIGEVA.dto.RolDTO;
import com.equipo3.SIGEVA.utils.Utilidades;

@SpringBootTest
class ListarUsuariosTest {

    @Autowired
    private UsuarioController usuarioController;
    @Autowired
    private CentroController centroController;

    @Autowired
    private Utilidades utilidades;

    @Test
    void getTodosUsuarios(){
        assertNotNull(usuarioController.getUsuarioByRol("Todos"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Sanitario"})
    void getUsuariosSanitarios(String rol){
        RolDTO rolDTO = utilidades.getRolByNombre(rol);
        assertNotNull(usuarioController.getUsuarioByRol(rolDTO.getId()));
    }

    @Test void getPacientes(){
    	PacienteDTO paciente = new PacienteDTO();
    	paciente.setNombre(UUID.randomUUID().toString());
    	paciente.setUsername("Test getPacientes");
    	paciente.setRol(utilidades.getRolByNombre("Paciente"));
    	
    	CentroSaludDTO csDto = new CentroSaludDTO();
		csDto.setDireccion("test getPacientes direccion");
		csDto.setNombreCentro(UUID.randomUUID().toString());
		csDto.setNumVacunasDisponibles(80);

        centroController.crearCentroSalud(csDto);
		
		paciente.setCentroSalud(csDto);
		
    	usuarioController.crearUsuarioPaciente(paciente);
        for (PacienteDTO pacienteDTO : utilidades.getPacientes()){
            assertNotNull(pacienteDTO);
            utilidades.eliminarUsuario(paciente.getUsername());
        }
        utilidades.eliminarCentro(csDto.getId());
    }
}
