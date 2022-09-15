# WeatherDiary
Open Weather Map api 를 사용한 날씨 일기를 작성/조회/수정/삭제 하는 서비스


# 프로젝트 기능
다이어리 작성
✅ POST / create / diary
- date parameter 로 날짜를 받는다. (date 형식 : yyyy-MM-dd)
- text parameter 로 일기 글을 받는다.
- 외부 API 에서 받아온 날씨 데이터와 함께 DB에 저장.

지정 일자 다이어리 조회
✅ GET / read / diary
- date parameter 로 조회할 날짜를 받는다.
- 해당 날짜의 일기를 List 형태로 반환한다.

전체 다이어리 조회
✅ GET / read / diaries
- startDate, ednDate parameter 로 조회할 날짜 기간의 시작일/종료일을 받는다.
- 해당 기간의 일기를 List 형태로 반환한다.

지정 일자 다이어리 수정
✅ PUT / update / diary
- date parameter 로 수정할 날짜를 받는다.
- text parameter 로 수정할 일기 내용을 받는다.
- 해당 날짜의 중복 일기가 있을경우 첫번째 일기 글을 새로 받아온 일기글로 수정.

다이어리 삭제
✅ DELETE / delete / diary
- date parameter 로 삭제할 날짜를 받는다.
- 해당 날짜의 모든 일기 삭제.


# 사용 기술스택
- Spring Boot, Java, JPA, Swagger, MySQL
