package com.gestionevent.auth_service;

import com.gestionevent.auth_service.entities.User;
import com.gestionevent.auth_service.repositories.UserRepository;
import com.gestionevent.auth_service.services.JwtUtils;
import com.gestionevent.auth_service.services.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // On simule TOUTES les dépendances du contrôleur
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void testLoginShouldReturnOk() throws Exception {
        // 1. On prépare un utilisateur fictif
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setRole("STUDENT");
        mockUser.setPassword("password_hashed");

        // 2. On configure les simulations (Mocks) pour qu'ils disent "Oui"
        Mockito.when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(mockUser));

        Mockito.when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);

        Mockito.when(jwtUtils.generateToken(anyString(), anyString(), anyLong()))
                .thenReturn("fake-jwt-token");

        // 3. On lance l'appel avec le bon format JSON (email au lieu de username)
        String loginJson = "{\"email\":\"test@example.com\", \"password\":\"password\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk());
    }
    @Test
    void testLoginShouldReturnUnauthorized_WhenPasswordIsWrong() throws Exception {
        // 1. On prépare un utilisateur fictif qui existe en base
        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password_dans_la_base_hache");

        // 2. Simulation : L'utilisateur est TROUVÉ
        Mockito.when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(mockUser));

        // 3. Simulation : Le mot de passe NE CORRESPOND PAS (false)
        Mockito.when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(false);

        // 4. On envoie la requête
        String loginJson = "{\"email\":\"test@example.com\", \"password\":\"mauvais_password\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                // On vérifie que l'API répond bien 401 Unauthorized
                .andExpect(status().isUnauthorized());
    }
    @Test
    void testLoginShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // 1. Simulation : L'utilisateur n'est PAS trouvé (on renvoie un vide)
        Mockito.when(userRepository.findByEmail("inconnu@example.com"))
                .thenReturn(Optional.empty());

        // 2. On envoie la requête
        String loginJson = "{\"email\":\"inconnu@example.com\", \"password\":\"password123\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                // 3. On vérifie que l'API répond 404 Not Found
                .andExpect(status().isNotFound());
    }
    @Test
    void testRegisterStudentShouldReturnOk() throws Exception {
        // 1. On simule : l'email n'est pas encore pris
        Mockito.when(userRepository.findByEmail("nouveau@etudiant.com"))
                .thenReturn(Optional.empty());

        // 2. On prépare les données d'inscription
        String registerJson = """
        {
            "email": "nouveau@etudiant.com",
            "password": "password123",
            "fullName": "Rim karimi",
            "nomEtablissement": "ENSA",
            "filiere": "Génie Informatique"
        }
        """;

        mockMvc.perform(post("/api/auth/register/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                // 3. On vérifie que le statut est 200 OK
                .andExpect(status().isOk());

        // On vérifie que la méthode save() a bien été appelée une fois
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }
    @Test
    void testRegisterShouldFail_WhenEmailAlreadyExists() throws Exception {
        // 1. On simule : l'utilisateur EXISTE déjà
        Mockito.when(userRepository.findByEmail("existant@example.com"))
                .thenReturn(Optional.of(new User()));

        String registerJson = "{\"email\":\"existant@example.com\", \"password\":\"pass123\"}";

        mockMvc.perform(post("/api/auth/register/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                // 2. On vérifie que l'API bloque avec un 400 Bad Request
                .andExpect(status().isBadRequest());
    }
}