package ru.creditbank.apigateway.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "users_seq"
    )
    @SequenceGenerator(
            name = "users_seq",
            sequenceName = "users_seq"
    )
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "middle_name", length = 50)
    private String middleName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    public UserEntity(
            String email,
            String passwordHash,
            String firstName,
            String lastName,
            String middleName
    ) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.role = Role.USER;
    }

    @Setter
    @Column(name = "access_token", length = 2048)
    private String accessToken;

    @Setter
    @Column(name = "access_token_issued_at")
    private Instant accessTokenIssuedAt;

    @Setter
    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @Setter
    @Column(name = "refresh_token_expires_at")
    private Instant refreshTokenExpiresAt;
}
