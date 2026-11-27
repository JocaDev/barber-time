package br.com.barbertime.service;

import br.com.barbertime.dao.ProfissionalDAO;
import br.com.barbertime.dao.ServicoDAO;
import br.com.barbertime.dto.DisponibilidadeDTO;
import br.com.barbertime.dto.ProfissionalRequestDTO;
import br.com.barbertime.dto.ServicoRequestDTO;
import br.com.barbertime.exception.RecursoNaoEncontradoException;
import br.com.barbertime.model.Barbearia;
import br.com.barbertime.model.Disponibilidade;
import br.com.barbertime.model.Profissional;
import br.com.barbertime.model.Servico;

import java.math.BigDecimal;
import java.util.List;

/** Profissionais e serviços da barbearia. Persistência via DAO. */
public class CatalogoService {

    private final Barbearia barbearia;
    private final ProfissionalDAO profissionais;
    private final ServicoDAO servicos;

    public CatalogoService(Barbearia barbearia, ProfissionalDAO profissionais, ServicoDAO servicos) {
        this.barbearia = barbearia;
        this.profissionais = profissionais;
        this.servicos = servicos;
    }

    public Barbearia getBarbearia() {
        return barbearia;
    }

    public synchronized List<Profissional> listarProfissionais() {
        return profissionais.listar();
    }

    public synchronized List<Servico> listarServicos() {
        return servicos.listar();
    }

    public synchronized Profissional buscarProfissional(Long id) {
        return profissionais.buscarPorId(id).orElseThrow(
                () -> new RecursoNaoEncontradoException("Profissional " + id + " não encontrado."));
    }

    public synchronized Servico buscarServico(Long id) {
        return servicos.buscarPorId(id).orElseThrow(
                () -> new RecursoNaoEncontradoException("Serviço " + id + " não encontrado."));
    }

    public synchronized Profissional cadastrar(ProfissionalRequestDTO dto) {
        if (dto == null || dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do profissional é obrigatório (RN02).");
        }
        Profissional p = new Profissional(null, dto.getNome(), dto.getEspecialidade(), dto.getTelefone());
        if (dto.getDisponibilidades() != null) {
            for (DisponibilidadeDTO d : dto.getDisponibilidades()) {
                if (d.getDiaSemana() == null || d.getHoraInicio() == null || d.getHoraFim() == null) {
                    throw new IllegalArgumentException("Disponibilidade incompleta.");
                }
                p.adicionarDisponibilidade(
                        new Disponibilidade(d.getDiaSemana(), d.getHoraInicio(), d.getHoraFim()));
            }
        }
        p.setBarbearia(barbearia); // RN13
        return profissionais.inserir(p);
    }

    public synchronized Servico cadastrar(ServicoRequestDTO dto) {
        if (dto == null || dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do serviço é obrigatório (RN02).");
        }
        if (dto.getPreco() == null || dto.getPreco().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço inválido.");
        }
        if (dto.getDuracaoMinutos() <= 0) {
            throw new IllegalArgumentException("A duração deve ser maior que zero.");
        }
        Servico s = new Servico(null, dto.getNome(), dto.getDescricao(),
                dto.getPreco(), dto.getDuracaoMinutos());
        s.setBarbearia(barbearia);
        return servicos.inserir(s);
    }
}
