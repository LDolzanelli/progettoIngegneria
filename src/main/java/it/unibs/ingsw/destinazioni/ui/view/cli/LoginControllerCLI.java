package it.unibs.ingsw.destinazioni.ui.view.cli;


import java.util.Scanner;

import org.springframework.web.client.RestTemplate;

import it.unibs.ingsw.destinazioni.domain.dto.ChangeCredentialsDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LoginRequestDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LoginResponseDTO;

public class LoginControllerCLI {

    final static String baseUrl = "http://localhost:8080/api/users/login";

    public static void main(String[] args) {

        login();
    }


    private static void login() {
        RestTemplate restTemplate = new RestTemplate();

        Scanner scanner = new Scanner(System.in);

        System.err.println("\n\n");
        System.out.println("Benvenuto in Centro Visite!");
        System.out.println("Per favore, inserisci le tue credenziali.");
        System.out.print("Nickname: ");
        String nickname = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        LoginRequestDTO loginRequest = new LoginRequestDTO(nickname, password);

        try {
            LoginResponseDTO response = restTemplate.postForObject(baseUrl, loginRequest, LoginResponseDTO.class);
            System.out.println("Login effettuato con successo!");

            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n");
            System.out.println("Benvenuto " + response.nickname() + "!");

            if (response.firstLogin()) {
                ChangeCredentialsDTO newCredentials = getNewCredentials(nickname, scanner);
                String changeUrl = "http://localhost:8080/api/users/change-both-credentials";
                restTemplate.postForObject(changeUrl, newCredentials, Void.class);
                System.out.println("Credenziali cambiate con successo!");
                login();

            }

        } catch (Exception e) {
            System.out.println("Errore durante il login: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }


    private static ChangeCredentialsDTO getNewCredentials(String username, Scanner scanner) {

        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println("È il tuo primo accesso. Ti preghiamo di cambiare le credenziali.");
        System.out.println("Per favore, inserisci le nuove credenziali.");
        System.out.print("Nuovo nickname: ");
        String newNickname = scanner.nextLine();
        System.out.print("Vecchia password: ");
        String oldPassword = scanner.nextLine();
        System.out.print("Nuova password: ");
        String newPassword = scanner.nextLine();
        System.out.print("Conferma nuova password: ");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Le password non corrispondono. Riprova.");
            return getNewCredentials(username, scanner);
        }

        return new ChangeCredentialsDTO(username, newNickname, oldPassword, newPassword);
    }

}
