package teletubbies.map.bus;

import lombok.SneakyThrows;
import org.json.JSONObject;
import org.json.XML;
import org.json.JSONArray;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.jdbc.datasource.JdbcTransactionObjectSupport;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 반복되는 코드 많아서 이 부분 나중에 수정할 것!!!!

 */
@Service
public class BusServiceImpl implements BusService {
    @Value("${BUS_APPKEY}")
    private String bus_apikey; //버스 API 키 설정

    //버스 URL 설정
    @Value("${BUS_URL}")
    private String bus_url;
    @Value("${BSTOPID_URL}")
    private String busStopId_url;
    @Value("${BUSID_URL}")
    private String busId_url;
    @Value("${BUSLOCATION_URL}")
    private String busLocation_url;
    @Value("${BUSIDLIST_URL}")
    private String busIdList_url;
    @Value("${BUSNUM_URL}")
    private String busNum_url;

    @SneakyThrows
    public Object findBusStopByBusStopName(String name) { //정류소명으로 정류소(ID) 검색
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders(); //헤더
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // 한글깨짐 방지

        //서비스 키와, 검색할 정류소 인코딩
        String encodedKey = URLEncoder.encode(bus_apikey, "UTF-8");
        String encodedName = URLEncoder.encode(name, "UTF-8");

        UriComponents uri = UriComponentsBuilder
                .fromHttpUrl(bus_url)
                .queryParam("serviceKey", encodedKey) //서비스키
                .queryParam("bstopNm", encodedName) //버스정류소명
                .queryParam("numOfRows", 2) // 개수
                .queryParam("pageNo", 1)
                .build(true);

        ResponseEntity<String> result = restTemplate.exchange(uri.toUri(), HttpMethod.GET, new HttpEntity<String>(headers), String.class);

        //xml 형식을 json 형식으로 변환
        JSONObject response = XML.toJSONObject(result.getBody());

        JSONObject ServiceResult = (JSONObject)response.get("ServiceResult"); //ServiceResult의 value들
        JSONObject msgBody = (JSONObject)ServiceResult.get("msgBody"); //msgBody의 value들
        JSONArray itemList = (JSONArray) msgBody.get("itemList"); //itemList의 value들

        System.out.println("정류소명으로 정류소(ID) 검색");
        for(int i=0; i<itemList.length(); i++) { // 받아올 데이터 개수만큼 반복
            JSONObject array = (JSONObject) itemList.get(i);
            System.out.println("(" + i + ")");
            Integer BSTOPID = (Integer)array.get("BSTOPID"); //정류소 ID
            String ADMINNM = (String)array.get("ADMINNM"); // 정류소 관할관청명(BusZone)

            System.out.println("BSTOPID = " + BSTOPID);
            System.out.println("ADMINNM = " + ADMINNM);
        }

        return result.getBody();
    }

    @SneakyThrows
    public Object findBusArrivalByBusStopId(int bStopId) { //정류소 ID로 버스 도착정보목록 조회
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders(); //헤더
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // 한글깨짐 방지

        //서비스 키 인코딩
        String encodedKey = URLEncoder.encode(bus_apikey, "UTF-8");

        UriComponents uri = UriComponentsBuilder
                .fromHttpUrl(busStopId_url)
                .queryParam("serviceKey", encodedKey) //서비스키
                .queryParam("bstopId", bStopId) // 정류소ID
                .queryParam("numOfRows", 2) // 개수
                .queryParam("pageNo", 1)
                .build(true);

        ResponseEntity<String> result = restTemplate.exchange(uri.toUri(), HttpMethod.GET, new HttpEntity<String>(headers), String.class);


        //xml 형식을 json 형식으로 변환
        JSONObject response = XML.toJSONObject(result.getBody());

        JSONObject ServiceResult = (JSONObject)response.get("ServiceResult"); //ServiceResult의 value들
        JSONObject msgBody = (JSONObject)ServiceResult.get("msgBody"); //msgBody의 value들
        JSONArray itemList = (JSONArray) msgBody.get("itemList"); //itemList의 value들

        System.out.println("정류소 ID로 버스 도착정보목록 조회");
        for(int i=0; i<itemList.length(); i++) { // 받아올 데이터 개수만큼 반복
            JSONObject array = (JSONObject) itemList.get(i);
            System.out.println("(" + i + ")");
            Integer BUSID = (Integer)array.get("BUSID"); // 버스 ID(차량 고유 식별자)
            Integer ARRIVALESTIMATETIME = (Integer)array.get("ARRIVALESTIMATETIME"); // 도착예정시간(몇 초 전)
            Integer LOW_TP_CD = (Integer)array.get("LOW_TP_CD"); // 저상버스 여부
            Integer ROUTEID = (Integer)array.get("ROUTEID"); //버스 노선 ID
            Integer REST_STOP_COUNT = (Integer)array.get("REST_STOP_COUNT"); // 몇 정거장 전
            String LATEST_STOP_NAME = (String)array.get("LATEST_STOP_NAME"); //버스의 최근 정류소 명
            String BUS_NUM_PLATE = (String)array.get("BUS_NUM_PLATE"); // 차량 번호판


            System.out.println("BUSID = " + BUSID);
            System.out.println("ARRIVALESTIMATETIME = " + ARRIVALESTIMATETIME);
            System.out.println("LOW_TP_CD = " + LOW_TP_CD);
            System.out.println("ROUTEID = " + ROUTEID);
            System.out.println("REST_STOP_COUNT = " + REST_STOP_COUNT);
            System.out.println("LATEST_STOP_NAME = " + LATEST_STOP_NAME);
            System.out.println("BUS_NUM_PLATE = " + BUS_NUM_PLATE);
            System.out.println();
        }

        return result.getBody();
    }

