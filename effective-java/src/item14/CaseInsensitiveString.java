package item14;

import java.util.Objects;

//TODO
// CaseInsensitiveString의 참조는 CaseInsensitiveString 참조와만 비교할 수 있다
public final class CaseInsensitiveString implements Comparable<CaseInsensitiveString> {

    //TODO
    // 객체 참조필드가 하나뿐인 경우
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);;
    }

    @Override
    public int compareTo(CaseInsensitiveString cis) {
        //TODO
        // 관계연산자 <, > 를 사용하기 보다는 정적 메서드 .compare()를 사용하자
        // 자바 7부터 박싱 된 기본타입클래스에서 제공한다(Double.compare, Float.compare 등)
        return String.CASE_INSENSITIVE_ORDER.compare(s, cis.s);
    }

}
