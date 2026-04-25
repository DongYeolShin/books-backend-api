package co.books.api.exam.contrl;

import co.books.api.exam.entity.StudentEntity;
import co.books.api.exam.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor // 필수 파라메터를 생성자를 통해 받는다.
public class ExamController {

    private final ExamService examService;
    /*
      DI는 크게 3가지가 있음.
      1. 직접주입
      2. setter 를 이용한 주깁
      3. 생성자 주입
     */
    @GetMapping("/test")
    public ResponseEntity<String> hello (){
        //요청에대한 일을하고결과를 받는다.
        //요청에 대한 일은 service 가 한다.
        String str = examService.hello();

        //요청에 대한 응답
        return ResponseEntity.ok(str);
    }

    @GetMapping("/std/list")
    public ResponseEntity<List<StudentEntity>> getStdList() {
        return ResponseEntity.ok(examService.getStudentList());
    }
}
