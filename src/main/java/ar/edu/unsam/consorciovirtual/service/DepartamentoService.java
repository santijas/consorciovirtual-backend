package ar.edu.unsam.consorciovirtual.service;

import ar.edu.unsam.consorciovirtual.domain.Departamento;
import ar.edu.unsam.consorciovirtual.domain.DepartamentoDTOParaListado;
import ar.edu.unsam.consorciovirtual.domain.TipoRegistro;
import ar.edu.unsam.consorciovirtual.domain.Usuario;
import ar.edu.unsam.consorciovirtual.repository.DepartamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final UsuarioService usuarioService;
    private final RegistroModificacionService registroModificacionService;

    public List<DepartamentoDTOParaListado> buscarTodos(String palabraBuscada) {
        List<Departamento> departamentos = departamentoRepository.findByNroDepartamentoContainingAndBajaLogicaFalseOrNombrePropietarioContainingAndBajaLogicaFalseOrNombreInquilinoContainingAndBajaLogicaFalseOrPisoContainingAndBajaLogicaFalse(palabraBuscada, palabraBuscada, palabraBuscada, palabraBuscada);
        return departamentos.stream().map(x -> DepartamentoDTOParaListado.fromDepartamento(x)).collect(Collectors.toList());
    }

    public List<Departamento> registrarTodos(List <Departamento> listaDepartamentos) {
        return departamentoRepository.saveAll(listaDepartamentos);
    }

    public Departamento buscarPorId(Long id) {
        return departamentoRepository.findById(id).orElseThrow(() -> new RuntimeException("Departamento no encontrado"));
    }

    public List<Departamento> buscarPorUsuario(Long id) {
        return departamentoRepository.buscarPorUsuario(id);
    }

    public Departamento modificarDepartamento(Departamento departamento) {
        Departamento updatedDepartment = asignarPropietarioEInquilino(departamento);
        registroModificacionService.guardarPorTipoYId(TipoRegistro.DEPARTAMENTO, departamento.getId());
        return departamentoRepository.save(updatedDepartment);
    }

    public Departamento registrarDepartamento(Departamento departamento) {
        Departamento newDepartment = asignarPropietarioEInquilino(departamento);
        return departamentoRepository.save(newDepartment);
    }

    private Departamento asignarPropietarioEInquilino(Departamento departamento){
        System.out.println(departamento);
        Usuario _propietario = usuarioService.buscarPorId(departamento.getPropietario().getId());

        try{
            Usuario _inquilino = usuarioService.buscarPorId(departamento.getInquilino().getId());
            departamento.setNombreInquilino(_inquilino.getNombreYApellido());
        } catch (RuntimeException ignored){}

        departamento.setNombrePropietario(_propietario.getNombreYApellido());
        return departamento;
    }

    public void bajaLogica(Long id){
        Departamento departamento = departamentoRepository.findById(id).get();
        departamento.setBajaLogica(true);

        departamentoRepository.save(departamento);
    }

    public long count(){
        return departamentoRepository.count();
    }

}
