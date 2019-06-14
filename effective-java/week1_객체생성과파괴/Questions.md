### Item1
* 디자인패턴의 팩터리 메서드와 정적 팩터리 메서드는 어떻게 다른가?
    * https://github.com/keesun/study/issues/7
* enumset
    * http://alecture.blogspot.com/2012/11/enumset.html
    * https://www.baeldung.com/java-enumset

#### Item2
* 점층적 생성자 패턴(telescoping constructor pattern)
    * 생성자 오버라이딩을 통해 매개변수의 수가 다른 생성자를 늘려나가는 패턴
* 매개변수(Parameter)와 전달인자(Argument)의 구분
    * 매개변수는 변수, 전달인자는 값
    * [위키](https://ko.wikipedia.org/wiki/%EB%A7%A4%EA%B0%9C%EB%B3%80%EC%88%98_(%EC%BB%B4%ED%93%A8%ED%84%B0_%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D)
* 제네릭의 와일드카드 <?>
    * https://palpit.tistory.com/668
* 재귀적 한정 타입
    * [아이템30]((/effective-java/Item30.md)
* simulated self-type(시뮬레이트한 셀프 타입)
    * self 타입이 없는 자바를 위한 우회 방법
    * 추상메서드인 self를 더해 하위 클래스에서는 형변환하지 않고도 메서드 연쇄를 지원할 수 있다
    
### Item3
* 리플렉션 공격은 실제하는가? 어떻게 왜 일어나는가?
* 정말 싱글턴 생성 방법 중 열거타입(enum)이 가장 좋은가?