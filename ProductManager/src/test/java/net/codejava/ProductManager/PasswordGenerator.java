package net.codejava.ProductManager;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
        String rp="pass3";
        String ep= encoder.encode(rp);
        System.out.println(ep);
    }
}
