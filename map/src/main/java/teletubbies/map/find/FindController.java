package teletubbies.map.find;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@ComponentScan
public class FindController {

    @Autowired
    private FindServiceImpl findService;
    //(티맵) 명칭(POI) 통합 검색, 엘리베이터 검색을 위한 API 컨트롤러
    @RequestMapping(value="/find/address", method = {RequestMethod.POST})
//    @GetMapping("/find/address")
    public List<FindDto> FindByAPI(String keyword) {
//        String keyword = "스타벅스 부평";
//        String keyword = "모다백화점";
//        String address = "부평구 부평문화로 35";
//        String address = "부평구 경원대로 1397";
//        return findService.findElevatorByAPI(address); //엘리베이터 api
        return findService.findAddressByTmapAPI(keyword); //티맵 api
    }


    /**
     * 엘리베이터만 있는 컨트롤러 추가
     */
    //    @GetMapping("/find/elevator")
    @RequestMapping(value="/find/elevator", method = {RequestMethod.POST})
    public Object ElevatorByAPI(String address) {
        return findService.findElevatorByAPI(address); //엘리베이터 api
    }


    //데이터가 무려 1996개!
//    @GetMapping("/find/stair")
    @RequestMapping(value="/find/stair", method = {RequestMethod.POST})
    public List<StairDto> StairByAPI() { //계단 api
        return findService.findStairs();
    }

}