    @SneakyThrows
    public List<BusLocationDto> findBusLocationtByRouteId(int routeId) { // 노선 ID로 버스 위치 조회
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders(); //헤더
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // 한글깨짐 방지

        //서비스 키 인코딩
        String encodedKey = URLEncoder.encode(bus_apikey, "UTF-8");

        UriComponents uri = UriComponentsBuilder
                .fromHttpUrl(busLocation_url)
                .queryParam("serviceKey", encodedKey) //서비스키
                .queryParam("routeId", routeId) // 정류소ID
                .queryParam("numOfRows", 20) // 개수
                .queryParam("pageNo", 1)
                .build(true);

        ResponseEntity<String> result = restTemplate.exchange(uri.toUri(), HttpMethod.GET, new HttpEntity<String>(headers), String.class);

        //xml 형식을 json 형식으로 변환
        JSONObject response = XML.toJSONObject(result.getBody());

        JSONObject ServiceResult = (JSONObject)response.get("ServiceResult"); //ServiceResult의 value들
        JSONObject msgHeader = (JSONObject)ServiceResult.get("msgHeader"); //msgHeader의 value들
        Integer totalCount = (Integer) msgHeader.get("totalCount"); // 총 개수

        JSONObject msgBody = (JSONObject)ServiceResult.get("msgBody"); //msgBody의 value들
        JSONArray itemList = (JSONArray) msgBody.get("itemList"); //itemList의 value들

        List<BusLocationDto> dtos = new ArrayList<>();
        System.out.println("routeID로 버스 위치 조회");
        for(int i=0; i<totalCount; i++) { // 정류장 개수만큼 반복
            JSONObject array = (JSONObject) itemList.get(i);
            BusLocationDto busLocationDto = new BusLocationDto();

            System.out.println("(" + i + ")");
            Integer BUSID = (Integer)array.get("BUSID"); // 버스 ID
            String BUS_NUM_PLATE = (String)array.get("BUS_NUM_PLATE"); // 차량 번호
            Integer LOW_TP_CD = (Integer)array.get("LOW_TP_CD"); // 저상버스 여부 (0:일반, 1:저상)
            Integer DIRCD = (Integer)array.get("DIRCD"); // 진행방향코드(0:상행, 1:하행, 2:순환)
            Integer PATHSEQ = (Integer)array.get("PATHSEQ"); // 노드 순번
            Integer LATEST_STOPSEQ = (Integer)array.get("LATEST_STOPSEQ"); // 최근 정류소순번
            Integer LATEST_STOP_ID = (Integer)array.get("LATEST_STOP_ID"); // 최근 정류소ID
            String LATEST_STOP_NAME = (String)array.get("LATEST_STOP_NAME"); // 최근 정류소 명
            Integer REMAIND_SEAT = (Integer)array.get("REMAIND_SEAT"); // 차량 빈자리 수(255:사용안함)
            Integer CONGESTION = (Integer)array.get("CONGESTION"); // 혼잡도(1:여유, 2:보통, 3:혼잡, 255:사용안함)
            Integer LASTBUSYN = (Integer)array.get("LASTBUSYN"); // 막차 코드(0:일반, 1:막차)

            busLocationDto.setBUSID(BUSID);
            busLocationDto.setBUS_NUM_PLATE(BUS_NUM_PLATE);
            busLocationDto.setLOW_TP_CD(LOW_TP_CD);
            busLocationDto.setDIRCD(DIRCD);
            busLocationDto.setPATHSEQ(PATHSEQ);
            busLocationDto.setLATEST_STOPSEQ(LATEST_STOPSEQ);
            busLocationDto.setLATEST_STOP_ID(LATEST_STOP_ID);
            busLocationDto.setLATEST_STOP_NAME(LATEST_STOP_NAME);
            busLocationDto.setREMAIND_SEAT(REMAIND_SEAT);
            busLocationDto.setCONGESTION(CONGESTION);
            busLocationDto.setLASTBUSYN(LASTBUSYN);

            dtos.add(i, busLocationDto);
            System.out.println("array = " + array);
            System.out.println();
        }

        return dtos;
    }

