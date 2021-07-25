package ar.edu.unsam.consorciovirtual.service;

import ar.edu.unsam.consorciovirtual.domain.Gasto;
import ar.edu.unsam.consorciovirtual.domain.TipoRegistro;
import ar.edu.unsam.consorciovirtual.repository.GastoRepository;
import ar.edu.unsam.consorciovirtual.repository.ExpensaGeneralRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.YearMonth;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class GastoService {

    private final GastoRepository gastoRepository;
    private final ExpensaGeneralRepository expensaRepository;
    private final RegistroModificacionService registroModificacionService;
    private final UsuarioService usuarioService;

    private Boolean importeDeGastoValido(Gasto nuevoGasto){
        return (!nuevoGasto.tieneItems() || (nuevoGasto.tieneItems() && nuevoGasto.importeCoincideConSumaDeIntems()))
                && nuevoGasto.getImporte() != 0 ;
    }

    public void ingresarNuevoGasto(Gasto nuevoGasto){
        if(this.importeDeGastoValido(nuevoGasto)){
            gastoRepository.save(nuevoGasto);
        } else throw new IllegalArgumentException("Error en los importes ingresados");
    }

    public List<Gasto> buscarTodos(String palabraBuscada) {
        Double importe = busquedaToDouble(palabraBuscada);
        List<Gasto> gastos = gastoRepository.findByAnuladoFalseAndTituloContainingOrAnuladoFalseAndImporte(palabraBuscada, importe);
        gastos.forEach(this::agregarUltimaModificacion);
        return gastos;
    }

    private Double busquedaToDouble(String palabraBuscada) {
        try {
            return Double.valueOf(palabraBuscada);
        } catch (NumberFormatException ex){
            return null;
        }
    }

    public void registrarTodos(List<Gasto> gastos) {
        gastoRepository.saveAll(gastos);
    }

    public Gasto buscarPorId(Long id) {
        return gastoRepository.findById(id).orElseThrow(() -> new RuntimeException("Gasto no encontrado"));
    }

    @Transactional
    public Gasto modificar(Long idLogueado, Gasto gastoActualizado) {
        if(noExisteExpensaEnElPeriodo(gastoActualizado.getPeriodo())){
            registroModificacionService.guardarPorTipoYId(TipoRegistro.GASTO, gastoActualizado.getId(), usuarioService.getNombreYApellidoById(idLogueado));
            return gastoRepository.save(gastoActualizado);
        } else throw new IllegalArgumentException("No puede modificar un gasto que ya fue agregado a una expensa");
    }

    @Transactional
    public void bajaLogica(Long id){
        Gasto gasto = gastoRepository.findById(id).get();
        if(noExisteExpensaEnElPeriodo(gasto.getPeriodo())){
            gasto.setAnulado(true);
            registroModificacionService.eliminarTodosPorTipoYId(TipoRegistro.GASTO, id);
            gastoRepository.save(gasto);
        } else throw new IllegalArgumentException("No puede anular un gasto que ya fue agregado a una expensa");
    }

    private Boolean noExisteExpensaEnElPeriodo(YearMonth periodo){
        return expensaRepository.findByPeriodoAndAnuladaFalse(periodo).isEmpty();
    }

    public List<Gasto> buscarPorPeriodo(YearMonth periodo){
        return gastoRepository.findGastosByPeriodo(periodo);
    }

    private void agregarUltimaModificacion(@NotNull Gasto gasto){
        String fechaUltimaModificacion = registroModificacionService.getUltimaModificacion(TipoRegistro.GASTO, gasto.getId());
        gasto.setUltimaModificacion(fechaUltimaModificacion);
    }
}
