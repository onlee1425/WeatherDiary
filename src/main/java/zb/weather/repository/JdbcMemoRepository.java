package zb.weather.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zb.weather.domain.Memo;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemoRepository {
    //mysql 연동
    private final JdbcTemplate jdbcTemplate;

    @Autowired //properties의 datasourcer가 dataSource 변수에 담겨짐
    public JdbcMemoRepository(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //insert into sql문
    public Memo save(Memo memo){
        String sql = "insert into memo values(?,?)";
        jdbcTemplate.update(sql,memo.getId(),memo.getText());
        return memo;

    }

    //저장한것을 조회하는 find문(mapper 메서드 활용)
    public List<Memo> findAll(){ //전체정보 조회
        String sql= "select * from memo";
        return jdbcTemplate.query(sql,memoRowMapper()); //mysql로 가서 쿼리문으로 검사 -> 메모 맵퍼를 이용해 메모 객체로 가져옴
    }

    public Optional<Memo> findById(int id){
        String sql = "select * from memo where id = ?"; //id를 갖고 select하는 쿼리문
        return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst(); // id가 없는경우 optional로 null값 제거
    }

    //rowmapper = SQL의 결과(record type)를 객체(object type)에 매핑하여 결과를 리턴
    private RowMapper<Memo> memoRowMapper(){
        return ((rs, rowNum) -> new Memo(
                rs.getInt("id"),
                rs.getString("text")
        ));

    }


}
