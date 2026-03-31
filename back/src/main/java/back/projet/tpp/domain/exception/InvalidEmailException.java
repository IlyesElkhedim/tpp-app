package back.projet.tpp.domain.exception;

public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException(String value) {
        super("Invalid email: " + value);
    }
}