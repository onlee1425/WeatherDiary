package zb.weather.service;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zb.weather.domain.Diary;
import zb.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class DiaryService {

    @Value("${openweathermap.key}")
    //Value => 스프링 부트에 openweathermap.key라는 변수의 값을 가져와서 apikey 객체에 넣어줌.
    private String apikey;

    //서비스의 값을 레포지토리에 전달하기위해 레포지토리 선언
    private final DiaryRepository diaryRepository;
    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    public void createDiary(LocalDate date, String text) {
        //Open Weather Map 에서 날씨 데이터 가져오기
        String weatherData = getWeatherString();

        //Json 파싱하기
        Map<String, Object> parseWeather = parseWeather(weatherData);

        //파싱된 데이터 + 일기 값 DB에 넣기
        Diary nowDiary = new Diary();
        nowDiary.setWeather(parseWeather.get("main").toString());
        nowDiary.setIcon(parseWeather.get("icon").toString());
        nowDiary.setTemperature((Double) parseWeather.get("temp"));
        nowDiary.setText(text);
        nowDiary.setDate(date);

        diaryRepository.save(nowDiary);

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


}
