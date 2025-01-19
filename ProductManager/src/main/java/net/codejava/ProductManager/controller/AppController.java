package net.codejava.ProductManager.controller;

import net.codejava.ProductManager.entity.Product;

import net.codejava.ProductManager.entity.User;
import net.codejava.ProductManager.repository.UserRepository;
import net.codejava.ProductManager.service.PasswordEncryptionService;
import net.codejava.ProductManager.service.ProductService;
import net.codejava.ProductManager.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Objects;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;


@Controller
public class AppController {

    @Autowired
    private ProductService service;

    @Autowired
    private UserRepository userRepo;


    @Autowired
    private UserDetailsServiceImpl userService;
    @Autowired
    private PasswordEncryptionService ps;

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
        System.out.println(session.getId());
        List<Product> listProducts = service.listAll();
        model.addAttribute("listProducts", listProducts);
        return "index";
    }

    @RequestMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/process_login")
    public String processLogin(@RequestParam String email, @RequestParam String password, HttpSession session) throws NoSuchAlgorithmException, InvalidKeySpecException {
        User user = userService.loadUserByUsername(email);



        if (user != null && ps.verifyPassword(password,user.getPassword())){
            // stored user in ssession
            session.setAttribute("user", user);
            session.setAttribute("role", user.getRole());


            System.out.println("User: " + user.getUsername());

            return "redirect:/"; // Successful login, redirect to home
        }

        return "login";
    }


    @RequestMapping("/logout")
    public String logout(HttpSession session,HttpServletResponse response) {
        session.invalidate();
        Cookie cookie = new Cookie("JSESSIONID", null); // Name of the session cookie
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Set this to true if using HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0); // Delete the cookie
        response.addCookie(cookie);// Invalidate session
        return "redirect:/login"; // Redirect to login page after logout
    }

    @RequestMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "signup_form";
    }

    @PostMapping("/process_register")
    public String processRegister(User user) throws NoSuchAlgorithmException, InvalidKeySpecException {

        user.setEnabled(true);


        user.setRole("user");
        String s=user.getPassword();
        s=ps.hashPassword(s);
        user.setPassword(s);



        userRepo.save(user);




        return "register_success";
    }

    // New Product Page
    @RequestMapping("/new")
    public String showNewProductPage(Model model, HttpSession session) {
        User user = getLoggedInUser(session);
        if (user == null || !Objects.equals(user.getRole(), "admin")) {
            return "403";
        }

        model.addAttribute("product", new Product());
        return "new_product";
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
            return "403";
        }

        service.delete((long) id);
        return "redirect:/";
    }


    private User getLoggedInUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }



    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveProduct(@ModelAttribute("product") Product product) {
        service.save(product);

        return "redirect:/";
    }
    @RequestMapping("/change_password")
    public String showChangePasswordForm() {
        return "change_password_form";
    }
    @PostMapping("/process_change_password")
    public String processChangePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            HttpSession session) throws NoSuchAlgorithmException, InvalidKeySpecException {
        User user = getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        if (!ps.verifyPassword(oldPassword, user.getPassword())) {
            // Old password is incorrect
            return "change_password_form";
        }

        // Update the password
        user.setPassword(ps.hashPassword(newPassword));
        userRepo.save(user);
        return "password_change_success";
    }
    @RequestMapping("/profile")
    public String showProfilePage(Model model, HttpSession session) {
        // Get the logged-in user
        User user = getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        // Pass the user to the model for the frontend
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update_profile")
    public String updateProfile(
            @RequestParam("username") String username,
            @RequestParam("role") String role,
            HttpSession session) {
        // Get the logged-in user
        User user = getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        // Update the user's details
        user.setUsername(username);
        user.setRole(role);
        userRepo.save(user);

        // Update the session with the new details
        session.setAttribute("user", user);

        return "redirect:/profile?success";
    }
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchProducts(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "filterBy", required = false) String filterBy,
            Model model,
            HttpSession session) {
        User user = getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        // Filter products based on query and filterBy
        List<Product> filteredProducts;
        if (query != null && filterBy != null) {
            switch (filterBy) {
                case "name":
                    filteredProducts = service.findByNameContaining(query);
                    break;
                case "brand":
                    filteredProducts = service.findByBrandContaining(query);
                    break;
                default:
                    filteredProducts = service.listAll();
            }
        } else {
            filteredProducts = service.listAll(); // Default to all products
        }

        model.addAttribute("listProducts", filteredProducts);
        model.addAttribute("user", user);
        return "index";
    }




}