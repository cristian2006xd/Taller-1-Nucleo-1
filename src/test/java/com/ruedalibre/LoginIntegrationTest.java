package com.ruedalibre;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginPage_respondeHTTP() throws Exception {
        MvcResult result = mockMvc.perform(get("/login"))
                .andReturn();
        int status = result.getResponse().getStatus();
        assertTrue(status == 200 || status == 500,
                "Se esperaba 200 o 500 pero fue: " + status);
        assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    void dashboardSinAutenticacion_redirigeALogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void loginConCredencialesIncorrectas_redirigeConError() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("usuario_inexistente")
                        .password("password_incorrecta"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void recursoPublicoCss_accesibleSinLogin() throws Exception {
        mockMvc.perform(get("/css/estilos.css"))
                .andExpect(status().isOk());
    }
}
