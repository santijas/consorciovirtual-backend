package ar.edu.unsam.consorciovirtual.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;

import static ar.edu.unsam.consorciovirtual.domain.Constants.ZONE_ID_ARGENTINA;

@Data
@Entity
public class Reclamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String asunto;
    private String mensaje;
    private LocalDate fecha = LocalDate.now(ZONE_ID_ARGENTINA);
    private String nombreAutor;

    @JsonIgnore
    private Boolean bajaLogica = false;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name="idAutor")
    private Usuario autor;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name="idEstado")
    private Estado estado;

    @JsonProperty("estado")
    private String getNombreEstado() {
        return estado.getNombreEstado();
    }

    @JsonProperty("autor")
    private String getNombreYApellidoAutor() {
        return autor.getNombreYApellido();
    }
}