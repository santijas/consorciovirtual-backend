package ar.edu.unsam.consorciovirtual.controller;

import ar.edu.unsam.consorciovirtual.businessExceptions.DataConsistencyException;
import ar.edu.unsam.consorciovirtual.domain.Anuncio;
import ar.edu.unsam.consorciovirtual.domainDTO.AnuncioDTOParaListado;
import ar.edu.unsam.consorciovirtual.service.AnuncioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AnuncioRestController {

    private final AnuncioService anuncioService;

    @GetMapping("/anuncios")
    public List<AnuncioDTOParaListado> buscarTodos(@RequestParam(defaultValue="") String palabraBuscada) {
        return this.anuncioService.buscarTodos(palabraBuscada);
    }

    @GetMapping("/anuncios/vigentes")
    public List<AnuncioDTOParaListado> buscarTodosLosVigentes(@RequestParam(defaultValue="") String palabraBuscada) {
        return this.anuncioService.buscarTodosLosVigentes(palabraBuscada);
    }

    @GetMapping("/anuncios/{idAnuncio}")
    public Anuncio getAnuncioById(@PathVariable Long idAnuncio){ return anuncioService.getAnuncioById(idAnuncio);}

    @PutMapping("/anuncios/eliminar/{id}")
    public void bajaLogicaAnuncio(@RequestParam Long idLogueado, @PathVariable Long id) {
        anuncioService.bajaLogica(idLogueado, id);
    }

    //No se le pasa el autor desde el front, se le carga en el back por idAutor
    @PutMapping("/anuncios/crear/{idAutor}")
    public void createAnuncio(@PathVariable Long idAutor, @RequestBody Anuncio nuevoAnuncio) throws DataConsistencyException {
        anuncioService.crearAnuncio(idAutor, nuevoAnuncio);
    }

    /* No se le pasa el autor desde el front dado que no es necesario, solo utiliza el id del usuario
    que intenta la modificación para corroborar que es el mismo que lo creo, si no es el miemo tira
    una excepción */
    @PutMapping("/anuncios/modificar/{idLogueado}")
    public void modificarAnuncio(@PathVariable Long idLogueado, @RequestBody Anuncio anuncioActualizado) throws DataConsistencyException {
        anuncioService.modificarAnuncio(idLogueado, anuncioActualizado);
    }

}
