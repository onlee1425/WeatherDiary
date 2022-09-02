package zb.weather.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
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
    @ApiOperation(value = "일기 텍스트와 날씨를 이용하여 DB에 저장", notes = "note")
    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                     @ApiParam(value="날짜형식 : 일기를 기록한 날짜",example = "2022-01-01") LocalDate date
            , @RequestBody String text) {
        //@RequestParam LocalDate => url뒤에?date=2022~~ 식으로 날짜를 url에 파라미터로 요청할 수 있음.
        //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE => Date타임 형식 지정
        //@RequestBody String text => post요청시에는 body에 데이터를 넣어 전송함. 바디에 문자열값인 일기내용(text)를 넣어 전달
        //ex: 0월0일 오늘은~~했다.
        diaryService.createDiary(date, text);

    }

    //특정 날짜 다이어리 조회
    @ApiOperation(value = "선택한 날짜의 모든 일기 데이터를 가져옵니다")
    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  @ApiParam(value="날짜형식 : 조회할 날짜",example = "2022-01-01") LocalDate date) {
        return diaryService.readDiary(date);
    }

    //특정 기간 다이어리 조회
    @ApiOperation(value = "선택한 기간 내의 모든 일기 데이터를 가져옵니다")
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                            @ApiParam(value="날짜형식 : 조회할 기간의 첫번째날",example = "2022-01-01") LocalDate startDate
            ,@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                            @ApiParam(value="날짜형식 : 조회할 기간의 마지막날",example = "2022-01-01") LocalDate endDate) {
        return diaryService.readDiaries(startDate,endDate);

    }

    //다이어리 수정
    @ApiOperation(value = "선택한 날짜의 첫번째 일기 데이터를 수정합니다")
    @PutMapping("/update/diary")
    void updateDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  @ApiParam(value="날짜형식 : 수정할 날짜",example = "2022-01-01") LocalDate date
            , @RequestBody String text){
        diaryService.updateDiary(date,text);
    }

    //다이어리 삭제
    @ApiOperation(value = "선택한 날짜의 모든 일기 데이터를 삭제합니다")
    @DeleteMapping("/delete/diary")
    void deleteDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  @ApiParam(value="날짜형식 : 삭제할 날짜",example = "2022-01-01") LocalDate date){
        diaryService.deleteDiary(date);
    }
}
