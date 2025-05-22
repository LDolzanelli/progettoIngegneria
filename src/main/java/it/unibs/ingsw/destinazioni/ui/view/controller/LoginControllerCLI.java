package it.unibs.ingsw.destinazioni.ui.view.controller;

import java.util.Scanner;
import org.springframework.web.client.RestTemplate;
import it.unibs.ingsw.destinazioni.domain.dto.ChangeCredentialsDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LoginRequestDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LoginResponseDTO;

public class LoginControllerCLI {

    final static String baseUrl = "http://localhost:8080/api/users/login";

    public static void main(String[] args) {

        RestTemplate restTemplate = new RestTemplate();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Benvenuto nel sistema di login!");
        System.out.println("Per favore, inserisci le tue credenziali.");
        System.out.print("Nickname: ");
        String nickname = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        LoginRequestDTO loginRequest = new LoginRequestDTO(nickname, password);

        try {
            LoginResponseDTO response = restTemplate.postForObject(baseUrl, loginRequest, LoginResponseDTO.class);
            System.out.println("Login effettuato con successo!");
            System.out.println("Benvenuto " + response.nickname() + "!");

            if (response.firstLogin()) {
                System.out.println("È il tuo primo accesso. Ti preghiamo di cambiare le credenziali.");
                ChangeCredentialsDTO newCredentials = getNewCredentials(nickname, scanner);
                String changeUrl = "http://localhost:8080/api/users/change-both-credentials";
                restTemplate.postForObject(changeUrl, newCredentials, Void.class);
                System.out.println("Credenziali cambiate con successo!");
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static ChangeCredentialsDTO getNewCredentials(String username, Scanner scanner) {

        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
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
