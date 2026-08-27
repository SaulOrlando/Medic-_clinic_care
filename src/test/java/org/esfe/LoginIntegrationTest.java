package org.esfe;

import org.esfe.modelos.Usuario;
import org.esfe.modelos.enums.RolUsuario;
import org.esfe.repositorios.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setCorreo("saul@mediclinic.com");
        usuario.setContrasena("12345678");
        usuario.setRol(RolUsuario.ADMINISTRADOR);
        usuario.setNombreCompleto("Saul Administrador");
        usuario.setTelefono("1234567890");
        usuarioRepository.save(usuario);
    }

    @Test
    void usuarioLogueadoPuedeAccederAlPanel() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/login")
                        .param("username", "saul@mediclinic.com")
                        .param("password", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/panel"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertNotNull(session, "La sesion debe existir despues del login");

        mockMvc.perform(get("/panel").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("panel"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    void usuarioNoLogueadoEsRedirigidoAlLogin() throws Exception {
        mockMvc.perform(get("/panel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void credencialesInvalidasRedirigeAlLoginConError() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "saul@mediclinic.com")
                        .param("password", "wrongpassword")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }
}
