package com.equipo3.SIGEVA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.equipo3.SIGEVA.dao.TokenUsuarioDao;
import com.equipo3.SIGEVA.model.TokenUsuario;

@CrossOrigin
@RestController
@RequestMapping("tokenUsuario")
public class TokenUsuarioController {

	@Autowired
	private TokenUsuarioDao tokenUsuarioDao;

	@PostMapping("/cogerId")
	public void cogerId(@RequestBody String idUsuarios) {
			tokenUsuarioDao.deleteAll();
			TokenUsuario tok = new TokenUsuario();
			idUsuarios=idUsuarios.substring(10,46);
			tok.setIdUsuario(idUsuarios);
			tokenUsuarioDao.save(tok);
	}
	@GetMapping("/getId")
	public String getId() {
		TokenUsuario tok = tokenUsuarioDao.findAll().get(0);
		return tok.getIdUsuario(); 
		
	}
}