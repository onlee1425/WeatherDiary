package zb.weather.service;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zb.weather.WeatherApplication;
import zb.weather.domain.DateWeather;
import zb.weather.domain.Diary;
import zb.weather.error.InvalidDate;
import zb.weather.repository.DateWeatherRepository;
import zb.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DiaryService {

    @Value("${openweathermap.key}")
    //Value => 스프링 부트에 openweathermap.key라는 변수의 값을 가져와서 apikey 객체에 넣어줌.
    private String apikey;

    //서비스의 값을 레포지토리에 전달하기위해 레포지토리 선언
    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;

    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    //날씨 데이터 저장을 위한 메서드
    @Transactional
    @Scheduled(cron = "0 0 1 * * *") //매일 새벽 1시 진행 (0/5 * * * * * 5초마다 저장되는지 test)
    public void saveWeatherDate() {
        logger.info("날씨 데이터 성공적으로 가져옴!");
        dateWeatherRepository.save(getWeatherFromApi());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        //로그 쌓기
        logger.info("started to create diary");

        //날씨 데이터 가져오기 (API에서 가져오기 or DB에서 가져오기)
        DateWeather dateWeather = getDateWeather(date);

        //파싱된 데이터 + 일기 값 DB에 넣기
        Diary nowDiary = new Diary();
        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);
        nowDiary.setDate(date);

        diaryRepository.save(nowDiary);
        logger.info("end to create diary");

    }

    //다이어리 작성 시 날씨 데이터(API or DB 에서) 가져오기
    private DateWeather getDateWeather(LocalDate date) {
        //다이어리 작성일자의 날씨가 있는지 db에서 확인
        List<DateWeather> dateWeathersListFromDB = dateWeatherRepository.findAllByDate(date);
        if (dateWeathersListFromDB.size() == 0) {
            //없으면? api에서 가져오기
            //정책상 현재 날씨를 가져오도록 하거나 , 날씨 없이 일기쓰도록 할 수도 있음
            //해당 프로젝트의 정책은 없을경우 api에서 가져오기
            return getWeatherFromApi();
        } else {
            return dateWeathersListFromDB.get(0);
        }
    }

    //스케쥴링을 위한 api 가져오는 메서드
    private DateWeather getWeatherFromApi() {
        //Open Weather Map 에서 날씨 데이터 가져오기
        String weatherData = getWeatherString();

        //Json 파싱하기
        Map<String, Object> parseWeather = parseWeather(weatherData);
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now()); //날씨를 가져온 시점의 date를 저장
        dateWeather.setWeather(parseWeather.get("main").toString());
        dateWeather.setIcon(parseWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parseWeather.get("temp"));

        return dateWeather;
    }

    //api에서 날씨 데이터 가져오기
    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apikey;
        //System.out.println(apiUrl);

        try {
            URL url = new URL(apiUrl);

            //apiUrl 을 http 형식으로 connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); //GET 방식으로 connection
            int responseCode = connection.getResponseCode(); //응답결과의 코드를 받아옴

            BufferedReader br; //응답객체를 br에 넣어둠(응답내용이 클경우 속도향상,성능향상을 위해 br 사용)
            if (responseCode == 200) { //정상동작일경우(응답코드 200)
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else { //오류일경우 (그 외)
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine); //br에 넣어둔 정보를 읽으면서 stringbuilder 에 결과값을 쌓음
            }
            br.close();

            return response.toString(); //response 를 string 으로 바꿔 반환

        } catch (Exception e) {
            return "failed to get response";
        }
    }


    //json 파싱
    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser(); //json "simple" parser import
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        //jsonObject 로 파싱된 정보중 필요한 데이터만 가져오기
        Map<String, Object> resultMap = new HashMap<>(); //HashMap 형태로 반환

        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        //weather 안에 있는 main과 icon 가져오기 , weather 는 array
        resultMap.put("main", weatherData.get("main")); //map string = main , 값은 json main의 값
        resultMap.put("icon", weatherData.get("icon"));

        //main 안에있는 temp 가져오기
        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));

        return resultMap;
    }


    //특정 날짜 다이어리 조회
    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
//        if (date.isAfter(LocalDate.ofYearDay(3050,1))){
//            throw new InvalidDate();
//        }
        logger.debug("read diary");
        return diaryRepository.findAllByDate(date);
    }

    //특정 기간 다이어리 조회
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    //다이어리 수정
    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }

    //다이어리 삭제
    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }
}
