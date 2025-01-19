package net.codejava.ProductManager;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AppControllerTest {

    @InjectMocks
    private AppController appController;

    @Mock
    private ProductService productService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRolesRepository userRolesRepository;

    @Mock
    private UserDetailsServiceImpl userService;



    @Mock
    private HttpSession session;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Model model;
    @Mock
    private User mockUser;
    @Mock
    private  PasswordEncryptionService ps;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setID(1);
        mockUser.setUsername("testuser");
        mockUser.setPassword("password");
        mockUser.setEmail("test@example.com");
        mockUser.setRole("admin");
        mockUser.setEnabled(true);
    }

    @Test
    public void testViewHomePage() {
        List<Product> mockProducts = new ArrayList<>();
        when(session.getAttribute("user")).thenReturn(mockUser);
        when(productService.listAll()).thenReturn(mockProducts);

        String viewName = appController.viewHomePage(model, session);

        assertEquals("index", viewName);
        verify(model).addAttribute("user", mockUser);
        verify(model).addAttribute("listProducts", mockProducts);
    }

    @Test
    public void testViewHomePageWithoutUser() {
        when(session.getAttribute("user")).thenReturn(null);

        String viewName = appController.viewHomePage(model, session);

        assertEquals("redirect:/login", viewName);
    }

    @Test
    public void testShowLoginForm() {
        String viewName = appController.showLoginForm();
        assertEquals("login", viewName);
    }

    @Test
    public void testProcessLoginSuccess() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Mocking the behavior of dependencies
        when(userService.loadUserByUsername("test@example.com")).thenReturn(mockUser);
        when(ps.verifyPassword(anyString(), anyString())).thenReturn(true);

        // Calling the method under test
        String viewName = appController.processLogin("test@example.com", "password", session);

        // Verifying the result and interactions
        assertEquals("redirect:/", viewName);
        verify(session).setAttribute("user", mockUser);
        verify(session).setAttribute("role", "admin");
    }


    @Test
    public void testProcessRegister() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Role userRole = new Role();
        userRole.setId(1);

        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(ps.hashPassword(anyString())).thenReturn("hashedPassword");

        String viewName = appController.processRegister(mockUser);

        assertEquals("register_success", viewName);
        verify(userRepository).save(mockUser);
        verify(userRolesRepository).save(any(UserRoles.class));
    }


    @Test
    public void testProcessLoginFailure() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Simulating the behavior when the user is not found
        when(userService.loadUserByUsername("test@example.com")).thenReturn(null);

        // Calling the method under test
        String viewName = appController.processLogin("test@example.com", "wrongpassword", session);

        // Verifying that the user is redirected to the login page on failure
        assertEquals("login", viewName);
    }


    @Test
    public void testLogout() {
        String viewName = appController.logout(session, response);

        assertEquals("redirect:/login", viewName);
        verify(session).invalidate();
        verify(response).addCookie(any(Cookie.class));
    }

    @Test
    public void testShowRegistrationForm() {
        String viewName = appController.showRegistrationForm(model);

        assertEquals("signup_form", viewName);
        verify(model).addAttribute(eq("user"), any(User.class));
    }



    @Test
    public void testShowNewProductPageWithAdmin() {
        when(session.getAttribute("user")).thenReturn(mockUser);

        String viewName = appController.showNewProductPage(model, session);

        assertEquals("new_product", viewName);
        verify(model).addAttribute(eq("product"), any(Product.class));
    }

    @Test
    public void testShowNewProductPageWithoutAdmin() {
        mockUser.setRole("user");
        when(session.getAttribute("user")).thenReturn(mockUser);

        String viewName = appController.showNewProductPage(model, session);

        assertEquals("403", viewName);
    }

    @Test
    public void testShowEditProductPage() {
        Product mockProduct = new Product();
        when(productService.get(1L)).thenReturn(mockProduct);

        ModelAndView mav = appController.showEditProductPage(1);

        assertEquals("edit_product", mav.getViewName());
        assertEquals(mockProduct, mav.getModel().get("product"));
    }

    @Test
    public void testDeleteProductWithAdmin() {
        when(session.getAttribute("user")).thenReturn(mockUser);

        String viewName = appController.deleteProduct(1, session);

        assertEquals("redirect:/", viewName);
        verify(productService).delete(1L);
    }

    @Test
    public void testDeleteProductWithoutAdmin() {
        mockUser.setRole("user");
        when(session.getAttribute("user")).thenReturn(mockUser);

        String viewName = appController.deleteProduct(1, session);

        assertEquals("403", viewName);
        verify(productService, never()).delete(anyLong());
    }

    @Test
    public void testSaveProduct() {
        Product mockProduct = new Product();

        String viewName = appController.saveProduct(mockProduct);

        assertEquals("redirect:/", viewName);
        verify(productService).save(mockProduct);
    }
}
