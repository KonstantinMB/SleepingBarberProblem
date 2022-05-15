import java.util.Date;

class Customer implements Runnable {

    int customerId;
    Date inTime;

    Barbershop shop;

    public Customer(Barbershop shop) {

        this.shop = shop;
    }

    public int getCustomerId() {
        return customerId;
    }

    public Date getInTime() {
        return inTime;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    public void run() { // customer thread goes to the shop for the haircut

        goForHairCut();
    }
    private synchronized void goForHairCut() { //customer is added to the list

        shop.add(this);
    }
}