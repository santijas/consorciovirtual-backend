package ar.edu.unsam.consorciovirtual.controller;

import ar.edu.unsam.consorciovirtual.businessExceptions.DataConsistencyException;
import ar.edu.unsam.consorciovirtual.domain.Mensaje;
import ar.edu.unsam.consorciovirtual.domain.MensajeRequest;
import ar.edu.unsam.consorciovirtual.service.MensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin()
public class MensajeRestController {
    @Autowired
    private final MensajeService mensajeService;

    //Sin parámetro de busqueda, si lo hacemos tipo Wathsapp la busqueda podría ser en el front directamente.
    //DEFINIR: como limitamos la cantidad de mensajes que se cargan
    @GetMapping("/mensajes")
    public List<Mensaje> getMensajes(@RequestParam(defaultValue="") String palabraBuscada){
        return mensajeService.getMensajes(palabraBuscada);
    }

    @PostMapping("/mensajes/send")
    public void createMensaje( @RequestBody MensajeRequest mensaje) throws DataConsistencyException {
        mensajeService.createMensaje(mensaje);
    }

    @PostMapping("/mensajes/cantidad/{usuarioId}")
    public Long getCantidadDeMensajes(@PathVariable Long usuarioId){

        return mensajeService.getCantidadMensajes(usuarioId);
    }

    @PostMapping("/mensajes/registro/{userId}/{mensajeId}")
    public void setRegistroMensaje(@PathVariable Long userId, @PathVariable Long mensajeId){
        mensajeService.guardarRegistro(userId,mensajeId);
    }

    //No se le pasa el autor completo desde el front, se le carga en el back por idAutor
    /*No se le pasa el mensajeCitado completo desde el front,
    se le carga en el back por idMensajeCitado, que es pasado como requestParam (no es obligatorio pasar algo)*/
//    @PostMapping("/mensajes/create/{idAutor}")
//    public void createMensaje(@PathVariable Long idAutor, @RequestBody Mensaje mensajeNuevo, @RequestParam(required=false) Long idMensajeCitado){
//        mensajeService.createMensaje(idAutor, mensajeNuevo, idMensajeCitado);
//    }

    //DEFINIR: Si se puede eliminar mensaje (de poder hacerlo, creo que tiene que haber un límite de tiempo)

}
