package traffic;


import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

class TrafficLightSimulator {
  private final TrafficLightsInfo trafficLightsInfo = new TrafficLightsInfo();
  private final Timer timer = new Timer("QueueThread", trafficLightsInfo);
  private final Scanner scanner = new Scanner(System.in);

  public TrafficLightSimulator() {
    timer.start();
  }

  public void printMenu() {
    System.out.println("Menu:");
    System.out.println("1. Add road");
    System.out.println("2. Delete road");
    System.out.println("3. Open system");
    System.out.println("0. Quit");
  }

  public void setup() {
    System.out.println("Welcome to the traffic management system!");

    System.out.print("Input the number of roads: ");
    while (true) {
      try {
        var input = scanner.nextLine();
        int option = Integer.parseInt(input);
        if (option <= 0) {
          System.out.print("Error! Incorrect Input. Try again: ");
          continue;
        }
        trafficLightsInfo.setRoads(option);
        trafficLightsInfo.setQueue(new Road[option]);
        break;
      } catch (NumberFormatException e) {
        System.out.print("Error! Incorrect Input. Try again: ");
      }
    }

    System.out.print("Input the interval: ");
    while (true) {
      try {
        var input = scanner.nextLine();
        int option = Integer.parseInt(input);
        if (option <= 0) {
          System.out.print("Error! Incorrect Input. Try again: ");
          continue;
        }
        trafficLightsInfo.setInterval(option);
        break;
      } catch (NumberFormatException e) {
        System.out.print("Error! Incorrect Input. Try again: ");
      }
    }
    Util.clearConsole();

    while (true) {
      printMenu();
      var option = scanner.nextLine();
      if (option.equals("0")) {
        timer.stopTimer(); // Stop the timer thread
        System.out.println("Bye!");
        break;
      }
      switch (option) {
        case "1":
          System.out.print("Input road name: ");
          String roadName = scanner.nextLine();
          trafficLightsInfo.addRoad(roadName);
          scanner.nextLine();
          // Wait for Enter
          break;
        case "2":
          trafficLightsInfo.deleteRoad();
          scanner.nextLine(); // Wait for Enter
          break;
        case "3":
          openSystemState();
          break;
        default:
          Util.clearConsole();
          System.out.println("Incorrect option");
          scanner.nextLine();
      }
      Util.clearConsole();
    }
  }

  private void openSystemState() {
    Util.clearConsole();
    trafficLightsInfo.setSystemState(true);
    while (true) {
      var input = scanner.nextLine(); // Wait for Enter
      trafficLightsInfo.setSystemState(false);
      break;
    }
    Util.clearConsole();
  }
}

class Timer extends Thread {
  private volatile boolean running = true; // Control thread state
  private final TrafficLightsInfo trafficLightsInfo;

  Timer(String name, TrafficLightsInfo trafficLightsInfo) {
    super(name);
    this.trafficLightsInfo = trafficLightsInfo;
  }

  @Override
  public void run() {
    while (running) {
      try {
        if (trafficLightsInfo.isSystemState()) {
          Util.clearConsole();
          printInfo();

        }
        trafficLightsInfo.updateRoad();
        TimeUnit.SECONDS.sleep(1);
        trafficLightsInfo.setSecondsElapsed(trafficLightsInfo.getSecondsElapsed()+1);
      } catch (InterruptedException e) {
        running = false; // Exit the loop if thread is interrupted
      }
    }
  }

  private void printInfo() {
    System.out.println("! " + trafficLightsInfo.getSecondsElapsed() + "s. have passed since system startup !");
    System.out.println("! Number of roads: " + trafficLightsInfo.getRoads() + " !");
    System.out.println("! Interval: " + trafficLightsInfo.getInterval() + " !");
    System.out.println();
    trafficLightsInfo.displayRoads();
    System.out.println();
    System.out.println("! Press \"Enter\" to open menu !");
  }

  public void stopTimer() {
    running = false; // Safely stop the thread
    interrupt();
  }
}

class Road {
  private String name;
  private boolean isOpen;
  private int timeRemaining;

  public Road(String name, boolean isOpen, int timeRemaining) {
    this.name = name;
    this.isOpen = isOpen;
    this.timeRemaining = timeRemaining;
  }

  @Override
  synchronized public String toString() {
    String state = isOpen ? "open" : "closed";
    return String.format("%s will be %s for %ds.",name,state,timeRemaining);
  }

  public synchronized String getName() {
    return name;
  }

  public synchronized void setName(String name) {
    this.name = name;
  }

  public synchronized boolean isOpen() {
    return isOpen;
  }

  public synchronized void setOpen(boolean open) {
    isOpen = open;
  }

  public synchronized int getTimeRemaining() {
    return timeRemaining;
  }

  public synchronized void setTimeRemaining(int timeRemaining) {
    this.timeRemaining = timeRemaining;
  }
}

