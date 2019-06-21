# equals를 재정의하려거든 hashCode도 재정의하라  

equals를 재정의한 클래스 모두에서 hashCode를 재정의 하는 이유는 hashCode를 재정의 하지 않은 클래스를 HashMap 이나 HashSet 같은 컬렉션의 원소로 사용할 때 문제를 일으 키기 때문이다.  
다음은 Object 명세에서 발췌한 규약이다.  
* equals 비교에 사용되는 정보가 변경되지 않았다면, 애플리케이션이 실행되는 동안 그 객체의 hashCode 메서드는 몇 번을 호출해도 일관되게 같은 값을 반환해야 한다.
    * equals변경 X -> hashCode값 일정  
* equals(Object)가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 똑같은 값을 반환해야 한다.
    * equals가 true -> hashCode값 같다.  
* equals(Object)가 두 객체를 다르다고 판단했더라도, 두 객체의 hashCode가 서로 다른 값을 반환할 필요는 없다.
    * equals가 false -> hashCode값 다를 필요X  

**hashCode 재정의를 잘못했을 때 크게 문제가 되는 조항은 두번째다. 즉, 논리 적으로 같은 객체는 같은 해시코드를 반환해야 한다.**  
equals는 물리적으로 다른 두객체를 두 객체의 값을 기준으로 논리적으로 같다고 볼 수 있다. 하지만 Object의 기본 hashCode 메서드는 이 둘이 전혀 다르다고 판단하여, 서로 다르 값을 반환한다. 아래 예시를 보자.

```java
public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    public PhoneNumber (int areaCode, int prefix, int lineNum) {
        this.areaCode = areaCode;
        this.prefix = prefix;
        this.lineNum = lineNum;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        if(!(o instanceof PhoneNumber))
            return false;
        PhoneNumber pn = (PhoneNumber)o;
        return pn.lineNum && pn.prefix == prefix && pn.areaCode == areaCode;
    }

     ...
}
```

```java
    Map<PhoneNumber,String> m = new HashMap<>();
    m.put(new PhoneNumber(707, 867, 5309),"제니");
    m.get(new PhoneNumber(707, 867, 5309)); // null 반환
```  
위 코드에서 제니가 반환될거 같지만 실제로 null이 반환된다.  
여기에는 2개의 PhoneNumber인스턴스가 사용되었다. 하나는 HashMap에 "제니"를 넣을때 사용됐고, 두번째는 이를 꺼내려 할 때 사용됐다.  
PhoneNumber 클래스는 hashCode를 재정의하지 않았기 때문에 논리적 동치인 두 객체가 서로 다르해시코드를 반환하여 두 번째 규약을 지키지 못한다.  
hashMap은 해시코드가 다른 엔트리끼리는 동치성 비교를 시도조차 하지 않도록 최적화 되어 있다.  
초간단 hashCode를 작성하면 다음과 같다.
```java
// 최악의 (하지만 적법한) hashCode 구현 - 사용금지!
@Override
public int hashCode() {
    return 42;
}
```  
모든 객체에 똑같은 해시코드를 반환하여 해시테이블의 버킷 하나에 생성된 모든 객체가 담겨 마치 연결 리스트(Linked list)처럼 동작한다. 그 결과 평균 수행시간이 O(1)인 해시테이블이 O(n)으로 느려져서 객체가 많아지면 쓸 수 없게 된다.  
> 좋은 해시 함수라면 서로 다른 인스턴스에 다른 해시코드를 반환한다.

좋은 hashCode를 작성하고 동치인 인스턴스에 대해 똑같은 해시코드를 반환할지 단위 테스트를 시행해 보자.(equals와 AutoValue로 생성했다면 단위 테스트는 할 필요가 없다.) 동치인 인스턴스가 서로 다른 해시코드를 반환한다면 원인을 찾아 해결 하자.  
해시 코드 계산에서 반드시 제외 시켜야 할 것  
* 파생 필드
* equals 비교에 사용되지 않은 필드 

이 2가지는 반드시 제거해 주자.그렇지 않으면 hashCode 규약 두 번째를 어기될 위험이 있다.  
Object 클래스는 임의의 개수만큼 객체를 받아 해시코드를 계산해주는 정적 메서드인 hash를 제공한다.  
```java
// 한 줄짜리 hashCode 메서드 - 성능이 살짝 아쉽다.
@Override
public int hashCode() {
        return Ojbects.hash(lineNum, prefix, areaCode)
}
```
 이 메서드를 사용할시 한줄로 간결하게 표현 할 수 있지만 성능은 느리다.입력 인수를 담기 위한 배열이 만들어지고, 입력 중 기본타입이 있다면 박싱과 언박싱도 거쳐야 하기 때문이다.  
따라서 Object가 제공하는 hash메서드는 성능에 민감하지 않은 상황에서만 사용하도록 하자.

##

### 클래스가 불변이고 해시코드를 계산하는 비용이 크다면, 매번 새로 계산하기 보다는 캐싱하는 방식을 고려해야 한다.
이 타입의 객체가 주로 해시의 키로 사용될 것 같다면 객체 생성시 해시 코드를 계산해둬야 한다. 해시의 키로 사용되지 않는 경우라면 hashCode가 처음 불릴 때 계산하는 지연 초기화 전략을 사용해 보자.

```java
해시코드를 지연 초기화하는 hashCode메서드 - 스레드 안정성 까지 고려해야 한다.
private int hashCode;

@Override
public int hashCode() {
    int result = hashCode;
    if(result == 0) {
        result = Short.hashCode(areaCode);
        result = 31 * result + Short.hashCode(prefix);
        result = 31 * result + Short.hashCode(lineNum);
        hashCode = result;
    }
    return result;
}
```

**성능 때문에 해시코드를 계산할 때 핵심 필드를 생략해서는 안된다.**  
속도는 개선 되겠지마나 해시 품질이 나빠져 해시 테이블의 성능을 심각하게 떨어 뜨릴 수 있다.

---

>핵심 정리
1. equals를 재정의 할때는 hashCode도 반드시 재정의 해야한다. 그렇지 않으면 프로그램이 제대로 동작하지 않을 것이다.
2. 아이템 10에서 이야기한 AutoValue프레임워크를 사용하면 멋진 equals와 hashCode를 자동으로 만들어 준다.
