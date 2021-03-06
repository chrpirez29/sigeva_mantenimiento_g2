package com.equipo3.SIGEVA.controller;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.equipo3.SIGEVA.dao.CentroSaludDao;
import com.equipo3.SIGEVA.dao.CitaDao;
import com.equipo3.SIGEVA.dao.ConfiguracionCuposDao;
import com.equipo3.SIGEVA.dao.CupoDao;
import com.equipo3.SIGEVA.dao.UsuarioDao;
import com.equipo3.SIGEVA.dto.CentroSaludDTO;
import com.equipo3.SIGEVA.dto.CitaDTO;
import com.equipo3.SIGEVA.dto.CupoDTO;
import com.equipo3.SIGEVA.dto.PacienteDTO;
import com.equipo3.SIGEVA.dto.WrapperDTOtoModel;
import com.equipo3.SIGEVA.dto.WrapperModelToDTO;
import com.equipo3.SIGEVA.exception.CitaException;
import com.equipo3.SIGEVA.exception.CupoException;
import com.equipo3.SIGEVA.exception.IdentificadorException;
import com.equipo3.SIGEVA.exception.VacunaException;
import com.equipo3.SIGEVA.model.Cita;
import com.equipo3.SIGEVA.model.ConfiguracionCupos;
import com.equipo3.SIGEVA.model.Cupo;
import com.equipo3.SIGEVA.model.Usuario;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.logging.Logger.getLogger;

@CrossOrigin
@RestController
@RequestMapping("cita")
public class CitaController {

	@Autowired
	CitaDao citaDao;

	@Autowired
	CupoDao cupoDao;

	@Autowired
	UsuarioDao usuarioDao;

	@Autowired
	CupoController cupoController;

	@Autowired
	ConfiguracionCuposDao configuracionCuposDao;

	@Autowired
	WrapperModelToDTO wrapperModelToDTO;

	@Autowired
	WrapperDTOtoModel wrapperDTOtoModel;

	@Autowired
	CentroSaludDao centroSaludDao;

	final Logger LOG = getLogger(com.equipo3.SIGEVA.controller.CitaController.class.toString()) ;

	private static final int PRIMERA_DOSIS = 1;
	private static final int SEGUNDA_DOSIS = 2;

