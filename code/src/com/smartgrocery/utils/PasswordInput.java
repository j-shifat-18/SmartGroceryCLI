package com.smartgrocery.utils;

import java.io.Console;
import java.util.Scanner;


public class PasswordInput {
    
    public static String readPassword(Scanner scanner, String prompt) {
        Console console = System.console();
        
        if (console != null) {
            char[] passwordChars = console.readPassword(prompt);
            return new String(passwordChars);
        } else {
            System.out.print(prompt);
            return scanner.nextLine();
        }
    }
    
    
    public static String readPasswordWithConfirmation(Scanner scanner, String prompt, String confirmPrompt) {
        String password = readPassword(scanner, prompt);
        String confirmPassword = readPassword(scanner, confirmPrompt);
        
        if (password.equals(confirmPassword)) {
            return password;
        } else {
            System.out.println("Passwords do not match!");
            return null;
        }
    }
}