class Util {
  public static void clearConsole() {
    try {
      var clearCommand = System.getProperty("os.name").contains("Windows") ? new ProcessBuilder("cmd", "/c", "cls") : new ProcessBuilder("clear");
      clearCommand.inheritIO().start().waitFor();
    } catch (InterruptedException | IOException e) {
      System.out.println("Error" + e.toString());
    }
  }
}


class TrafficLightsInfo {
  private int roads;
  private int interval;
  private int secondsElapsed;
  private boolean systemState;
  private Road[] queue;
  private int front = -1;
  private int rear = -1;
  private int size = 0;

  synchronized boolean isQueueFull() {
    if (front == 0 && rear == roads - 1) {
      return true;
    }
    return rear + 1 == front;
  }

  synchronized public void addRoad(String roadName) {
    // check if queue is full
    if (isQueueFull()) {
      System.out.println("Queue is full");
      return;
    } else if (isQueueEmpty()) {
      front = 0;
      rear = 0;
      queue[rear] = new Road(roadName, true, interval);
    } else {
      var lastRoad = queue[rear];
      rear = (rear + 1) % roads;
      Road newRoad;
      if(lastRoad.isOpen()){
        newRoad = new Road(roadName, false, lastRoad.getTimeRemaining());
      } else {
        newRoad = new Road(roadName, false, lastRoad.getTimeRemaining() +interval);
      }
      queue[rear] = newRoad;
    }
    System.out.printf("%s Added!\n", roadName);
    size++;
    addIntervalForNewRoad();
  }


  synchronized public boolean isQueueEmpty() {
    return front == -1;
  }

  synchronized public void deleteRoad() {
    // check if queue is empty
    if (isQueueEmpty()) {
      System.out.println("Queue is empty");
    } else {
      Road road = queue[front];
      if (front == rear) {
        // Resetting the queue.
        front = -1;
        rear = -1;
      } else {
        front = (front + 1) % roads;
      }
      System.out.printf("%s deleted!\n", road);
    }
    size--;
  }

  synchronized public void displayRoads() {
    if (!isQueueEmpty()) {
      for (int i = front; i != rear; i = (i + 1) % roads) {
        System.out.println(queue[i]);
      }
      System.out.println(queue[rear]);
    }
  }

  public synchronized void addIntervalForNewRoad(){
    if(size > 2){
      for (int i = front; i != rear; i = (i + 1) % roads) {
        var road = queue[i];
        if(road.isOpen()){
          return;
        }
        road.setTimeRemaining(road.getTimeRemaining()+interval);
      }
    }

  }

  synchronized public void updateRoad() {
    if(isQueueEmpty()){
      return;
    }
    // if only one road is prsenet no any operation just return after updating the time and status.
    if(size == 1){
      var road = queue[front];
      if(road.getTimeRemaining() == 1){
        road.setOpen(true);
        road.setTimeRemaining(interval);
      } else {
        road.setTimeRemaining(road.getTimeRemaining()-1);
      }
      return;
    }
    int i = front;
    do{
      var road = queue[i];
      // road is going to close.
      if(road.isOpen() && road.getTimeRemaining() == 1){
        road.setOpen(false);
        road.setTimeRemaining(interval *(size-1));
      } else if(!road.isOpen() && road.getTimeRemaining() == 1){ // road is goint to open
        road.setOpen(true);
        road.setTimeRemaining(interval);
      } else {
        road.setTimeRemaining(road.getTimeRemaining()-1);
      }
      i = (i+1)%roads;
    }while (i!= (rear+1)%roads);

  }

  public synchronized int getRoads() {
    return roads;
  }

  public synchronized void setRoads(int roads) {
    this.roads = roads;
  }

  public synchronized int getInterval() {
    return interval;
  }

  public synchronized void setInterval(int interval) {
    this.interval = interval;
  }

  public synchronized int getSecondsElapsed() {
    return secondsElapsed;
  }

  public synchronized void setSecondsElapsed(int secondsElapsed) {
    this.secondsElapsed = secondsElapsed;
  }

  public synchronized boolean isSystemState() {
    return systemState;
  }

  public synchronized void setSystemState(boolean systemState) {
    this.systemState = systemState;
  }

  public synchronized Road[] getQueue() {
    return queue;
  }

  public synchronized void setQueue(Road[] queue) {
    this.queue = queue;
  }

  public synchronized int getFront() {
    return front;
  }

  public synchronized void setFront(int front) {
    this.front = front;
  }

  public synchronized int getRear() {
    return rear;
  }

  public synchronized void setRear(int rear) {
    this.rear = rear;
  }

  public synchronized int getSize() {
    return size;
  }

  public synchronized void setSize(int size) {
    this.size = size;
  }
}

public class Main {
  public static void main(String[] args) {
    var trafficLightSimulator = new TrafficLightSimulator();
    trafficLightSimulator.setup();
  }
}
