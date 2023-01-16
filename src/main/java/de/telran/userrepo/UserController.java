package de.telran.userrepo;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<Iterable<User>> getAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/users")
    public ResponseEntity<String> addUser(
            @Valid @RequestBody User user
    ) {
        userRepository.save(user);
        return ResponseEntity.ok(
                "User is valid"
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(
                error ->
                        errors.put(
                                ((FieldError) error).getField(),
                                error.getDefaultMessage()
                        )
        );
        return errors;
    }

    // http://localhost:8080/users/1 -> "bob:bob@gmail.com"
    @GetMapping("/users/{key}")
    public ResponseEntity<String> nameEmailCombo(
            @PathVariable(name = "key") Long key
    ) {
        return ResponseEntity.ok(
                userRepository.findById(key).map(user -> user.getName() + ":" + user.getEmail())
                        .orElse("")
        );
    }

    // 1. Напишите функцию обрабатывающую запрос такого вида
    // http://localhost:8080/upper?text=hello -> {"result": "HELLO"} (json)
    // 2. Напишите тест, который это проверит
    @GetMapping("/upper")
    public ResponseEntity<String> convertStringToUpperCase(
            @RequestParam(name = "text") String text
    ) {
        return ResponseEntity.ok(
                "{\"result\": \"" + text.toUpperCase(Locale.ROOT) + "\"}"
        );
    }
}