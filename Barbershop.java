import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

class Barbershop {

    Random r = new Random();
    private final AtomicInteger totalHairCuts = new AtomicInteger(0);
    private final AtomicInteger customersLost = new AtomicInteger(0);
    int numberOfChairs, numberOfBarbers, availableBarbers;
    List<Customer> listCustomer;

    public Barbershop(int noOfBarbers, int noOfChairs) {

        this.numberOfChairs = noOfChairs; // number of chairs in the waiting room
        listCustomer = new LinkedList<Customer>(); // list to store the arriving customers
        this.numberOfBarbers = noOfBarbers; // the total number of barbers
        availableBarbers = noOfBarbers;
    }

    public AtomicInteger getTotalHairCuts() {

        totalHairCuts.get();
        return totalHairCuts;
    }

    public AtomicInteger getCustomerLost() {

        customersLost.get();
        return customersLost;
    }

    public void cutHair(int barberId) {
        Customer customer;
        // listCustomer is a shared resource so it has been synchronized to avoid any unexpected errors in the list when multiple threads access it
        synchronized (listCustomer) {
            while (listCustomer.size() == 0) {

                System.out.println("\nBarber " + barberId + " is waiting for the customer and sleeps in his chair");
                try {
                    listCustomer.wait(); //barber sleeps if there are no customers in the shop
                } catch (InterruptedException iex) {
                    iex.printStackTrace();
                }
            }

            // Implementing the FIFO approach, barber takes the first customer from the head of the list for haircut
            customer = (Customer) ((LinkedList<?>) listCustomer).poll();

            System.out.println("Customer " + customer.getCustomerId() +
                    " finds Barber " + barberId +" asleep and wakes up Barber " + barberId);
        }

        int millisDelay = 0;

        try {

            availableBarbers--; //decreases the amount of available barbers as one of them starts working
            //cutting hair of the customer and the customer sleeps
            System.out.println("Barber " + barberId + " cutting hair of Customer " + customer.getCustomerId() + " so customer sleeps");

            double val = r.nextGaussian() * 2000 + 4000;
            millisDelay = Math.abs((int) Math.round(val));
            Thread.sleep(millisDelay);

            System.out.println("\nCompleted Cutting hair of " + customer.getCustomerId() + " by Barber " +
                    barberId + " in " + millisDelay + " milliseconds.");

            totalHairCuts.incrementAndGet();

            // customer exits barbershop:
            if (listCustomer.size() > 0) {
                System.out.println("Barber " + barberId + " wakes up " + customer.getCustomerId() +" in the waiting room");
            }

            availableBarbers++; // barber is available for haircut for the next customer
        } catch (InterruptedException iex) {
            iex.printStackTrace();
        }

    }

    public void add(Customer customer) {

        System.out.println("\nCustomer " + customer.getCustomerId() +
                " enters through the entrance door in the the shop at " + customer.getInTime());
        synchronized (listCustomer) {

            // If no chairs are available for the customer, he leaves the shop
            if (listCustomer.size() == numberOfChairs) {

                System.out.println("\nNo chair available for customer " + customer.getCustomerId() +
                        " so customer leaves the shop");
                customersLost.incrementAndGet();
                return;
            } else if (availableBarbers > 0) {
                ((LinkedList<Customer>) listCustomer).offer(customer); // adds the customer at the tail of the customerList (FIFO)
                listCustomer.notify();
            } else { //If barbers are busy and there are chairs in the waiting room then the customer sits on the chair in the waiting room
                ((LinkedList<Customer>) listCustomer).offer(customer);

                System.out.println("All barber(s) are busy so Customer " + customer.getCustomerId() + " takes a chair in the waiting room");

                if (listCustomer.size() == 1)
                    listCustomer.notify();
            }
        }
    }
}
