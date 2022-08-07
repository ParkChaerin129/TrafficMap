package teletubbies.map.way;

import java.util.List;

public interface WayService {
    List<WayDto> findWay(double startX, double startY, double endX, double endY, String startName, String endName, Number option);

    String findTransWay(String sName, String eName);
}