    @SneakyThrows
    public List<BusRouteListDto> findBusRouteListByRouteId(int routeId) { // 노선 ID로 버스 정류소 목록 검색
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders(); //헤더
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // 한글깨짐 방지

        //서비스 키 인코딩
        String encodedKey = URLEncoder.encode(bus_apikey, "UTF-8");

        UriComponents uri = UriComponentsBuilder
                .fromHttpUrl(busIdList_url)
                .queryParam("serviceKey", encodedKey) //서비스키
                .queryParam("routeId", routeId) // 정류소ID
                .queryParam("numOfRows", 300) // 개수
                .queryParam("pageNo", 1)
                .build(true);

        ResponseEntity<String> result = restTemplate.exchange(uri.toUri(), HttpMethod.GET, new HttpEntity<String>(headers), String.class);

        //xml 형식을 json 형식으로 변환
        JSONObject response = XML.toJSONObject(result.getBody());

        JSONObject ServiceResult = (JSONObject)response.get("ServiceResult"); //ServiceResult의 value들
        JSONObject msgHeader = (JSONObject)ServiceResult.get("msgHeader"); //msgHeader의 value들
        Integer totalCount = (Integer) msgHeader.get("totalCount"); // 정류장 총 개수

        JSONObject msgBody = (JSONObject)ServiceResult.get("msgBody"); //msgBody의 value들
        JSONArray itemList = (JSONArray) msgBody.get("itemList"); //itemList의 value들

        List<BusRouteListDto> dtos = new ArrayList<>();
        System.out.println("routeID로 노선 검색");
        for(int i=0; i<totalCount; i++) { // 정류장 개수만큼 반복
            JSONObject array = (JSONObject) itemList.get(i);
            BusRouteListDto busRouteListDto = new BusRouteListDto();

            System.out.println("(" + i + ")");
            String BSTOPNM = (String)array.get("BSTOPNM"); // 정류장 이름
            Integer BSTOPID = (Integer)array.get("BSTOPID"); // 정류소 ID
            Integer SHORT_BSTOPID = (Integer)array.get("SHORT_BSTOPID"); // 단축 정류소ID
            Integer PATHSEQ = (Integer)array.get("PATHSEQ"); // 노드 순번
            Integer BSTOPSEQ = (Integer)array.get("BSTOPSEQ"); // 정류소 순번
            Integer DIRCD = (Integer)array.get("DIRCD"); // 방향코드(0:상행, 1:하행, 2:순환)
            BigDecimal POSX = (BigDecimal)array.get("POSX"); // X 좌표
            BigDecimal POSY = (BigDecimal)array.get("POSY"); // Y 좌표

            busRouteListDto.setBSTOPNM(BSTOPNM);
            busRouteListDto.setBSTOPID(BSTOPID);
            busRouteListDto.setSHORT_BSTOPID(SHORT_BSTOPID);
            busRouteListDto.setPATHSEQ(PATHSEQ);
            busRouteListDto.setBSTOPSEQ(BSTOPSEQ);
            busRouteListDto.setDIRCD(DIRCD);
            busRouteListDto.setPOSX(POSX);
            busRouteListDto.setPOSY(POSY);

            dtos.add(i, busRouteListDto);
            System.out.println("array = " + array);
            System.out.println();
        }

        return dtos;
    }

