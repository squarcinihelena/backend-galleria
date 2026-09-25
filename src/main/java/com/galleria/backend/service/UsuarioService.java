package com.galleria.backend.service;

import com.galleria.backend.dto.request.LoginRequestDTO;
import com.galleria.backend.dto.request.UsuarioRequestDTO;
import com.galleria.backend.dto.response.LoginResponseDTO;
import com.galleria.backend.dto.response.UsuarioResponseDTO;
import com.galleria.backend.model.Usuario;
import com.galleria.backend.repository.UsuarioRepository;
import com.galleria.backend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByLogin(dto.login())) {
            throw new IllegalArgumentException("Já existe um usuario cadastrado com este login.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setLogin(dto.login());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setAtivo(true);

        return toResponseDTO(usuarioRepository.save(usuario));
    }

    public Optional<LoginResponseDTO> autenticar(LoginRequestDTO dto) {
        var credenciais = new UsernamePasswordAuthenticationToken(dto.login(), dto.senha());

        Authentication authentication = authenticationManager.authenticate(credenciais);

        if (authentication.isAuthenticated()) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(dto.login());

            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                String token = jwtService.generateToken(usuario.getLogin());

                return Optional.of(new LoginResponseDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getLogin(),
                        token
                ));
            }
        }

        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado com id: " + id));
        return toResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado com id: " + id));

        if (!usuario.getLogin().equals(dto.login()) && usuarioRepository.existsByLogin(dto.login())) {
            throw new IllegalArgumentException("Esse login já está sendo usado.");
        }

        usuario.setNome(dto.nome());
        usuario.setLogin(dto.login());
        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }

        return toResponseDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void remover(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado com id: " + id));
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getLogin(),
                usuario.getAtivo()
        );
    }
}