package teletubbies.map.find;

import lombok.SneakyThrows;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class FindServiceImpl implements FindService {
    @Value("${TMAP_APPKEY}")
    private String tmap_apiKey; //티맵 API 앱키 설정

    @Value("${ELEVATOR_APPKEY}")
    private String elevator_apikey; //엘리베이터 API 키 설정

    @Value("${TMAP_URL}")
    private String tmap_url;

    @Value("${ELEVATOR_URL}")
    private String elevator_url;

    FindDto findDto = new FindDto();

//    @SneakyThrows
    @SneakyThrows
    public List<FindDto> findAddressByTmapAPI(String FindName) { // 통합 검색해서
        //RestTemplate : REST API 호출이후 응답을 받을 때까지 기다리는 동기방식
        RestTemplate restTemplate = new RestTemplate();

        //URI 생성
        URI uri = UriComponentsBuilder
                .fromUriString(tmap_url)
                .queryParam("version", 1) //version은 1
                .queryParam("searchKeyword",FindName) //일단 스타벅스 부평점으로 검색
                .queryParam("count",10) // 10개만 출력
                .encode()
                .build()
                .toUri();

        //헤더를 넣기 위한 것
        RequestEntity<Void> req = RequestEntity
                .get(uri)
                .header("appKey", tmap_apiKey) //앱키
                .build();

        //response
        ResponseEntity<String> result = restTemplate.exchange(req, String.class);

        /**
         * 수정해야할 부분!!!!!!!!!!!!!
         * JSON 가공 부분
         * findDto의 set 부분
         */
        //받아온 JSON 데이터 가공
        //json parser
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject)parser.parse(result.getBody());
        //searchPoiInfo의 value들
        JSONObject searchPoiInfo = (JSONObject)object.get("searchPoiInfo");
        //pois의 value들
        JSONObject pois = (JSONObject)searchPoiInfo.get("pois");
        //poi의 value는 배열이라 JSONArray 사용
        JSONArray poiArr = (JSONArray) pois.get("poi");

        List<FindDto> dtos = new ArrayList<>(); //리스트에 담을 dtos 선언

        //다시 poi의 value를 받아온 배열을 10개 담기 (검색했을 때 출력하는 리스트 10개)
        for (int i=0; i<10; i++) {
            FindDto findDto = new FindDto();
            object = (JSONObject) poiArr.get(i);


            //이제 newAddress 안의 경도, 위도, 도로명 주소 쓰기 위해 또 파싱
            JSONObject newAddressList = (JSONObject) object.get("newAddressList");
            JSONArray newAddress = (JSONArray) newAddressList.get("newAddress");
            JSONObject object1 = (JSONObject) newAddress.get(0);

            //이제 필요한 애들 받아오기
            String fullAddressRoad = (String) object1.get("fullAddressRoad"); //도로명 주소
            String centerLat = (String) object1.get("centerLat"); //위도
            String centerLon = (String) object1.get("centerLon"); //경도
            String name = (String) object.get("name"); // 이름
            String bizName = (String) object.get("bizName"); // 업종명
            String upperBizName = (String) object.get("upperBizName"); //업종명 대분류
//            String middleAddrName = (String) object.get("middleAddrName"); // 도로명주소 ㅇㅇ로
//            String roadName = (String) object.get("roadName"); // 도로명주소 ㅇㅇ로
//            String firstBuildNo = (String) object.get("firstBuildNo"); //건물번호1

            //일단 테스트로 이제 가공한 데이터를 findDto에 저장
            findDto.setName(name);
            findDto.setFullAddressRoad(fullAddressRoad);
            findDto.setLatitude(Double.parseDouble(centerLat));
            findDto.setLongitude(Double.parseDouble(centerLon));
            findDto.setBizName(bizName);
            findDto.setUpperBizName(upperBizName);
//            findDto.setMiddleAddrName(middleAddrName);
//            findDto.setRoadName(roadName);
//            findDto.setFirstBuildNo(firstBuildNo);
//            String addr = middleAddrName + " " + roadName + " " + firstBuildNo;
//            String elev = findElevatorByAPI(addr).toString();
//            findDto.setElevatorState(elev);

            dtos.add(i, findDto);
            System.out.println("dtos = " + dtos);
        }
        System.out.println("dtos = " + dtos);
        return dtos;
    }

    @SneakyThrows
    public Object findElevatorByAPI(String address)  {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders(); //헤더

        UriComponents uri = UriComponentsBuilder
                .fromHttpUrl(elevator_url)
                .queryParam("serviceKey", elevator_apikey) //서비스키
                .queryParam("buld_address", address) //주소
                .queryParam("numOfRows", 1) // 개수
                .queryParam("pageNo", 3)
                .build();

        ResponseEntity<String> result = restTemplate.exchange(uri.toUriString(), HttpMethod.GET, new HttpEntity<String>(headers), String.class);

        /**
         * 수정해야할 부분!!!!!!!!!!!!!
         * JSON 가공 부분
         * findDto의 set 부분
         */
        //데이터 가공
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject)parser.parse(result.getBody());
        System.out.println("object = " + object);
        //response의 value들
        JSONObject response = (JSONObject)object.get("response");
        System.out.println("response = " + response);
        //body의 value들
        JSONObject body = (JSONObject)response.get("body");
        System.out.println("body = " + body);
        //items value들
        JSONObject items = (JSONObject)body.get("items");
        System.out.println("items = " + items);
        //item value들
        JSONObject item = (JSONObject)items.get("item");
        System.out.println("item = " + item);
        //필요한 엘리베이터 정보 받아오기
        String elvtrSttsNm = (String) item.get("elvtrSttsNm");
        System.out.println("elvtrSttsNm = " + elvtrSttsNm);

//        //일단 테스트용 findDto에 저장
//        findDto.setElevatorState(elvtrSttsNm);
        return elvtrSttsNm;
    }

}