	/**
	 * M??todo encargado de buscar y asignar citas. En funci??n de las dosis aplicadas
	 * y citas futuras del paciente, procurar?? asignar m??s o menos cantidad de
	 * citas.
	 * 
	 * @param uuidPaciente
	 * @return List<CitaDTO>
	 */
	@SuppressWarnings("deprecation")
	@GetMapping("/buscarYAsignarCitas")
	public List<CitaDTO> buscarYAsignarCitas(@RequestParam String uuidPaciente) {
		if (uuidPaciente == null) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "UUID no contemplado.");
		}

		PacienteDTO pacienteDTO = null;
		try {
			pacienteDTO = wrapperModelToDTO.getPacienteDTOfromUuid(uuidPaciente);
		} catch (IdentificadorException e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no contemplado en BBDD.");
		}

		if (new Date().after(Condicionamientos.fechaFin())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"A partir del " + Condicionamientos.fechaFin() + " ya no se vacunaba gente.");
		}

		int dosisAplicadas = pacienteDTO.getNumDosisAplicadas();

		if (dosisAplicadas == 2) { // CASO 1.

			throw new ResponseStatusException(HttpStatus.CONFLICT, "??El paciente ya estaba vacunado de las dos dosis!");

		} else if (dosisAplicadas == 1) { // CASO 2.

			// Objetivo: Comprobar con respecto a la segunda cita.

			List<CitaDTO> citasFuturasDTO = null;

			citasFuturasDTO = obtenerCitasFuturasDelPaciente(pacienteDTO.getIdUsuario());

			int numCitasFuturasAsignadas = citasFuturasDTO.size();

			if (numCitasFuturasAsignadas == 1) { // CASO 2.1. Segunda cita ya asignada.
				throw new ResponseStatusException(HttpStatus.CONFLICT,
						"El paciente ya est?? vacunado de la primera, y ya tiene programada la segunda cita para el "
								+ citasFuturasDTO.get(0).getCupo().getFechaYHoraInicio() + ".");

			} else { // CASO 2.2. numCitasFuturasAsignadas = 0

				return caso22(pacienteDTO);

			}
		} else { // CASO 3. dosisAplicadas == 0
			// Objetivo: Asegurarse de asignar la primera y segunda cita.

			List<CitaDTO> citasFuturasDTO = null;

			citasFuturasDTO = obtenerCitasFuturasDelPaciente(pacienteDTO.getIdUsuario());

			int numCitasFuturasAsignadas = citasFuturasDTO.size();

			if (numCitasFuturasAsignadas == 2) { // CASO 3.1.
				throw new ResponseStatusException(HttpStatus.CONFLICT,
						"Sus dos citas ya est??n programadas: " + citasFuturasDTO.get(0).getCupo().getFechaYHoraInicio()
								+ " y " + citasFuturasDTO.get(1).getCupo().getFechaYHoraInicio() + ".");

			} else if (numCitasFuturasAsignadas == 1) { // CASO 3.2.

				return caso32(pacienteDTO, citasFuturasDTO);

			} else { // CASO 3.3. numCitasFuturasAsignadas == 0.

				return caso33(pacienteDTO); // Con las dos citas.

			}

		}

		// Revisar con respecto al c??digo de confirmaci??n del primer sprint.
	}

	@SuppressWarnings("deprecation")
	private List<CitaDTO> caso32(PacienteDTO pacienteDTO, List<CitaDTO> citasFuturasDTO){
		// CASO 3.2.1.
		if (pacienteDTO.getCentroSalud().getNumVacunasDisponibles() < 1) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "El centro de salud no tiene stock (1).");
		}

		Date fechaPrimeraDosis = citasFuturasDTO.get(0).getCupo().getFechaYHoraInicio();
		Date fechaSegundaDosis = (Date) fechaPrimeraDosis.clone();

		fechaSegundaDosis.setDate(fechaSegundaDosis.getDate() + Condicionamientos.tiempoEntreDosis());

		// CASO 3.2.2.
		if (fechaSegundaDosis.after(Condicionamientos.fechaFin())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Respetando los " + Condicionamientos.tiempoEntreDosis() + " d??as desde la primera dosis ("
							+ fechaPrimeraDosis + "), a partir del " + Condicionamientos.fechaFin()
							+ " no habr??n vacunaciones.");
		}

		// CASO 3.2.3.
		CupoDTO cupoLibre = wrapperModelToDTO.cupoToCupoDTO(
				cupoController.buscarPrimerCupoLibreAPartirDe(pacienteDTO.getCentroSalud(), fechaSegundaDosis));
		// ??Lanzar?? exception avisando de que no hay hueco en ese caso!

		// CASO 3.2.4.
		CitaDTO citaProgramadaSegundaDosis = confirmarCita(cupoLibre, pacienteDTO, SEGUNDA_DOSIS);
		List<CitaDTO> lista = new ArrayList<>();
		lista.add(citaProgramadaSegundaDosis);
		// El paciente no tiene dosis aplicadas, pero ya ten??a programada la cita futura
		// para su primera dosis.

		return lista;
	}

	@SuppressWarnings("deprecation")
	private List<CitaDTO> caso33(PacienteDTO pacienteDTO){
		// CASO 3.3.1.
		if (pacienteDTO.getCentroSalud().getNumVacunasDisponibles() < 2) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"El centro de salud no tiene suficiente stock (2).");
		}

		// 3.3.2. Autom??tico

		// 3.3.3.
		CupoDTO primerCupoLibreDTO = wrapperModelToDTO.cupoToCupoDTO(
				cupoController.buscarPrimerCupoLibreAPartirDe(pacienteDTO.getCentroSalud(), new Date()));
		// 3.3.3.1. Si no se encuentra hueco, ya lanza ResponseStatusException.

		// 3.3.3.2.
		CitaDTO primeraCitaDTO = confirmarCita(primerCupoLibreDTO, pacienteDTO, 1);
		List<CitaDTO> lista = new ArrayList<>();
		lista.add(primeraCitaDTO);

		// 3.3.4.
		Date fechaPrimeraCita = primeraCitaDTO.getCupo().getFechaYHoraInicio();
		Date fechaSegundaCita = (Date) fechaPrimeraCita.clone();
		fechaSegundaCita.setDate(fechaSegundaCita.getDate() + Condicionamientos.tiempoEntreDosis());
		if (fechaSegundaCita.after(Condicionamientos.fechaFin())) {
			// La fecha de la segunda cita supera la fecha m??xima.
			return lista; // Con solo la primera dosis.
		}

		// 3.3.5.
		CupoDTO cupoLibreDTOSegundaCita = wrapperModelToDTO.cupoToCupoDTO(
				cupoController.buscarPrimerCupoLibreAPartirDe(pacienteDTO.getCentroSalud(), fechaSegundaCita));
		// 3.3.5.1. Si no se encuentra hueco, ya lanza ResponseStatusException.

		// 3.3.5.2.
		CitaDTO segundaCitaDTO = confirmarCita(cupoLibreDTOSegundaCita, pacienteDTO, 2);
		lista.add(segundaCitaDTO);

		return lista;
	}

	@SuppressWarnings("deprecation")
	public List<CitaDTO> caso22(PacienteDTO pacienteDTO){
		// Objetivo: Asignar la segunda 21 d??as despu??s de la primera.

		// CASO 2.2.1.
		if (pacienteDTO.getCentroSalud().getNumVacunasDisponibles() < 1) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "El centro de salud no tiene stock (1).");
		}

		CitaDTO citaPrimerPinchazo = buscarUltimaCitaPinchazo(pacienteDTO, PRIMERA_DOSIS);

		Date fechaPrimerPinchazo = citaPrimerPinchazo.getCupo().getFechaYHoraInicio();
		Date fechaMinimaSegundoPinchazo = (Date) fechaPrimerPinchazo.clone();
		fechaMinimaSegundoPinchazo
				.setDate(fechaMinimaSegundoPinchazo.getDate() + Condicionamientos.tiempoEntreDosis());

		// CASO 2.2.2.
		if (fechaMinimaSegundoPinchazo.after(Condicionamientos.fechaFin())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Respetando los " + Condicionamientos.tiempoEntreDosis() + " d??as desde la primera dosis ("
							+ citaPrimerPinchazo.getCupo().getFechaYHoraInicio() + "), a partir del "
							+ Condicionamientos.fechaFin() + " no habr??n vacunaciones.");
		}

		// CASO 2.2.3.
		CupoDTO cupoLibre = wrapperModelToDTO.cupoToCupoDTO(cupoController
				.buscarPrimerCupoLibreAPartirDe(pacienteDTO.getCentroSalud(), fechaMinimaSegundoPinchazo));
		// ??Lanzar?? exception avisando de que no hay hueco en ese caso!

		// CASO 2.2.4.
		CitaDTO citaProgramadaSegundaDosis = confirmarCita(cupoLibre, pacienteDTO, SEGUNDA_DOSIS);
		List<CitaDTO> lista = new ArrayList<>();
		lista.add(citaProgramadaSegundaDosis);
		// El paciente ya ten??a aplicada la primera dosis.

		return lista;
	}

	/**
	 * M??todo que sirve para confirmar una cita, es decir, programar una cita de un
	 * paciente concreto en un cupo concreto.
	 * 
	 * @param cupoDTO
	 * @param pacienteDTO
	 * @param dosis
	 * @return
	 */
	@SuppressWarnings("static-access")
	public CitaDTO confirmarCita(CupoDTO cupoDTO, PacienteDTO pacienteDTO, int dosis) {

		CitaDTO citaDTO = new CitaDTO(cupoDTO, pacienteDTO, dosis);

		ConfiguracionCupos configuracionCupos = configuracionCuposDao.findAll().get(0);
		try {
			cupoDTO.incrementarTamanoActual(configuracionCupos.getNumeroPacientes());
		} catch (CupoException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Error del servidor. " + e);
			// No deber??a saltar la excepci??n, salvo por cuestiones de concurrencia, porque
			// solamente traemos cupos libres.
		}

		cupoDao.save(wrapperDTOtoModel.cupoDTOToCupo(cupoDTO));
		citaDao.save(wrapperDTOtoModel.citaDTOToCita(citaDTO));

		return citaDTO;
	}

	/**
	 * Dado que un paciente que s?? tenga dosis aplicadas deber?? tener al menos una
	 * cita antigua con respecto a la dosis aplicada; para asignar la cita de la
	 * segunda dosis, en caso de ya estar aplicada la primera, se busca cu??l fue la
	 * primera cita para empezar a buscar a partir de 21 d??as.
	 * 
	 * @param pacienteDTO
	 * @param dosis
	 * @return
	 */
	public CitaDTO buscarUltimaCitaPinchazo(PacienteDTO pacienteDTO, int dosis) {
		List<CitaDTO> citasAntiguasDTO = obtenerCitasAntiguasPaciente(pacienteDTO);
		/*
		 * Ya que el paciente est?? vacunado de la primera (al estarlo, no puede
		 * programar de nuevo la primera), entendemos que la cita del pinchazo fue la
		 * ??ltima cita de esa dosis.
		 */
		CitaDTO citaPinchazo = null;
		for (int i = citasAntiguasDTO.size() - 1; i >= 0 && citaPinchazo == null; i--) {
			// Empezando desde el final.
			CitaDTO cita = citasAntiguasDTO.get(i);
			if (cita.getDosis() == dosis) {
				citaPinchazo = cita;
			}
		}
		return citaPinchazo;
	}

	/**
	 * Una cita, solamente se puede modificar en un rango de d??as concreto, para
	 * poder respetar los 21 d??as.
	 * 
	 * @param uuidCita
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@GetMapping("/buscarDiasModificacionCita")
	public List<Date> buscarDiasModificacionCita(@RequestParam String uuidCita) {
		if (uuidCita != null) {
			List<Date> lista = new ArrayList<>();
			CitaDTO citaDTO = new CitaDTO();
			try {
				citaDTO = wrapperModelToDTO.getCitaDTOfromUuid(uuidCita);

				if (citaDTO.getDosis() == PRIMERA_DOSIS) { // En caso de ser la primera, da igual.
					lista = priemeraDosis(lista, citaDTO);

				} else { // SEGUNDA_DOSIS // En el caso de ser la segunda, +21 d??as.

					List<CitaDTO> listaCitasFuturas = obtenerCitasFuturasDelPaciente(citaDTO.getPaciente().getIdUsuario());

					CitaDTO citaPrimerPinchazo = null;

					if (listaCitasFuturas.size() == 2) {
						citaPrimerPinchazo = listaCitasFuturas.get(0);
					} else {
						citaPrimerPinchazo = buscarUltimaCitaPinchazo(citaDTO.getPaciente(), PRIMERA_DOSIS);
					}

					Date fechaPrimerPinchazo = citaPrimerPinchazo.getCupo().getFechaYHoraInicio();
					Date fechaMinimaSegundoPinchazo = (Date) fechaPrimerPinchazo.clone();
					fechaMinimaSegundoPinchazo
							.setDate(fechaMinimaSegundoPinchazo.getDate() + Condicionamientos.tiempoEntreDosis());
					lista.add(fechaMinimaSegundoPinchazo);
					lista.add(Condicionamientos.fechaFin());

				}
				return lista;
			} catch (IdentificadorException e) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "DTO to Model parsing error");
			}

		} else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "UUID no contemplado.");
		}
	}

	@SuppressWarnings("deprecation")
	private List<Date> priemeraDosis(List<Date> lista, CitaDTO citaDTO){
		Date hoy = new Date();
		if (Condicionamientos.buscarAPartirDeManana())
			hoy.setDate(hoy.getDate() + 1);
		lista.add(hoy); // Desde hoy

		/*
		 * Si tiene segunda cita, es hasta el d??a anterior a la segunda; y si no, fecha
		 * fin:
		 */
		List<CitaDTO> citasDTO = null;

		citasDTO = obtenerCitasFuturasDelPaciente(citaDTO.getPaciente().getIdUsuario());

		if (citasDTO.size() == 2) {
			Date fechaSegundaCita = citasDTO.get(1).getCupo().getFechaYHoraInicio();
			Date fechaMaxima = (Date) fechaSegundaCita.clone();
			fechaMaxima.setDate(fechaMaxima.getDate() - 1);
			lista.add(fechaMaxima); // Hasta el d??a antes de la segunda cita
		} else {
			lista.add(Condicionamientos.fechaFin()); // Hasta el d??a tope
		}

		return lista;
	}

	/**
	 * Este m??todo devuelve las citas de exactamente una fecha (d??a) en concreto, de
	 * un centro. Servir?? para saber qu?? citas se vacunan.
	 * 
	 * @param centroSaludDTOJson
	 * @param fechaJson
	 * @return
	 */
	@GetMapping(value = "/obtenerCitasFecha")
	public List<CitaDTO> obtenerCitasFecha(@RequestParam(name = "centroSaludDTO") String centroSaludDTOJson,
			@RequestParam(name = "fecha") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") String fechaJson) {
		if (!centroSaludDTOJson.isEmpty() && !fechaJson.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			CentroSaludDTO centroSaludDTO = null;
			Date fecha = null;

			try {
				centroSaludDTO = mapper.readValue(centroSaludDTOJson, CentroSaludDTO.class);
				fecha = mapper.readValue(fechaJson, Date.class);
			} catch (JsonProcessingException j) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de fecha inv??lido");
			}
			/*
			 * (Posiblemente se exija desde el frontend pasar String y parseo de fecha de
			 * String a Date).
			 */

			// (La hora de la fecha no importa, solamente importa el d??a)

			List<CupoDTO> cuposDTO = cupoController.buscarTodosCuposFecha(centroSaludDTO, fecha);

			List<Cita> citas = new ArrayList<>();
			for (int i = 0; i < cuposDTO.size(); i++) {
				citas.addAll(citaDao.buscarCitasDelCupo(cuposDTO.get(i).getUuidCupo()));
			}

			List<CitaDTO> citasDTO = wrapperModelToDTO.allCitaToCitaDTO(citas);
			Collections.sort(citasDTO); // Ordenaci??n

			return citasDTO;

		} else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"Centro de salud o fecha no contemplada en el par??metro.");
		}
	}

	/**
	 * Este m??todo devuelve las citas futuras de un paciente en concreto. Servir??
	 * para poder consultar sus citas.
	 * 
	 * @param idPaciente
	 * @return
	 */
	@GetMapping("/obtenerCitasFuturasDelPaciente")
	public List<CitaDTO> obtenerCitasFuturasDelPaciente(@RequestParam String idPaciente) {
		if (idPaciente == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El par??metro de entrada no se contemplaba");
		}
		Optional<Usuario> optUsuario = usuarioDao.findById(idPaciente);
		if (optUsuario.isPresent()) {
			List<CitaDTO> citasDTO = wrapperModelToDTO.allCitaToCitaDTO(citaDao.buscarCitasDelPaciente(idPaciente));
			List<CitaDTO> citasSeleccionadas = new ArrayList<>();
			for (int i = 0; i < citasDTO.size(); i++) {
				if (!citasDTO.get(i).getCupo().getFechaYHoraInicio().before(new Date()) &&
						citasDTO.get(i).getPaciente().getNumDosisAplicadas() != citasDTO.get(i).getDosis()) { // ??Es antigua?
					citasSeleccionadas.add(citasDTO.get(i));
				}
			}
			Collections.sort(citasSeleccionadas);
			return citasSeleccionadas;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado en la bbdd");
		}
	}

	/**
	 * Este m??todo devuelve las citas antiguas (ya celebradas) de un paciente en
	 * concreto. Servir?? para poder consultar sus citas antiguas ante ciertas
	 * necesidades.
	 * 
	 * @param pacienteDTO
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public List<CitaDTO> obtenerCitasAntiguasPaciente(PacienteDTO pacienteDTO) {
		List<CitaDTO> citasDTO = wrapperModelToDTO
				.allCitaToCitaDTO(citaDao.buscarCitasDelPaciente(pacienteDTO.getIdUsuario()));
		List<CitaDTO> citasSeleccionadas  = new ArrayList<>();
		for (int i = 0; i < citasDTO.size(); i++) {
			if (!citasDTO.get(i).getCupo().getFechaYHoraInicio().after(new Date())) { // ??Es antigua?
				citasSeleccionadas.add(citasDTO.get(i));
			}
		}

		Collections.sort(citasSeleccionadas);
		return citasSeleccionadas;
	}

	/**
	 * Este m??todo ayuda a eliminar una cita en concreto dadas unas condiciones.
	 * 
	 * @param uuidCita
	 */
	@SuppressWarnings("static-access")
	@DeleteMapping("/eliminarCita/{uuidCita}")
	public void eliminarCita(@PathVariable String uuidCita) {
		CitaDTO citaDTO = null;
		try {
			citaDTO = wrapperModelToDTO.getCitaDTOfromUuid(uuidCita);
		} catch (IdentificadorException e) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
		}

		// ??Era primera o segunda?
		int dosis = citaDTO.getDosis();

		citaDao.deleteById(citaDTO.getUuidCita());
		try {
			cupoController.decrementarTamanoActualCupo(citaDTO.getCupo().getUuidCupo());
		} catch (CupoException e) {
			// Cupo no existente en la BD.
			LOG.log(Level.INFO, e.getMessage());
		}

		// Si se elimina la primera dosis, la segunda pasa a la primera.
		// En caso de ser primera, y haber segunda cita programada, ??sta pasa a primera:
		if (dosis == 1) {
			PacienteDTO pacienteDTO = citaDTO.getPaciente();

			List<CitaDTO> citasDTO = null;
			citasDTO = obtenerCitasFuturasDelPaciente(pacienteDTO.getIdUsuario()); // Nueva consulta BD

			if (citasDTO.size() == 1) { // La primera se acaba de borrar y esta es la segunda.
				CitaDTO segundaCitaDTO = citasDTO.get(0);
				if (segundaCitaDTO.getDosis() == 2) { // Precauci??n - Seguridad
					segundaCitaDTO.setDosis(1);
					Cita segundaCita = wrapperDTOtoModel.citaDTOToCita(segundaCitaDTO);
					citaDao.save(segundaCita); // Modificaci??n
				}
			}
		}

	}

	/**
	 * El m??todo ayudar?? a eliminar todas las citas incluidas en la lista del
	 * par??metro.
	 * 
	 * @param citasDTO
	 */
	public void eliminarCitas(List<CitaDTO> citasDTO) {
		for (int i = 0; i < citasDTO.size(); i++) {
			eliminarCita(citasDTO.get(i).getUuidCita());
		}
	}

	/**
	 * El m??todo ayudar?? a eliminar (desprogramar) todas las citas futuras de un
	 * paciente, dejando evidentemente sucesivos huecos.
	 * 
	 * @param paciente
	 */
	@PutMapping("/eliminarCitasFuturasDelPaciente")
	public void eliminarCitasFuturasDelPaciente(@RequestBody PacienteDTO paciente) {
		eliminarCitas(obtenerCitasFuturasDelPaciente(paciente.getIdUsuario()));
	}

	/**
	 * El m??todo ayudar?? a eliminar todas las citas (todas) de un paciente.
	 * 
	 * @param pacienteDTO
	 */
	public void eliminarTodasLasCitasDelPaciente(PacienteDTO pacienteDTO) {
		try {
			eliminarCitas(
					wrapperModelToDTO.allCitaToCitaDTO(citaDao.buscarCitasDelPaciente(pacienteDTO.getIdUsuario())));
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}

	}

	/**
	 * El m??todo ayudar?? a eliminar todas las citas del cupo introducido por
	 * par??metro.
	 * 
	 * @param uuidCupo
	 */
	public void eliminarTodasLasCitasDelCupo(String uuidCupo) {
		citaDao.deleteAllByUuidCupo(uuidCupo);
		try {
			cupoController.anularTamanoActual(uuidCupo);
		} catch (CupoException e) {
			LOG.log(Level.INFO, e.getMessage());
		}
	}

	/**
	 * Recurso web para la modficaci??n de la cita de un paciente
	 * 
	 * @param idCita    Identificador de la cita que se va a modificar
	 * @param cupoNuevo Identificador del nuevo cupo al que va a pertenecer la cita
	 *                  del paciente y el cual contiene la fecha y hora de la
	 *                  vacunaci??n
	 */
	@GetMapping("/modificarCita")
	public void modificarCita(@RequestParam String idCita, @RequestParam String cupoNuevo) {
		try {
			Cita cita = null;
			Cupo cupo = null;

			Optional<Cita> optCita = citaDao.findById(idCita);
			if (optCita.isPresent()) {
				cita = optCita.get();
			} else {
				throw new CitaException("La cita que se intenta modificar no existe");
			}

			Optional<Cupo> optCupo1 = cupoDao.findById(cita.getUuidCupo());
			if (optCupo1.isPresent()) {
				cupo = optCupo1.get();
			} else {
				throw new CupoException("El cupo que tiene asociado la cita no existe");
			}

			cupo.setTamanoActual(cupo.getTamanoActual() - 1);
			cupoDao.save(cupo);

			Optional<Cupo> optCupo2 = cupoDao.findById(cupoNuevo);
			if (optCupo2.isPresent()) {
				cupo = optCupo2.get();
			} else {
				throw new CupoException("El cupo no existe");
			}

			cupo.setTamanoActual(cupo.getTamanoActual() + 1);
			cita.setUuidCupo(cupoNuevo);

			cupoDao.save(cupo);
			citaDao.save(cita);

		} catch (CitaException a) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, a.getMessage());
		} catch (CupoException b) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, b.getMessage());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}

	/**
	 * Este m??todo ejecutar?? la vacunaci??n de una cierta dosis de un paciente en
	 * concreto, dada la cita de vacunaci??n.
	 * 
	 * @param cita
	 */
	@PostMapping("/vacunar")
	public void vacunarPaciente(@RequestBody CitaDTO cita) {
		try {
			cita.getPaciente().setNumDosisAplicadas(cita.getPaciente().getNumDosisAplicadas() + 1);

			if (cita.getPaciente().getNumDosisAplicadas() > 2)
				throw new VacunaException("El paciente no puede tener m??s de dos dosis aplicadas");

			CentroSaludDTO centroSalud = cita.getCupo().getCentroSalud();
			centroSalud.decrementarNumVacunasDisponibles();
			if (cita.getDosis() == cita.getPaciente().getNumDosisAplicadas()) {
				this.usuarioDao.save(this.wrapperDTOtoModel.pacienteDTOtoPaciente(cita.getPaciente()));
				centroSaludDao.save(this.wrapperDTOtoModel.centroSaludDTOtoCentroSalud(centroSalud));
			} else {
				throw new VacunaException("Las dosis del paciente no coinciden con la dosis supuesta para la cita");
			}
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}

	}

	/**
	 * El m??todo ayudar?? a crear una nueva cita en la base de datos, pudiendo
	 * existir otras alternativas m??s o menos directas.
	 * 
	 * @param cita
	 */
	@SuppressWarnings("static-access")
	public void crearCita(CitaDTO cita) {
		citaDao.save(wrapperDTOtoModel.citaDTOToCita(cita));
	}

}
