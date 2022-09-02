package zb.weather.Config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class globalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //에러코드 500번대 반환
    @ExceptionHandler(Exception.class)
    public Exception handleAllException(){
        System.out.println("error form GlobalExceptionHandler");
        //예외처리를 위한 로직 담기

        return new Exception();
    }

}
