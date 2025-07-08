package it.unibs.ingsw.destinazioni.adapters.jpa.adapter;

import it.unibs.ingsw.destinazioni.adapters.jpa.entity.UserEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.UserRepository;
import it.unibs.ingsw.destinazioni.application.port.out.UserRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter per il repository degli utenti.
 * Questo adapter si occupa di convertire le entità JPA in oggetti di dominio e viceversa.
 * Utilizza il repository JPA per eseguire le operazioni di persistenza.
 * 
 * @version 1.0
 */
@Repository
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository jpaRepo;

    public JpaUserRepositoryAdapter(UserRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public Optional<User> findById(int id) {
        return jpaRepo.findById(id).map(JpaUserRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return jpaRepo.findByNickname(nickname).map(JpaUserRepositoryAdapter::toDomain);
    }
    
    @Override
    public void deleteByNickname(String nickname) {
        jpaRepo.deleteByNickname(nickname);
    }

    @Override
    public void deleteById(int id) {
        jpaRepo.deleteById(id);
    }
    @Override
    public User save(User user) {
        UserEntity savedEntity = jpaRepo.save(toEntity(user));
        return toDomain(savedEntity);
    }

    protected static User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getNickname(),
                entity.getPassword(),
                Role.fromString(entity.getRole()),
                Boolean.TRUE.equals(entity.getFirstLogin())
        );
    }

    protected static UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setNickname(user.getNickname());
        entity.setPassword(user.getPassword());
        entity.setRole(user.getRole().getName());
        entity.setFirstLogin(user.isFirstLogin());
        return entity;
    }

    @Override
    public List<User> findAllByRole(Role role) {
        return jpaRepo.findAllByRole(role.getName()).stream()
                .map(JpaUserRepositoryAdapter::toDomain)
                .toList();
    }

}
