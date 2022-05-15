class Barber implements Runnable {										// initializing the barber

    Barbershop shop;
    int barberId;

    public Barber(Barbershop shop, int barberId) {

        this.shop = shop;
        this.barberId = barberId;
    }

    public void run() {

        while(true) {

            shop.cutHair(barberId);
        }
    }
}