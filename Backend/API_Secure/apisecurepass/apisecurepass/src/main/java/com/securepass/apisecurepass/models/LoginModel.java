package com.securepass.apisecurepass.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tb_login")
@NoArgsConstructor
public class LoginModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_usuario" ,referencedColumnName = "id")
    private UserModel user;

    private LocalDateTime login_time;

    // Construtor personalizado para definir login_time como a data e hora atuais
    public LoginModel(UserModel user) {
        this.user = user;
        this.login_time = LocalDateTime.now();; // Definindo login_time como a data e hora atuais
    }
}
