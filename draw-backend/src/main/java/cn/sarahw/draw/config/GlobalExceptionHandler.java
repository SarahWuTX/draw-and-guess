package cn.sarahw.draw.config;

import com.mongodb.MongoWriteException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;
import java.text.ParseException;


@ControllerAdvice(basePackages = "cn.sarah.draw" )
public class GlobalExceptionHandler {
    //处理自定义的异常
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolationExceptionHandler(ConstraintViolationException e){
        e.printStackTrace();
        return new ResponseEntity<>("参数验证失败", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> dataIntegrityViolationExceptionHandler(DataIntegrityViolationException e){
        e.printStackTrace();
        return new ResponseEntity<>("mysql: 违反数据库约束", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<?> transactionSystemExceptionHandler(TransactionSystemException e){
        e.printStackTrace();
        return new ResponseEntity<>("mysql: 错误的数据库操作", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<?> InvalidDataAccessResourceUsageExceptionHandler(InvalidDataAccessResourceUsageException e){
        e.printStackTrace();
        return new ResponseEntity<>("玄学错误，我搞不懂", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MongoWriteException.class)
    public ResponseEntity<?> mongoWriteExceptionHandler(MongoWriteException e){
        e.printStackTrace();
        return new ResponseEntity<>("mongodb: 违反数据库约束", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ParseException.class)
    public ResponseEntity<?> ParseExceptionHandler(ParseException e){
        e.printStackTrace();
        return new ResponseEntity<>("格式解析出错（日期）", HttpStatus.BAD_REQUEST);
    }


    //其他未处理的异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<?> exceptionHandler(Exception e){
        e.printStackTrace();
        return new ResponseEntity<>("未处理的异常", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
