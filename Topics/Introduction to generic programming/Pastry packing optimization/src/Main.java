/**
    Box for cakes
*/
class CakeBox {

    private Cake cake;

    public void put(Cake cake) {
    	this.cake = cake;
    }

    public Cake get() {
        return this.cake;
    }
}

/**
    Box for pies
*/
class PieBox {

    private Pie pie;

    public void put(Pie pie) {
    	this.pie = pie;
    }

    public Pie get() {
        return this.pie;
    }
}


/**
    Box for tarts
*/
class TartBox {

    private Tart tart;

    public void put(Tart tart) {
    	this.tart = tart;
    }

    public Tart get() {
        return this.tart;
    }
}

/*
    Hundred more such boring classes OR ...
    magic class for everything everybody is waiting for
*/
class Box<T>{
    T food;

    public void put(T food){
        this.food = food;
    }

    public T get(){
        return this.food;
    }
}

// Don't change classes below
class Cake { }

class Pie { }

class Tart { }