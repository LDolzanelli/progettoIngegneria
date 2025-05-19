package it.unibs.ingsw.destinazioni.application.port.in;

public interface ChangeCredentialsUseCase {
    
    void changePassword(String username, String oldPassword, String newPassword);
    void changeUsername(String oldUsername, String newUsername);
    void changeBothCredentials(String oldUsername, String oldPassword, String newUsername, String newPassword);
}
