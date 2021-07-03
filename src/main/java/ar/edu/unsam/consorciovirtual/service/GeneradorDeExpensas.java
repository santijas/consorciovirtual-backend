package ar.edu.unsam.consorciovirtual.service;

import ar.edu.unsam.consorciovirtual.domain.*;
import ar.edu.unsam.consorciovirtual.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.YearMonth;
import java.util.List;

import static ar.edu.unsam.consorciovirtual.domain.Constants.CARPETA_DE_EXPENSAS;

@RequiredArgsConstructor
@Service
@Transactional
public class GeneradorDeExpensas {

    private final GastoRepository gastoRepository;
    private final ExpensaGeneralRepository expensaGeneralRepository;
    private final ExpensaDeDepartamentoRepository expensaDeDepartamentoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final DocumentoRepository documentoRepository;
    private final UsuarioRepository usuarioRepository;


    public void generarExpensasPorImportePredefinido(Double importeComun, Double importeExtraordinaria, YearMonth periodo){
        generarExpensas(importeComun, importeExtraordinaria, periodo);
    }

    private Double calcularValorDeGastos(YearMonth periodo, String tipo){
        List<Double> gastos = gastoRepository.findImporteByPeriodoAndByTipo(periodo, tipo);
        return  gastos.stream()
                    .mapToDouble(a -> a)
                    .sum();
    }

    public void generarExpensasPorImporteDeGastos(YearMonth periodo){
        Double importeGastosComunes= (double)Math.round(calcularValorDeGastos(periodo, "Común")* 100d) / 100d;
        Double importeGastosExtraordinarias=(double)Math.round(calcularValorDeGastos(periodo, "Extraordinaria")* 100d) / 100d;

        generarExpensas(importeGastosComunes, importeGastosExtraordinarias, periodo);
    }

    @Transactional
    private void generarExpensas(Double importeComun, Double importeExtraordinaria, YearMonth periodo) {
        if(expensaGeneralRepository.findOneByPeriodoAndAnuladaFalse(periodo) != null){
            throw new IllegalArgumentException("Ya existe una expensa activa de ese periodo");
        }
        ExpensaGeneral expensaGeneral = new ExpensaGeneral();
        expensaGeneral.setPeriodo(periodo);
        expensaGeneral.setValorTotalExpensaExtraordinaria(importeExtraordinaria);
        expensaGeneral.setValorTotalExpensaComun(importeComun);
        expensaGeneralRepository.save(expensaGeneral);
        ExpensaGeneral expensaGeneralConId = expensaGeneralRepository.findOneByPeriodoAndAnuladaFalse(periodo);
        List<Departamento> departamentos = departamentoRepository.findByBajaLogicaFalse();

        List <Gasto> gastosDelPeriodo = gastoRepository.findGastosByPeriodo(periodo);
        Usuario administradorConsorcio = usuarioRepository.buscarAdministradorDeConsorcioActivo().orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        int x;
        for (x=0; x<departamentos.size(); x++){
            //Genera la expensa
            ExpensaDeDepartamento unaExpensa = new ExpensaDeDepartamento();
            unaExpensa.setExpensaGeneral(expensaGeneralConId);
            unaExpensa.setDepartamento(departamentos.get(x));
            unaExpensa.cargarImportesYPeriodo();
            unaExpensa.cargarUnidadDepto();
            //Genera el resumen de expensa
            String nombreSimple = "Expensa"+unaExpensa.getPeriodo().toString()+"-"+unaExpensa.getUnidad();
            String nombreArchivo = CARPETA_DE_EXPENSAS+nombreSimple+".pdf";
            CreadorDePDF.createResumenDeExpensa(unaExpensa, gastosDelPeriodo, nombreArchivo, departamentos, importeComun, importeExtraordinaria);
            Documento unDocumento = new Documento();
            unDocumento.setTitulo(nombreSimple);
            unDocumento.setDescripcion("Archivo PDF correpsondiente a: " + nombreSimple);
            unDocumento.setEnlaceDeDescarga(nombreArchivo);
            unDocumento.setAutor(administradorConsorcio);
            //Guarda el documento y lo trae para agregarlo a la expensa
            documentoRepository.save(unDocumento);
            Documento resumenExpensas = documentoRepository.findByTitulo(nombreSimple).orElseThrow(() -> new RuntimeException("Resumen de expensas no encontrado"));
            unaExpensa.setResumenDeExpensa(resumenExpensas);
            //Guarda la expensa.
            expensaDeDepartamentoRepository.save(unaExpensa);

        }
    }
}
