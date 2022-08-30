package zb.weather.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import zb.weather.domain.Diary;
import zb.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController //http 응답시 상태코드를 지정해서 내려줄 수 있게해줌
public class DiaryController {

    private final DiaryService diaryService; //컨트롤러에서 받은 값들을 서비스로 전달해주기 위한 서비스 선언.

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    //일기 생성
    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
            , @RequestBody String text){
        //@RequestParam LocalDate => url뒤에?date=2022~~ 식으로 날짜를 url에 파라미터로 요청할 수 있음.
        //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE => Date타임 형식 지정
        //@RequestBody String text => post요청시에는 body에 데이터를 넣어 전송함. 바디에 문자열값인 일기내용(text)를 넣어 전달
        //ex: 0월0일 오늘은~~했다.
        diaryService.createDiary(date,text);

    }

    //일기 조회
    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date){
        return diaryService.readDiary(date);

    }
}
