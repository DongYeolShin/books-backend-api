package co.books.api.exam.service;

import co.books.api.exam.entity.StudentEntity;
import co.books.api.exam.repo.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // 생성자를 통한 의존성 주입
public class ExamService {

    private final StudentRepository repository;

    public String hello () {
        return "안녕하세요!!!";
    }

    public List<StudentEntity> getStudentList() {
        // 학생정보 전체 출력
        return repository.findAll();
    }
}
