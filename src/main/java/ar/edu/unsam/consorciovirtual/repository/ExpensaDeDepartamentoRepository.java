package ar.edu.unsam.consorciovirtual.repository;

import ar.edu.unsam.consorciovirtual.domain.ExpensaDeDepartamento;
import ar.edu.unsam.consorciovirtual.domain.ExpensaGeneral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;

public interface ExpensaDeDepartamentoRepository extends JpaRepository<ExpensaDeDepartamento, Long> {
    List<ExpensaDeDepartamento> findByAnuladaFalse();

    //TODO: Falta filtrado por períodos. Se va a hacer un desplegable en el front para que venga el tipo de dato que necesitamos
    List<ExpensaDeDepartamento> findByUnidadContainingAndAnuladaFalseOrEstadoContainingAndAnuladaFalse(String unidad, String estado);

    List<ExpensaDeDepartamento> findByPeriodoAndAnuladaFalse(YearMonth periodo);
}