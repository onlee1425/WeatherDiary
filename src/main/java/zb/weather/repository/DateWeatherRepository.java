package zb.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zb.weather.domain.DateWeather;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DateWeatherRepository extends JpaRepository<DateWeather, LocalDate > {
    //상속 <사용할 엔티티 클래스, id의 형식>

    //date에 따라 그날의 날씨값을 가져오는 함수
    List<DateWeather> findAllByDate(LocalDate localDate);
}
