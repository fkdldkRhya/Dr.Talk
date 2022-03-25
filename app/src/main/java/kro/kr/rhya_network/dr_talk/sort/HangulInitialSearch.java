package kro.kr.rhya_network.dr_talk.sort;

public class HangulInitialSearch {

    private final char HANGUL_BEGIN_UNICODE = 44032;
    private final char HANGUL_LAST_UNICODE = 55203;
    private final char HANGUL_BASE_UNIT = 588;
    private final char[] INITIAL_SOUND = { 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };

    private boolean isInitialSound(char searchar){
        for(char c:INITIAL_SOUND){
            if(c == searchar){
                return true;
            }
        }
        return false;
    }
    private char getInitialSound(char c) {
        int hanBegin = (c - HANGUL_BEGIN_UNICODE);
        int index = hanBegin / HANGUL_BASE_UNIT;
        return INITIAL_SOUND[index];
    }

    private boolean isHangul(char c) {
        return HANGUL_BEGIN_UNICODE <= c && c <= HANGUL_LAST_UNICODE;
    }

    public HangulInitialSearch() { }

    public boolean matchString(String value, String search){
        int t = 0;
        int seof = value.length() - search.length();
        int slen = search.length();
        if(seof < 0)
            return false;
        for(int i = 0;i <= seof;i++){
            t = 0;
            while(t < slen){
                if(isInitialSound(search.charAt(t)) && isHangul(value.charAt(i+t))){
                    if(getInitialSound(value.charAt(i+t))==search.charAt(t))
                        t++;
                    else
                        break;
                } else {
                    if(value.charAt(i+t)==search.charAt(t))
                        t++;
                    else
                        break;
                }
            }
            if(t == slen)
                return true;
        }
        return false;
    }
}
