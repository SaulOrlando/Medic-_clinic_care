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
class ConsultaAdminIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    private MockHttpSession adminSession;

    @BeforeEach
    void setUp() throws Exception {
        usuarioRepository.deleteAll();

        Usuario admin = new Usuario();
        admin.setCorreo("admin@mediclinic.com");
        admin.setContrasena("12345678");
        admin.setRol(RolUsuario.ADMINISTRADOR);
        admin.setNombreCompleto("Admin Test");
        admin.setTelefono("1234567890");
        usuarioRepository.save(admin);

        MvcResult loginResult = mockMvc.perform(post("/login")
                        .param("username", "admin@mediclinic.com")
                        .param("password", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/panel"))
                .andReturn();

        adminSession = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertNotNull(adminSession, "La sesion debe existir despues del login");
    }

    @Test
    void adminPuedeAccederAConsultas() throws Exception {
        mockMvc.perform(get("/consultas").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(view().name("consultas"))
                .andExpect(model().attributeExists("consultas"))
                .andExpect(model().attributeExists("activePage"))
                .andExpect(model().attribute("activePage", "consultas"));
    }

    @Test
    void adminPuedeAccederAConsultasConBusqueda() throws Exception {
        mockMvc.perform(get("/consultas").session(adminSession)
                        .param("busqueda", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("consultas"))
                .andExpect(model().attributeExists("consultas"))
                .andExpect(model().attribute("busqueda", "test"));
    }

    @Test
    void usuarioNoLogueadoEsRedirigidoAlLoginDesdeConsultas() throws Exception {
        mockMvc.perform(get("/consultas"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
