package ar.edu.unsam.consorciovirtual.repository;

import ar.edu.unsam.consorciovirtual.domain.Reclamo;
import ar.edu.unsam.consorciovirtual.domain.SolicitudTecnica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReclamoRepository extends JpaRepository<Reclamo, Long> {

    List<Reclamo> findByIdAndBajaLogicaFalseOrAutorNombreContainingAndBajaLogicaFalseOrAutorApellidoContainingAndBajaLogicaFalseAndBajaLogicaFalseOrAsuntoContainingAndBajaLogicaFalseOrEstadoNombreEstadoContainingAndBajaLogicaFalse(Long id, String nombreAutor, String apellidoAutor, String asunto, String nombreEstado);    

    @Query(value = "SELECT * FROM reclamo r " +
            "INNER JOIN estado e ON r.id_estado = e.id " +
            "INNER JOIN usuario a ON r.id_autor = a.id " +
            "WHERE r.baja_logica = false " +
            "AND r.id_autor = :idUsuario " +
            "AND (r.id = :id " +
            "OR a.nombre LIKE %:nombreAutor% " +
            "OR e.nombre_estado LIKE %:nombreEstado% " +
            "OR r.asunto LIKE %:asunto%)" , nativeQuery=true)
    List<Reclamo> buscarPropios(@Param("idUsuario") Long idUsuario, @Param("id") Long id, @Param("nombreAutor") String nombreAutor, @Param("asunto") String asunto, @Param("nombreEstado") String nombreEstado);

    @Query(value = "SELECT * FROM reclamo r " +
            "INNER JOIN estado e ON r.id_estado = e.id " +
            "INNER JOIN usuario a ON r.id_autor = a.id " +
            "INNER JOIN departamento d ON d.id_propietario=:idUsuario " +
            "WHERE r.baja_logica = false " +
            "AND (r.id_autor=d.id_inquilino " +
            "OR r.id_autor=:idUsuario) " +
            "AND (r.id = :id " +
            "OR a.nombre LIKE %:nombreAutor% " +
            "OR e.nombre_estado LIKE %:nombreEstado% " +
            "OR r.asunto LIKE %:asunto%)" , nativeQuery=true)
    List<Reclamo> buscarPropiosODeMisInquilinos(@Param("idUsuario") Long idUsuario, @Param("id") Long id, @Param("nombreAutor") String nombreAutor, @Param("asunto") String asunto, @Param("nombreEstado") String nombreEstado);
}
