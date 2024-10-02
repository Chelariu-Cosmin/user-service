package ro.onlineshop.userservice.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@Entity(name = "refreshtoken")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Date expiryDate;

    @Value("${onlineshop.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public RefreshToken(String token) {
        super();
        this.expiryDate = getTokenExpirationTime();
        this.token = token;
    }


    public RefreshToken(User user, String token) {
        super();
        this.user = user;
        this.token = token;
        this.expiryDate = getTokenExpirationTime();
    }


    public RefreshToken() {
    }

    public static int millisecondsToMinutes(int milliseconds) {
        return milliseconds / (1000 * 60);
    }

    public Date getTokenExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        int jwtExpirationMinutes = millisecondsToMinutes(280000000);
        calendar.add(Calendar.MINUTE, jwtExpirationMinutes);
        return new Date(calendar.getTime().getTime());
    }
}
