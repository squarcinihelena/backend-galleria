package com.galleria.backend.service;

import com.galleria.backend.dto.request.ClienteRequestDTO;
import com.galleria.backend.dto.response.ClienteResponseDTO;
import com.galleria.backend.model.Cliente;
import com.galleria.backend.repository.ClienteRepository;
import com.galleria.backend.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    public ClienteService(ClienteRepository clienteRepository, PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public ClienteResponseDTO cadastrar(ClienteRequestDTO dto) {
        String cpfLimpo = normalizarCpf(dto.cpf());

        if (clienteRepository.existsByCpf(cpfLimpo)) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com este CPF.");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setCpf(cpfLimpo);
        cliente.setTelefone(dto.telefone());
        cliente.setAtivo(true);

        return toResponseDTO(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));
        return toResponseDTO(cliente);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));

        String novoCpf = normalizarCpf(dto.cpf());
        if (!cliente.getCpf().equals(novoCpf) && clienteRepository.existsByCpf(novoCpf)) {
            throw new IllegalArgumentException("O CPF já está em uso.");
        }

        cliente.setNome(dto.nome());
        cliente.setCpf(novoCpf);
        cliente.setTelefone(dto.telefone());

        return toResponseDTO(clienteRepository.save(cliente));
    }

    @Transactional
    public void remover(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));

        if (pedidoRepository.existsByClienteId(id)) {
            throw new IllegalStateException("Não é possível remover o cliente, pois possui pedidos associados.");
        }

        clienteRepository.delete(cliente);
    }

    private String normalizarCpf(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replace(".", "").replace("-", "").trim();
    }

    private ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getTelefone(),
                cliente.getAtivo()
        );
    }
}