    /**
     * 
     *  JsonArray 아니고 JsonObject일 경우도 get 해야하는데 너무 반복이라 일단 코드 안넣어서 그거도 해야함
     */
    @SneakyThrows
    public List<BusInfoDto> findBusInfoByBusNum(Object busNum) { // 버스 번호로 버스 정보 조회
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders(); //헤더
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // 한글깨짐 방지

        //서비스 키 인코딩
        String encodedKey = URLEncoder.encode(bus_apikey, "UTF-8");
        if(busNum.getClass().getName() == "java.lang.String") {
            busNum = URLEncoder.encode(busNum.toString(), "UTF-8");
        }

        UriComponents uri = UriComponentsBuilder
                .fromHttpUrl(busNum_url)
                .queryParam("serviceKey", encodedKey) //서비스키
                .queryParam("routeNo", busNum) // 버스 번호
                .queryParam("numOfRows", 1000) // 개수
                .queryParam("pageNo", 1)
                .build(true);

        ResponseEntity<String> result = restTemplate.exchange(uri.toUri(), HttpMethod.GET, new HttpEntity<String>(headers), String.class);

        //xml 형식을 json 형식으로 변환
        JSONObject response = XML.toJSONObject(result.getBody());

        //json파싱
        JSONObject ServiceResult = (JSONObject)response.get("ServiceResult"); //ServiceResult의 value들
        JSONObject msgBody = (JSONObject)ServiceResult.get("msgBody"); //msgBody의 value들
        JSONObject msgHeader = (JSONObject)ServiceResult.get("msgHeader"); //msgBody의 value들
        Integer totalCount = (Integer)msgHeader.get("totalCount"); //msgBody의 value들

        // 만약 배열로 제공된다면
        if(msgBody.get("itemList").getClass().getName() == "org.json.JSONArray") {
            JSONArray itemList = (JSONArray) msgBody.get("itemList"); //itemList의 value들

            List<BusInfoDto> dtos = new ArrayList<>();
            System.out.println(" 버스번호로 버스정보 조회");
            for(int i=0; i< totalCount; i++) { // 아이템리스트 반환개수만큼
                JSONObject array = (JSONObject) itemList.get(i);
                BusInfoDto busInfoDto = new BusInfoDto();

                Object ROUTENO = array.get("ROUTENO"); // 노선 번호

                if (ROUTENO.equals(busNum)) { //해당하는 버스 번호의 버스 정보만 출력
                    Integer ROUTEID = (Integer) array.get("ROUTEID"); // 노선 ID
                    Integer ROUTETPCD = (Integer) array.get("ROUTETPCD"); // 노선 유형코드
                    Object FBUS_DEPHMS = array.get("FBUS_DEPHMS"); // 첫차 시간
                    Object LBUS_DEPHMS = array.get("LBUS_DEPHMS"); // 막차 시간
                    Integer MIN_ALLOCGAP = (Integer) array.get("MIN_ALLOCGAP"); //최소 배차간격
                    Integer MAX_ALLOCGAP = (Integer) array.get("MAX_ALLOCGAP"); //최대 배차간격
                    Integer TURN_BSTOPID = (Integer) array.get("TURN_BSTOPID"); //회차지 정류소 ID
                    String TURN_BSTOPNM = (String) array.get("TURN_BSTOPNM"); // 회차지 정류소명
                    Integer ORIGIN_BSTOPID = (Integer) array.get("ORIGIN_BSTOPID"); // 기점 정류소 ID
                    String ORIGIN_BSTOPNM = (String) array.get("ORIGIN_BSTOPNM"); // 기점 정류소 명
                    Integer DEST_BSTOPID = (Integer) array.get("DEST_BSTOPID"); //종점 정류소 ID
                    String DEST_BSTOPNM = (String) array.get("DEST_BSTOPNM"); // 종점 정류소명

                    busInfoDto.setROUTEID(ROUTEID);
                    busInfoDto.setROUTETPCD(ROUTETPCD);
                    busInfoDto.setFBUS_DEPHMS(FBUS_DEPHMS);
                    busInfoDto.setLBUS_DEPHMS(LBUS_DEPHMS);
                    busInfoDto.setMIN_ALLOCGAP(MIN_ALLOCGAP);
                    busInfoDto.setMAX_ALLOCGAP(MAX_ALLOCGAP);
                    busInfoDto.setTURN_BSTOPID(TURN_BSTOPID);
                    busInfoDto.setTURN_BSTOPNM(TURN_BSTOPNM);
                    busInfoDto.setORIGIN_BSTOPID(ORIGIN_BSTOPID);
                    busInfoDto.setORIGIN_BSTOPNM(ORIGIN_BSTOPNM);
                    busInfoDto.setDEST_BSTOPID(DEST_BSTOPID);
                    busInfoDto.setDEST_BSTOPNM(DEST_BSTOPNM);

                    dtos.add(i, busInfoDto);
                    System.out.println("array = " + array);

                    return dtos;
                }

            }
        }
        else { // 만약 배열이 아닌 1개의 객체만 제공된다면
            JSONObject itemList = (JSONObject) msgBody.get("itemList");
            System.out.println("itemList = " + itemList);
        }
        return null;
    }
}
