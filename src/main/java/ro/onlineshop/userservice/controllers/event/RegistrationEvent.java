package ro.onlineshop.userservice.controllers.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import ro.onlineshop.userservice.entities.User;

@Getter
@Setter
public class RegistrationEvent extends ApplicationEvent {

    private User user;
    private String url;

    public RegistrationEvent(User user, String url) {
        super(user);
        this.user = user;
        this.url = url;
    }
}
