package flyffbot.exceptions;

public class BindingRowNotFound extends RuntimeException{
    public BindingRowNotFound(String message, Exception e){
        super(message, e);
    }
    public BindingRowNotFound(String message){
        super(message);
    }
}
