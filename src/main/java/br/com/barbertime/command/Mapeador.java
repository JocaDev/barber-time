package br.com.barbertime.command;

import br.com.barbertime.dto.AgendamentoResponseDTO;
import br.com.barbertime.dto.DisponibilidadeDTO;
import br.com.barbertime.dto.ProfissionalResponseDTO;
import br.com.barbertime.dto.ServicoResponseDTO;
import br.com.barbertime.model.Agendamento;
import br.com.barbertime.model.Disponibilidade;
import br.com.barbertime.model.Profissional;
import br.com.barbertime.model.Servico;

import java.util.ArrayList;
import java.util.List;


public final class Mapeador {

    private Mapeador() {
    }

    public static AgendamentoResponseDTO paraDto(Agendamento a) {
        AgendamentoResponseDTO dto = new AgendamentoResponseDTO();
        dto.setId(a.getId());
        dto.setCodigoConfirmacao(a.getCodigoConfirmacao());
        dto.setStatus(a.getStatus());
        dto.setDataHoraInicio(a.getDataHoraInicio());
        dto.setDataHoraFim(a.getDataHoraFim());
        dto.setCriadoEm(a.getCriadoEm());
        dto.setClienteId(a.getCliente().getId());
        dto.setClienteNome(a.getCliente().getNome());
        dto.setProfissionalId(a.getProfissional().getId());
        dto.setProfissionalNome(a.getProfissional().getNome());
        dto.setServicoId(a.getServico().getId());
        dto.setServicoNome(a.getServico().getNome());
        dto.setServicoPreco(a.getServico().getPreco());
        if (a.getProfissional().getBarbearia() != null) {
            dto.setBarbeariaNome(a.getProfissional().getBarbearia().getNome());
        }
        return dto;
    }

    public static List<AgendamentoResponseDTO> agendamentosParaDto(List<Agendamento> lista) {
        List<AgendamentoResponseDTO> saida = new ArrayList<>();
        for (Agendamento a : lista) {
            saida.add(paraDto(a));
        }
        return saida;
    }

    public static ProfissionalResponseDTO paraDto(Profissional p) {
        ProfissionalResponseDTO dto = new ProfissionalResponseDTO();
        dto.setId(p.getId());
        dto.setNome(p.getNome());
        dto.setEspecialidade(p.getEspecialidade());
        dto.setTelefone(p.getTelefone());
        dto.setAtivo(p.isAtivo());
        if (p.getBarbearia() != null) {
            dto.setBarbeariaId(p.getBarbearia().getId());
        }
        List<DisponibilidadeDTO> disponibilidades = new ArrayList<>();
        for (Disponibilidade d : p.getDisponibilidades()) {
            DisponibilidadeDTO dd = new DisponibilidadeDTO();
            dd.setId(d.getId());
            dd.setDiaSemana(d.getDiaSemana());
            dd.setHoraInicio(d.getHoraInicio());
            dd.setHoraFim(d.getHoraFim());
            disponibilidades.add(dd);
        }
        dto.setDisponibilidades(disponibilidades);
        return dto;
    }

    public static ServicoResponseDTO paraDto(Servico s) {
        ServicoResponseDTO dto = new ServicoResponseDTO();
        dto.setId(s.getId());
        dto.setNome(s.getNome());
        dto.setDescricao(s.getDescricao());
        dto.setPreco(s.getPreco());
        dto.setDuracaoMinutos(s.getDuracaoMinutos());
        if (s.getBarbearia() != null) {
            dto.setBarbeariaId(s.getBarbearia().getId());
        }
        return dto;
    }
}
