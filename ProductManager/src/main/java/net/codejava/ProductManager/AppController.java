package net.codejava.ProductManager;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    private UserRolesRepository userRolesRepo;

    @Autowired
    private UserDetailsServiceImpl userService;
    @Autowired
    private  PasswordEncryptionService ps;

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
            System.out.println("Roles: " + user.getRoles());
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


        Role userRole = new Role();
        userRole.setId(1);
        user.getRoles().add(userRole);
   String s=user.getPassword();
   s=ps.hashPassword(s);
      user.setPassword(s);



        userRepo.save(user);


        UserRoles userRoles = new UserRoles();
        userRoles.setUserId(user.getID());
        userRoles.setRoleId(4);
        userRolesRepo.save(userRoles);

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


    private boolean hasRole(User user, String... roles) {
        for (String role : roles) {
            if (user.getRoles().stream().anyMatch(r -> r.getName().equals(role))) {
                return true;
            }
        }
        return false;
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

            return "change_password_form";
        }


        user.setPassword(ps.hashPassword(newPassword));
        userRepo.save(user);
        return "password_change_success";
    }


}
