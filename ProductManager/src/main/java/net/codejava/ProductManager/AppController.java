package net.codejava.ProductManager;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

@Controller
public class AppController {

    @Autowired
    private ProductService service;

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserRolesRepository userRolesRepo;

    @Autowired
    private UserDetailsServiceImpl userService;

    // Home Page
    @RequestMapping("/")
    public String viewHomePage(Model model, HttpSession session) {
        // Get the logged-in user from the session
        User user = getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        // Pass the user and roles to the model for the frontend
        model.addAttribute("user", user);

        List<Product> listProducts = service.listAll();
        model.addAttribute("listProducts", listProducts);
        return "index";
    }

    // Login Page
    @RequestMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/process_login")
    public String processLogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
        User user = userService.loadUserByUsername(email);
        if (user != null && user.getPassword().equals(password)) {
            // Store user and role in session
            session.setAttribute("user", user);
            session.setAttribute("role", user.getRole());

            // Print user and role information for debugging
            System.out.println("User: " + user.getUsername());
            System.out.println("Roles: " + user.getRoles());
            return "redirect:/"; // Successful login, redirect to home
        }

        // Login failed
        return "login"; // Return to login page
    }

    // Logout
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalidate session
        return "redirect:/login"; // Redirect to login page after logout
    }

    // Register Page
    @RequestMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "signup_form";
    }

    @PostMapping("/process_register")
    public String processRegister(User user) {
        // Enable the user
        user.setEnabled(true);

        // Assign default role (e.g., "USER" with roleId=1)
        Role userRole = new Role();
        userRole.setId(1); // Assuming '1' corresponds to the "USER" role in your database
        user.getRoles().add(userRole);

        // Save the user first
        userRepo.save(user);

        // Now that the user has been saved and has an ID, create and save UserRoles
        UserRoles userRoles = new UserRoles();
        userRoles.setUserId(user.getID());  // Now the ID is available
        userRoles.setRoleId(4);  // Assuming '4' corresponds to a specific role like "USER"
        userRolesRepo.save(userRoles);

        return "register_success"; // Redirect to a success page
    }

    // New Product Page
    @RequestMapping("/new")
    public String showNewProductPage(Model model, HttpSession session) {
        User user = getLoggedInUser(session);
        if (user == null || !Objects.equals(user.getRole(), "admin")) {
            return "403"; // Access denied if user doesn't have 'ADMIN' or 'CREATOR' role
        }

        model.addAttribute("product", new Product());
        return "new_product"; // Show form for new product
    }

    // Edit Product Page
    @RequestMapping("/edit/{id}")
    public ModelAndView showEditProductPage(@PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("edit_product");
        Product product = service.get((long) id);
        mav.addObject("product", product);

        return mav;
    }

    // Delete Product
    @RequestMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") int id, HttpSession session) {
        User user = getLoggedInUser(session);
        if (user == null || !Objects.equals(user.getRole(), "admin")) {
            return "403"; // Access denied if user is not an 'ADMIN'
        }

        service.delete((long) id);
        return "redirect:/"; // Redirect to home after deletion
    }

    // Helper method to get the logged-in user from the session
    private User getLoggedInUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    // Helper method to check if user has specific roles
    private boolean hasRole(User user, String... roles) {
        for (String role : roles) {
            if (user.getRoles().stream().anyMatch(r -> r.getName().equals(role))) {
                return true;
            }
        }
        return false; // Return false if no matching role is found
    }
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveProduct(@ModelAttribute("product") Product product) {
        service.save(product);

        return "redirect:/";
    }
}
