package com.fluffytiger.earlygamewebapp.restcontrollers;

import com.fluffytiger.earlygamewebapp.exceptions.CustomException;
import com.fluffytiger.earlygamewebapp.model.User;
import com.fluffytiger.earlygamewebapp.payload.*;
import com.fluffytiger.earlygamewebapp.services.SignupRequestValidator;
import com.fluffytiger.earlygamewebapp.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "api/users")
public class UserController {
    private final UserService users;
    private final SignupRequestValidator validator;

    public UserController(UserService users, SignupRequestValidator validator) {
        this.users = users;
        this.validator = validator;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @ExceptionHandler({ CustomException.class })
    public ResponseEntity<ErrorResponse> handleExceptions(Exception ex, WebRequest request) {
        return ResponseEntity.status(((CustomException)ex).getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @RequestMapping(value = "authenticate", method = RequestMethod.POST)
    public ResponseEntity<SigninResponse> authenticate(@Valid @RequestBody SignupRequest req, BindingResult result) {
        if (!result.hasErrors()) {
            User user = this.users.authenticateInGame(req);

            return ResponseEntity.ok()
                    .body(new SigninResponse(this.users.createToken(user), user.getId(), user.getUsername()));
        } else {
            throw new CustomException("Validation error", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @RequestMapping(value = "signin", method = RequestMethod.POST)
    public ResponseEntity<SigninResponse> signin(@RequestBody SigninRequest req) {
        User user = this.users.signin(req.getUsername(), req.getPassword());

        return ResponseEntity.ok()
                    .body(new SigninResponse(this.users.createToken(user), user.getId(), user.getUsername()));
    }

    @RequestMapping(value = "signup", method = RequestMethod.POST)
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest req) {
        User user = this.users.signup(req);

        return ResponseEntity.ok()
                .body(new SignupResponse(this.users.createToken(user), user.getId(), user.getUsername()));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<PublicUserResponse> getUserPublicData(@PathVariable Long id) {
        Optional<User> user = this.users.getById(id);

        if (user.isPresent()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new PublicUserResponse(user.get()));
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .build();
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Iterable<PublicUserResponse>> listUsers() {
        List<PublicUserResponse> list = StreamSupport.stream(this.users.list().spliterator(), false)
                .map(PublicUserResponse::new).collect(Collectors.toList());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(list);
    }
}
