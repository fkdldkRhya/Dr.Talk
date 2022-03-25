package kro.kr.rhya_network.dr_talk.util;


/*
 * getCookie 함수의 결과에서 특정 쿠키만 가져오는 함수
 *
 * result --> CookieManager.getCookie result
 * key --> Cookie name
 */
public class CookieFinder {
    /**
     * 쿠키 데이터 출력
     * @param result All cookie
     * @param key Cookie name
     * @return Cookie value
     */
    public String findCookie(String result, String key) {
        if (result == null) {
            return  null;
        }

        if (!result.contains(";")) {
            if (result.split("=")[0].trim().equals(key.trim())) {
                return result.split("=")[1].trim();
            }else {
                return null;
            }
        }

        String[] root_split = result.split(";");
        for (String root_node : root_split) {
            root_node = root_node.trim();
            if (root_node.split("=")[0].trim().equals(key.trim())) {
                return root_node.split("=")[1].trim();
            }
        }

        return null;
    }
}
