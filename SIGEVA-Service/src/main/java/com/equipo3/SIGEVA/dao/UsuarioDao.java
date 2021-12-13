package com.equipo3.SIGEVA.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.equipo3.SIGEVA.model.Rol;
import com.equipo3.SIGEVA.model.Usuario;

/***
 * Interfaz que nos permite introducir, modificar, borrar... usuarios en la
 * bbdd.
 * 
 * @author Equipo3
 *
 */
@Repository
public interface UsuarioDao extends MongoRepository<Usuario, String> {

	Optional<Usuario> findByUsername(String username);

	List<Usuario> findAllByRol(String id);

	List<Usuario> findAllByCentroSalud(String centroSalud);
	
	List<Usuario> findAllByRolAndCentroSalud(String rol, String centroSalud);

	@Query("{ '_class' : ?0 }")
	List<Usuario> findAllByClass(String className);

	void deleteByUsername(String username);
}
