package item2.hierarchicalbuilder;

public class Calzone extends Pizza {

    private final boolean sauceInside;

    public static class Builder extends Pizza.Builder<Builder> {
        private boolean sauceInside = false;

        public Builder sauceInside() {
            this.sauceInside = true;
            return this;
        }

        @Override
        public Calzone build() {
            return new Calzone(this);
        }

        @Override
        protected Builder self() {
            return null;
        }
    }

    private Calzone(Builder builder) {
        super(builder);
        sauceInside = builder.sauceInside;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Calzone{");
        sb.append("sauceInside=").append(sauceInside);
        sb.append(", toppings=").append(toppings);
        sb.append('}');
        return sb.toString();
    }

}
